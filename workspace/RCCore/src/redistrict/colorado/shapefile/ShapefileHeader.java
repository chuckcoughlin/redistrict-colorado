/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.shapefile;

import java.io.IOException;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

import redistrict.colorado.io.EndianAwareInputStream;
import redistrict.colorado.io.EndianAwareOutputStream;
import redistrict.colorado.io.EndianType;


/**
 * Wrapper for a shapefile header. The Input/Output streams should be BIG Endian up
 * through the length, then LITTLE.
 *
 * @author  jamesm
 */
public class ShapefileHeader{
	private static final String CLSS = "ShapefileHeader";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    private int fileCode = -1;
    public int fileLength = -1;
    private int indexLength = -1;
    private int version = -1;
    private int shapeType = -1;
    private Envelope bounds;
    // added by mmichaud on 4 nov. 2004 in order to handle shapefile 3D
    // the right way (zmin and z max may be used by arcgis data translator when
    // transforming shapefiles to geodatabase)
    private double zmin = 0.0;
    private double zmax = 0.0;
    
    public ShapefileHeader(EndianAwareInputStream inputStream) throws IOException {
    	inputStream.setType(EndianType.BIG);
        fileCode = inputStream.readInt();
        if ( fileCode != Shapefile.SHAPEFILE_ID )
            LOGGER.warning(String.format("%s: WARNING - filecode (%d) does not match code for a shapefile (%d)",CLSS,fileCode,Shapefile.SHAPEFILE_ID));
        
        for(int i=0 ; i<5 ; i++){
            int tmp = inputStream.readInt();
        }
        fileLength = inputStream.readInt();
        
        inputStream.setType(EndianType.LITTLE);
        version=inputStream.readInt();
        shapeType=inputStream.readInt();
       
        //read in and for now ignore the bounding box
        for(int i=0 ; i<4 ; i++){
            inputStream.readDouble();
        }
        
        //skip remaining unused bytes
        inputStream.skipBytes(32);
    }
    
    public ShapefileHeader(GeometryCollection geometries, int dims) throws Exception
    {
        ShapeHandler handle;
        if (geometries.getNumGeometries() == 0)
        {
            handle = new PointHandler(); //default
        }
        else
        {
            handle = Shapefile.getShapeHandler(geometries.getGeometryN(0), dims);
        }
        int numShapes = geometries.getNumGeometries();
        shapeType = handle.getShapeType();
        // added by mmichaud on 4 nov. 2004
        boolean zvalues = false;
        if (shapeType==11 || shapeType==13 || shapeType==15 || shapeType==18) {
            zvalues = true;
            zmin = Double.MAX_VALUE;
            zmax = Double.MIN_VALUE;
        }
        version = Shapefile.VERSION;
        fileCode = Shapefile.SHAPEFILE_ID;
        bounds = geometries.getEnvelopeInternal();
        fileLength = 0;
        for(int i=0 ; i<numShapes ; i++){
            Geometry g = geometries.getGeometryN(i);
            fileLength += handle.getLength(g);
            fileLength += 4; //for each header
            // added by mmichaud on 4 nov. 2004
            if (zvalues) {
                Coordinate[] cc = g.getCoordinates();
                for (int j = 0 ; j < cc.length ; j++) {
                    if (Double.isNaN(cc[j].z)) continue;
                    if (cc[j].z < zmin) zmin = cc[j].z;
                    if (cc[j].z > zmax) zmax = cc[j].z;
                }
            }
        }
        fileLength += 50; //space used by this, the main header
        indexLength = 50 + (4*numShapes);
    }
    
    public void setFileLength(int fileLength){
        this.fileLength = fileLength;
    }
        
    public void write(EndianAwareOutputStream out) throws IOException {
        int pos = 0;
        out.setType(EndianType.BIG);
        out.writeInt(fileCode);
        pos+=4;
        
        for(int i=0;i<5;i++){
            out.writeInt(0); //Skip unused part of header
            pos+=4;
        }
        out.writeInt(fileLength);  // The writer was BIG, reader LITTLE?
        pos+=4;
        
        out.setType(EndianType.LITTLE);
        out.writeInt(version);
        pos+=4;
        
        out.writeInt(shapeType);
        pos+=4;
        
        //write the bounding box
        out.writeDouble(bounds.getMinX());
        out.writeDouble(bounds.getMinY());
        out.writeDouble(bounds.getMaxX());
        out.writeDouble(bounds.getMaxY());
        pos+=8*4;
        
        // added by mmichaud on 4 nov. 2004
        out.writeDouble(zmin);
        out.writeDouble(zmax);
        pos+=8*2;
        
        //skip remaining unused bytes
        out.writeDouble(0.0);
        out.writeDouble(0.0);//Skip unused part of header
        pos+=8;
        
        LOGGER.info(String.format("%s.write: Position = %d",CLSS,pos));
    }
    
    public void writeToIndex(EndianAwareOutputStream out)throws IOException {
        int pos = 0;
        out.setType(EndianType.BIG);
        out.writeInt(fileCode);
        pos+=4;
        
        for(int i=0;i<5;i++){
            out.writeInt(0);//Skip unused part of header
            pos+=4;
        }
        
        out.writeInt(indexLength);
        pos+=4;
        
        out.setType(EndianType.LITTLE);
        out.writeInt(version);
        pos+=4;
        
        out.writeInt(shapeType);
        pos+=4;
        
        //write the bounding box
        pos+=8;
        out.writeDouble(bounds.getMinX() );
        pos+=8;
        out.writeDouble(bounds.getMinY() );
        pos+=8;
        out.writeDouble(bounds.getMaxX() );
        pos+=8;
        out.writeDouble(bounds.getMaxY() );
        
        //skip remaining unused bytes
        for(int i=0;i<4;i++){
            out.writeDouble(0.0);//Skip unused part of header
            pos+=8;
        }
        
        LOGGER.info(String.format("%s.writeToIndex: Position = %d",CLSS,pos));
    }
    
    public int getShapeType(){
        return shapeType;
    }
    
    public int getVersion(){
        return version;
    }
    
    public Envelope getBounds(){
        return bounds;
    }
    
    public String toString()  {
        String res = new String("Sf-->type "+fileCode+" size "+fileLength+" version "+ version + " Shape Type "+shapeType);
        return res;
    }
    
}
