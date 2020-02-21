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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.geometry.MutableGeometryCollection;
import org.geotools.geometry.MutablePolygon;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.DefaultCoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;

/**
 * Accepts geometries and collapses all the vertices that will be rendered to the same pixel. 
 * We focus soley on a shape already transformed to screen coordinates.
 *
 * @author jeichar
 * @since 2.1.x
 */
public final class Decimator {
	private final static String CLSS = "Decimator";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private static final double DP_THRESHOLD = 3;
    private static final double EPS = 1e-9;

    private final double pixelDistance;

    /**
     * Builds a decimator that will simplify geometries so that two subsequent points will be at
     * least pixelDistance away from each other when painted on the screen. Set pixelDistance to 0
     * if you don't want any generalization.
     *ce
     */
    public Decimator(double pd) {
       this.pixelDistance = pd;
    }

    

    /** Decimates JTS geometries returning a "Decimated" version. */
    public final Geometry decimate(Geometry geom) {
        if (pixelDistance <= 0.) return geom;
        
        if (geom instanceof MultiPoint) {
            // TODO If bounding box is too small turn it into a single point
            return geom;
        }
        if (geom instanceof GeometryCollection) {
            GeometryCollection current = (GeometryCollection) geom;
            int count = current.getNumGeometries();
            MutableGeometryCollection collection = new MutableGeometryCollection(new Geometry[count],current.getFactory());
            final int numGeometries = current.getNumGeometries();
            for (int i = 0; i < numGeometries; i++) {
                Geometry geo = decimate(current.getGeometryN(i));
                collection.setGeometryN(geo, i);
            }
            geom = collection;
        } 
        else if (geom instanceof LineString) {
            LineString line = (LineString) geom;
            CoordinateSequence seq = line.getCoordinateSequence();
            geom = decimate(line, seq);
        } 
        else if (geom instanceof Polygon) {
            Polygon line = (Polygon) geom;
            final int numRings = line.getNumInteriorRing();  // holes
            LinearRing[] holes = new LinearRing[numRings];
            int index = 0;
            while(index<numRings) {
            	
            }
            MutablePolygon poly = new MutablePolygon((LinearRing)(line.getExteriorRing()),holes,line.getFactory());
            Geometry geo = decimate(line.getExteriorRing());
            poly.setShell((LinearRing)geo);
            
            for (int i = 0; i < numRings; i++) {
                geo = decimate(line.getInteriorRingN(i));
                poly.setHoleN((LinearRing)geo,i);
            }
            geom = poly;
        }
        return geom;
    }
    
    private LineString decimate(LineString g,CoordinateSequence seq) {
        Coordinate[] coords = seq.toCoordinateArray();
        if (g instanceof LinearRing && coords.length<4 ) {
            return decimateRingFully(seq,g.getFactory());
        } 
        
        List<Coordinate> newcoords = new ArrayList<>();
        Coordinate prior = null;
        for(Coordinate c:coords) {
            if(prior==null) {
            	newcoords.add(c);
            }
            else if(	Math.abs(c.x-prior.x)>pixelDistance ||
                		Math.abs(c.y-prior.y)>pixelDistance   ) {
                	newcoords.add(c);
            }
            prior = c;
        }
        if( !newcoords.get(newcoords.size()-1).equals(prior) ) newcoords.add(prior);  // Make sure the last point is included

        Coordinate[] carray = newcoords.stream().toArray(Coordinate[]::new);
        @SuppressWarnings("deprecation")
		CoordinateSequenceFactory csfact = new DefaultCoordinateSequenceFactory();
        CoordinateSequence cs = csfact.create(carray);
        if (g instanceof LinearRing) {
        	return new LinearRing(cs,g.getFactory());
        }
        else {
        	return new LineString(cs,g.getFactory());
        } 
    }

    /**
     * Makes sure the ring is turned into a minimal 3 non equal points one.
     * Use the first, second and last 2 points.
     * @param seq
     */
    private LinearRing decimateRingFully(CoordinateSequence seq,GeometryFactory fact) {
        // degenerate, not enough points to decimate
        if (seq.size() <= 4) return new LinearRing(seq,fact) ;

        Coordinate[] newcoords = new Coordinate[4];
        newcoords[0] = seq.getCoordinate(0);
        newcoords[1] = seq.getCoordinate(1);
        newcoords[2] = seq.getCoordinate(seq.size()-2);
        newcoords[3] = seq.getCoordinate(seq.size()-1);
        @SuppressWarnings("deprecation")
		CoordinateSequenceFactory csfact = new DefaultCoordinateSequenceFactory();
        CoordinateSequence cs = csfact.create(newcoords);
        return new LinearRing(cs,fact);
    }
}
