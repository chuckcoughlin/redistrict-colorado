/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateSystem;

/**
 * Convert geometry type to a string
 */
public final class GeometryUtilities {
	public static String toText(Geometry geom) {
		String text = geom.getGeometryType();
		switch (Geometries.get(geom)) {
			case POINT:
				text = "Point"; break;
			case MULTIPOINT:
				text = "MultiPoint"; break;
			case LINESTRING:
				// This handles open and closed lines (LinearRings)
				text = "LineString"; break;
			case MULTILINESTRING:
				text = "MultiLineString"; break;
			case POLYGON:
				Polygon polygon = (Polygon)geom;
				text = String.format("Polygon (%d)",polygon.getCoordinates().length); break;
			case MULTIPOLYGON:
				text = "MultiPolygon"; break;
			case GEOMETRYCOLLECTION:
				text = "GeometryCollection"; break;
			default:
				;
		}
		return text;
	}
	/**
     * @return the index of an axis in a given coordinate system,
     * axes are specified using org.opengis.referencing.cs.AxisDirection
     * used by JTS geometry wrappers
     * - code from com.polexis.referencing.cs.CSUtils
     * - is AbstractCS a more appropriate place for this?
     */
    public static int getDirectedAxisIndex(
            final CoordinateSystem cs, final AxisDirection direction) {
        int dimension = cs.getDimension();
        for (int i = 0; i < dimension; i++) {
            if (cs.getAxis(i).getDirection().equals(direction)) {
                return i;
            }
        }
        return -1;
    }
}
