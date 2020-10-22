/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.wkt;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base class for <cite>Well Know Text</cite> (WKT) parser. We've removed all
 * code dealing with formatted output.
 * 
 * Parse then maintain a list of elements. This class maintains the current
 * parse position in that list.
 *
 * @author Remi Eve
 * @author Martin Desruisseaux (IRD)
 * @see <A
 *     HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well
 *     Know Text specification</A>
 * @see <A HREF="http://gdal.org/wktproblems.html">OGC WKT Coordinate System Issues</A>
 */
public abstract class AbstractParser {
	private static final long serialVersionUID = -5563084488367279495L;
	private static final String CLSS = "AbstractParser";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
    private static final boolean SCIENTIFIC_NOTATION = true;  // Allow parsing of numbers in scientific notation
    private static final char[] DELIMS = { '(',')',']','[','"',',' };
    protected final Symbols symbols;           // Well-known symbols
    private Element root = new Element();
   

    /**
     * Constructs a parser using the specified set of symbols.
     * Note that scientific notation for numeric quantities will be recognized.
     *
     * @param symbols The set of symbols to use.
     */
    public AbstractParser(final Symbols symbols) {
        this.symbols = symbols;
    }

    /**
     * Parses a <cite>Well Known Text</cite> (WKT) string. Analyze the
     * complete string, generating a list of elements each of which
     * can hold a list of child elements. There is a single root element
     * which is the coordinate reference system.
     * 
     * The parsing takes two passes. In the first the original string
     * is separated into a tree of elements. In the second that tree
     * is traversed to analyze what effect, if any, those elements have
     * on the math transform (which is our objective).
     *
     * @param text The text to be parsed.
     * @throws ParseException if the string can't be parsed.
     */
    public final void parseTree(final String text) throws ParseException {
        createElements(root,text);
        for(Element element:root.getChildren()) {
        	analyzeElement(element);  // Analyze the children of the root
        }
        
    }

    private void createElements(Element start, String text) throws ParseException {
    	Element element = start;  // Current element
    	ParseStatus pstatus;       // current parse position and token
    	boolean inquote = false;
    	
    	while( text.length()>0 ) {
    		// Quoted text can only be used as a property
    		if( inquote ) {
    			pstatus = getQuotedString(text);
    			element.addParameter(pstatus.getText());
    			
    			
    		}
    		else {
    			pstatus = getNextToken(text);
    			char delim = pstatus.getDelimiter();
    			
    			if( delim=='(' || delim=='[' ) {
    				String keyword = pstatus.getText().trim();
    				if( keyword.isBlank() ) {
    					Element child = new Element();
    					element.addChild(child);
    					element = child; // Child is now the current
    				}
    				else {
    					keyword = pstatus.getText().trim();
    					if(!keyword.isBlank()) {
    						element.setKeyword(pstatus.getText().toUpperCase());
    					}
    				}
    				
    			}
    			else if( delim==',' ||  delim==']' ) {
    				String param = pstatus.getText().trim();
					if(!param.isBlank()) {
						element.addParameter(pstatus.getText());
					}
    			}
    			else {
    				throw new ParseException(String.format("%s.createElements: Unrecognized delimiter (%c)",
    						CLSS,delim),0);
    			}
    			
    		}
    		// Remove current fragment from the remainder
    		if( pstatus.getLength()>text.length()) {
				text = "";
			}
			else {
				text = text.substring(pstatus.getLength());
			}	
    	}
    }
    
    /**
     * Analyze an element and its children. Contribute to the construction
     * of the MathTransform, if appropriate. Otherwise do nothing.
     *
     * @param element The element to be analyzed.
     * @return true if the element was successfully analyzed.
     */
    public abstract boolean analyzeElement(Element element) throws ParseException;
    

    /**
     * This is a poor-man's parser. Look for the next occurrence from a list of delimiters.
     */
    private ParseStatus getNextToken(String text) {
    	ParseStatus status = new ParseStatus();
    	char delimiter = '\0';
    	int pos = Integer.MAX_VALUE;
    	
    	for( char d:DELIMS) {
    		int p = text.indexOf(d);
    		if( p>=0 && p<pos ) {
    			pos = p;
    			delimiter = d;
    		}
    	}
    	if( pos<Integer.MAX_VALUE ) {
    		status.setDelimiter(delimiter);
    		status.setLength(pos+1);
    		status.setText(text.substring(0, pos));
    	}
    	return status;
    }
    
    /**
     * The previous token was a begin-quote. Find its match for the end.
     * Pass over escaped quotes.
     */
    private ParseStatus getQuotedString(String text) {
    	ParseStatus status = new ParseStatus();
    	byte[] bytes = text.getBytes();
    	boolean escaped = false;
    	int index = 0;
    	for( byte c: bytes) {
    		index++;
    		if( c=='\'') {
    			escaped = true;
    			continue;
    		}
    		else if( escaped ) {
    			escaped = false;
    			continue;
    		}
    		else if( c=='"' ) {
    			text = text.substring(index);
    			status.setLength(index);
    			status.setText(text.trim());
    			break;			
    		}
    	}
    	return status;
    }

}
