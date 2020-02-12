/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.openjump.coordsys;


import org.opengis.geometry.MismatchedDimensionException;



/**
 * A two- or three-dimensional coordinate system in which position is specified by geodetic
 * latitude, geodetic longitude, and (in the three-dimensional case) ellipsoidal height. An {@code
 * EllipsoidalCS} shall have two or three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotools.referencing.crs.DefaultGeographicCRS  Geographic},
 *   {@link org.geotools.referencing.crs.DefaultEngineeringCRS Engineering}
 * </TD></TR></TABLE>
 *
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class CoordinateSystem  {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -1452492488902329211L;
    private final String name;
    private final CoordinateSystemAxis[] axes;
    private final int latitudeAxis  = 0;
    private final int longitudeAxis = 1;
    private final int heightAxis 	= 2;

    /**
     * This is the default coordinate system.
     * A two-dimensional ellipsoidal CS with <var>{@linkplain
     * CoordinateSystemAxis#GEODETIC_LONGITUDE geodetic longitude}</var>, <var>{@linkplain
     * CoordinateSystemAxis#GEODETIC_LATITUDE geodetic latitude}</var> axis in decimal
     * degrees.
     */
    public static CoordinateSystem GEODETIC =
            new CoordinateSystem("GEODETIC_2D",
                    CoordinateSystemAxis.GEODETIC_LONGITUDE,
                    CoordinateSystemAxis.GEODETIC_LATITUDE);

    /**
     * A three-dimensional ellipsoidal CS with <var>{@linkplain
     * CoordinateSystemAxis#GEODETIC_LONGITUDE geodetic longitude}</var>, <var>{@linkplain
     * CoordinateSystemAxis#GEODETIC_LATITUDE geodetic latitude}</var>, <var>{@linkplain
     * CoordinateSystemAxis#ELLIPSOIDAL_HEIGHT ellipsoidal height}</var> axis.
     */
    public static CoordinateSystem GEODETIC_3D =
            new CoordinateSystem(
                    "GEODETIC_3D",
                    CoordinateSystemAxis.GEODETIC_LONGITUDE,
                    CoordinateSystemAxis.GEODETIC_LATITUDE,
                    CoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);


    /**
     * Constructs a two-dimensional coordinate system from a name.
     *
     * @param name The coordinate system name.
     * @param axis0 The first axis (latitude).
     * @param axis1 The second axis (longitude).
     */
    public CoordinateSystem(
            final String name, final CoordinateSystemAxis axis0, final CoordinateSystemAxis axis1) {
    	this.name = name;
    	this.axes = new CoordinateSystemAxis[2];
    	this.axes[latitudeAxis] = axis0;
    	this.axes[longitudeAxis] = axis1;

    }

    /**
     * Constructs a three-dimensional coordinate system from a name.
     *
     * @param name The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public CoordinateSystem(
            final String name,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) {
    	this.name = name;
    	this.axes = new CoordinateSystemAxis[3];
    	this.axes[latitudeAxis] = axis0;
    	this.axes[longitudeAxis] = axis1;
    	this.axes[heightAxis] = axis2;
    }

    public String getName() { return this.name; }


    /**
     * Returns {@code true} if the specified axis direction is allowed for this coordinate system.
     * The default implementation accepts only the following directions: {@link AxisDirection#NORTH
     * NORTH}, {@link AxisDirection#SOUTH SOUTH}, {@link AxisDirection#EAST EAST}, {@link
     * AxisDirection#WEST WEST}, {@link AxisDirection#UP UP} and {@link AxisDirection#DOWN DOWN}.
     */
    public boolean isCompatibleDirection(AxisDirection direction) {
        return AxisDirection.NORTH.equals(direction)
                || AxisDirection.EAST.equals(direction)
                || AxisDirection.UP.equals(direction);
    }

    /**
     * Returns the longitude found in the specified coordinate point, always in {@linkplain
     * NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param coordinates The coordinate point expressed in this coordinate system.
     * @return The longitude in the specified array, in {@linkplain NonSI#DEGREE_ANGLE decimal
     *     degrees}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected
     *     dimension.
     */
    public double getLongitude(final double[] coordinates) throws MismatchedDimensionException {
        return coordinates[longitudeAxis];
    }

    /**
     * Returns the latitude found in the specified coordinate point, always in {@linkplain
     * NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param coordinates The coordinate point expressed in this coordinate system.
     * @return The latitude in the specified array, in {@linkplain NonSI#DEGREE_ANGLE decimal
     *     degrees}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected
     *     dimension.
     */
    public double getLatitude(final double[] coordinates) throws MismatchedDimensionException {
    	return coordinates[latitudeAxis];
    }

    /**
     * Returns the height found in the specified coordinate point, always in {@linkplain SI#METER
     * meters}.
     *
     * @param coordinates The coordinate point expressed in this coordinate system.
     * @return The height in the specified array, in {@linkplain SI#METER meters}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected
     *     dimension.
     */
    public double getHeight(final double[] coordinates) throws MismatchedDimensionException {
    	return coordinates[heightAxis];
    }
	/**
	 * @return The dimension of the coordinate system.
	 */
	public int getDimension() { return axes.length; }
	
	/**
	 * Returns the axis for this coordinate system at the specified dimension. Each coordinate
	 * system must have at least one axis.
	 *
	 * @param dimension The zero based index of axis.
	 * @return The axis at the specified dimension.
	 * @throws IndexOutOfBoundsException if {@code dimension} is out of bounds.
	 */
	public CoordinateSystemAxis getAxis(int dimension) throws IndexOutOfBoundsException {
		return axes[dimension];
	}
}
