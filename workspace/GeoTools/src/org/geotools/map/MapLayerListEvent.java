/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.EventObject;

/**
 * Event object used to report changes in the list of layers managed by a MapContent.
 *
 * @author wolf
 */
public class MapLayerListEvent extends EventObject {
	private static final long serialVersionUID = 742769616357947429L;
	private final MapLayer layer;

    /** Create a new instance of MapLayerListEvent for a layer edit action */
    public MapLayerListEvent(MapContent source, MapLayer layer) {
        super(source);
        this.layer = layer;
    }
    
    /** Creates a new instance of MapLayerListEvent for a layer list modification */
    public MapLayerListEvent(MapContent source) {
        super(source);
        this.layer = null;
    }

    /**
     * Return the layer involved in a layer edit.
     * @return the modified layer
     */
    public MapLayer getLayer() { return this.layer; }
}
