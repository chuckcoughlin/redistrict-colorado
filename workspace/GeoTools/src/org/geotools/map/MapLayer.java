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
package org.geotools.map;

import java.util.logging.Logger;

import org.geotools.referencing.ReferencedEnvelope;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.Style;
import org.locationtech.jts.geom.Envelope;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureSchema;

/**
 * MapLayer responsible for rendering vector information provided by a FeatureCollection.
 * Layers usually represent a single dataset, and arranged into a z-order by a Map for display.
 *
 * Please note that a StyleLayerDescriptor (defined by SLD) document is usually used to describe the
 * rendering requirements for an entire Map; while a Style (defined by SE) is focused on a single
 * layer of content
 *
 * @since 2.7
 * @version 8.0
 */
public class MapLayer {
	private final static String CLSS = "MapLayer";
	protected static Logger LOGGER = Logger.getLogger(CLSS);
	
	private final FeatureCollection collection;
	private boolean selected = true;
    private Style2D style;
    private String title;
    private boolean visible = true;   //Flag to mark the layer as visible when being rendered
    
    /**
     * Creates a new instance of FeatureLayer
     *
     * @param features the collection of features for this layer
     * @param style the style used to represent this layer
     */
    public MapLayer(FeatureCollection features, Style2D style) {
        this(features,style,"");
    }

    public MapLayer(FeatureCollection features, Style2D style, String title) {
        this.collection = features;
        this.style = style;
        this.title = title;
    }

    public void dispose() {
        style = null;
    }

    public ReferencedEnvelope getBounds() {
    	ReferencedEnvelope bounds = null;
    	Envelope envelope;
    	envelope = collection.getEnvelope();
    	
    	if (envelope != null) {
    		FeatureSchema schema = collection.getFeatureSchema();
    		CoordinateSystem coordsys = schema.getCoordinateSystem();

    		if ( coordsys != null) {
    			bounds = new ReferencedEnvelope(
    					envelope.getMinX(),
    					envelope.getMaxX(),
    					envelope.getMinY(),
    					envelope.getMaxY(),
    					coordsys);
    		}
    		return bounds;
    	}
    	return null; // unknown
    }
    
    public FeatureCollection getFeatures() { return this.collection; }
    /**
     * Get the style for this layer.
     */
    public Style2D getStyle() {return style; }

    /**
     * Sets the style for this layer.
     * @param style The new style
     */
    public void setStyle(Style2D style) {
        if (style == null) {
            throw new NullPointerException("Style is required");
        }
        this.style = style;
    }
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
