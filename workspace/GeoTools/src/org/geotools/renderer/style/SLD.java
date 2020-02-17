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
package org.geotools.renderer.style;

import java.awt.Color;
import java.util.logging.Logger;

import org.geotools.geometry.jts.Geometries;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Symbolizer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;


/**
 * Style Layer Descriptor.
 * Utility class that provides static helper methods for common operations with GeoTools styling.
 *
 * @author Jody Garnett
 * @version $Id$
 */
public class SLD {
	private final static String CLSS = "SLD";
	private static Logger LOGGER = Logger.getLogger(CLSS);


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
        Color fillColor = null;
        Geometry geom = feature.getGeometry();
        LOGGER.info(String.format("%s.createSimpleStyle: %s",CLSS,geom.getGeometryType()));
        switch (Geometries.get(geom)) {
			case POINT:
			case MULTIPOINT:
				if (color.equals(Color.BLACK)) { fillColor = null;} 
	            else {fillColor = color;}
	            return createPointStyle("Circle", color, fillColor, 0.5f, 3.0f);
			case LINESTRING:
			case MULTILINESTRING:
				return createLineStyle(color, 1.0f);
			case POLYGON:
			case MULTIPOLYGON:
				 if (color.equals(Color.BLACK)) {fillColor = null; }
		         else {fillColor = color;}
		         return createPolygonStyle(color, fillColor, 0.5f);
			case GEOMETRYCOLLECTION:
			default:
				throw new UnsupportedOperationException("No style method for " + geom.getGeometryType());
        }
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
        Stroke stroke = new Stroke(outlineColor,1.0f);
        Fill fill = new Fill();
        if (fillColor != null) {
            fill.setColor(fillColor);
            fill.setOpacity(opacity);
        }
        PolygonSymbolizer psym = new PolygonSymbolizer();
        psym.setFill(fill);
        psym.setStroke(stroke);
        return wrapSymbolizers(psym);
    }

  

    /**
     * Create a line style with given color and line width
     *
     * @param lineColor color of lines
     * @param width width of lines
     * @return a new Style instance
     */
    public static Style createLineStyle(Color lineColor, float width) {
        Stroke stroke = new Stroke(lineColor, width);
        LineSymbolizer lsym = new LineSymbolizer();
        lsym.setStroke(stroke);
        return wrapSymbolizers(lsym);
    }

    /**
     * Create a point style without text labels
     *
     * @param wellKnownName one of: Circle, Square, Cross, X, Triangle or Star
     * @param lineColor color for the point symbol outline
     * @param fillColor color for the point symbol fill
     * @param opacity a value between 0 and 1 for the opacity of the fill
     * @param size size of the point symbol
     * @return a new Style instance
     */
    public static Style createPointStyle(String wellKnownName,Color lineColor,Color fillColor,
            								float opacity,float size) {

        Stroke stroke = new Stroke(lineColor,1.0f);
        Fill fill = new Fill();
        if (fillColor != null) { 
        	fill.setColor(fillColor);
        	fill.setOpacity(opacity);
        }

        Mark mark = new Mark(wellKnownName);
        mark.setFill(fill);
        mark.setStroke(stroke);
        Graphic graphic =  new Graphic();
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(mark);
        graphic.setSize(size);

        PointSymbolizer pointSym = new PointSymbolizer();
        pointSym.setGraphic(graphic);
        return wrapSymbolizers(pointSym);
    }

    /**
     * Wrap one or more symbolizers into a Style
     *
     * @param symbolizers one or more symbolizer objects
     * @return a new Style instance or null if no symbolizers are provided
     */
    public static Style wrapSymbolizers(Symbolizer... symbolizers) {
        if (symbolizers == null || symbolizers.length == 0) {
            return null;
        }


        Style style = new Style();

        return style;
    }
}
