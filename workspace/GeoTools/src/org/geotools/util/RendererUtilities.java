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
 */
package org.geotools.util;

import java.util.logging.Logger;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

import javafx.scene.shape.Rectangle;

/**
 * Class for holding static utility functions that are common tasks for people using the
 * ShapefileRenderer.
 *
 * @author dblasby
 * @author Simone Giannecchini
 */
public final class RendererUtilities {
	private final static String CLSS = "RendererUtilities";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    /** Utilities classes should not be instantiated. */
    private RendererUtilities() {};

    /**
     * @see https://www.nhc.noaa.gov/gccalc.shtml
     *
     * Convert degrees of latitude to meters. Conversion made at 45 deg (which is appropriate
     * for Colorado.
     *
     * @param deg degrees of latitude
     * @return degrees adjusted to meters
     */
    public static double latitudeToMeters(double degrees) {
        return degrees * 111131.777652;
    }
    public static double longitudeToMeters(double degrees) {
        return degrees * 788463.3470959268;
    }
    
    /**
     * Calculate a scaling conversion from physical to screen coordinates.
     * @param from the source coordinates in degrees lat/lon. 
     * @param to the destination envelope in screen coordinates
     */
    public static double calculateXScale(Envelope from, Rectangle to) {
    	double fromWidth = from.getWidth();
    	double toWidth   = to.getWidth();
    	double scale = toWidth/fromWidth;
        return scale;
    }
    
    /**
     * Calculate a scaling conversion from physical to screen coordinates.
     * @param from the source coordinates in degrees lat/lon. 
     * @param to the destination envelope in screen coordinates
     */
    public static double calculateYScale(Envelope from, Rectangle to) {
    	double fromHeight = from.getHeight();
    	double toHeight   = to.getHeight();
    	double scale = toHeight/fromHeight;
        return scale;
    }


    /**
     * Finds the centroid of the input geometry if input = point, line, polygon --> return a point
     * that represents the centroid of that geom if input = geometry collection --> return a
     * multipoint that represents the centoid of each sub-geom
     *
     * @param g
     */
    public static Geometry getCentroid(Geometry g) {
        if (g instanceof Point || g instanceof MultiPoint) {
            return g;
        } 
        else if (g instanceof GeometryCollection) {
            final GeometryCollection gc = (GeometryCollection) g;
            final Coordinate[] pts = new Coordinate[gc.getNumGeometries()];
            final int length = gc.getNumGeometries();
            for (int t = 0; t < length; t++) {
                pts[t] = pointInGeometry(gc.getGeometryN(t)).getCoordinate();
            }
            return g.getFactory().createMultiPoint(new CoordinateArraySequence(pts));
        } 
        else if (g != null) {
            return pointInGeometry(g);
        }
        return null;
    }

    private static Geometry pointInGeometry(Geometry g) {
        Point p = g.getCentroid();
        if (g instanceof Polygon) {
            // if the geometry is heavily generalized centroid computation may fail and return NaN
            if (Double.isNaN(p.getX()) || Double.isNaN(p.getY()))
                return g.getFactory().createPoint(g.getCoordinate());
            // otherwise let's check if the point is inside. Again, this check and
            // "getInteriorPoint"
            // will work only if the geometry is valid
            if (g.isValid() && !g.contains(p)) {
                try {
                    p = g.getInteriorPoint();
                } catch (Exception e) {
                    // generalized geometries might make interior point go bye bye
                    return p;
                }
            } else {
                return p;
            }
        }
        return p;
    }

  
    /**
     * Finds a centroid for a polygon catching any exceptions resulting from generalization or other
     * polygon irregularities.
     *
     * @param geom The polygon.
     * @return The polygon centroid, or null if it can't be found.
     */
    public static Point getPolygonCentroid(Polygon geom) {
        Point centroid;
        try {
            centroid = geom.getCentroid();
        } catch (Exception e) {
            // generalized polygons causes problems - this
            // tries to hide them.
            try {
                centroid = geom.getExteriorRing().getCentroid();
            } catch (Exception ee) {
                try {
                    centroid = geom.getFactory().createPoint(geom.getCoordinate());
                } catch (Exception eee) {
                    return null; // we're hooped
                }
            }
        }
        return centroid;
    }

    /**
     * Uses a sampling technique to obtain a central point that lies inside the specified polygon.
     *
     * <p>Sampling occurs horizontally along the middle of the polygon obtained from the y
     * coordinate of the polygon centroid.
     *
     * @param geom The polygon.
     * @param centroid The centroid of the polygon, can be null in which case it will be computed
     *     from {@link #getPolygonCentroid(Polygon)}.
     * @param pg The prepared version of geom, can be null in which case it will be computed on
     *     demand.
     * @param gf The geometry factory, can be null in which case the polygons factory will be used.
     * @return A central point that lies inside of the polygon, or null if one could not be found.
     */
    public static Point sampleForInternalPoint(
            Polygon geom,
            Point centroid,
            PreparedGeometry pg,
            GeometryFactory gf,
            double stepSize,
            int numSamples) {

        if (centroid == null) {
            centroid = getPolygonCentroid(geom);
        }
        if (pg == null) {
            pg = PreparedGeometryFactory.prepare(geom);
        }
        if (gf == null) {
            gf = geom.getFactory();
        }

        if (pg.contains(centroid)) {
            return centroid;
        }

        Envelope env = geom.getEnvelopeInternal();
        if (stepSize > 0) {
            numSamples = (int) Math.round(env.getWidth() / stepSize);
        } else if (numSamples > 0) {
            stepSize = env.getWidth() / numSamples;
        } else {
            throw new IllegalArgumentException(
                    "One of stepSize or numSamples must be greater than zero");
        }

        Coordinate c = new Coordinate();
        Point pp = gf.createPoint(c);
        c.y = centroid.getY();
        int max = -1;
        int maxIdx = -1;
        int containCounter = -1;
        for (int i = 0; i < numSamples; i++) {
            c.x = env.getMinX() + stepSize * i;
            pp.geometryChanged();
            if (!pg.contains(pp)) {
                containCounter = 0;
            } else if (i == 0) {
                containCounter = 1;
            } else {
                containCounter++;
                if (containCounter > max) {
                    max = containCounter;
                    maxIdx = i;
                }
            }
        }

        if (maxIdx != -1) {
            int midIdx = max > 1 ? maxIdx - max / 2 : maxIdx;
            c.x = env.getMinX() + stepSize * midIdx;
            pp.geometryChanged();
            return pp;
        } else {
            return null;
        }
    }
}
