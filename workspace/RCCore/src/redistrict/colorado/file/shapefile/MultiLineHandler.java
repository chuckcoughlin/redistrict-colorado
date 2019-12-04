/*
 * $Id: MultiLineHandler.java 2110 2010-10-10 15:07:03Z michaudm $
 *
 */

package redistrict.colorado.file.shapefile;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

import redistrict.colorado.io.EndianAwareDataInputStream;
import redistrict.colorado.io.EndianAwareDataOutputStream;

/**
 * Wrapper for a Shapefile PolyLine.
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
    
    public Geometry read(EndianAwareDataInputStream file,
                         GeometryFactory geometryFactory,
                         int contentLength) throws IOException, ShapefileException {
        
        double junk;
		int actualReadWords = 0; //actual number of 16 bits words read
		Geometry geom = null;
        
        int shapeType = file.readIntLE();
		actualReadWords += 2;
        
        if (shapeType == 0) {
            geom = geometryFactory.createMultiLineString(new LineString[0]); //null shape
        }
        
        else if (shapeType != myShapeType) {
            throw new ShapefileException("MultilineHandler.read()  - file says its type "+shapeType+" but i'm expecting type "+myShapeType);
        }
        
        else {
            //read bounding box (not needed)
            junk = file.readDoubleLE();
            junk = file.readDoubleLE();
            junk = file.readDoubleLE();
            junk = file.readDoubleLE();
		    actualReadWords += 4*4;
            
            int numParts = file.readIntLE();
            int numPoints = file.readIntLE(); //total number of points
		    actualReadWords += 4;      
            
            int[] partOffsets = new int[numParts];
            
            for (int i=0 ; i<numParts ; i++){
                partOffsets[i]=file.readIntLE();
		    	actualReadWords += 2;
            }
            
            LineString lines[] = new LineString[numParts];
            Coordinate[] coords = new Coordinate[numPoints];
            
            for (int t=0 ; t<numPoints ; t++) {
                coords[t] = new Coordinate(file.readDoubleLE(),file.readDoubleLE());
		    	actualReadWords += 8;
            }
            
            if (myShapeType == 13) {
                junk =file.readDoubleLE();  //z min, max
                junk =file.readDoubleLE();
                actualReadWords += 8;
                for (int t = 0 ; t<numPoints ; t++) {
                    coords[t].z =   file.readDoubleLE(); //z value
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
                    junk =file.readDoubleLE();  //m min, max
                    junk =file.readDoubleLE();
		    	    actualReadWords += 8;
                    for (int t = 0 ; t<numPoints ; t++) {
                         junk =file.readDoubleLE(); //m value
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
		    int junk2 = file.readShortBE();	
		    actualReadWords += 1;
	    }
	    
        return geom;
    }
    
    public void write(Geometry geometry, EndianAwareDataOutputStream file) throws IOException {
        
        if (geometry.isEmpty()) {
            file.writeIntLE(0);
            return;
        }
        
        MultiLineString multi = (MultiLineString)geometry;
        int npoints;
        Coordinate[] coords;
        file.writeIntLE(getShapeType());
        
        Envelope box = multi.getEnvelopeInternal();
        file.writeDoubleLE(box.getMinX());
        file.writeDoubleLE(box.getMinY());
        file.writeDoubleLE(box.getMaxX());
        file.writeDoubleLE(box.getMaxY());
        
        int numParts = multi.getNumGeometries();
        
        file.writeIntLE(numParts);
        npoints= multi.getNumPoints();
        file.writeIntLE(npoints);
        
        LineString[] lines = new LineString[numParts];
        int idx = 0;
        
        for(int i=0 ; i<numParts ; i++){
            lines[i] = (LineString)multi.getGeometryN(i);
            file.writeIntLE(idx);
            idx = idx + lines[i].getNumPoints();
        }
        
        coords = multi.getCoordinates();
        for(int t=0 ; t<npoints ; t++) {
               file.writeDoubleLE(coords[t].x);
               file.writeDoubleLE(coords[t].y);
        }
        
        if (myShapeType == 13) { //z
            double[] zExtreame = zMinMax(multi);
            if (Double.isNaN(zExtreame[0] )) {
                file.writeDoubleLE(0.0);
                file.writeDoubleLE(0.0);
            }
            else {
                file.writeDoubleLE(zExtreame[0]);
                file.writeDoubleLE(zExtreame[1]);
            }
            for (int t=0 ; t<npoints ; t++) {
                double z = coords[t].z;
                if (Double.isNaN(z))
                     file.writeDoubleLE(0.0);
                else
                     file.writeDoubleLE(z);
            }
        }
        
        if (myShapeType >= 13) { //m
            file.writeDoubleLE(-10E40);
            file.writeDoubleLE(-10E40);
            for(int t=0 ; t<npoints ; t++) {
                file.writeDoubleLE(-10E40);
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

