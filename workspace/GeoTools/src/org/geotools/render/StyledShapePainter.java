/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.render;


import java.util.logging.Logger;

import org.geotools.geometry.Geometries;
import org.geotools.style.Style;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javafx.scene.canvas.GraphicsContext;



/**
 * A simple class that knows how to paint a Shape object onto a Graphic given a Style. It's the
 * last step of the rendering engine, and had been factored out since both renderers use the same
 * painting logic.
 *
 * @author Andrea Aime
 */
public class StyledShapePainter {
	private final static String CLSS = "StyledShapePainter";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	/** Whether icon centers should be matched to a pixel center, or not */
	public static boolean ROUND_ICON_COORDS = true;


	public StyledShapePainter() {
		// nothing do do, just needs to exist
	}

	/**
	 * Apply the specified style and draw the shape. The shapes are already properly
	 * scaled and positioned.
	 *
	 * @param graphics The graphics in which to draw.
	 * @param shape The polygon to draw.
	 * @param style The style to apply, or <code>null</code> if none.
	 */
	public void paint(GraphicsContext graphics,FeatureShape shape,Style style) {
		GeometryCollection collection = null;
		Geometry geom = shape.getGeometry();
		switch (Geometries.get(geom)) {
		case POINT:
			drawPoint(graphics,(Point)geom,style);
			break;
		case MULTIPOINT:
			collection = (GeometryCollection)geom;
			for(int index=0;index<collection.getNumGeometries();index++) {
				drawPoint(graphics,(Point)collection.getGeometryN(index),style);
			}
			break;
		case LINESTRING:
			drawLine(graphics,(LineString)geom,style);
			break;
		case MULTILINESTRING:
			collection = (GeometryCollection)geom;
			for(int index=0;index<collection.getNumGeometries();index++) {
				drawLine(graphics,(LineString)collection.getGeometryN(index),style);
			}
			break;
		case POLYGON:
			drawPolygon(graphics,(Polygon)geom,style);
			break;
		case MULTIPOLYGON:
			collection = (GeometryCollection)geom;
			for(int index=0;index<collection.getNumGeometries();index++) {
				drawPolygon(graphics,(Polygon)collection.getGeometryN(index),style);
			}
			break;
		case GEOMETRYCOLLECTION:
		default:
			throw new UnsupportedOperationException("No style method for " + geom.getGeometryType());
		}
	}

	/**
	 * Checks if the fill can simply be omitted because it's not going to be visible anyways. It
	 * takes a style that has a solid outline and a width or height that's less than the stroke
	 * width
	 *
	 * @param style
	 * @param shape
	 * @return true if rendering a fill can be skipped.
	 */
	private boolean ignoreFill(FeatureShape shape, Style style) {
		// if we have a graphic stroke the outline might not be solid, so, not covering
		if(style.getLineWidth() >= shape.getBounds().getHeight() ||
				style.getLineWidth() >= shape.getBounds().getWidth()	 ) {
			return true; 
		}
		return false;
	}

	public void drawLine(GraphicsContext graphics, LineString geom, Style style) {

	}
	public void drawPoint(GraphicsContext graphics, Point geom, Style style) {

	}
	public void drawPolygon(GraphicsContext graphics, Polygon geom, Style style) {

	}
}
