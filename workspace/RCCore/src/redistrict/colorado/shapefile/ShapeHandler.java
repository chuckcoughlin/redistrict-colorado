package redistrict.colorado.shapefile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;


/**
 * Interface implemented by all the ShapeType handlers
 */
public interface ShapeHandler {
   /**
    * Returns one of the ShapeType int defined by the specification.
    * <ul>
    * <li>0 Null Shape</li>
    * <li>1 Point</li>
    * <li>3 PolyLine</li>
    * <li>5 Polygon</li>
    * <li>8 MultiPoint</li>
    * <li>11 PointZ</li>
    * <li>13 PolyLineZ</li>
    * <li>15 PolygonZ</li>
    * <li>18 MultiPointZ</li>
    * <li>21 PointM</li>
    * <li>23 PolyLineM</li>
    * <li>25 PolygonM</li>
    * <li>28 MultiPointM</li>
    * <li>31 MultiPatch</li>
    * </ul>
    */
    public int getShapeType();
    
    public Geometry read(DataInput stream, GeometryFactory geometryFactory, int contentLength) throws IOException, ShapefileException;
    public void write(Geometry geometry, DataOutput file) throws IOException;
    public int getLength(Geometry geometry); //length in 16bit words
    
    /**
     * Return a empty geometry.
     */
    public Geometry getEmptyGeometry(GeometryFactory factory);
}
