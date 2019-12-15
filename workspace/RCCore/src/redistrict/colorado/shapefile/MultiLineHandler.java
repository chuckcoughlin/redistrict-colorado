/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 * 
 * $Id: MultiLineHandler.java 2110 2010-10-10 15:07:03Z michaudm $
 */

package redistrict.colorado.shapefile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

/**
 * Wrapper for a Shapefile PolyLine. The streams should be configured as LITTLE endian.
 */
public class MultiLineHandler implements ShapeHandler {
    
    int myShapeType= -1;
     
    public MultiLineHandler() {
        myShapeType = 3;
    }
    
    public MultiLineHandler(int type) throws ShapefileException {
        if ((type != 3) &&  (type != 13) &&  (type != 23)) {
            throw new ShapefileException("MultiLineHandler constructor - expected type to be 3, 13 or 23");
        }
        myShapeType = type;
    }
    
    public Geometry read(DataInput in,
                         GeometryFactory geometryFactory,
                         int contentLength) throws IOException, ShapefileException {
        
        double junk;
		int actualReadWords = 0; //actual number of 16 bits words read
		Geometry geom = null;
        
        int shapeType = in.readInt();
		actualReadWords += 2;
        
        if (shapeType == 0) {
            geom = geometryFactory.createMultiLineString(new LineString[0]); //null shape
        }
        
        else if (shapeType != myShapeType) {
            throw new ShapefileException("MultilineHandler.read()  - file says its type "+shapeType+" but i'm expecting type "+myShapeType);
        }
        
        else {
            //read bounding box (not needed)
            junk = in.readDouble();
            junk = in.readDouble();
            junk = in.readDouble();
            junk = in.readDouble();
		    actualReadWords += 4*4;
            
            int numParts = in.readInt();
            int numPoints = in.readInt(); //total number of points
		    actualReadWords += 4;      
            
            int[] partOffsets = new int[numParts];
            
            for (int i=0 ; i<numParts ; i++){
                partOffsets[i]=in.readInt();
		    	actualReadWords += 2;
            }
            
            LineString lines[] = new LineString[numParts];
            Coordinate[] coords = new Coordinate[numPoints];
            
            for (int t=0 ; t<numPoints ; t++) {
                coords[t] = new Coordinate(in.readDouble(),in.readDouble());
		    	actualReadWords += 8;
            }
            
            if (myShapeType == 13) {
                junk =in.readDouble();  //z min, max
                junk =in.readDouble();
                actualReadWords += 8;
                for (int t = 0 ; t<numPoints ; t++) {
                    coords[t].z =   in.readDouble(); //z value
		       	    actualReadWords += 4;
                }
            }
            
            if (myShapeType >= 13) {
		        int fullLength;
                if (myShapeType == 13) { // polylineZ (with M)
		    		fullLength =  22 + 2*numParts + (numPoints * 8) + 4+4+4*numPoints+ 4+4+4*numPoints;
                }
                else { // polylineM (with M)
		    		fullLength =  22 + 2*numParts + (numPoints * 8) + 4+4+4*numPoints;
                }
                if (contentLength >= fullLength) { //are ms actually there?
                    junk =in.readDouble();  //m min, max
                    junk =in.readDouble();
		    	    actualReadWords += 8;
                    for (int t = 0 ; t<numPoints ; t++) {
                         junk =in.readDouble(); //m value
		    			 actualReadWords += 4;
                    }
                }
            }
        
            int offset = 0;
            int start,finish,length;
            for(int part=0 ; part<numParts ; part++) {
                start = partOffsets[part];
                if(part == numParts-1) {
                    finish = numPoints;
                }
                else {
                    finish=partOffsets[part+1];
                }
                length = finish-start;
                Coordinate points[] = new Coordinate[length];
                for(int i=0 ; i<length ; i++) {
                    points[i]=coords[offset];
                    offset++;
                }
                lines[part] = geometryFactory.createLineString(points);
            }
            if (numParts ==1)
                geom = lines[0];
            else
                geom = geometryFactory.createMultiLineString(lines);
        }
        
        //verify that we have read everything we need
	    while (actualReadWords < contentLength) {
		    int junk2 = in.readShort();	
		    actualReadWords += 1;
	    }
	    
        return geom;
    }
    
    public void write(Geometry geometry, DataOutput out) throws IOException {
        
        if (geometry.isEmpty()) {
            out.writeInt(0);
            return;
        }
        
        MultiLineString multi = (MultiLineString)geometry;
        int npoints;
        Coordinate[] coords;
        out.writeInt(getShapeType());
        
        Envelope box = multi.getEnvelopeInternal();
        out.writeDouble(box.getMinX());
        out.writeDouble(box.getMinY());
        out.writeDouble(box.getMaxX());
        out.writeDouble(box.getMaxY());
        
        int numParts = multi.getNumGeometries();
        
        out.writeInt(numParts);
        npoints= multi.getNumPoints();
        out.writeInt(npoints);
        
        LineString[] lines = new LineString[numParts];
        int idx = 0;
        
        for(int i=0 ; i<numParts ; i++){
            lines[i] = (LineString)multi.getGeometryN(i);
            out.writeInt(idx);
            idx = idx + lines[i].getNumPoints();
        }
        
        coords = multi.getCoordinates();
        for(int t=0 ; t<npoints ; t++) {
               out.writeDouble(coords[t].x);
               out.writeDouble(coords[t].y);
        }
        
        if (myShapeType == 13) { //z
            double[] zExtreame = zMinMax(multi);
            if (Double.isNaN(zExtreame[0] )) {
                out.writeDouble(0.0);
                out.writeDouble(0.0);
            }
            else {
                out.writeDouble(zExtreame[0]);
                out.writeDouble(zExtreame[1]);
            }
            for (int t=0 ; t<npoints ; t++) {
                double z = coords[t].z;
                if (Double.isNaN(z))
                     out.writeDouble(0.0);
                else
                     out.writeDouble(z);
            }
        }
        
        if (myShapeType >= 13) { //m
            out.writeDouble(-10E40);
            out.writeDouble(-10E40);
            for(int t=0 ; t<npoints ; t++) {
                out.writeDouble(-10E40);
            }
        }
        
    }
    
    /**
     * Get the type of shape stored (Shapefile.ARC, Shapefile.ARCM, Shapefile.ARCZ)
     */
    public int getShapeType() {
        return myShapeType;
    }
    
    public int getLength(Geometry geometry){
        
        if (geometry.isEmpty())     return 2;
        
        MultiLineString multi = (MultiLineString) geometry;
        int numlines, numpoints;
        numlines = multi.getNumGeometries();
        numpoints = multi.getNumPoints();
        if (myShapeType == 3) {
             return 22 + 2*numlines + (numpoints * 8);
        }
        else if (myShapeType == 23) {
             return 22 + 2*numlines + (numpoints * 8) + 4+4+4*numpoints;
        }
        else {
            return 22 + 2*numlines + (numpoints * 8) + 4+4+4*numpoints+ 4+4+4*numpoints;
        }
    }
    
    
    double[] zMinMax(Geometry g) {
        double zmin = Double.NaN;
        double zmax = Double.NaN;
        boolean validZFound = false;
        Coordinate[] cs = g.getCoordinates();
        double z;
        for (int t=0 ; t<cs.length ; t++) {
            z= cs[t].z ;
            if (!(Double.isNaN(z))) {
                if (validZFound) {
                    if (z < zmin) zmin = z;
                    if (z > zmax) zmax = z;
                }
                else {
                    validZFound = true;
                    zmin =  z ;
                    zmax =  z ;
                }
            }
        }
        return new double[]{zmin, zmax};   
    }
    
    /**
     * Return a empty geometry.
     */
     public Geometry getEmptyGeometry(GeometryFactory factory) {
         return factory.createMultiLineString(new LineString[0]);
     }
}

