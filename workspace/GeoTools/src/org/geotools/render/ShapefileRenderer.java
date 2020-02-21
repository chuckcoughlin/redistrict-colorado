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
package org.geotools.render;


import java.util.logging.Logger;

import org.geotools.style.Style;
import org.geotools.util.RendererUtilities;
import org.locationtech.jts.geom.Envelope;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * A renderer designed specifically for shapefiles. There is no label caching.
 * Each shapefile is a separate canvas, a separate layer.
 */
public class ShapefileRenderer {
	private final static String CLSS = "ShapefileRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
    private final MapLayer layer;
    private final StyledShapePainter painter = new StyledShapePainter();
   
    
    public ShapefileRenderer(MapLayer ml) {
    	this.layer = ml;
    }

    /**
     * This is the paint method used to actually draw the map. We always go from geodesic coordinates
     * to screen coordinates.
     */
    public void paint( GraphicsContext graphics,Rectangle paintArea,Style style,FeatureFilter filter) {
    	Envelope mapArea = layer.getBounds();
        if (mapArea == null || paintArea == null) {
            LOGGER.info(String.format("%s.paint: paint or mapping area is null",CLSS));
            return;
        }
        if( layer.isVisible()) {
        	paint(graphics,mapArea,paintArea,style,filter);
        }
    }

    /**
     * Paint the map features after transforming to match the target area.
     * @param graphics the canvas
     * @param mapExtent the envelope surrounding the layer features.
     * @param paintArea target screen area.
     * @param filter containing pan or zoom commands.
     */
    private void paint(GraphicsContext graphics,Envelope mapExtent,Rectangle paintArea, Style style,FeatureFilter filter ) {
    	LOGGER.info(String.format("%s.paint: layer %s",CLSS,layer.getTitle()));
    	FeatureCollection collection = layer.getFeatures();
    	Envelope enclosure = collection.getEnvelope();
    	graphics.save();
    	graphics.setFill(Color.AQUA);
    	graphics.fillOval(200.,300., 250.,150.);
   
    	double minx = enclosure.getMinX();
    	double miny = enclosure.getMinY();
    	graphics.translate(-minx,-miny);  // Align origins
    	LOGGER.info(String.format("%s.paint: translate %2.1fx, %2.1fy",CLSS,-minx,-miny));
    	graphics.setFill(Color.RED);
    	graphics.fillOval(200.,300., 250.,150.);
    	
    	double scalex = RendererUtilities.calculateXScale(mapExtent, paintArea);
    	double scaley = RendererUtilities.calculateYScale(mapExtent, paintArea);
    	graphics.scale(1./scalex, 1./scaley);
    	LOGGER.info(String.format("%s.paint: scale %2.1fx, %2.1fy",CLSS,scalex,scaley));
    	graphics.setFill(Color.CORAL);
    	graphics.fillOval(200.,300., 250.,150.);
    	graphics.setFill(Color.DARKGREEN);
    	graphics.fillOval(20000.,30000., 25000.,15000.);
    	
    	Translate trans = filter.getTranslation();
    	if( trans!=null ) {
    		graphics.translate(trans.getX(), trans.getY());
    	}
    	Scale scale = filter.getScale();
    	if( scale!=null ) {
    		graphics.scale(scale.getX(), scale.getY());
    	}

    	// We've already transformed the graphics context
    	// Now draw features in the layer individually
        for( Feature feature:collection.getFeatures()) {
        	LOGGER.info(String.format("%s.paint: feature %s",CLSS,feature.getID()));
        	painter.paint(graphics, feature, style);
        }
        graphics.restore();
    }

}