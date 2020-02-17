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

import org.geotools.geometry.jts.Geometries;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * Miscellaneous methods dealing with the Geometry class
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
}
