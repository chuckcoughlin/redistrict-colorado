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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.Style;
import org.locationtech.jts.geom.Envelope;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureSchema;

/**
 * Layer responsible for rendering vector information provided by a FeatureSource.
 *
 * <p>The FeatureLayer combines:
 *
 * <ul>
 *   <li>data: FeatureSource
 *   <li>style: Style
 * </ul>
 *
 * Please note that a StyleLayerDescriptor (defined by SLD) document is usually used to describe the
 * rendering requirements for an entire Map; while a Style (defined by SE) is focused on a single
 * layer of content
 *
 * @since 2.7
 * @version 8.0
 */
public class FeatureLayer extends StyleLayer {
	private final FeatureCollection collection;

    /**
     * Creates a new instance of FeatureLayer
     *
     * @param features the collection of features for this layer
     * @param style the style used to represent this layer
     */
    public FeatureLayer(FeatureCollection features, Style style) {
        super(style);
        this.collection = features;
        this.style = style;
    }

    public FeatureLayer(FeatureCollection features, Style style, String title) {
        super(style, title);
        this.collection = features;
    }

    @Override
    public void dispose() {
        preDispose();
        style = null;
        super.dispose();
    }

    @Override
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
}
