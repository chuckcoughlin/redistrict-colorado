package org.geotools.data.wkt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.logging.Logger;

import org.openjump.coordsys.CoordinateSystem;
import org.openjump.io.EndianAwareInputStream;


/**
 * This class represents a file that encapsulates a Projection.<p>
 * Construct it with a filename (including the .prj)
 */
public class ProjectionFile  {
	private static final String CLSS = "ProjectionFile";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
    private final WktParser parser;
	private final Charset charset;
    private CoordinateSystem cs;

    /**
     * Constructor. 
     * @param charset name of the character set.
     */
    public ProjectionFile(Charset charset) {
    	this.charset = charset;
    	this.parser = new WktParser();
    	this.cs = null;
    }
    
    /**
     * Return the Coordinate System retrieved by this reader. We bypass
     * the Coordinate Reference System as it's just a pass-thru.
     *
     * @return the Coordinate System
     */
    public CoordinateSystem getCoordinateSystem() {
        return cs;
    }
    
    /**
     * Initializer: Read the open stream and populate the ProjectFile. 
     * @exception IOException If the file can't be opened.
     */
    public void load(EndianAwareInputStream instream) throws Exception {
    	StringBuffer buffer = new StringBuffer();
    	String line = "";
    	while( (line=instream.readLine())!=null) {
    		buffer.append(line);
    	}
    	String wkt = buffer.toString();
    	LOGGER.info(wkt);
    	try {
    		cs = parser.parseCoordinateSystem(wkt);
    	}
    	catch (ParseException pe) {
    		LOGGER.severe(String.format("%s.load: Exception %s", CLSS,pe.getLocalizedMessage()));
    	}
    	
    	LOGGER.fine("Prj file loaded");
    }
}
