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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.dbffile.CodePage;
import org.geotools.dbffile.DbfFile;
import org.geotools.shapefile.Shapefile;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.openjump.geometry.feature.AttributeType;
import org.openjump.geometry.feature.BasicFeature;
import org.openjump.geometry.feature.Feature;
import org.openjump.geometry.feature.FeatureCollection;
import org.openjump.geometry.feature.FeatureDataset;
import org.openjump.geometry.feature.FeatureSchema;

/**
 * ShapefileReader contains static methods for handling Shapefiles. is specialized to read Shapefiles. 
 * 
 * Uses a modified version of geotools to do the .dbf and .shp
 * file reading. 
 */
public class ShapefileReader {
	private static final String CLSS = "ShapefileReader";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	
    private static File delete_this_tmp_shx = null;

    
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
        DbfFile dbfFile = getDbfFile(shpFileName,Charset.forName(charsetName));
        // If the shape index file exists, then use it to pickup only indexed entries. Otherwise process the .shp file directly
        //ShapeIndexFile shxFile = getShx(shpFileName);
        Shapefile shapefile = getShapefile(shpFileName);
        
        try(InputStream shx = getShx(shpFileName)) {

            GeometryFactory factory = new GeometryFactory();
            GeometryCollection collection;
            // Read the shapefile either from shx (if provided) or directly from shp
        	collection = (shx == null ? shapefile.read(factory) : shapefile.readFromIndex(factory, shx));

            // Minimal schema for FeatureCollection (if no dbf is provided)
            FeatureSchema fs = new FeatureSchema();
            fs.addAttribute("GEOMETRY", AttributeType.GEOMETRY);

            FeatureCollection featureCollection;

            if ( dbfFile == null ) {
                // handle shapefiles without dbf files.
                featureCollection = new FeatureDataset(fs);

                int numGeometries = collection.getNumGeometries();

                for (int x = 0; x < numGeometries; x++) {
                    Feature feature = new BasicFeature(fs);
                    Geometry geo = collection.getGeometryN(x);

                    feature.setGeometry(geo);
                    featureCollection.add(feature);
                }
            } 
            else {
                // There is a DBF file so we have to set the Charset to use and
                // to associate the attributes in the DBF file with the features.

                int numfields = dbfFile.getHeader().getFieldCount();
                for (int j = 0; j < numfields; j++) {
                    AttributeType type = AttributeType.toAttributeType(dbfFile.getFieldType(j));
                    fs.addAttribute( dbfFile.getFieldName(j), type );
                }

                featureCollection = new FeatureDataset(fs);

                for (int x = 0; x < Math.min(dbfFile.getHeader().getLastRecord(), collection.getNumGeometries()); x++) {

                    // [sstein 9.Sept.08] Get bytes rather than String to be able to read multibytes strings
                    byte[] s = dbfFile.GetDbfRec(x);
                    // [mmichaud 2017-06-10] skip deleted records
                    if (s[0] == (byte)0x2A && System.getProperty("dbf.deleted.on")==null) {
                        LOGGER.fine("Skip deleted dbf record " + x);
                        continue;
                    }
                    Feature feature = new BasicFeature(fs);
                    Geometry geo = collection.getGeometryN(x);
                    for (int y = 0; y < numfields; y++) {
                        feature.setAttribute(y + 1, dbfFile.ParseRecordColumn(s, y));
                    }

                    feature.setGeometry(geo);
                    featureCollection.add(feature);
                }

                // [mmichaud 2013-10-07] if the number of shapes is greater than the number of records
                // it is better to go on and create features with a geometry and null attributes
                if (collection.getNumGeometries() > dbfFile.getLastRec()) {
                    String message = String.format("%s: Error reading shapefile %s, number of records in shp (%d) > number of records in dbf (%d)", CLSS,shpFileName, 
                    		collection.getNumGeometries(), dbfFile.getLastRec());
                    LOGGER.severe(message);
                    for (int x = dbfFile.getLastRec() ; x < collection.getNumGeometries() ; x++) {
                        Feature feature = new BasicFeature(fs);
                        Geometry geo = collection.getGeometryN(x);
                        feature.setGeometry(geo);
                        featureCollection.add(feature);
                    }
                }
                if (collection.getNumGeometries() < dbfFile.getLastRec()) {
                    String message = String.format("%s: Error reading shapefile %s, number of records in shp (%d) < number of records in dbf (%d)", CLSS,shpFileName, 
                    		collection.getNumGeometries(), dbfFile.getLastRec());
                    LOGGER.severe(message);
                    List emptyList = new ArrayList();
                    for (int x = collection.getNumGeometries() ; x < dbfFile.getLastRec() ; x++) {
                        Feature feature = new BasicFeature(fs);
                        Geometry geo = factory.buildGeometry(emptyList);
                        byte[] s = dbfFile.GetDbfRec(x); //[sstein 9.Sept.08]
                        // [mmichaud 2017-06-10] skip deleted records
                        if (s[0] == (byte)0x2A && System.getProperty("dbf.deleted.on")==null) {
                            continue;
                        }
                        for (int y = 0; y < numfields; y++) {
                            feature.setAttribute(y + 1, dbfFile.ParseRecordColumn(s, y));
                        }
                        feature.setGeometry(geo);
                        featureCollection.add(feature);
                    }
                }
            }
            return featureCollection;
        }
        finally {
            deleteTmpShx(); // delete shx file if it was decompressed
            shapefile.close(); //ensure we can delete input shape files before task is closed
        }
    }

    

    private static InputStream getShx(String srcFileName) throws Exception {
        FileInputStream shxInputStream;

        // default is a *.shx src file
        if (srcFileName.matches("(?i).*\\.shp$")) {
            // replace file name extension of compressedFname (probably .shp) with .shx
            srcFileName = srcFileName.replaceAll("\\.[^.]*$", ".shx");
            File shxFile = new File( srcFileName );
            if ( shxFile.exists() )
                return new FileInputStream(srcFileName);
        }
        // if we are in an archive that can hold multiple files compressedFname is defined and a String
        else if(CompressedFile.hasArchiveFileExtension(srcFileName)) {
            byte[] b = new byte[4096];
            int len = 0;

            // copy the file then use that copy
            File file = File.createTempFile("shx", ".shx");
            FileOutputStream out = new FileOutputStream(file);

            // replace file name extension of compressedFname (probably .shp) with .dbf
            String compressedFname = CompressedFile.getFnameByExtension(srcFileName, ".shx");
            LOGGER.info(String.format("%s.getShx: Source = %s,compressed = %s",CLSS,srcFileName,compressedFname));
            try {
                InputStream in = CompressedFile.openFile(srcFileName,compressedFname);
                while (len!=-1) {
                    len = in.read(b);
                    if (len > 0) {
                        out.write(b, 0, len);
                    }
                }
                out.flush();
                in.close();
                out.close();

                shxInputStream = new FileInputStream(file);
                delete_this_tmp_shx = file; // to be deleted later on
                return shxInputStream;
            } 
            catch (Exception e) {
            	LOGGER.severe(e.getLocalizedMessage());
            }
        }

        return null;
    }


    private static void deleteTmpShx() {
        if (delete_this_tmp_shx != null) {
            delete_this_tmp_shx.delete();
            delete_this_tmp_shx = null;
        }
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
    private static DbfFile getDbfFile(String srcFileName, Charset charset) throws IOException {
    	DbfFile dbfFile = new DbfFile(charset);
    	InputStream in = null;
    	try {
    		// default is a *.dbf src file
    		if (srcFileName.matches("(?i).*\\.dbf$")) {
    			File file = new File( srcFileName );
    			in = new FileInputStream(file);
    		}
    		// An archive can hold multiple files, get the one with a .dbf extension
    		else if (CompressedFile.hasArchiveFileExtension(srcFileName)) {
    			try {
    				String compressedFname = CompressedFile.getFnameByExtension(srcFileName, ".dbf");
    				in = CompressedFile.openFile(srcFileName, compressedFname);
    			}
    			catch(Exception ex) {
    				LOGGER.warning(String.format("%s.getDbFile: Failed to create from %s (%s)",CLSS,srcFileName,ex.getLocalizedMessage()));
    			}
    		}
    		dbfFile.load(in);
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
private static Shapefile getShapefile(String shpfileName) throws Exception {
	String fname = CompressedFile.getFnameByExtension(shpfileName,".shp");
	InputStream in = CompressedFile.openFile(shpfileName,fname);
	return new Shapefile(in);
}

}
