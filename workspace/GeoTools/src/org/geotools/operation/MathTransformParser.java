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
package org.geotools.operation;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.wkt.AbstractParser;
import org.geotools.data.wkt.Element;
import org.geotools.data.wkt.Symbols;
import org.locationtech.jts.geom.CoordinateFilter;

/**
 * Parser for {@linkplain MathTransform math transform} <A
 * HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> of math transform.
 *
 * @since 2.0
 * @version $Id$
 * @author Remi Eve
 * @author Martin Desruisseaux (IRD)
 * @author Rueben Schulz
 */
public class MathTransformParser extends AbstractParser {
	private final static String CLSS = "MathTransformParser";

	private MathTransform transform;
	private CoordinateFilter filter;
	private String classification = null; // classification of the last math transform or projection parsed

	/**
	 * The method for the last math transform passed, or {@code null} if none.
	 *
	 * @see #getOperationMethod
	 */
	private OperationMethod lastMethod;

	/**
	 * Constructs a parser using a specified set of symbols.
	 *
	 * @param symbols The symbols for parsing and formatting numbers.
	 * @todo Pass hints in argument.
	 */
	public MathTransformParser(final Symbols symbols) {
		super(symbols);
		this.transform = new AffineTransform2D();
	}

	public CoordinateFilter getCoordinateFilter() { return new MathTransformFilter(transform); }
	public MathTransform getTransform() { return this.transform; }

	@Override
	public boolean analyzeElement( Element element) throws ParseException {
		return analyzeTransform(element);
	}

    /**
     * Attempt to parse the next element as a MathTransform, setting the local
     * transform and filter methods.
     *
     * @param element the element to be parsed..
     * @return true if the element is handled.
     * @throws ParseException if the next element can't be parsed.
     */
	private boolean analyzeTransform( Element element) throws ParseException {
		boolean result = false;
    	final String keyword = element.getKeyword();

            if ("PARAM_MT".equals(keyword)) result = parseParamMT(element);
            else if ("CONCAT_MT".equals(keyword)) result = parseConcatMT(element);
            else if ("INVERSE_MT".equals(keyword)) result = parseInverseMT(element);
            else if ("PASSTHROUGH_MT".equals(keyword)) result = parsePassThroughMT(element);
            
        return result;
    }

    /**
     * Parses a "PARAM_MT" element. This element has the following pattern:
     *
     * <blockquote> <code>
     * PARAM_MT["<classification-name>" {,<parameter>}* ]
     * </code> </blockquote>
     *
     * @param parent The parent element.
     * @return True if the "PARAM_MT" element is successfully parsed.
     * @throws ParseException if the "PARAM_MT" element can't be parsed.
     */
    private boolean parseParamMT(final Element element) throws ParseException {
    	if( element.getParameters().size()>0 ) {
    		classification = element.getParameters().get(0);
    	}
      
        /*
         * Scan over all PARAMETER["name", value] elements and
         * set the corresponding parameter in the parameter group.
         */
    	Map<String,Object> properties = new HashMap<>();
    	for(Element child:element.getChildren()) {
    		if( child.getKeyword().equalsIgnoreCase("PARAMETER")) {
    			List<String>params = child.getParameters();
    			if( params.size()>1) {
    				properties.put(params.get(0), params.get(1));
    			}
    		}
    	}
        /*
         * We now have all informations for constructing the math transform. If the factory is
         * a Geotools implementation, we will use a special method that returns the operation
         * method used. Otherwise, we will use the ordinary method and will performs a slower
         * search for the operation method later if the user ask for it.
         */
        this.transform = MathTransformFactory.createParameterizedTransform(classification,properties);
        return true;
    }

    /**
     * Parses a "INVERSE_MT" element. This element has the following pattern:
     *
     * <blockquote> <code>
     * INVERSE_MT[<math transform>]
     * </code> </blockquote>
     *
     * @param parent The element.
     * @return True if the "INVERSE_MT" element is successfully parsed.
     * @throws ParseException if the "INVERSE_MT" element can't be parsed.
     */
    private boolean parseInverseMT(final Element parent) throws ParseException {
    	if( parent.getChildren().size()==0) {
    		throw new ParseException(String.format("%s.parseInverseMT: Missing child element",
        			CLSS),0);
    	}
    	Element element = parent.getChildren().get(0);
        try {
            if( analyzeTransform(element) ) {
            	transform = transform.inverse();
            }
        }
        catch (TransformException exception) {
            throw new ParseException(String.format("%s.parseInverseMT: Exception inverting matrix (%s)",
            		CLSS,exception.getLocalizedMessage()),0);
        }
        return true;
    }

    /**
     * Parses a "PASSTHROUGH_MT" element. This element has the following pattern:
     *
     * <blockquote> <code>
     * PASSTHROUGH_MT[<integer>, <math transform>]
     * </code> </blockquote>
     *
     * @param element The element.
     * @return True if the "PASSTHROUGH_MT" element is successfully parsed.
     * @throws ParseException if the "PASSTHROUGH_MT" element can't be parsed.
     */
    private boolean parsePassThroughMT(final Element parent) throws ParseException {
    	if( parent.getParameters().size()==0 || parent.getChildren().size()==0) {
    		throw new ParseException(String.format(
    				"%s.parsePassThroughMT: Missing parameter or child",CLSS),0);
    	}
        final int firstAffectedOrdinate = Integer.parseInt(parent.getParameters().get(0));
        Element element = parent.getChildren().get(0);
        // TODO: Complete
        if( analyzeTransform(element) ) {
        	int numTrailing = 42;
        	transform = MathTransformFactory.createPassThroughTransform(firstAffectedOrdinate, null,numTrailing);
        }

        return true;
    }

    /**
     * Parses a "CONCAT_MT" element. This element has the following pattern:
     *
     * <blockquote> <code>
     * CONCAT_MT[<math transform> {,<math transform>}*]
     * </code> </blockquote>
     *
     * @param element The element to parse.
     * @return True if the "CONCAT_MT" element is successfully parsed.
     * @throws ParseException if the "CONCAT_MT" element can't be parsed.
     */
    private boolean parseConcatMT(final Element element) throws ParseException {
    	MathTransform base = null;
        for( Element child: element.getChildren()) {
        	if( base==null ) {
        		analyzeTransform(child);
        		base = transform.clone();  
        	}
        	else {
        		base = MathTransformFactory.createConcatenatedTransform(base, transform);
        	}
        	transform = base;
        }
        return true;
    }
    
}
