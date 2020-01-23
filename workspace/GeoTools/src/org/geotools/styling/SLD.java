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
package org.geotools.styling;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.logging.Logger;

import org.geotools.filter.FilterFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Symbolizer;
import org.openjump.feature.Feature;


/**
 * Utility class that provides static helper methods for common operations with GeoTools styling
 *
 * @author Jody Garnett
 * @version $Id$
 */
public class SLD {
	private final static String CLSS = "SLD";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
    private static StyleFactory sf = new StyleFactory();
    private static FilterFactory ff = new FilterFactory();


    public static final double ALIGN_LEFT = 1.0;
    public static final double ALIGN_CENTER = 0.5;
    public static final double ALIGN_RIGHT = 0.0;
    public static final double ALIGN_BOTTOM = 1.0;
    public static final double ALIGN_MIDDLE = 0.5;
    public static final double ALIGN_TOP = 0.0;

    /**
     * Create a minimal style to render features 
     *
     * @param features the feature
     * @return a new Style instance
     */
    public static Style createSimpleStyle(Feature feature) {
        return createSimpleStyle(feature, Color.BLACK);
    }

    /**
     * Create a minimal style to render features of type {@code type}
     *
     * @param type the feature type to create the style for
     * @param color single color to use for all components of the Style
     * @return a new Style instance
     * @throws java.io.IOException if the data store cannot be accessed
     */
    public static Style createSimpleStyle(Feature feature, Color color) {
        Geometry geom = feature.getGeometry();
        String type = geom.getGeometryType();
        LOGGER.info(String.format("%s.createSimpleStyle: %s",CLSS,type));
        Color fillColor = null;

        if(type.equalsIgnoreCase("Polygon") || type.equalsIgnoreCase("MultiPolygon")) {
            if (color.equals(Color.BLACK)) {
                fillColor = null;
            } else {
                fillColor = color;
            }
            return createPolygonStyle(color, fillColor, 0.5f);

        } 
        else if( type.equalsIgnoreCase("LineString")
                ||  type.equalsIgnoreCase("MultiLineString") ) {
            return createLineStyle(color, 1.0f);

        } 
        else if( type.equalsIgnoreCase("Point")
                ||  type.equalsIgnoreCase("MultiPoint")) {
            if (color.equals(Color.BLACK)) {
                fillColor = null;
            } else {
                fillColor = color;
            }
            return createPointStyle("Circle", color, fillColor, 0.5f, 3.0f);
        }

        throw new UnsupportedOperationException("No style method for " + type);
    }

    /**
     * Create a polygon style with the given colors and opacity.
     *
     * @param outlineColor color of polygon outlines
     * @param fillColor color for the fill
     * @param opacity proportional opacity (0 to 1)
     * @return a new Style instance
     */
    public static Style createPolygonStyle(Color outlineColor, Color fillColor, float opacity) {
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(1.0f));
        Fill fill = Fill.NULL;
        if (fillColor != null) {
            fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
        }
        return wrapSymbolizers(sf.createPolygonSymbolizer(stroke, fill, null));
    }

    /**
     * Create a polygon style with the given colors, opacity and optional labels.
     *
     * @param outlineColor color of polygon outlines
     * @param fillColor color for the fill
     * @param opacity proportional opacity (0 to 1)
     * @param labelField name of the feature field (attribute) to use for labelling; mauy be {@code
     *     null} for no labels
     * @param labelFont GeoTools Font object to use for labelling; if {@code null} and {@code
     *     labelField} is not {@code null} the default font will be used
     * @return a new Style instance
     */
    public static Style createPolygonStyle(
            Color outlineColor, Color fillColor, float opacity, String labelField, Font labelFont) {
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(1.0f));
        Fill fill = Fill.NULL;
        if (fillColor != null) {
            fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
        }
        PolygonSymbolizer polySym = sf.createPolygonSymbolizer(stroke, fill, null);

        if (labelField == null) {
            return wrapSymbolizers(polySym);

        } else {
            Font font = (labelFont == null ? sf.getDefaultFont() : labelFont);
            Fill labelFill = sf.createFill(ff.literal(Color.BLACK));

            TextSymbolizer textSym =
                    sf.createTextSymbolizer(
                            labelFill,
                            new Font[] {font},
                            null,
                            ff.property(labelField),
                            null,
                            null);

            return wrapSymbolizers(polySym, textSym);
        }
    }

    /**
     * Create a line style with given color and line width
     *
     * @param lineColor color of lines
     * @param width width of lines
     * @return a new Style instance
     */
    public static Style createLineStyle(Color lineColor, float width) {
        Stroke stroke = sf.createStroke(ff.literal(lineColor), ff.literal(width));
        return wrapSymbolizers(sf.createLineSymbolizer(stroke, null));
    }

    /**
     * Create a line style with given color, line width and optional labels
     *
     * @param lineColor color of lines
     * @param width width of lines
     * @param labelField name of the feature field (attribute) to use for labelling; mauy be {@code
     *     null} for no labels
     * @param labelFont GeoTools Font object to use for labelling; if {@code null} and {@code
     *     labelField} is not {@code null} the default font will be used
     * @return a new Style instance
     */
    public static Style createLineStyle(
            Color lineColor, float width, String labelField, Font labelFont) {
        Stroke stroke = sf.createStroke(ff.literal(lineColor), ff.literal(width));
        LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);

        if (labelField == null) {
            return wrapSymbolizers(lineSym);

        } else {
            Font font = (labelFont == null ? sf.getDefaultFont() : labelFont);
            Fill labelFill = sf.createFill(ff.literal(Color.BLACK));

            TextSymbolizer textSym =
                    sf.createTextSymbolizer(
                            labelFill,
                            new Font[] {font},
                            null,
                            ff.property(labelField),
                            null,
                            null);

            return wrapSymbolizers(lineSym, textSym);
        }
    }

    /**
     * Create a point style without labels
     *
     * @param wellKnownName one of: Circle, Square, Cross, X, Triangle or Star
     * @param lineColor color for the point symbol outline
     * @param fillColor color for the point symbol fill
     * @param opacity a value between 0 and 1 for the opacity of the fill
     * @param size size of the point symbol
     * @return a new Style instance
     */
    public static Style createPointStyle(
            String wellKnownName, Color lineColor, Color fillColor, float opacity, float size) {
        return createPointStyle(wellKnownName, lineColor, fillColor, opacity, size, null, null);
    }

    /**
     * Create a point style, optionally with text labels
     *
     * @param wellKnownName one of: Circle, Square, Cross, X, Triangle or Star
     * @param lineColor color for the point symbol outline
     * @param fillColor color for the point symbol fill
     * @param opacity a value between 0 and 1 for the opacity of the fill
     * @param size size of the point symbol
     * @param labelField name of the feature field (attribute) to use for labelling; mauy be {@code
     *     null} for no labels
     * @param labelFont GeoTools Font object to use for labelling; if {@code null} and {@code
     *     labelField} is not {@code null} the default font will be used
     * @return a new Style instance
     */
    public static Style createPointStyle(
            String wellKnownName,
            Color lineColor,
            Color fillColor,
            float opacity,
            float size,
            String labelField,
            Font labelFont) {

        Stroke stroke = sf.createStroke(ff.literal(lineColor), ff.literal(1.0f));
        Fill fill = Fill.NULL;
        if (fillColor != null) {
            fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
        }

        Mark mark =
                sf.createMark(
                        ff.literal(wellKnownName), stroke, fill, ff.literal(size), ff.literal(0));

        Graphic graphic = sf.createDefaultGraphic();
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(mark);
        graphic.setSize(ff.literal(size));

        PointSymbolizer pointSym = sf.createPointSymbolizer(graphic, null);

        if (labelField == null) {
            return wrapSymbolizers(pointSym);

        } else {
            Font font = (labelFont == null ? sf.getDefaultFont() : labelFont);
            Fill labelFill = sf.createFill(ff.literal(Color.BLACK));
            AnchorPoint anchor = sf.createAnchorPoint(ff.literal(0.5), ff.literal(0.0));
            Displacement disp = sf.createDisplacement(ff.literal(0), ff.literal(5));
            LabelPlacement placement = sf.createPointPlacement(anchor, disp, ff.literal(0));

            TextSymbolizer textSym =
                    sf.createTextSymbolizer(
                            labelFill,
                            new Font[] {font},
                            null,
                            ff.property(labelField),
                            placement,
                            null);

            return wrapSymbolizers(pointSym, textSym);
        }
    }

    /**
     * Wrap one or more symbolizers into a Rule / FeatureTypeStyle / Style
     *
     * @param symbolizers one or more symbolizer objects
     * @return a new Style instance or null if no symbolizers are provided
     */
    public static Style wrapSymbolizers(Symbolizer... symbolizers) {
        if (symbolizers == null || symbolizers.length == 0) {
            return null;
        }

        Rule rule = sf.createRule();

        for (Symbolizer sym : symbolizers) {
            rule.symbolizers().add(sym);
        }

        FeatureTypeStyle fts = sf.createFeatureTypeStyle(new Rule[] {rule});

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
}
