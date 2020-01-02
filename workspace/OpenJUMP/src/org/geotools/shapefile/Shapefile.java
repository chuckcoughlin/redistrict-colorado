package org.geotools.shapefile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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
import org.openjump.io.EndianAwareOutputStream;
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
    //Types 2,4,6,7 and 9 were undefined at time or writeing
    
    private File infile;
    private File outfile = null;
    private InputStream shpInputStream = null;
    private int errorCount;
    
    /**
     * Creates a shapefile and opens its stream.
     * @param file the shapefile
     */
    public Shapefile(File f) {
    	this.infile = f;
    	try {
    		this.shpInputStream = new FileInputStream(infile);
    	}
    	catch(FileNotFoundException e) {
        	LOGGER.severe(String.format("%s: %s not found (%s)",CLSS, infile.getAbsolutePath(),e.getLocalizedMessage()));
        }
    	this.errorCount = 0;
    }
    
    /**
     * Creates a shapefile from an open stream.
     * @param file the shapefile
     */
    public Shapefile(InputStream in) {
    	this.shpInputStream = in;
    	this.errorCount = 0;
    }
    
    public void close() {
    	if(shpInputStream!=null ) {
    		try {
    			shpInputStream.close();
    			shpInputStream = null;
    		}
    		catch(IOException ioe) {
    			LOGGER.warning(String.format("%s: Failed to close input stream (%s)",CLSS,ioe.getLocalizedMessage()));
    		}
    	}
    }
    
    /**
     * Initializes a shapefile using either the file or stream supplied in the constructor.
     * @param geometryFactory the geometry factory to use to read the shapes
     */
    public GeometryCollection read(GeometryFactory geometryFactory) throws Exception {
    	LOGGER.info(String.format("%s.read: Started read",CLSS));
        ArrayList<Geometry> list = new ArrayList<>();
        int pos = 0;
        try (
    		BufferedInputStream reader = new BufferedInputStream(new DataInputStream(shpInputStream));
        	EndianAwareInputStream eais = new EndianAwareInputStream(reader,EndianType.LITTLE); )  {
    
            ShapefileHeader mainHeader = new ShapefileHeader(eais);
            if(mainHeader.getVersion() != VERSION){
                LOGGER.warning(String.format("%s.read: Unknown shapefile version (%s) : try to read anyway",CLSS, mainHeader.getVersion()));
            }
            pos += 50;

            Geometry body;
            int type = mainHeader.getShapeType();
            ShapeHandler handler = getShapeHandler(type);
            if(handler==null) throw new ShapefileException("Unsupported shape type: " + type);

            errorCount = 0;
            int count = 1;

            while(true){
                int recordNumber=eais.readInt(); pos+=2;
                if (recordNumber != count) {
                	LOGGER.warning(String.format("%s.read: wrong record number (%d)",CLSS,recordNumber));
                    break;
                }
                int contentLength=eais.readInt(); pos+=2;
                if (contentLength <= 0) {
                	LOGGER.warning(String.format("%s.read: found a negative content length (%d)",CLSS,contentLength));
                    break;
                }
                try{
                    body = handler.read(eais,geometryFactory,contentLength);
                    LOGGER.info(String.format("%s.read: record %d : from %d for %d (%d pts)",CLSS,recordNumber,pos-4,contentLength, body.getNumPoints()));
                    pos += contentLength;
                    list.add(body);
                    count++;
                    if (body.getUserData() != null) errorCount++;
                } 
                catch(Exception e) {
                	LOGGER.warning(String.format("%s.read: Error processing record %d (%s)",CLSS,recordNumber,e.getLocalizedMessage()));
                	errorCount++;
                }
            }
        }
    	catch(EOFException e) {
        	LOGGER.info(String.format("%s.read: EOF (%d records)",CLSS, list.size()));
        }

        return geometryFactory.createGeometryCollection((Geometry[])list.toArray(new Geometry[]{}));
    }
    
    /**
     * Get the number of errors found after a read.
     */
     public int getErrorCount() {return errorCount;}
    
    /**
     * Saves a shapefile to an output stream. Use the same path as the file used to generate the 
     * shapefile in the first place. Infile must be defined in the constructor.
     * @param geometries geometry collection to write
     * @param ShapeFileDimension shapefile dimension (2=x,y ; 3=x,y,m ; 4=x,y,z,m)
     */
     public void write(GeometryCollection geometries, int shapefileDimension) throws Exception {
    	 this.outfile = infile;
    	 try( BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outfile));
    		  EndianAwareOutputStream eaos = new EndianAwareOutputStream(out,EndianType.BIG);) {
    		 ShapefileHeader mainHeader = new ShapefileHeader(geometries, shapefileDimension);
    		 mainHeader.write(eaos);
    		 int pos = 50; // header length in WORDS

    		 int numShapes = geometries.getNumGeometries();
    		 Geometry body;
    		 ShapeHandler handler = null;

    		 if (geometries.getNumGeometries() == 0) {
    			 handler = new PointHandler(); //default
    		 } 
    		 else {
    			 handler = Shapefile.getShapeHandler(geometries.getGeometryN(0), shapefileDimension);
    		 }

    		 for (int i = 0; i < numShapes; i++) {
    			 body = geometries.getGeometryN(i);
    			 eaos.writeInt(i + 1);
    			 eaos.writeInt(handler.getLength(body));
    			 pos += 4; // length of header in WORDS
    			 handler.write(body, eaos);
    			 pos += handler.getLength(body); // length of shape in WORDS
    		 }
    		 eaos.flush();
    	 }
    	 catch(EOFException e) {
    		 LOGGER.severe(String.format("%s.write: shapefile %s end-of-file (%s)",CLSS, e.getLocalizedMessage()));
    	 }
     }
  
  
    //ShapeFileDimension =>    2=x,y ; 3=x,y,m ; 4=x,y,z,m
    /**
     * Saves a shapefile index (shx) to an output stream.
     * @param geometries geometry collection to write
     * @param file file to write to
     * @param ShapeFileDimension shapefile dimension (2=x,y ; 3=x,y,m ; 4=x,y,z,m)
     */
     public synchronized void writeIndex(GeometryCollection geometries,
    		 File file,
    		 int ShapeFileDimension) throws Exception {
    	 Geometry geom;   
    	 this.outfile = file;
    	 try( BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outfile));
    		  EndianAwareOutputStream eaos = new EndianAwareOutputStream(out,EndianType.BIG);) {
    		 ShapeHandler handler ;
    		 int nrecords = geometries.getNumGeometries();
    		 ShapefileHeader mainHeader = new ShapefileHeader(geometries,ShapeFileDimension);

    		 if (geometries.getNumGeometries() == 0) {
    			 handler = new PointHandler(); //default
    		 }
    		 else {
    			 handler = Shapefile.getShapeHandler(geometries.getGeometryN(0), ShapeFileDimension);
    		 }

    		 mainHeader.writeToIndex(eaos);
    		 int pos = 50;
    		 int len;

    		 for(int i=0 ; i<nrecords ; i++){
    			 geom = geometries.getGeometryN(i);
    			 len = handler.getLength(geom);
    			 eaos.writeInt(pos);
    			 eaos.writeInt(len);
    			 pos = pos+len+4;
    		 }
    		 eaos.flush();
    	 }
    	 catch(IOException e) {
    		 LOGGER.severe(String.format("%s.writeIndex: shapefile %s end-of-file (%s)",CLSS, e.getLocalizedMessage()));
    	 }
     }
   
    
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


    /**
     * The purpose of this new reader [mmichaud 2015-04-11] is to read a shapefile using the shx
     * index file.
     * While the legacy reader #read(GeometryFactory geometryFactory) read the shapefile sequentially
     * and don't need the shx index file, this new parser read the shx file and access the shp file
     * with a RandomAccessReader.
     * Because the shapefile may come from a compressed input stream, the method first writes the
     * shapefile in a temporary file.
     * @param geometryFactory geometry factory to use to build geometries
     * @param is shx input stream
     * @return a GeometryCollection containing all the shapes.
     */
    public synchronized GeometryCollection readFromIndex(GeometryFactory geometryFactory, InputStream is) throws Exception {

        // Flush shapefile inputStream to a temporary file, because inputStream
        // may come from a zipped archive, and we want to access data in Random mode
        File tmpShp = File.createTempFile("tmpshp", ".shp");
        LOGGER.info(String.format("%s.readFromIndex: Started read",CLSS));
        ArrayList<Geometry> list = new ArrayList<>();
        try (
        	BufferedInputStream bis= new BufferedInputStream(is, 4096);
        	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpShp), 4096);
        	RandomAccessFile raf = new RandomAccessFile(tmpShp, "r");
        	EndianAwareInputStream shx = new EndianAwareInputStream(is); ) {
        	int nb;
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,1));
        	byte[] bytes = new byte[4096];
        	while (-1 != (nb = bis.read(bytes))) {
        		bos.write(bytes, 0, nb);
        	}
        	bos.flush();
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,2));
        	// read shapefile header
        	bytes = new byte[100];
        	ByteBuffer bb = ByteBuffer.wrap(bytes);
        	raf.getChannel().read(bb);
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,3));
        	EndianAwareInputStream shp = new EndianAwareInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)),EndianType.BIG);
        	ShapefileHeader shpMainHeader = new ShapefileHeader(shp);
        	if (shpMainHeader.getVersion() != VERSION) {
        		LOGGER.warning(String.format("%s.readFromIndex: Unknown shp version (%s) : try to read anyway",CLSS,shpMainHeader.getVersion()));
        	}
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,4));
        	ShapefileHeader shxMainHeader = new ShapefileHeader(shx);
        	if (shxMainHeader.getVersion() != VERSION) {
        		LOGGER.warning(String.format("%s.readFromIndex: Unknown shx version (%s) : try to read anyway", CLSS,shxMainHeader.getVersion()));
        	}	
        	//shp.close();
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,5));
        	Geometry body;
        	int type = shpMainHeader.getShapeType();
        	ShapeHandler handler = getShapeHandler(type);
        	if(handler==null) throw new ShapefileException("Unsupported shape type:" + type);

        	int recordNumber = 0;
        	LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,6));
        	while (true) {
        		LOGGER.info(String.format("%s:readFromIndex %d:",CLSS,recordNumber));
        		long offset = shx.readInt() & 0x00000000ffffffffL;
        		LOGGER.info(String.format("%s:readFromIndex %d: offset = %d",CLSS,recordNumber,offset));
        		int length = shx.readInt();
        		LOGGER.info(String.format("%s:readFromIndex %d: length = %d",CLSS,recordNumber,length));
        		recordNumber++;
        		try{
        			bytes = new byte[length*2];
        			LOGGER.info(String.format("%s:readFromIndex %d: %d bytes",CLSS,recordNumber,bytes.length));
        			bb = ByteBuffer.wrap(bytes);
        			raf.getChannel().read(bb, offset*2 + 8);
        			shp = new EndianAwareInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)),EndianType.BIG);
        			body = handler.read(shp, geometryFactory, length);
        			shp.close();
        			LOGGER.info(String.format("%s:readFromIndex %d: From %d for %d bytes (%d pts)",CLSS,recordNumber,offset,length,body.getNumPoints()));
        			list.add(body);
        			if (body.getUserData() != null) errorCount++;
        		} 
        		catch(Exception e) {
        			LOGGER.warning(String.format("%s.readFromIndex: Error processing record %d (%s)",CLSS, recordNumber ,e.getMessage()));
        			LOGGER.info(String.format("%s.readFromIndex: an empty Geometry has been returned",CLSS));
        			list.add(handler.getEmptyGeometry(geometryFactory));
        			errorCount++;
        		}
        	}

        }
        catch (EOFException e) {
        	LOGGER.info(String.format("%s.readFromIndex: EOF (%d records)",CLSS, list.size()));
        }
        finally {
        	if (tmpShp.exists()) {
        		if (!tmpShp.delete()) {
        			LOGGER.warning(tmpShp + " could not be deleted");
        		}
        	}
        }
        return geometryFactory.createGeometryCollection(list.toArray(new Geometry[]{}));
    }
}

