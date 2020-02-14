/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2011, Open Source Geospatial Foundation (OSGeo)
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

import java.util.EventListener;

/**
 * Listens for {@linkplain MapLayerListEvent} events.
 *
 * @author wolf
 */
public interface MapLayerListListener extends EventListener {
    /**
     * Triggered when something in a layer changed (data, style, title)
     *
     * @param event encapsulating the event information
     */
    public void layerModified(MapLayerListEvent event);

    /**
     * Triggered when one or more layers changes position in the layer list,
     * or a layer is added or removed. It is assumed that the recipient
     * will query the source for the new order.
     *
     * @param event encapsulating the event information
     */
    public void layerListModified(MapLayerListEvent event);

}
