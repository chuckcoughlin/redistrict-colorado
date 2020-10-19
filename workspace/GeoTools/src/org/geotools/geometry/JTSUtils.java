/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geotools.geometry.GeometryFactoryFinder;
import org.graalvm.compiler.hotspot.replacements.TypeCheckSnippetUtils.Hints;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateReferenceSystem;
import org.openjump.coordsys.CoordinateSystem;

/**
 * Class with static methods to help the conversion process between JTS geometries and GO-1
 * geometries.
 */
public final class JTSUtils {
    /**
     * This class has only static methods, so we make the constructor private to prevent
     * instantiation.
     */
    private JTSUtils() {}

    /**
     * Common instance of GEOMETRY_FACTORY with the default JTS precision model that can be used to
     * make new geometries.
     */
    public static final org.locationtech.jts.geom.GeometryFactory GEOMETRY_FACTORY =
            new org.locationtech.jts.geom.GeometryFactory();

    
    /**
     * Converts a DirectPosition to a JTS Coordinate. Returns a newly instantiated Coordinate
     * object.
     */
    public static org.locationtech.jts.geom.Coordinate directPositionToCoordinate(
            DirectPosition dp) {
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;
        int d = dp.getDimension();
        if (d >= 1) {
            x = dp.getOrdinate(0);
            if (d >= 2) {
                y = dp.getOrdinate(1);
                if (d >= 3) {
                    z = dp.getOrdinate(2);
                }
            }
        }
        return new org.locationtech.jts.geom.Coordinate(x, y, z);
    }

    /**
     * Sets the coordinate values of an existing JTS Coordinate by extracting values from a
     * DirectPosition. If the dimension of the DirectPosition is less than three, then the unused
     * ordinates of the Coordinate are set to Double.NaN.
     */
    public static void directPositionToCoordinate(
            DirectPosition dp, org.locationtech.jts.geom.Coordinate result) {
        int d = dp.getDimension();
        if (d >= 1) {
            result.x = dp.getOrdinate(0);
            if (d >= 2) {
                result.y = dp.getOrdinate(1);
                if (d >= 3) {
                    result.setZ(dp.getOrdinate(3));
                } else {
                    result.setZ(Double.NaN);
                }
            } else {
                result.y = Double.NaN;
                result.setZ(Double.NaN);
            }
        } else {
            // I can't imagine a DirectPosition with dimension zero, but it
            // can't hurt to have code to handle that case...
            result.x = result.y = Double.NaN;
            result.setZ(Double.NaN);
        }
    }

    /**
     * Converts a DirectPosition to a JTS Point primitive. Returns a newly instantiated Point object
     * that was created using the default GeometryFactory instance.
     */
    public static org.locationtech.jts.geom.Point directPositionToPoint(DirectPosition dp) {
        return GEOMETRY_FACTORY.createPoint(directPositionToCoordinate(dp));
    }

    /** Converts a JTS Coordinate to a DirectPosition with the given CRS. */
    public static DirectPosition coordinateToDirectPosition(
            org.locationtech.jts.geom.Coordinate c, CoordinateReferenceSystem crs) {

        Hints hints = new Hints(Hints.CRS, crs);
        PositionFactory pf = GeometryFactoryFinder.getPositionFactory(hints);

        double[] vertices;
        if (crs == null) vertices = new double[3];
        else vertices = new double[crs.getCoordinateSystem().getDimension()];
        DirectPosition result = pf.createDirectPosition(vertices);
        coordinateToDirectPosition(c, result);
        return result;
    }

    /** Extracts the values of a JTS coordinate into an existing DirectPosition object. */
    public static void coordinateToDirectPosition(
            org.locationtech.jts.geom.Coordinate c, DirectPosition result) {
        // Get the CRS so we can figure out the dimension of the result.
        CoordinateReferenceSystem crs = result.getCoordinateReferenceSystem();
        int d = crs.getCoordinateSystem().getDimension();
        final CoordinateSystem cs = crs.getCoordinateSystem();

        if (d >= 1) {
            int xIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.EAST);
            result.setOrdinate(xIndex, c.x); // 0
            if (d >= 2) {
                int yIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.NORTH);
                result.setOrdinate(yIndex, c.y); // 1
                if (d >= 3) {
                    int zIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.UP);
                    result.setOrdinate(zIndex, c.getZ()); // 2
                    // If d > 3, then the remaining ordinates of the DP are
                    // (so far) left with their original values.  So we init
                    // them to zero here.
                    if (d > 3) {
                        for (int i = 3; i < d; i++) {
                            result.setOrdinate(i, 0.0);
                        }
                    }
                }
            }
        }
    }

    /** Converts a JTS Point to a DirectPosition with the given CRS. */
    public static DirectPosition pointToDirectPosition(
            org.locationtech.jts.geom.Point p, CoordinateReferenceSystem crs) {
        return coordinateToDirectPosition(p.getCoordinate(), crs);
    }

    
}
