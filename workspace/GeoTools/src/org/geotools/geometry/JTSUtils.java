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

import org.geotools.util.GeometryUtilities;
import org.locationtech.jts.geom.Coordinate;
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
    public static Coordinate directPositionToCoordinate(
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
    public static void directPositionToCoordinate( DirectPosition dp, Coordinate result) {
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

    /** Extracts the values of a JTS coordinate into an existing DirectPosition object. */
    public static void coordinateToDirectPosition(
            org.locationtech.jts.geom.Coordinate c, DirectPosition result) {
        // Get the CRS so we can figure out the dimension of the result.
        CoordinateReferenceSystem crs = result.getCoordinateReferenceSystem();
        int d = crs.getCoordinateSystem().getDimension();
        final CoordinateSystem cs = crs.getCoordinateSystem();

        if (d >= 1) {
            int xIndex = GeometryUtilities.getDirectedAxisIndex(cs, AxisDirection.EAST);
            result.setOrdinate(xIndex, c.x); // 0
            if (d >= 2) {
                int yIndex = GeometryUtilities.getDirectedAxisIndex(cs, AxisDirection.NORTH);
                result.setOrdinate(yIndex, c.y); // 1
                if (d >= 3) {
                    int zIndex = GeometryUtilities.getDirectedAxisIndex(cs, AxisDirection.UP);
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
}
