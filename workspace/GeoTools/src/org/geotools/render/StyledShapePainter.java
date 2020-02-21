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
import org.openjump.feature.Feature;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;



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
	private static final double DECIMATOR_TOLERANCE = 0.0001; // ~degrees latitue or longitude
	private static final double POINT_HEIGHT = 2.0;
	private static final double POINT_WIDTH = 2.0;
	private final Decimator decimator;

	public StyledShapePainter() {
		this.decimator = new Decimator(DECIMATOR_TOLERANCE);
	}

	/**
	 * Apply the specified style and draw the shape. The shapes are already properly
	 * scaled and positioned.
	 *
	 * @param graphics The graphics in which to draw.
	 * @param feature the feature to draw.
	 * @param style The style to apply, or <code>null</code> if none.
	 */
	public void paint(GraphicsContext graphics,Feature feature,Style style) {
		GeometryCollection collection = null;
		Geometry geom = feature.getGeometry();
		switch (Geometries.get(geom)) {
		case POINT:
			drawPoint(graphics,feature,(Point)geom,style);
			break;
		case MULTIPOINT:
			collection = (GeometryCollection)geom;
			for(int index=0;index<collection.getNumGeometries();index++) {
				drawPoint(graphics,feature,(Point)collection.getGeometryN(index),style);
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
	private boolean ignoreFill(Geometry geom, Style style) {
		// if we have a graphic stroke the outline might not be solid, so, not covering
		if( style.getLineWidth() >= geom.getEnvelopeInternal().getHeight() ||
			style.getLineWidth() >= geom.getEnvelopeInternal().getWidth()	 ) {
			return true; 
		}
		return false;
	}

	private void drawLine(GraphicsContext graphics, LineString geom, Style style) {
		int size = geom.getNumPoints();  // Max points (there may be fewer)
		double[] x = new double[size];
		double[] y = new double[size];
		int n = decimator.decimateLine(geom,x,y);
		graphics.setStroke(style.getLineColor());
		graphics.setLineWidth(style.getLineWidth());
		graphics.setLineCap(StrokeLineCap.ROUND);
		graphics.setLineJoin(StrokeLineJoin.MITER);
		graphics.strokePolyline(x, y, n);
	}
	// Plot a 2-pixel wide point, no border.
	private void drawPoint(GraphicsContext graphics, Feature feature, Point geom, Style style) {
		graphics.setFill(style.getFillColor(feature));
		graphics.fillOval(geom.getX(), geom.getY(), POINT_HEIGHT, POINT_WIDTH);
	}
	private void drawPolygon(GraphicsContext graphics, Polygon geom, Style style) {
		int size = geom.getNumPoints();  // Max points (there may be fewer)
		double[] x = new double[size];
		double[] y = new double[size];
		int n = decimator.decimatePolygon(geom,x,y);
		LOGGER.info(String.format("%s.drawPolygon: %d of %d points",CLSS,n,size));
		/*
		for(int index=0;index<n&&index<10;index++) {
			LOGGER.info(String.format("     %2.1f\t%2.1f",x[index],y[index]));
		}
		*/
		graphics.setStroke(style.getLineColor());
		graphics.setLineWidth(style.getLineWidth());
		graphics.setLineCap(StrokeLineCap.ROUND);
		graphics.setLineJoin(StrokeLineJoin.MITER);
		if( !ignoreFill(geom,style) ) {
			graphics.fillPolygon(x,y,n);
		}
		graphics.strokePolygon(x, y, n);
	}
}
