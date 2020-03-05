/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
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

import org.locationtech.jts.geom.Envelope;
import org.openjump.feature.FeatureCollection;

/**
 * A MapLayer is responsible for rendering vector information provided by a FeatureCollection.
 * Layers usually represent a single dataset, and are arranged into a z-order by a Map for display.
 */
public class MapLayer {
	private final static String CLSS = "MapLayer";
	protected static Logger LOGGER = Logger.getLogger(CLSS);
	
	private final FeatureCollection collection;
	private boolean selected = true;
    private String title;
    private boolean visible = true;   //Flag to mark the layer as visible when being rendered
    
    /**
     * Creates a new instance of MapLayer
     *
     * @param features the collection of features for this layer
     * @param style the style used to represent this layer
     */
    public MapLayer(FeatureCollection features) {
        this(features,"");
    }

    public MapLayer(FeatureCollection features, String title) {
        this.collection = features;
        this.title = title;
    }

    public Envelope getBounds() {
    	Envelope bounds = null;
    	Envelope envelope;
    	envelope = collection.getEnvelope();
    	
    	if (envelope != null) {
    		bounds = new Envelope(
    					envelope.getMinX(),
    					envelope.getMaxX(),
    					envelope.getMinY(),
    					envelope.getMaxY());
    	}
    	return bounds;

    }
    
    public FeatureCollection getFeatures() { return this.collection; }

    /**
     * Get the title of this layer. If title has not been defined then an empty string is returned.
     *
     * @return The title of this layer.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of this layer. A {@link LayerEvent} is fired if the new title is different from
     * the previous one.
     *
     * @param title The title of this layer.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Determines whether this layer is visible or hidden.
     *
     * @return {@code true} if the layer is visible, or {@code false} if hidden
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether the layer is to be shown or hidden when rendering. {@link LayerEvent} is fired
     * if the visibility changed.
     *
     * @param visible {@code true} to show the layer; {@code false} to hide it
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
        }
    }
    /**
     * Determines whether this layer is selected. Selection status can be used by clients such as
     * {@code JMapPane} for selective processing of layers.
     *
     * @return {@code true} if the layer is selected, or {@code false} otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets layer selection status. This can be used by clients such as {@code JMapPane} for
     * selective processing of layers.
     *
     * @param selected new selection status.
     */
    public void setSelected(boolean selected) {
        if (selected != this.selected) {
            this.selected = selected;
        }
    }
  
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName());
        buf.append("[");
        if (title != null && title.length() != 0) {
            buf.append(getTitle());
        }
        if (visible) {
            buf.append(", VISIBLE");
        } 
        else {
            buf.append(", HIDDEN");
        }
        buf.append("]");
        return buf.toString();
    }
}
