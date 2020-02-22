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
 */
package org.geotools.render;

import java.util.logging.Logger;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 * Accepts geometries and collapses all the vertices that will be rendered to the same pixel. 
 * The shape dimensions are lat/long.
 */
public final class Decimator {
	private final static String CLSS = "Decimator";
	private static Logger LOGGER = Logger.getLogger(CLSS);
    private final double minimumSpacing;

    /**
     * Builds a decimator that will simplify geometries so that two subsequent points will be at
     * least minimumSpacing away from each other when painted on the screen. Set minimumSpacing to 0
     * if you don't want any generalization.
     *ce
     */
    public Decimator(double pd) {
       this.minimumSpacing = pd;
    }
    
    public int decimateLine(LineString g,double[] x, double[] y) {
    	Coordinate[] coords = g.getCoordinates();
        if (g instanceof LinearRing && coords.length<4 ) {
            return decimateRingFully((LinearRing)g, x,y);
        } 
        
        int n = 0;
        Coordinate prior = null;
        for(Coordinate c:coords) {
            if(prior==null) {
            	x[n] = c.x;
            	y[n] = c.y;
            	n++;
            }
            else if(	Math.abs(c.x-prior.x)>minimumSpacing ||
                		Math.abs(c.y-prior.y)>minimumSpacing   ) {
            	x[n] = c.x;
            	y[n] = c.y;
            	n++;
            }
            prior = c;
        }
        if( n<x.length && (x[n]!=prior.x || y[n]!=prior.y )) {
        	x[n] = prior.x;
        	y[n] = prior.y;
        	n++;
        };  // Make sure the last point is included

        return n;
    }
    
    /**
     * Decimate the outer shell of a polygon
     * @param g
     * @param x
     * @param y
     * @return
     */
    public int decimatePolygon(Polygon g,double[] x, double[] y) {
    	LineString ls = g.getExteriorRing();
        return decimateLine(ls,x,y);
    }

    /**
     * Makes sure the ring is turned into a minimal 3 non equal points one.
     * Use the first, second and last 2 points.
     * @param seq
     */
    public int decimateRingFully(LinearRing g,double[] x, double[] y) {
    	Coordinate[] coords = g.getCoordinates();
        // degenerate, not enough points to decimate
        if( x.length <= 4 || x.length<=4) return 0 ;

        x[0] = coords[0].x;
        y[0] = coords[0].y;
        x[1] = coords[0].x;
        y[1] = coords[0].y;
        x[2] = coords[coords.length-2].x;
        y[2] = coords[coords.length-2].y;
        x[3] = coords[coords.length-1].x;
        y[3] = coords[coords.length-1].y;
        return 4;
    }
}
