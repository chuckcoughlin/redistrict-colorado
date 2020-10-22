/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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
/**
 * ***************************************************************************** $Id$ $Source:
 * /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/GeometryImpl.java,v $
 * Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 * ****************************************************************************
 */
package org.geotools.operation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.opengis.MismatchedDimensionException;


/**
 * This class implements JTS's CoordinateFilter interface using a GeoAPI MathTransform object to
 * actually perform the work. From GeometryImpl.java.
 */
public class MathTransformFilter implements CoordinateFilter {
	private MathTransform transform;
	private Coordinate tmp;

	public MathTransformFilter(MathTransform transform) {
		this.transform = transform;
		tmp = new Coordinate();
	}

    // Transform the coordinate in place
	public void filter(Coordinate coord) {
		try {
			// Do the transform math, then load the result
			// back into the coordinate.
			transform.transform(coord, tmp);
			coord.x = tmp.x;
			coord.y = tmp.y;
			coord.z = tmp.z;
		} 
		catch (MismatchedDimensionException e) {
			throw new RuntimeException(e);
		} 
		catch (TransformException e) {
			throw new RuntimeException(e);
		}
	}
}

