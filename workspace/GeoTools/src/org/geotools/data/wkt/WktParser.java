/*
parseMathTransform *    GeoTools - The Open Source Java GIS Toolkit
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

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.measure.unit.MetricSystem;

import org.geotools.datum.BursaWolfParameters;
import org.geotools.datum.Datum;
import org.geotools.datum.Ellipsoid;
import org.geotools.datum.GeodeticDatum;
import org.geotools.datum.PrimeMeridian;
import org.geotools.datum.VerticalDatum;
import org.geotools.operation.MathTransform;
import org.geotools.operation.MathTransformParser;

/**
 * Parser for <A
 * HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A>. This parser can parse {@linkplain MathTransform math transform}
 * objects as well, which is part of the WKT's {@code FITTED_CS} element.
 *
 * @since 2.0
 * @version $Id$
 * @author Remi Eve
 * @author Martin Desruisseaux (IRD)
 * @see <A
 *     HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well
 *     Known Text specification</A>
 * @see <A HREF="http://gdal.org/wktproblems.html">OGC WKT Coordinate System Issues</A>
 */
public class WktParser extends MathTransformParser {
	private static final String CLSS = "WktParser";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final long serialVersionUID = -144097689843465085L;
	/**
	 * {@code true} in order to allows the non-standard Oracle syntax. Oracle put the Bursa-Wolf
	 * parameters straight into the {@code DATUM} elements, without enclosing them in a {@code
	 * TOWGS84} element.
	 */
	private static final boolean ALLOW_ORACLE_SYNTAX = true;
	private Datum datum = null;


	/** Constructs a parser using the default set of symbols and factories. */
	public WktParser() {
		super(Symbols.DEFAULT);
	}



	/**
	 * This method is called by the abstract class as it iterates over the elements.
	 * In general we skip over all except those elements which influence the transform.
	 *
	 * These are the root elements, the coordinate reference systems.
	 * 
	 * @param element
	 * @return true if the current element is handled.
	 * @throws ParseException if the element should have been handled, 
	 *         but wasn't.
	 */
	@Override
	public boolean analyzeElement( Element element) throws ParseException {
		boolean result = 
				analyzeCRS(element)   ||
				analyzeDatum(element) ||
				super.analyzeElement(element);
		return result;
	}

	/**
	 * Analyze the root element, the CoordinateReferenceSystem
	 * @param element
	 * @return true if the keyword was recognized.
	 * @throws ParseException
	 */
	private boolean analyzeCRS( Element element) throws ParseException {
		final String keyword = element.getKeyword();
		boolean result = false;

		if(      "GEOGCS".equals(keyword)) result = true;
		else if ("PROJCS".equals(keyword)) result = true;
		else if ("GEOCCS".equals(keyword)) result = true;
		else if ("VERT_CS".equals(keyword)) result = true;
		else if ("LOCAL_CS".equals(keyword)) result = true;
		else if ("COMPD_CS".equals(keyword)) result = true;
		else if ("FITTED_CS".equals(keyword)) result = true;

		return result;
	}


	/**  
	 * Analyze an element that describes a datum.
	 *
	 * @param element
	 * @return true if the keyword was recognized.
	 * @throws ParseException
	 */
	protected boolean analyzeDatum( Element element) throws ParseException {
		final String keyword = element.getKeyword();
		boolean result = false;

		if (     "AXIS".equals(keyword)) result = true;
		else if ("PRIMEM".equals(keyword)) result = true;
		else if ("TOWGS84".equals(keyword)) result = true;
		else if ("SPHEROID".equals(keyword)) result = true;
		else if ("VERT_DATUM".equals(keyword)) result = parseVertDatum(element);
		else if ("LOCAL_DATUM".equals(keyword)) result = true;
		else if ("DATUM".equals(keyword)) result = parseDatum(element,PrimeMeridian.GREENWICH);

		return result;
	}

    /**
     * Parses an <strong>optional</strong> "AUTHORITY" element. 
     * This element has the following pattern:
     *
     * <blockquote> <code>
     * AUTHORITY["&lt;name&gt;", "&lt;code&gt;"]
     * AUTHORITY["&lt;name&gt;", &lt;code&gt;]
     * </code> </blockquote>
     *
     * @param parent The parent element.
     * @param name The name of the parent object being parsed.
     * @return A properties map with the parent name and the optional authority code.
     * @throws ParseException if the "AUTHORITY" can't be parsed.
     */
    private Map<String, Object> parseAuthority(final Element parent, final String name) throws ParseException {
        final boolean isRoot = parent.getIsRoot();
        final Element element = parent.findChild("AUTHORITY");
        Map<String, Object> properties;
        if (element == null) {
            if (isRoot) {
                properties = new HashMap<String, Object>(4);
                properties.put(IdentifiedObject.NAME_KEY, name);
            } 
            else {
                properties = Collections.singletonMap(IdentifiedObject.NAME_KEY, (Object) name);
            }
        } 
        else {
        	List<String> parameters = element.getParameters();
        	if( parameters.size()>1 ) {
        		final String auth = parameters.get(0);
        		String code = parameters.get(1);

        		properties = new HashMap<String, Object>(4);
        		properties.put(IdentifiedObject.NAME_KEY, auth);
        		properties.put(IdentifiedObject.IDENTIFIERS_KEY, code);
        	}
        	else {
        		throw new ParseException(String.format("%s.parseAuthority: Incorrect parameter count (%d)",
            			CLSS,parameters.size()),0);
        	}
        }
        return properties;
    }
	/**
     * Parse a "DATUM" element and set the parser's datum object.
     * This element has the following pattern:
     *
     * <blockquote> <code>
     * DATUM["<name>", <spheroid> {,<to wgs84>} {,<authority>}]
     * </code> </blockquote>
     *
     * @param element The datum element.
     * @param meridian the prime meridian.
     * @return true if the parse was successful
     * @throws ParseException if the "DATUM" element can't be parsed.
     */
	private boolean parseDatum(final Element element, final PrimeMeridian meridian)
            throws ParseException {
        String name = element.getKeyword();
        Ellipsoid ellipsoid = parseSpheroid(element);
        BursaWolfParameters toWGS84 = parseToWGS84(element); // Optional; may be null.
        Map<String,Object> properties = parseAuthority(element, name);
        if (ALLOW_ORACLE_SYNTAX && (toWGS84 == null) ) {
        	toWGS84 = new BursaWolfParameters(GeodeticDatum.WGS84);
        	List<String> parameters = element.getParameters();
        	try {
        		toWGS84.dx = Double.parseDouble(parameters.get(0));   // dx
        		toWGS84.dy = Double.parseDouble(parameters.get(1));   // dy
        		toWGS84.dz = Double.parseDouble(parameters.get(2));   // dz
        		toWGS84.ex = Double.parseDouble(parameters.get(3));   // ex
        		toWGS84.ey = Double.parseDouble(parameters.get(4));   // ey
        		toWGS84.ez = Double.parseDouble(parameters.get(5));   // ez
        		toWGS84.ppm = Double.parseDouble(parameters.get(6));  // ppm
            }
            catch(NumberFormatException nfe) {
            	throw new ParseException(String.format("%s.parseDatum: Invalid number (%s)",
            			CLSS,nfe.getLocalizedMessage()),0);
            }
        }   
        if (toWGS84 != null) {
            if (!(properties instanceof HashMap)) {
                properties = new HashMap<String, Object>(properties);
            }   
            properties.put(GeodeticDatum.BURSA_WOLF_KEY, toWGS84);
        } 
        this.datum = new GeodeticDatum(properties, ellipsoid, meridian);
        return true;
	}
    /**
     * Parse a "SPHEROID" element. This element has the following pattern:
     *
     * <blockquote> <code>
     * SPHEROID["<name>", <semi-major axis>, <inverse flattening> {,<authority>}]
     * </code> </blockquote>
     *
     * @param parent The parent element.
     * @return The "SPHEROID" element as an {@link Ellipsoid} object.
     * @throws ParseException if the "SPHEROID" element can't be parsed.
     */
	private Ellipsoid parseSpheroid(final Element parent) throws ParseException {
		Ellipsoid ellipsoid = null;
		Element element = parent.findChild("SPHEROID");
		if( element==null ) {
			throw new ParseException(String.format("%s.parseSpheroid: No SPHERIOD element",
					CLSS),0);
		}
		String name = element.getKeyword();
		List<String> parameters = element.getParameters();
		if( parameters.size()< 2 ) {
			throw new ParseException(String.format("%s.parseSpheroid: Too few parameters (%d)",
					CLSS,parameters.size()),0);
		}
		try {
			double semiMajorAxis = Double.parseDouble(parameters.get(0)); // SemiMajor
			double inverseFlattening = Double.parseDouble(parameters.get(1)); // Inverse flattenng
			Map<String,Object>properties = parseAuthority(element, name);
			if (inverseFlattening == 0) {
				// Inverse flattening null is an OGC convention for a sphere.
				inverseFlattening = Double.POSITIVE_INFINITY;
			}
			ellipsoid = Ellipsoid.createFlattenedSphere(
					properties, semiMajorAxis, inverseFlattening, MetricSystem.METRE);

		}
		catch(NumberFormatException nfe) {
			throw new ParseException(String.format("%s.parseToWGS84: Invalid number (%s)",
					CLSS,nfe.getLocalizedMessage()),0);
		}
		return ellipsoid;
	}
    /**
     * Parses a "VERT_DATUM" element and make it our datum. 
     * This element has the following pattern:
     *
     * <blockquote> <code>
     * VERT_DATUM["<name>", <datum type> {,<authority>}]
     * </code> </blockquote>
     *
     * @param element The datum element.
     * @return true if The "VERT_DATUM" element as a {@link VerticalDatum} object.
     * @throws ParseException if the "VERT_DATUM" element can't be parsed.
     */
    private boolean parseVertDatum(final Element element) throws ParseException {
    	List<String> parameters = element.getParameters();
    	if( parameters.size() <2 ) {
    		throw new ParseException(String.format("%s.parseVertDatum: Too few parameters (%d)",
					CLSS,parameters.size()),0);
    	}
        final String name = parameters.get(0);
        final int type =  Integer.parseInt(parameters.get(1));
        final Map<String,Object> properties = parseAuthority(element, name);
        this.datum = new VerticalDatum(properties, type);
        return true;
    }
    /**
     * Parse an <strong>optional</strong> "TOWGS84" element. This element has the following
     * pattern:
     *
     * <blockquote>  <code>
     * TOWGS84[<dx>, <dy>, <dz>, <ex>, <ey>, <ez>, <ppm>]
     * </code> </blockquote>
     *
     * @param parent The parent element.
     * @return The BurseWolfParamaters from the element
     * @throws ParseException if the "TOWGS84" can't be parsed.
     */
    private BursaWolfParameters parseToWGS84(final Element element) throws ParseException {
        final BursaWolfParameters info = new BursaWolfParameters(GeodeticDatum.WGS84);
        List<String> parameters = element.getParameters();
        int count = element.getParameters().size();
        if( count<3 || (count>3 && count<7) ) {
        	throw new ParseException(String.format("%s.parseToWGS84: Incorrect parameter count (%d)",
        			CLSS,count),0);
        }
        try {
        	info.dx = Double.parseDouble(parameters.get(0));   // dx
        	info.dy = Double.parseDouble(parameters.get(1));   // dy
        	info.dz = Double.parseDouble(parameters.get(2));   // dz
        	if( count>3 ) {
        		info.ex = Double.parseDouble(parameters.get(3));   // ex
        		info.ey = Double.parseDouble(parameters.get(4));   // ey
        		info.ez = Double.parseDouble(parameters.get(5));   // ez
        		info.ppm = Double.parseDouble(parameters.get(6));  // ppm
        	}
        }
        catch(NumberFormatException nfe) {
        	throw new ParseException(String.format("%s.parseToWGS84: Invalid number (%s)",
        			CLSS,nfe.getLocalizedMessage()),0);
        }
        return info;
    }
}


