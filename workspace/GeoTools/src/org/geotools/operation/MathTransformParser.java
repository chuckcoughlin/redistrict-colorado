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

import java.net.URI;
import java.text.ParseException;
import java.text.ParsePosition;

import org.geotools.data.wkt.AbstractParser;
import org.geotools.data.wkt.Element;
import org.geotools.data.wkt.Symbols;
import org.locationtech.jts.geom.CoordinateFilter;
import org.openjump.feature.Operation;

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
    /**
     * The classification of the last math transform or projection parsed, or {@code null} if none.
     */
    private String classification;

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



    /**
     * Parses the next element as a MathTransform.
     *
     * @param element the element to be parsed..
     * @param required True if parameter is required and false in other case.
     * @return The next element as a {@link MathTransform} object.
     * @throws ParseException if the next element can't be parsed.
     */
    protected boolean analyzeMathTransform( Element element) throws ParseException {
    	final String keyword = element.getKeyword();
    	boolean result = false;
        lastMethod = null;
        classification = null;

            if ("PARAM_MT".equals(keyword)) return parseParamMT(element);
            else if ("CONCAT_MT".equals(keyword)) return parseConcatMT(element);
            else if ("INVERSE_MT".equals(keyword)) return parseInverseMT(element);
            else if ("PASSTHROUGH_MT".equals(keyword)) return parsePassThroughMT(element);
        

        return result;
    }

    /**
     * Parses a "PARAM_MT" element. This element has the following pattern:
     *
     * <blockquote>
     *
     * <code>
     * PARAM_MT["<classification-name>" {,<parameter>}* ]
     * </code>
     *
     * </blockquote>
     *
     * @param parent The parent element.
     * @return The "PARAM_MT" element as an {@link MathTransform} object.
     * @throws ParseException if the "PARAM_MT" element can't be parsed.
     */
    private MathTransform parseParamMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("PARAM_MT");
        classification = element.pullString("classification");
        final ParameterValueGroup parameters;
        try {
            parameters = mtFactory.getDefaultParameters(classification);
        } catch (NoSuchIdentifierException exception) {
            throw element.parseFailed(exception, null);
        }
        /*
         * Scan over all PARAMETER["name", value] elements and
         * set the corresponding parameter in the parameter group.
         */
        Element param;
        while ((param = element.pullOptionalElement("PARAMETER")) != null) {
            final String name = param.pullString("name");
            final ParameterValue parameter = parameters.parameter(name);
            final Class type = parameter.getDescriptor().getValueClass();
            if (Integer.class.equals(type)) {
                parameter.setValue(param.pullInteger("value"));
            } else if (Double.class.equals(type)) {
                parameter.setValue(param.pullDouble("value"));
            } else if (URI.class.equals(type)) {
                parameter.setValue(URI.create(param.pullString("value")));
            } else {
                parameter.setValue(param.pullString("value"));
            }
            param.close();
        }
        element.close();
        /*
         * We now have all informations for constructing the math transform. If the factory is
         * a Geotools implementation, we will use a special method that returns the operation
         * method used. Otherwise, we will use the ordinary method and will performs a slower
         * search for the operation method later if the user ask for it.
         */
        final MathTransform transform;
        try {
            transform = mtFactory.createParameterizedTransform(parameters);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
        lastMethod = mtFactory.getLastMethodUsed();
        return transform;
    }

    /**
     * Parses a "INVERSE_MT" element. This element has the following pattern:
     *
     * <blockquote>
     *
     * <code>
     * INVERSE_MT[<math transform>]
     * </code>
     *
     * </blockquote>
     *
     * @param parent The parent element.
     * @return The "INVERSE_MT" element as an {@link MathTransform} object.
     * @throws ParseException if the "INVERSE_MT" element can't be parsed.
     */
    private MathTransform parseInverseMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("INVERSE_MT");
        try {
            final MathTransform transform;
            transform = parseMathTransform(element, true).inverse();
            element.close();
            return transform;
        }
        catch (TransformException exception) {
            throw new ParseException(String.format("%s.parseInverseMT: Exception inverting matrix (%s)",
            		CLSS,exception.getLocalizedMessage()),0);
        }
    }

    /**
     * Parses a "PASSTHROUGH_MT" element. This element has the following pattern:
     *
     * <blockquote>
     *
     * <code>
     * PASSTHROUGH_MT[<integer>, <math transform>]
     * </code>
     *
     * </blockquote>
     *
     * @param parent The parent element.
     * @return The "PASSTHROUGH_MT" element as an {@link MathTransform} object.
     * @throws ParseException if the "PASSTHROUGH_MT" element can't be parsed.
     */
    private MathTransform parsePassThroughMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("PASSTHROUGH_MT");
        final int firstAffectedOrdinate = parent.pullInteger("firstAffectedOrdinate");
        final MathTransform transform = parseMathTransform(element, true);
        element.close();
        try {
            return mtFactory.createPassThroughTransform(firstAffectedOrdinate, transform, 0);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a "CONCAT_MT" element. This element has the following pattern:
     *
     * <blockquote>
     *
     * <code>
     * CONCAT_MT[<math transform> {,<math transform>}*]
     * </code>
     *
     * </blockquote>
     *
     * @param parent The parent element.
     * @return The "CONCAT_MT" element as an {@link MathTransform} object.
     * @throws ParseException if the "CONCAT_MT" element can't be parsed.
     */
    private MathTransform parseConcatMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("CONCAT_MT");
        MathTransform transform = parseMathTransform(element, true);
        MathTransform optionalTransform;
        while ((optionalTransform = parseMathTransform(element, false)) != null) {
            try {
                transform = mtFactory.createConcatenatedTransform(transform, optionalTransform);
            } catch (FactoryException exception) {
                throw element.parseFailed(exception, null);
            }
        }
        element.close();
        return transform;
    }

    /**
     * Returns the operation method for the last math transform parsed. This is used by {@link
     * Parser} in order to built {@link org.opengis.referencing.crs.DerivedCRS}.
     */
    final OperationMethod getOperationMethod() {
        if (lastMethod == null) {
            /*
             * Safety in case come MathTransformFactory implementation do not support
             * getLastMethod(). Performs a slower and less robust check as a fallback.
             */
            if (classification != null) {
                for (final OperationMethod method :
                        mtFactory.getAvailableMethods(Operation.class)) {
                    if (AbstractIdentifiedObject.nameMatches(method, classification)) {
                        lastMethod = method;
                        break;
                    }
                }
            }
        }
        return lastMethod;
    }
}
