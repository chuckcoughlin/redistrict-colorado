/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 * 
 * Inspired by GMapsFX by Rob Terpilowski. This version is much 
 * simplified and streamlined.
 */
package redistrict.colorado.gmaps;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.layout.AnchorPane;
import redistrict.colorado.gmaps.javascript.object.GoogleMap;
import redistrict.colorado.gmaps.javascript.object.MapOptions;

/**
 *
 * @author Rob Terpilowski
 */
public class GoogleMapView extends AnchorPane {
	private static final String CLSS = "GoogleMapView";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    public static final String GOOGLE_MAPS_API_LINK = "https://maps.googleapis.com/maps/api/js?v=3.exp";
   
    protected boolean disableDoubleClick = false;
    protected final List<MapComponentInitializedListener> mapInitializedListeners = new ArrayList<>();


    /**
     * Creates a new map view using the API key.
     *
     * @param key Google Maps API key or null
     */
    public GoogleMapView(String key) {
        
    }
    
    public GoogleMap createMap(MapOptions options) {
    	GoogleMap map = new GoogleMap();
    	return map;
    }
    public void addMapInitializedListener(MapComponentInitializedListener listener) {
        synchronized (mapInitializedListeners) {
            mapInitializedListeners.add(listener);
        }
    }
    protected void fireMapInitializedListeners() {
        synchronized (mapInitializedListeners) {
            for (MapComponentInitializedListener listener : mapInitializedListeners) {
                listener.mapInitialized();
            }
        }
    }
    public void removeMacpInitializedListener(MapComponentInitializedListener listener) {
        synchronized (mapInitializedListeners) {
            mapInitializedListeners.remove(listener);
        }
    }
    public void setDisableDoubleClick(boolean disableDoubleClick) {
        this.disableDoubleClick = disableDoubleClick;
    }

}
