package org.geotools.data.shapefile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * Wrapper for a Shapefile Point.
 */
// getLength() modified by Micha&euml;l MICHAUD on 3 nov. 2004 to handle
// Point, PointM and PointZ length properly
public class PointHandler implements ShapeHandler {
    
    int Ncoords=2; //2 = x,y ;  3= x,y,m ; 4 = x,y,z,m
    int myShapeType = -1;
    
    public PointHandler(int type) throws ShapefileException {
        if ((type != 1) && (type != 11) && (type != 21)) {
            throw new ShapefileException("PointHandler constructor: expected a type of 1, 11 or 21");
        }
        myShapeType = type;
    }
    
    public PointHandler() {
        myShapeType = 1; //2d
    }
    
    public Geometry read(DataInput in,
                         GeometryFactory geometryFactory,
                         int contentLength) throws IOException, ShapefileException {

	    int actualReadWords = 0; //actual number of 16 bits words
	    Geometry geom = null;
	
        int shapeType = in.readInt();
		actualReadWords += 2;
		
		if (shapeType == 0) {
		    geom = geometryFactory.createPoint(new CoordinateArraySequence(0));
		}
        else if (shapeType != myShapeType) {
            throw new ShapefileException("pointhandler.read() - handler's shapetype doesnt match file's");
        }
        else {
            double x = in.readDouble();
            double y = in.readDouble();
            double m , z = Double.NaN;
		    actualReadWords += 8;
            
            if ( shapeType ==21 ) {
                m= in.readDouble();
                actualReadWords += 4;
            }
            
            else if ( shapeType ==11 ) {
                z = in.readDouble();
                actualReadWords += 4;
                if (contentLength>actualReadWords) {
                    m = in.readDouble();
                    actualReadWords += 8;
                }
            }
            
            geom = geometryFactory.createPoint(new Coordinate(x,y,z));
            
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
        out.writeInt(getShapeType());
        Coordinate c = geometry.getCoordinates()[0];
        out.writeDouble(c.x);
        out.writeDouble(c.y);
        
        if  (myShapeType ==11) {
             if (Double.isNaN(c.z)) // nan means not defined
                 out.writeDouble(0.0);
             else
                 out.writeDouble(c.z); 
        }
        if ( (myShapeType ==11) || (myShapeType ==21) ) {
             out.writeDouble(-10E40); //M
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
     * Calcuates the record length of this object.
     * @return the length of the record that this point will take up in a shapefile (in WORDS)
     **/
    public int getLength(Geometry geometry) {
        if (geometry.isEmpty())     return 2;
        else if (myShapeType == 1)  return 10;
        else if (myShapeType == 21) return 14;
        else                        return 18;
    }
    
    /**
     * Return a empty geometry.
     */
     public Geometry getEmptyGeometry(GeometryFactory factory) {
         return factory.createPoint(new CoordinateArraySequence(0));
     }
}
