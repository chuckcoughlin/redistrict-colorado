/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.shapefile;

import java.io.IOException;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Envelope;
import org.openjump.io.EndianAwareInputStream;
import org.openjump.io.EndianType;


/**
 * Class holds a shapefile header. The Input/Output streams should be BIG Endian up
 * through the length, then LITTLE. The header is 100 bytes.
 *
 * @author  jamesm
 */
public class ShapefileHeader{
	private static final String CLSS = "ShapefileHeader";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    private int fileCode = -1;
    public int fileLength = -1;
    private int version = -1;
    private int shapeType = -1;
    private Envelope bounds;
    
    public ShapefileHeader() {
    	
    }
    
    public void load(EndianAwareInputStream inputStream) throws IOException {
    	inputStream.setType(EndianType.BIG);
        fileCode = inputStream.readInt();
        //LOGGER.info(String.format("%s.load: - filecode (%d)",CLSS,fileCode));
        if ( fileCode != Shapefile.SHAPEFILE_ID )
            LOGGER.warning(String.format("%s: WARNING - filecode (%d) does not match code for a shapefile (%d)",CLSS,fileCode,Shapefile.SHAPEFILE_ID));
        
        for(int i=0 ; i<5 ; i++){
            inputStream.readInt();
        }
        fileLength = inputStream.readInt();  // Note: bytes = length*2
        //LOGGER.info(String.format("%s.load: - length (%d)",CLSS,fileLength));
        inputStream.setType(EndianType.LITTLE);
        version=inputStream.readInt();
        //LOGGER.info(String.format("%s.load: - version (%d)",CLSS,version));
        shapeType=inputStream.readInt();
        //LOGGER.info(String.format("%s.load: - shapetype (%d)",CLSS,shapeType));
       
        // Minimum bounding rectangle - minx, miny, maxx, maxy
        for(int i=0 ; i<4 ; i++){
            inputStream.readDouble();
        }
        // Zmin, Zmax, Mmin, Mmax
        inputStream.readDouble();
        inputStream.readDouble();
        inputStream.readDouble();
        inputStream.readDouble();
    }
    
    public void setFileLength(int fileLength){
        this.fileLength = fileLength;
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
        String res = new String("ShapefileHeader: filecode "+fileCode+" size "+fileLength+" version "+ version + " Shape Type "+shapeType);
        return res;
    }
    
}
