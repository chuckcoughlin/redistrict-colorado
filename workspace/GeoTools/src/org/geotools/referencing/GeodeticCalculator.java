/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2015, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    Portions of this file is adapted from Fortran code provided by NOAA.
 *    Programmed for CDC-6600 by LCDR L.Pfeifer NGS ROCKVILLE MD 18FEB75
 *    Modified for IBM SYSTEM 360 by John G.Gergen NGS ROCKVILLE MD 7507
 *    Source: ftp://ftp.ngs.noaa.gov/pub/pcsoft/for_inv.3d/source/
 */
package org.geotools.referencing;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.DirectPosition;
import org.geotools.measure.Latitude;
import org.locationtech.jts.geomgraph.Position;
import org.openjump.coordsys.CoordinateSystem;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import net.sf.geographiclib.GeodesicMask;


/**
 * Performs geodetic calculations on an {@linkplain Ellipsoid ellipsoid}. This class encapsulates a
 * generic ellipsoid and calculates the following properties:
 *
 * <p>
 * <ul>
 *   <li>Distance and azimuth between two points.
 *   <li>Point located at a given distance and azimuth from an other point.
 * </ul>
 * <p>The calculation uses the following information:
 * <p>
 *
 * <ul>
 *   <li>The {@linkplain #setStartingPosition starting position}, which is always considered valid.
 *       It is initially set at (0,0) and can only be changed to another legitimate value.
 *   <li><strong>Only one</strong> of the following:
 *       <ul>
 *         <li>The {@linkplain #setDestinationPosition destination position}, or
 *         <li>An {@linkplain #setDirection azimuth and distance}.
 *       </ul>
 *       The last one set overrides the other and determines what will be calculated.
 * </ul>
 *
 * <p>Note: This class is not thread-safe. If geodetic calculations are needed in a multi-threads
 * environment, create one distinct instance of {@code GeodeticCalculator} for each thread.
 * 
 * For starting and ending position, we assume that user and geodetic coordinates are the same.
 *
 * @since 2.1
 * @version $Id$
 * @author Daniele Franzoni
 * @author Martin Desruisseaux
 */
public class GeodeticCalculator {
	public final static String CLSS = "GeodeticCalculator";

    /**
     * The coordinate reference system for all methods working on {@link Position} objects. If
     * {@code null}, will be created the first time {@link #getCoordinateSystem} is
     * invoked.
     */
    private final CoordinateSystem coordinateSystem;
    private final Ellipsoid ellipsoid;   // The encapsulated ellipsoid
    private final double semiMajorAxis;  //The semi major axis of the reference ellipsoid.
    private final double flattening;     //The flattening the reference ellipsoid.

    /**
     * The (<var>latitude</var>, <var>longitude</var>) coordinate of the first point <strong>in
     * degrees</strong>. This point is set by {@link #setStartingGeographicPoint}.
     */
    private double lat1, long1;

    /**
     * The (<var>latitude</var>, <var>longitude</var>) coordinate of the destination point
     * <strong>in degrees</strong>. This point is set by {@link #setDestinationGeographicPoint}.
     */
    private double lat2, long2;

    /**
     * The distance and azimuth (in degrees) from the starting point ({@link #long1}, {@link #lat1})
     * to the destination point ({@link #long2}, {@link #lat2}).
     */
    private double distance, azimuth;

    /**
     * Tell if the destination point is valid. {@code false} if {@link #long2} and {@link #lat2}
     * need to be computed.
     */
    private boolean destinationValid;

    /**
     * Tell if the azimuth and the distance are valids. {@code false} if {@link #distance} and
     * {@link #azimuth} need to be computed.
     */
    private boolean directionValid;

    /** The object that carries out the geodesic calculations. */
    private Geodesic geod;

    /** Constructs a new geodetic calculator associated with the WGS84 ellipsoid. 
     *  
     */
    public GeodeticCalculator() {
        this.coordinateSystem = CoordinateSystem.GEODETIC;  // Geodetic 2D
        this.ellipsoid = Ellipsoid.DEFAULT;
        this.semiMajorAxis = ellipsoid.getSemiMajorAxis();
        this.flattening = 1 / ellipsoid.getInverseFlattening();
        this.geod = new Geodesic(semiMajorAxis, flattening);
    }

    ///////////////////////////////////////////////////////////
    ////////                                           ////////
    ////////        H E L P E R   M E T H O D S        ////////
    ////////                                           ////////
    ///////////////////////////////////////////////////////////

    /**
     * Checks the latidude validity. The argument {@code latidude} should be greater than or equal
     * to -90 degrees and less than or equal to +90 degrees.
     *
     * @param latitude The latitude value in <strong>decimal degrees</strong>.
     * @throws IllegalArgumentException if {@code latitude} is not between -90 and +90 degrees.
     */
    private static void checkLatitude(final double latitude) throws IllegalArgumentException {
        if (!(latitude >= Latitude.MIN_VALUE && latitude <= Latitude.MAX_VALUE)) {
        	throw new IllegalArgumentException(String.format("%s.checkLatitude: Out of range (%0.2f)", CLSS,latitude));
        }
    }

    /**
     * Checks the longitude validity. The argument {@code longitude} should be finite.
     *
     * @param longitude The longitude value in <strong>decimal degrees</strong>.
     * @throws IllegalArgumentException if {@code longitude} is not finite.
     */
    private static void checkLongitude(final double longitude) throws IllegalArgumentException {
        if (!(Math.abs(longitude) <= Double.MAX_VALUE)) {
        	throw new IllegalArgumentException(String.format("%s.checkLongitude: Out of range (%0.2f)", CLSS,longitude));
        }
    }

    /**
     * Checks the azimuth validity. The argument {@code azimuth} should be finite.
     *
     * @param azimuth The azimuth value in <strong>decimal degrees</strong>.
     * @throws IllegalArgumentException if {@code azimuth} is not finite.
     */
    private static void checkAzimuth(final double azimuth) throws IllegalArgumentException {
        if (!(Math.abs(azimuth) <= Double.MAX_VALUE)) {
        	throw new IllegalArgumentException(String.format("%s.checkAzimuth: Out of range (%0.2f)", CLSS,azimuth));
        }
    }

    /**
     * Checks the orthodromic distance validity. Arguments {@code orthodromicDistance} should be
     * finite.
     *
     * @param distance The orthodromic distance value.
     * @throws IllegalArgumentException if {@code orthodromic distance} is not finite.
     */
    private static void checkOrthodromicDistance(final double distance)
            throws IllegalArgumentException {
        if (!(Math.abs(distance) <= Double.MAX_VALUE)) {
        	throw new IllegalArgumentException(String.format("%s.constructor: Illegal argument : %s", CLSS,"ellipsoid"));
        }
    }

    ///////////////////////////////////////////////////////////////
    ////////                                               ////////
    ////////        G E O D E T I C   M E T H O D S        ////////
    ////////                                               ////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the coordinate system for all methods working on {@link Position} objects.
     *
     * @return The coordinate system for all {@link Position}s.
     * @since 2.2
     */
    public CoordinateSystem getCoordinateSystem() {return coordinateSystem;}

    /**
     * Returns the referenced ellipsoid.
     *
     * @return The referenced ellipsoid.
     */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Set the starting point in geographic coordinates. The {@linkplain #getAzimuth() azimuth}, the
     * {@linkplain #getOrthodromicDistance() orthodromic distance} and the {@linkplain
     * #getDestinationGeographicPoint() destination point} are discarted. They will need to be
     * specified again.
     *
     * @param longitude The longitude in decimal degrees between -180 and +180°
     * @param latitude The latitude in decimal degrees between -90 and +90°
     * @throws IllegalArgumentException if the longitude or the latitude is out of bounds.
     * @since 2.3
     */
    public void setStartingGeographicPoint(double longitude, double latitude)
            throws IllegalArgumentException {
        // Check first in case an exception is raised
        // (in other words, we change all or nothing).
        checkLongitude(longitude);
        checkLatitude(latitude);
        // Check passed. Now performs the changes in this object.
        long1 = longitude;
        lat1 = latitude;
        destinationValid = false;
        directionValid = false;
    }

    /**
     * Set the starting point in geographic coordinates. The <var>x</var> and <var>y</var>
     * coordinates must be the longitude and latitude in decimal degrees, respectively.
     *
     * <p>This is a convenience method for <code>
     * {@linkplain #setStartingGeographicPoint(double,double)
     * setStartingGeographicPoint}(x,y)</code>.
     *
     * @param point The starting point.
     * @throws IllegalArgumentException if the longitude or the latitude is out of bounds.
     * @since 2.3
     */
    public void setStartingGeographicPoint(final Point2D point) throws IllegalArgumentException {
        setStartingGeographicPoint(point.getX(), point.getY());
    }

    /**
     * Set the starting position in user coordinates, which doesn't need to be geographic. The
     * coordinate reference system is the one specified to the {@linkplain
     * #GeodeticCalculator(CoordinateSystem) constructor}.
     *
     * @param position The position in user coordinate reference system.
     * @since 2.3
     */
    public void setStartingPosition(final DirectPosition position)  {
        setStartingGeographicPoint(position.getOrdinate(0), position.getOrdinate(1));
    }

    /**
     * Returns the starting point in geographic coordinates. The <var>x</var> and <var>y</var>
     * coordinates are the longitude and latitude in decimal degrees, respectively. If the starting
     * point has never been set, then the default value is (0,0).
     *
     * @return The starting point in geographic coordinates.
     * @since 2.3
     */
    public Point2D getStartingGeographicPoint() {
        return new Point2D.Double(long1, lat1);
    }

    /**
     * Returns the starting position in user coordinates, which doesn't need to be geographic. The
     * coordinate reference system is the one specified to the {@linkplain
     * #GeodeticCalculator(CoordinateSystem) constructor}.
     *
     * @return The starting position in user CRS.
     * @since 2.3
     */
    public DirectPosition getStartingPosition() {
        DirectPosition position =  new DirectPosition(CoordinateSystem.GEODETIC);
        position.setOrdinate(0, long1);
        position.setOrdinate(1, lat1);
        return position;
    }

    /**
     * Set the destination point in geographic coordinates. The azimuth and distance values will be
     * updated as a side effect of this call. They will be recomputed the next time {@link
     * #getAzimuth()} or {@link #getOrthodromicDistance()} are invoked.
     *
     * @param longitude The longitude in decimal degrees
     * @param latitude The latitude in decimal degrees between -90 and +90°
     * @throws IllegalArgumentException if the longitude or the latitude is out of bounds.
     * @since 2.3
     */
    public void setDestinationGeographicPoint(double longitude, double latitude)
            throws IllegalArgumentException {
        // Check first in case an exception is raised
        // (in other words, we change all or nothing).
        checkLongitude(longitude);
        checkLatitude(latitude);
        // Check passed. Now performs the changes in this object.
        long2 = longitude;
        lat2 = latitude;
        destinationValid = true;
        directionValid = false;
    }

    /**
     * Set the destination point in geographic coordinates. The <var>x</var> and <var>y</var>
     * coordinates must be the longitude and latitude in decimal degrees, respectively.
     *
     * <p>This is a convenience method for <code>
     * {@linkplain #setDestinationGeographicPoint(double,double)
     * setDestinationGeographicPoint}(x,y)</code>.
     *
     * @param point The destination point.
     * @throws IllegalArgumentException if the longitude or the latitude is out of bounds.
     * @since 2.3
     */
    public void setDestinationGeographicPoint(final Point2D point) throws IllegalArgumentException {
        setDestinationGeographicPoint(point.getX(), point.getY());
    }

    /**
     * Set the destination position in user coordinates, which doesn't need to be geographic. The
     * coordinate reference system is the one specified to the {@linkplain
     * #GeodeticCalculator(CoordinateSystem) constructor}.
     *
     * @param position The position in user coordinate reference system.
     * @since 2.2
     */
    public void setDestinationPosition(final DirectPosition p)  {
        setDestinationGeographicPoint(p.getOrdinate(0), p.getOrdinate(1));
    }

    /**
     * Returns the destination point. This method returns the point set by the last call to a <code>
     * {@linkplain #setDestinationGeographicPoint(double,double)
     * setDestinationGeographicPoint}(...)</code> method, <strong>except</strong> if <code>
     * {@linkplain #setDirection(double,double) setDirection}(...)</code> has been invoked after. In
     * this later case, the destination point will be computed from the {@linkplain
     * #getStartingGeographicPoint starting point} to the azimuth and distance specified.
     *
     * @return The destination point. The <var>x</var> and <var>y</var> coordinates are the
     *     longitude and latitude in decimal degrees, respectively.
     * @throws IllegalStateException if the azimuth and the distance have not been set.
     * @since 2.3
     */
    public Point2D getDestinationGeographicPoint() throws IllegalStateException {
        if (!destinationValid) {
            computeDestinationPoint();
        }
        return new Point2D.Double(long2, lat2);
    }

    /**
     * Returns the destination position in user coordinates, which doesn't need to be geographic.
     * The coordinate reference system is the one specified to the {@linkplain
     * #GeodeticCalculator(CoordinateSystem) constructor}.
     *
     * @return The destination position in user CRS.
     * @since 2.2
     */
    public DirectPosition getDestinationPosition() {
        if (!destinationValid) {
            computeDestinationPoint();
        }
        DirectPosition position = new DirectPosition(CoordinateSystem.GEODETIC);
        position.setOrdinate(0, long2);
        position.setOrdinate(1, lat2);
        return position;
    }

    /**
     * Set the azimuth and the distance from the {@linkplain #getStartingGeographicPoint starting
     * point}. The destination point will be updated as a side effect of this call. It will be
     * recomputed the next time {@link #getDestinationGeographicPoint()} is invoked.
     *
     * @param azimuth The azimuth in decimal degrees
     * @param distance The orthodromic distance in the same units as the {@linkplain #getEllipsoid
     *     ellipsoid} axis (meters by default)
     * @throws IllegalArgumentException if the azimuth or the distance is out of bounds.
     * @see #getAzimuth
     * @see #getOrthodromicDistance
     */
    public void setDirection(double azimuth, final double distance)
            throws IllegalArgumentException {
        // Check first in case an exception is raised
        // (in other words, we change all or nothing).
        checkAzimuth(azimuth);
        checkOrthodromicDistance(distance);
        // Check passed. Now performs the changes in this object.
        this.azimuth = azimuth;
        this.distance = distance;
        destinationValid = false;
        directionValid = true;
    }

    /**
     * Returns the azimuth. This method returns the value set by the last call to <code>
     * {@linkplain #setDirection(double,double) setDirection}(azimuth,distance)</code>,
     * <strong>except</strong> if <code>{@linkplain #setDestinationGeographicPoint(double,double)
     * setDestinationGeographicPoint}(...)</code> has been invoked after. In this later case, the
     * azimuth will be computed from the {@linkplain #getStartingGeographicPoint starting point} to
     * the destination point.
     *
     * @return The azimuth, in decimal degrees from -180° to +180°.
     * @throws IllegalStateException if the destination point has not been set.
     * @todo Current implementation will provides an innacurate value for antipodal points. For now
     *     a warning is logged in such case. In a future version (if we have volunter time) we
     *     should provides a solution (search Internet for "<cite>azimuth antipodal points</cite>").
     */
    public double getAzimuth() throws IllegalStateException {
        if (!directionValid) {
            computeDirection();
        }
        return azimuth;
    }

    /**
     * Returns the orthodromic distance (expressed in meters). This method returns the value set by
     * the last call to <code>
     * {@linkplain #setDirection(double,double) setDirection}(azimuth,distance)</code>,
     * <strong>except</strong> if <code>{@linkplain #setDestinationGeographicPoint(double,double)
     * setDestinationGeographicPoint}(...)</code> has been invoked after. In this later case, the
     * distance will be computed from the {@linkplain #getStartingGeographicPoint starting point} to
     * the destination point.
     *
     * @return The orthodromic distance, in the same units as the {@linkplain #getEllipsoid
     *     ellipsoid} axis.
     * @throws IllegalStateException if the destination point has not been set.
     */
    public double getOrthodromicDistance() throws IllegalStateException {
        if (!directionValid) {
            computeDirection();
        }
        return distance;
    }

    /**
     * Computes the destination point from the {@linkplain #getStartingGeographicPoint starting
     * point}, the {@linkplain #getAzimuth azimuth} and the {@linkplain #getOrthodromicDistance
     * orthodromic distance}.
     *
     * @throws IllegalStateException if the azimuth and the distance have not been set.
     * @see #getDestinationGeographicPoint
     */
    private void computeDestinationPoint() throws IllegalStateException {
        if (!directionValid) {
        	throw new IllegalArgumentException(String.format("%s.computeDestinationPoint: Direction not set", CLSS));
        }
        GeodesicData g = geod.Direct(lat1, long1, azimuth, distance);
        lat2 = g.lat2;
        long2 = g.lon2;
        destinationValid = true;
    }

    /**
     * Calculates the meridian arc length between two points in the same meridian in the referenced
     * ellipsoid.
     *
     * @param latitude1 The latitude of the first point (in decimal degrees).
     * @param latitude2 The latitude of the second point (in decimal degrees).
     * @return Returned the meridian arc length between latitude1 and latitude2
     */
    public double getMeridianArcLength(final double latitude1, final double latitude2) {
        checkLatitude(latitude1);
        checkLatitude(latitude2);
        GeodesicData g = geod.Inverse(latitude1, 0, latitude2, 0, GeodesicMask.DISTANCE);
        return g.s12;
    }

    /**
     * Computes the azimuth and orthodromic distance from the {@linkplain
     * #getStartingGeographicPoint starting point} and the {@linkplain
     * #getDestinationGeographicPoint destination point}.
     *
     * @throws IllegalStateException if the destination point has not been set.
     * @see #getAzimuth
     * @see #getOrthodromicDistance
     */
    private void computeDirection() throws IllegalStateException {
        if (!destinationValid) {
            throw new IllegalArgumentException(String.format("%s.computeDirection: Destination not set", CLSS));
        }
        GeodesicData g = geod.Inverse(lat1, long1, lat2, long2);
        azimuth = g.azi1;
        distance = g.s12;
        directionValid = true;
    }

    /**
     * Calculates the geodetic curve between two points in the referenced ellipsoid. A curve in the
     * ellipsoid is a path which points contain the longitude and latitude of the points in the
     * geodetic curve. The geodetic curve is computed from the {@linkplain
     * #getStartingGeographicPoint starting point} to the {@linkplain #getDestinationGeographicPoint
     * destination point}.
     *
     * @param numberOfPoints The number of vertex in the geodetic curve. <strong>NOTE:</strong> This
     *     argument is only a hint and may be ignored in future version (if we compute a real curve
     *     rather than a list of line segments).
     * @return The path that represents the geodetic curve from the {@linkplain
     *     #getStartingGeographicPoint starting point} to the {@linkplain
     *     #getDestinationGeographicPoint destination point}.
     * @todo We should check for cases where the path cross the 90°N, 90°S, 90°E or 90°W boundaries.
     */
    public Shape getGeodeticCurve(final int numberOfPoints) {
        List<Point2D> points = getGeodeticPath(numberOfPoints);
        final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, numberOfPoints + 1);
        Point2D start = points.get(0);
        path.moveTo(start.getX(), start.getY());
        for (int i = 1; i < points.size(); i++) {
            Point2D p = points.get(i);
            path.lineTo(p.getX(), p.getY());
        }

        return path;
    }

    /**
     * Calculates the geodetic curve between two points in the referenced ellipsoid. A curve in the
     * ellipsoid is a path which points contain the longitude and latitude of the points in the
     * geodetic curve. The geodetic curve is computed from the {@linkplain
     * #getStartingGeographicPoint starting point} to the {@linkplain #getDestinationGeographicPoint
     * destination point}.
     *
     * @return The path that represents the geodetic curve from the {@linkplain
     *     #getStartingGeographicPoint starting point} to the {@linkplain
     *     #getDestinationGeographicPoint destination point}.
     */
    public Shape getGeodeticCurve() {
        return getGeodeticCurve(10);
    }

    /**
     * Calculates the geodetic curve between two points in the referenced ellipsoid. A curve in the
     * ellipsoid is a path which points contain the longitude and latitude of the points in the
     * geodetic curve. The geodetic curve is computed from the {@linkplain
     * #getStartingGeographicPoint starting point} to the {@linkplain #getDestinationGeographicPoint
     * destination point}.
     *
     * @param numPoints The number of vertices <strong>between</strong> the start and destination
     *     points
     * @return vertices approximating the curve
     * @todo We should check for cases where the path cross the 90Â°N, 90Â°S, 90Â°E or 90Â°W
     *     boundaries.
     */
    public List<Point2D> getGeodeticPath(int numPoints) {
        if (numPoints < 0) {
        	throw new IllegalArgumentException(String.format("%s.getGeodeticPath: Illegal argument: numPoints = %s", CLSS,numPoints));
        }

        List<Point2D> points = new ArrayList<Point2D>(numPoints + 2);

        if (!directionValid) {
            computeDirection();
        }

        if (!destinationValid) {
            computeDestinationPoint();
        }

        final double delta = distance / (numPoints + 1);

        points.add(new Point2D.Double(long1, lat1));
        GeodesicLine line = geod.Line(lat1, long1, azimuth);

        for (int i = 1; i <= numPoints + 1; i++) {
            GeodesicData g =
                    line.Position(
                            i * delta,
                            GeodesicMask.LATITUDE
                                    | GeodesicMask.LONGITUDE
                                    | GeodesicMask.LONG_UNROLL);
            points.add(new Point2D.Double(g.lon2, g.lat2));
        }

        return points;
    }

    /** Returns a string representation of the current state of this calculator. 
    @Override
    @SuppressWarnings("PMD.CloseResource")
    public String toString() {
        final Vocabulary resources = Vocabulary.getResources(null);
        final TableWriter buffer = new TableWriter(null, " ");
        if (CoordinateSystem != null) {
            buffer.write(resources.getLabel(VocabularyKeys.COORDINATE_REFERENCE_SYSTEM));
            buffer.nextColumn();
            buffer.write(CoordinateSystem.getName().getCode());
            buffer.nextLine();
        }
        if (ellipsoid != null) {
            buffer.write(resources.getLabel(VocabularyKeys.ELLIPSOID));
            buffer.nextColumn();
            buffer.write(ellipsoid.getName().getCode());
            buffer.nextLine();
        }
        final CoordinateFormat cf = new CoordinateFormat();
        final Format nf = cf.getFormat(0);
        buffer.write(resources.getLabel(VocabularyKeys.SOURCE_POINT));
        buffer.nextColumn();
        buffer.write(format(cf, long1, lat1));
        buffer.nextLine();
        if (destinationValid) {
            buffer.write(resources.getLabel(VocabularyKeys.TARGET_POINT));
            buffer.nextColumn();
            buffer.write(format(cf, long2, lat2));
            buffer.nextLine();
        }
        if (directionValid) {
            buffer.write(resources.getLabel(VocabularyKeys.AZIMUTH));
            buffer.nextColumn();
            buffer.write(nf.format(new Angle(azimuth)));
            buffer.nextLine();
        }
        if (directionValid) {
            buffer.write(resources.getLabel(VocabularyKeys.ORTHODROMIC_DISTANCE));
            buffer.nextColumn();
            buffer.write(nf.format(distance));
            if (ellipsoid != null) {
                buffer.write(' ');
                buffer.write(ellipsoid.getAxisUnit().toString());
            }
            buffer.nextLine();
        }
        return buffer.toString();
    }
    */
}
