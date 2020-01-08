package org.geotools.data.shapefile;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.openjump.io.EndianAwareInputStream;
import org.openjump.io.EndianType;

/**
 * This class represents an ESRI Shape file.<p>
 * You construct it with a file name, and later
 * you can read the file's properties, i.e. Sizes, Types, and the data itself.<p>
 * Copyright 1998 by James Macgill. <p>
 *
 * Modified 2019, Charles Coughlin to use EndianAware streams.
 *
 * This class supports the Shape file as set out in :-<br>
 * <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf"><b>"ESRI(r) Shapefile - A Technical Description"</b><br>
 * <i>'An ESRI White Paper . May 1997'</i></a><p>
 *
 * This code is covered by the LGPL.
 *
 * <a href="mailto:j.macgill@geog.leeds.ac.uk">Mail the Author</a>
 */
public class Shapefile  {
	private static final String CLSS = "Shapefile";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    static final int    SHAPEFILE_ID = 9994;
    static final int    VERSION = 1000;
    
    public static final int NULL        =  0;
    public static final int POINT       =  1;
    public static final int POINTZ      = 11;
    public static final int POINTM      = 21;
    public static final int ARC         =  3;
    public static final int ARCM        = 23;
    public static final int ARCZ        = 13;
    public static final int POLYGON     =  5;
    public static final int POLYGONM    = 25;
    public static final int POLYGONZ    = 15;
    public static final int MULTIPOINT  =  8;
    public static final int MULTIPOINTM = 28;
    public static final int MULTIPOINTZ = 18;
    public static final int MULTIPATCH  = 31;
    public static final int UNDEFINED   = -1;

    private int errorCount;
    private final ShapefileHeader header;
    private GeometryCollection geometryCollection = null;
    
    /**
     * Creates a shapefile and  clears the error count.
     * @param file the shapefile
     */
    public Shapefile() {
    	this.header = new ShapefileHeader();
    	this.errorCount = 0;
    }

    /**
     * Initializer: Read the open stream and populate the shapefile. This
     * version of the method reads the file directly without referencing
     * any index.
     * @param in InputStream ready for reading
     * @exception IOException If the file can't be opened.
     */
    public void load(EndianAwareInputStream instream) throws Exception {
    	header.load(instream);
    	LOGGER.info(String.format("%s.load: Completed read of header ...",CLSS));
    	ArrayList<Geometry> list = new ArrayList<>();
    	GeometryFactory factory = new GeometryFactory();
    	Geometry body;
    	int type = header.getShapeType();
    	ShapeHandler handler = getShapeHandler(type);
    	if(handler==null) throw new ShapefileException("Unsupported shape type: " + type);

    	errorCount = 0;
    	int count = 1;
    	int recordNumber = 0;

    	while(true){
    		try {
    			instream.setType(EndianType.BIG);
    			recordNumber=instream.readInt(); 
    			if (recordNumber != count) {
    				LOGGER.info(String.format("%s.load: wrong record number (%d vs %d)",CLSS,recordNumber,count));
    				break;
    			}
    			int contentLength=instream.readInt();
    			if (contentLength <= 0) {
    				LOGGER.warning(String.format("%s.load: found a negative content length (%d)",CLSS,contentLength));
    				break;
    			}
    			instream.setType(EndianType.LITTLE);

    			body = handler.read(instream,factory,contentLength);
    			LOGGER.info(String.format("%s.load(%d) geometry: (%d bytes, %d pts)",CLSS,recordNumber,contentLength, body.getNumPoints()));
    			list.add(body);
    			count++;
    			if (body.getUserData() != null) errorCount++;
    		} 
    		catch(Exception e) {
    			LOGGER.warning(String.format("%s.load: Error processing record %d (%s)",CLSS,recordNumber,e.getLocalizedMessage()));
    			errorCount++;
    		}
    	}
    	geometryCollection = factory.createGeometryCollection((Geometry[])list.toArray(new Geometry[]{}));
    	LOGGER.info(String.format("%s.load: Completed read with %d geometries, %d errors.",CLSS,geometryCollection.getNumGeometries(),errorCount));
    }

    /**
     * Initializer: Read the open stream and populate the shapefile. This
     * version of the method reads the index file to determine which
     * records are of interest.
     * @param in InputStream ready for reading
     * @exception IOException If the file can't be opened.
     */
    public void load(EndianAwareInputStream instream,ShapeIndexFile shx) throws Exception {
    	header.load(instream);
    	LOGGER.info(String.format("%s.load with index: Completed read of header ...",CLSS));
    	//byte[] bytes = instream.readBytes(64);
    	//LOGGER.info(HexDump.dump(bytes, 0, 64));

    	ArrayList<Geometry> list = new ArrayList<>();
    	GeometryFactory factory = new GeometryFactory();
    	Geometry body = null;
    	int type = header.getShapeType();
    	ShapeHandler handler = getShapeHandler(type);
    	if(handler==null) throw new ShapefileException("Unsupported shape type: " + type);

    	errorCount = 0;
    	int count = 1;
    	int recordNumber = 0;

    	while(true){
    		try{
    			instream.setType(EndianType.BIG);
    			recordNumber=instream.readInt(); 
    			//LOGGER.info(String.format("%s.load with index: record number = %d",CLSS,recordNumber));
    			if (recordNumber != count) {
    				LOGGER.warning(String.format("%s.load with index: wrong record number (%d vs %d)",CLSS,recordNumber,count));
    				break;
    			}
    			int contentLength=instream.readInt();
    			//LOGGER.info(String.format("%s.load with index: content length = %d",CLSS,contentLength));
    			if (contentLength <= 0) {
    				LOGGER.warning(String.format("%s.load with index: found a negative content length (%d)",CLSS,contentLength));
    				break;
    			}

    			instream.setType(EndianType.LITTLE);
    			body = handler.read(instream,factory,contentLength);
    			//LOGGER.info(String.format("%s.load with index(%d) geometry: (%d bytes, %d pts)",CLSS,recordNumber,contentLength, body.getNumPoints()));
    			list.add(body);
    			count++;
    			if (body.getUserData() != null) errorCount++;

    		} 
    		catch(EOFException eofe) {
    			LOGGER.info(String.format("%s.load with index: EOF after record %d (%s)",CLSS,recordNumber,eofe.getLocalizedMessage()));
    			break;
    		}
    		catch(IOException ioe) {
    			LOGGER.warning(String.format("%s.load with index: Error reading record %d (%s)",CLSS,recordNumber,ioe.getLocalizedMessage()));
    			errorCount++;
    		}
    		catch(ShapefileException se) {
    			LOGGER.warning(String.format("%s.load with index: Error processing record %d (%s)",CLSS,recordNumber,se.getLocalizedMessage()));
    			errorCount++;
    		}
    	}
    	geometryCollection = factory.createGeometryCollection((Geometry[])list.toArray(new Geometry[]{}));
    	LOGGER.info(String.format("%s.load with index: Completed read with %d geometries, %d errors.",CLSS,geometryCollection.getNumGeometries(),errorCount));
    }
    /**
     * Get the number of errors found after a read.
     */
     public int getErrorCount() {return errorCount;}
     /**
      * Get the array of geometries
      */
      public GeometryCollection getGeometryCollection() {return geometryCollection;}

   
    
    /**
     * Returns a string describing the shape type.
     * @param index An int coresponding to the shape type to be described
     * @return String describing the shape type
     */
    public static String getShapeTypeDescription(int index){
        switch(index){
            case(NULL):return ("Null Shape");
            case(POINT):return ("Point");
            case(POINTZ):return ("PointZ");
            case(POINTM):return ("PointM");
            case(ARC):return ("PolyLine");
            case(ARCM):return ("PolyLineM");
            case(ARCZ):return ("PolyLineZ");
            case(POLYGON):return ("Polygon");
            case(POLYGONM):return ("PolygonM");
            case(POLYGONZ):return ("PolygonZ");
            case(MULTIPOINT):return ("MultiPoint");
            case(MULTIPOINTM):return ("MultiPointM");
            case(MULTIPOINTZ):return ("MultiPointZ");
            default:return ("Undefined"); 
        }
    }
    
    public static ShapeHandler getShapeHandler(Geometry geom, int ShapeFileDimension) throws Exception {
        return getShapeHandler(getShapeType(geom, ShapeFileDimension));
    }
    
    public static ShapeHandler getShapeHandler(int type) throws Exception {
        switch(type){
            case Shapefile.NULL: return new NullShapeHandler();
            case Shapefile.POINT: return new PointHandler();
            case Shapefile.POINTZ: return new PointHandler(Shapefile.POINTZ);
            case Shapefile.POINTM: return new PointHandler(Shapefile.POINTM);
            case Shapefile.POLYGON: return new PolygonHandler();
            case Shapefile.POLYGONM: return new PolygonHandler(Shapefile.POLYGONM);
            case Shapefile.POLYGONZ: return new PolygonHandler(Shapefile.POLYGONZ);
            case Shapefile.ARC: return new MultiLineHandler();
            case Shapefile.ARCM: return new MultiLineHandler(Shapefile.ARCM);
            case Shapefile.ARCZ: return new MultiLineHandler(Shapefile.ARCZ);
            case Shapefile.MULTIPOINT: return new MultiPointHandler();
            case Shapefile.MULTIPOINTM: return new MultiPointHandler(Shapefile.MULTIPOINTM);
            case Shapefile.MULTIPOINTZ: return new MultiPointHandler(Shapefile.MULTIPOINTZ);
        }
        return null;
    }
    
    /**
     * Returns the Shape Type corresponding to geometry geom of dimension
     * ShapeFileDimension.
     * @param geom the geom
     * @param ShapeFileDimension the dimension of the geom (2=x,y ; 3=x,y,m ; 4=x,y,z,m)
     * @return A int representing the Shape Type
     */
    public static int getShapeType(Geometry geom, int shapefileDimension) throws ShapefileException {
        
        if ((shapefileDimension !=2) && (shapefileDimension !=3) && (shapefileDimension !=4)) {
            throw new ShapefileException(
                "invalid ShapeFileDimension for getShapeType - expected 2,3,or 4 but got "
                + shapefileDimension + "  (2=x,y ; 3=x,y,m ; 4=x,y,z,m)"
            );
        }
        
        if(geom instanceof Point) {
            switch (shapefileDimension) {
                case 2: return Shapefile.POINT;
                case 3: return Shapefile.POINTM;
                case 4: return Shapefile.POINTZ;    
            }
        }
        
        if(geom instanceof MultiPoint) {
            switch (shapefileDimension) {
                case 2: return Shapefile.MULTIPOINT;
                case 3: return Shapefile.MULTIPOINTM;
                case 4: return Shapefile.MULTIPOINTZ;    
            }
        }
        
        if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
            switch (shapefileDimension) {
                case 2: return Shapefile.POLYGON;
                case 3: return Shapefile.POLYGONM;
                case 4: return Shapefile.POLYGONZ;    
            }
        }
        
        if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
            switch (shapefileDimension) {
                case 2: return Shapefile.ARC;
                case 3: return Shapefile.ARCM;
                case 4: return Shapefile.ARCZ;    
            }
        }
        
        if ((geom instanceof GeometryCollection) && (geom.isEmpty())) {
            return Shapefile.NULL;
        }
        
        return Shapefile.UNDEFINED;
    }
}

