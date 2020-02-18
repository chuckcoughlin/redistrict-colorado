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
package org.geotools.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.FeatureShape;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayer;
import org.geotools.referencing.ReferencedEnvelope;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.Style;
import org.geotools.util.RendererUtilities;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureFilter;

/**
 * A renderer designed specifically for shapefiles. There is no label caching.
 * 
 * @author jeichar
 * @since 2.1.x
 *
 * @source $URL$
 */
public class ShapefileRenderer {
	private final static String CLSS = "ShapefileRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Graphics2D graphics = null;
    private List<RenderListener> renderListeners = new CopyOnWriteArrayList<RenderListener>();
    private MapContent content = null;
   
    /** The painter class we use to depict shapes onto the screen */
    private final StyledShapePainter painter = new StyledShapePainter();
   
    
    public ShapefileRenderer() {
    }

    /**
     * This is the paint method used to actually draw the map. We always go from geodesic coordinates
     * to screen coordinates.
     */
    public void paint( Graphics2D graphics, ReferencedEnvelope mapArea,Rectangle paintArea  ) {
        if (mapArea == null || paintArea == null) {
            LOGGER.info(String.format("%s.paint: paint or mapping area is null",CLSS));
            return;
        } 
        this.graphics = graphics;
        paint(mapArea,paintArea, null);
    }

    /**
     * adds a listener that responds to error events of feature rendered events.
     * 
     * @param listener the listener to add.
     * @see RenderListener
     */
    public void addRenderListener( RenderListener listener ) {
        renderListeners.add(listener);
    }

    /**
     * Removes a render listener.
     * 
     * @param listener the listener to remove.
     * @see RenderListener
     */
    public void removeRenderListener( RenderListener listener ) {
        renderListeners.remove(listener);
    }

    private void fireErrorEvent(Exception e) {
        if (renderListeners.size() > 0) {
            RenderListener listener;
            for (int i = 0; i < renderListeners.size(); i++) {
                try {
                    listener = renderListeners.get(i);
                    listener.errorOccurred(e);
                } 
                catch (RuntimeException ignore) {
                    LOGGER.fine("Provided RenderListener could not handle error message:" + ignore);
                    LOGGER.throwing(getClass().getName(), "fireErrorEvent", ignore);
                }
            }
        }
    }



    private void paint(ReferencedEnvelope mapExtent,Rectangle paintArea, FeatureFilter filter ) {
    	AffineTransform transform = RendererUtilities.worldToScreenTransform(mapExtent, paintArea);

        /*
         * If we are rendering to a component which has already set up some form of transformation
         * then we can concatenate our filter transformation to it. This is used for panning or zooming.
         */
        if (filter!=null) {
            AffineTransform atg = filter.getTransform();
            atg.concatenate(transform);
            transform = atg;
        }

        /*
         * Do not consider rotation, scale is simply the minimum of the horizontal and vertical scales.
         */
        double scale = RendererUtilities.calculateScale(mapExtent, paintArea.getWidth(), paintArea.getHeight());


        List<MapLayer> layers = content.layers();
        for( MapLayer layer:layers ) {
        	if (layer.isVisible()) {
        		try {
        			layer.getBounds();
        			Style style = layer.getStyle();
        			FeatureCollection collection = layer.getFeatures();
        			for( Feature feature:collection.getFeatures()) {
        				FeatureShape shape = new FeatureShape(feature,transform);
        				painter.paint(graphics, shape, style, scale);
        			}
        		} 
        		catch (Exception exception) {
        			Exception e = new Exception(String.format("%s.paint: Exception rendering layer %s",CLSS,layer.getTitle()), exception);
        			fireErrorEvent(e);
        		}
        	}
        }
    }

	public void setMapContent(MapContent mapContent) {
		this.content = mapContent;
	}
}