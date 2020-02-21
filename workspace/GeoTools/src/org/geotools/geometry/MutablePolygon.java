/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.geometry;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 * Extend the Polygon class so that it can be used for decimated
 * geometries - allowing us to update its internal and external lists.
 */
public class MutablePolygon extends Polygon {
	private static final long serialVersionUID = -3725175183948430034L;

	public MutablePolygon(LinearRing shell, LinearRing[] holes, GeometryFactory factory) {
		super(shell, holes, factory);
	}

	public void setShell(LinearRing ring) {
		this.shell = ring;
	}
	
	public void setHoleN(LinearRing ring,int n) {
		this.holes[n] = ring;
	}

}
