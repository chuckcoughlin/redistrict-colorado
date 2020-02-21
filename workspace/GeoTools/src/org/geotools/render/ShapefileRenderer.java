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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

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
     * @param mapExtent the envelope surrounding the layer features.
     * @param paintArea target screen area.
     * @param filter containing pan or zoom commands.
     */
    private void paint(GraphicsContext graphics,Envelope mapExtent,Rectangle paintArea, Style style,FeatureFilter filter ) {
    	double scalex = RendererUtilities.calculateXScale(mapExtent, paintArea);
    	double scaley = RendererUtilities.calculateYScale(mapExtent, paintArea);
    	Scale scale = new Scale(scalex,scaley);
    	filter.concatenateTransforms(scale);

    	// Transform and draw features in the layer individually
        FeatureCollection collection = layer.getFeatures();
        for( Feature feature:collection.getFeatures()) {
        	FeatureShape shape = new FeatureShape(feature);
        	painter.paint(graphics, shape, style);
        }
    }

}