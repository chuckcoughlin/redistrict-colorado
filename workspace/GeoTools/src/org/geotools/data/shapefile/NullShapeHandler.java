package org.geotools.data.shapefile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * Null Shape handler for files containing only null shapes.
 */
public class NullShapeHandler implements ShapeHandler {
    
    int myShapeType = -1;
    
    public NullShapeHandler(int type) throws ShapefileException {
        if (type != 0) {
            throw new ShapefileException("NullShapeHandler constructor: expected a type of 0");
        }
        myShapeType = type;
    }
    
    public NullShapeHandler() {
        myShapeType = 0;
    }
    
    public Geometry read(DataInput file,
                         GeometryFactory geometryFactory,
                         int contentLength) throws IOException, ShapefileException {

        int actualReadWords = 0; //actual number of 16 bits words read
        Geometry geom = null;
        
        int shapeType = file.readInt();
		actualReadWords += 2;
		
		if (shapeType == 0) {
		    geom = geometryFactory.createGeometryCollection(new Geometry[0]);
		}
        else if (shapeType != myShapeType) {
            throw new ShapefileException("nullshapehandler.read() - handler's shapetype doesnt match file's");
        }
        else {}
        //verify that we have read everything we need
        while (actualReadWords < contentLength) {
            int junk2 = file.readShort();	
            actualReadWords += 1;
        }
        return geom;
    }
    
    public void write(Geometry geometry, DataOutput file) throws IOException {
        file.writeInt(0);
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
     * @return the length of a null shape (in 16 bits WORDS)
     **/
    public int getLength(Geometry geometry) {
        return 2;
    }
    
    /**
     * Return a empty geometry.
     */
    public Geometry getEmptyGeometry(GeometryFactory factory) {
        return factory.createPoint(new CoordinateArraySequence(0));
    }
}
