package org.geotools.data.wkt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.logging.Logger;

import org.geotools.operation.MathTransformFilter;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;
import org.openjump.coordsys.CoordinateReferenceSystem;
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
    private CoordinateFilter filter;

    /**
     * Constructor. 
     * @param charset name of the character set.
     */
    public ProjectionFile(Charset charset) {
    	this.charset = charset;
    	this.parser = new WktParser();
    	this.filter = null;
    }
    
    /**
     * Return the transform which we will use to modify the geometries.
     *
     * @return the transform
     */
    public CoordinateFilter getFilter() {
        return filter;
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
    		parser.parseTree(wkt);
    		
    		filter = parser.getCoordinateFilter(); 
    	}
    	catch (ParseException pe) {
    		LOGGER.severe(String.format("%s.load: Exception %s", CLSS,pe.getLocalizedMessage()));
    	}
    	
    	LOGGER.fine("Prj file loaded");
    }
    
    /**
     * Apply the transform to a geometry.
     * @param geom
     * @return
     */
    public Geometry reproject(Geometry geom) {
    	if( filter!=null ) {
    		geom.apply(filter);
    	}
    	return geom.copy();
    }
}
