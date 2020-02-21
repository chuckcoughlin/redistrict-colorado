/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.geometry;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Extend the GeometryCollection class so that it can be used for decimated
 * geometries - allowing us to update geometries in the collection.
 */
public class MutableGeometryCollection extends GeometryCollection {
	private static final long serialVersionUID = -3745175183948430034L;

	public MutableGeometryCollection(Geometry[] geometries,  GeometryFactory factory) {
		super(geometries, factory);
	}

	public void setGeometryN(Geometry geom,int n) {
		geometries[n] = geom;
	}

}
