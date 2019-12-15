/*
 * MultiPointHandler.java
 *
 * Created on July 17, 2002, 4:13 PM
 */

package redistrict.colorado.shapefile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

/**
 * Wrapper for a Shapefile MultiPoint. Streams should be configured as LITTLE endian.
 *
 * @author  dblasby
 */
public class MultiPointHandler  implements ShapeHandler  {
    int myShapeType= -1;
    
    /** Creates new MultiPointHandler */
    public MultiPointHandler() {
        myShapeType = 8;
    }
    
    public MultiPointHandler(int type) throws ShapefileException {
        if ((type != 8) && (type != 18) && (type != 28)) {
            throw new ShapefileException("Multipointhandler constructor - expected type to be 8, 18, or 28");
        }
        myShapeType = type;
    }
    
    public Geometry read(DataInput in,
                         GeometryFactory geometryFactory,
                         int contentLength) throws IOException, ShapefileException {
	
		int actualReadWords = 0; //actual number of 16 bits words read
		Geometry geom = null;
	
        int shapeType = in.readInt();  
		actualReadWords += 2;
        
        if (shapeType == 0) {
            geom = geometryFactory.createMultiPoint(new Point[0]);
        }
        else if (shapeType != myShapeType) {
            throw new ShapefileException("Multipointhandler.read() - expected type code "+myShapeType+" but got "+shapeType);
        }
        else {
            //read bbox
            in.readDouble();
            in.readDouble();
            in.readDouble();
            in.readDouble();
            
		    actualReadWords += 4*4;
            
            int numpoints = in.readInt(); 
		    actualReadWords += 2;
	        
            Coordinate[] coords = new Coordinate[numpoints];
            for (int t=0 ; t<numpoints ; t++) {
                double x = in.readDouble();
                double y = in.readDouble();
		    	actualReadWords += 8;
                coords[t] = new Coordinate(x,y);
            }
            
            if (myShapeType == 18) {
                in.readDouble(); //z min/max
                in.readDouble();
		    	actualReadWords += 8;
                for (int t=0 ; t<numpoints ; t++) { 
                    double z =  in.readDouble();//z
		    		actualReadWords += 4;
                    coords[t].z = z;
                }
            }
            
            if (myShapeType >= 18) {
		    	int fullLength;
		    	if (myShapeType == 18) { //multipoint Z (with Z and M)
		    		fullLength = 20 + (numpoints * 8)  +8 +4*numpoints + 8 +4*numpoints;
		    	}
		    	else { //multipoint M (with M)
		    		fullLength = 20 + (numpoints * 8)  +8 +4*numpoints;
		    	}
                if (contentLength >= fullLength) { //is the M portion actually there?
                    in.readDouble(); //m min/max
                    in.readDouble();
		    		actualReadWords += 8;
                    for (int t=0 ; t<numpoints ; t++) { 
                        in.readDouble();//m
		    			actualReadWords += 4;
                    }
                }
            }
            
            geom = geometryFactory.createMultiPoint(coords);
        }
        
	    //verify that we have read everything we need
	    while (actualReadWords < contentLength) {
		    int junk2 = in.readShort();	
		    actualReadWords += 1;
	    }
	
        return geom;
    }
    
    double[] zMinMax(Geometry g) {
        double zmin,zmax;
        boolean validZFound = false;
        Coordinate[] cs = g.getCoordinates();
        double[] result = new double[2];
        
        zmin = Double.NaN;
        zmax = Double.NaN;
        double z;
        
        for (int t=0;t<cs.length; t++) {
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
        
        result[0] = (zmin);
        result[1] = (zmax);
        return result;
        
    }
    
    
    public void write(Geometry geometry, DataOutput out) throws IOException {
        
        if (geometry.isEmpty()) {
            out.writeInt(0);
            return;
        }
        
        MultiPoint mp = (MultiPoint) geometry;
        out.writeInt(getShapeType());
        
        Envelope box = mp.getEnvelopeInternal();
        out.writeDouble(box.getMinX());
        out.writeDouble(box.getMinY());
        out.writeDouble(box.getMaxX());
        out.writeDouble(box.getMaxY());
        
        int numParts = mp.getNumGeometries();
        out.writeInt(numParts);
        
        for (int t=0;t<mp.getNumGeometries(); t++) {
            Coordinate c = (mp.getGeometryN(t)).getCoordinate();
            out.writeDouble(c.x);
            out.writeDouble(c.y);            
        }
        if (myShapeType == 18) {
            double[] zExtreame = zMinMax(mp);
            if (Double.isNaN(zExtreame[0])) {
                out.writeDouble(0.0);
                out.writeDouble(0.0);
            }
            else {
                out.writeDouble(zExtreame[0]);
                out.writeDouble(zExtreame[1]);
            }
            for (int t=0;t<mp.getNumGeometries(); t++) {
                Coordinate c = (mp.getGeometryN(t)).getCoordinate();
                double z = c.z;
                if (Double.isNaN(z)) {
                    out.writeDouble(0.0);
                }
                else {
                    out.writeDouble(z);
                }
            }
        }
        if (myShapeType >= 18) {
            out.writeDouble(-10E40);
            out.writeDouble(-10E40);
            for (int t=0;t<mp.getNumGeometries(); t++) {   
                out.writeDouble(-10E40);
            }
        }
    }
    
    /**
     * Returns the shapefile shape type value for a point
     * @return int Shapefile.POINT
     */
    public  int getShapeType() {
        return myShapeType;
    }
    
    /**
     * Calculates the record length of this object.
     * @return int The length of the record that this shapepoint will take up in a shapefile
     */
    public int getLength(Geometry geometry) {
        
        if (geometry.isEmpty())     return 2;
        
        MultiPoint mp = (MultiPoint) geometry;
        if (myShapeType == 8) {
            return mp.getNumGeometries() * 8 + 20;
        }
        else if (myShapeType == 28) {
            return mp.getNumGeometries() * 8 + 20 +8 +4*mp.getNumGeometries();
        }
        else {
            return mp.getNumGeometries() * 8 + 20 +8 +4*mp.getNumGeometries() + 8 +4*mp.getNumGeometries();
        }
    }
    
    /**
     * Return a empty geometry.
     */
     public Geometry getEmptyGeometry(GeometryFactory factory) {
         return factory.createMultiPoint(new Point[0]);
     }
}
