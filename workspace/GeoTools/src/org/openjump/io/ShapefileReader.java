/*
 * ShapefileReader.java
 *
 * Created on June 27, 2002, 2:49 PM
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package org.openjump.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.geotools.data.dbf.CodePage;
import org.geotools.data.dbf.DbaseFile;
import org.geotools.data.shapefile.ShapeIndexFile;
import org.geotools.data.shapefile.Shapefile;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.openjump.feature.AttributeType;
import org.openjump.feature.BasicFeature;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureDataset;
import org.openjump.feature.FeatureSchema;

/**
 * ShapefileReader contains static methods for handling Shapefiles. is specialized to read Shapefiles. 
 * 
 * Uses a modified version of geotools to do the .dbf and .shp
 * file reading. 
 */
public class ShapefileReader {
	private static final String CLSS = "ShapefileReader";
	private static final Logger LOGGER = Logger.getLogger(CLSS);

    
    /**
     * Test whether or not a path represents a legal Shapefile. It must be an archive
     * file with .shp, .dbf and .shx components. 
     * @param fname
     * @return
     */
    public static boolean isShapefile(String fname) throws Exception {
    	boolean success = false;
    	if( CompressedFile.isArchive(fname)) {
    		if( CompressedFile.getFnameByExtension(fname, ".dbf")!=null &&
    			CompressedFile.getFnameByExtension(fname, ".shp")!=null &&
    			CompressedFile.getFnameByExtension(fname, ".shx")!=null ) {
    			success = true;
    		}
    	}
    	return success;
    }
    
   
    /**
     * Main method to read a compressed shape-file. The .zip file may contain both
     * .shp and .dbf files. Most of the work is done in the org.geotools.* package.
     *
     * @param shpFileName path to the compressed shapefile.
     * @return a FeatureCollection created from .shp and .dbf (dbf is optional)
     */
    public static FeatureCollection read(String shpFileName) throws Exception {
    	if (shpFileName == null) {
    		throw new IllegalArgumentException(String.format("%s.read: No input file specified", CLSS));
    	}
    	if(!isShapefile(shpFileName)) {
    		throw new IllegalArgumentException(String.format("%s.read: File %s is not a shapefile", CLSS,shpFileName));
    	}

    	// Read the .cpg file, if it exists. It is the character set for DbFile. Else use default.
    	String charsetName = getCharset(shpFileName);   
    	DbaseFile dbfFile = getDbfFile(shpFileName,Charset.forName(charsetName));
    	Shapefile shapefile = getShapefile(shpFileName,dbfFile);
		FeatureSchema fs;
    	FeatureCollection featureCollection = null;
    	if( shapefile!=null ) {
    		GeometryCollection collection = shapefile.getGeometryCollection();
    		// handle shapefiles without .dbf files. Ignore the index file.
    		if ( dbfFile == null ) {
    			fs = new FeatureSchema();
    	    	featureCollection =  new FeatureDataset(fs);
    			// Minimal schema for FeatureCollection (if no dbf is provided)  
    			fs.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    			int numGeometries = collection.getNumGeometries();
    			for (int x = 0; x < numGeometries; x++) {
    				Feature feature = new BasicFeature(fs);
    				Geometry geo = collection.getGeometryN(x);
    				feature.setGeometry(geo);
    				featureCollection.add(feature);
    			}
    		}
    		// dbfFile exists. Use its features.
    		else {
    			fs = dbfFile.getFeatureSchema();
    			featureCollection =  new FeatureDataset(fs);
    			int recordCount = dbfFile.getHeader().getLastRecord();
    			if (collection.getNumGeometries() != recordCount) {
    				LOGGER.severe(String.format("%s: Error in %s, shp record count (%d) does not match dbf record count (%d)", CLSS,shpFileName, 
    						collection.getNumGeometries(), recordCount));
    				// Use only the geometries
    				for (int x = recordCount ; x < collection.getNumGeometries() ; x++) {
    					Feature feature = new BasicFeature(fs);
    					Geometry geo = collection.getGeometryN(x);
    					feature.setGeometry(geo);
    					featureCollection.add(feature);
    				}
    			}
    			// Merge geometries with features in .dbf
    			else {
    				for (int row = 0; row < recordCount; row++) {
    					Feature feature = dbfFile.getFeatureDataset().getFeature(row);
    					Geometry geo = collection.getGeometryN(row);
    					feature.setGeometry(geo);
    					featureCollection.add(feature);
    				}
    			}
    		}
    	}
    	return featureCollection;
    }
    
    /** ============================= Helper Methods ================================ **/
    private static String getCharset(String shpfileName) throws Exception {
        String charsetName = Charset.defaultCharset().name(); // Just return the platform default
        String fname = CompressedFile.getFnameByExtension(shpfileName,".cpg");
        
        try (InputStream in = CompressedFile.openFile(shpfileName,fname)) {
        	byte[] bytes = in.readAllBytes();
        	String code = new String(bytes);
        	charsetName = CodePage.getCharSet(code);
        	LOGGER.info(String.format("%s: Using charset %s (raw=%s)", CLSS,charsetName,code));
        }
        catch (Exception e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        
        return charsetName;
    }
    /**
     * Get a DbfFile.
     * @param srcFileName either a dbf or an archive file (*.zip etc.)
     * @param charset the charset to use to read this dbf file
     * @return a DbfFile object for the dbf file named FileName
     * @throws IOException if an I/O error occurs during dbf file reading
     */
    private static DbaseFile getDbfFile(String srcFileName, Charset charset)  {
    	DbaseFile dbfFile = new DbaseFile(charset);
    	EndianAwareInputStream in = null;
    	try {
    		// default is a *.dbf src file
    		if (srcFileName.matches("(?i).*\\.dbf$")) {
    			File file = new File( srcFileName );
    			in = new EndianAwareInputStream(new FileInputStream(file));
    		}
    		// An archive can hold multiple files, get the one with a .dbf extension
    		else if (CompressedFile.hasArchiveFileExtension(srcFileName)) {
    			try {
    				String compressedFname = CompressedFile.getFnameByExtension(srcFileName, ".dbf");
    				InputStream instream = CompressedFile.openFile(srcFileName, compressedFname);
    				in = new EndianAwareInputStream(instream);
    			}
    			catch(Exception ex) {
    				LOGGER.warning(String.format("%s.getDbFile: Failed to create from %s (%s)",CLSS,srcFileName,ex.getLocalizedMessage()));
    			}
    		}
    		if(in!=null) {
    			LOGGER.info(String.format("%s: Loading ... DbfFile %s",CLSS,srcFileName));
    			dbfFile.load(in);
    			dbfFile.loadFeatures(in);
    		}
    	}
    	catch(Exception ex) {
    		LOGGER.severe(String.format("%s: Failed to load DbfFile  (%s)",CLSS,srcFileName,ex.getLocalizedMessage()));
    	}
    	finally {
    		if( in!=null) {

    			try {
    				in.close();
    			}
    			catch(IOException ignore) {}
    		}
    	}
    	return dbfFile;
    }
    // If the dbfFile and shape index files exist, then use them. Otherwise process the .shp file directly
    private static Shapefile getShapefile(String shpfileName, DbaseFile dbfFile)  {
    	Shapefile shape = null;
    	String fname = "";
    	try {
    		fname = CompressedFile.getFnameByExtension(shpfileName,".shp");
    	}
    	catch(Exception ex) {
    		LOGGER.severe(String.format("%s.getShapefile: Failed to open shape file %s  (%s)",CLSS,shpfileName,ex.getLocalizedMessage()));
    		return null;
    	}
    	try (InputStream in = CompressedFile.openFile(shpfileName,fname);
       		 EndianAwareInputStream eastream = new EndianAwareInputStream(in)) {
       		
    		Shapefile shp = new Shapefile();
    		if( dbfFile==null ) {
    			shp.load(eastream);
        		
        	}
    		else {
    			int recordCount = dbfFile.getHeader().getLastRecord();
    			ShapeIndexFile shx = getShx(shpfileName,recordCount);
    			shp.load(eastream,shx);
    		}
    		shape = shp;
       	}
       	catch(Exception ex) {
       		LOGGER.severe(String.format("%s.getShapefile: Failed to load shape file %s (%s)",CLSS,shpfileName,ex.getLocalizedMessage()));
       		ex.printStackTrace();
       	}
    	return shape;
    }
    
    private static ShapeIndexFile getShx(String srcFileName,int count)  {
    	ShapeIndexFile sif = null;
    	String fname = "";
    	try {
    		fname = CompressedFile.getFnameByExtension(srcFileName,".shx");
    	}
    	catch(Exception ex) {
    		LOGGER.severe(String.format("%s: Failed to open shape index file %s  (%s)",CLSS,srcFileName,ex.getLocalizedMessage()));
    		return null;
    	}
    	try (InputStream in = CompressedFile.openFile(srcFileName,fname);
    		 EndianAwareInputStream eastream = new EndianAwareInputStream(in)) {
    		
    		ShapeIndexFile file = new ShapeIndexFile(count);
    		file.load(eastream);
    		sif = file;
    	}
    	catch(Exception ex) {
    		LOGGER.severe(String.format("%s: Failed to load shape index file %s (%s)",CLSS,srcFileName,ex.getLocalizedMessage()));
    	}
    	return sif;
    }

}
