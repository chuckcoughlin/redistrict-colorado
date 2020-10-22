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

import java.awt.geom.NoninvertibleTransformException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.unit.MetricSystem;
import javax.measure.unit.USCustomarySystem;
import javax.measure.unit.Unit;

import org.geotools.datum.Citation;
import org.geotools.datum.Citations;
import org.geotools.datum.Datum;
import org.geotools.datum.GeodeticDatum;
import org.geotools.datum.PrimeMeridian;
import org.geotools.datum.VerticalDatum;
import org.geotools.operation.MathTransform;
import org.geotools.operation.MathTransformParser;
import org.geotools.operation.OperationMethod;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateReferenceSystem;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.coordsys.CoordinateSystemAxis;

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
	 * @throws ParseException if the current element can't be parsed.
	 */
	@Override
	public boolean analyzeElement( Element element) throws ParseException {
		boolean result = 
				analyzeCRS(element)   ||
				analyzeDatum(element) ||
				analyzeMathTransform(element);

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
		else if ("VERT_DATUM".equals(keyword)) result = true;
		else if ("LOCAL_DATUM".equals(keyword)) result = true;
		else if ("DATUM".equals(keyword)) result = true;


		return result;
	}

}


