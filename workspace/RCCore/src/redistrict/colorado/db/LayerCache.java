/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import redistrict.colorado.core.LayerModel;

/**
 * The layer cache is a Singleton that holds references to all of
 * the currently instantiated layer models. Models are keyed by Id.
 */
public class LayerCache {
	private final static String CLSS = "LayerCache";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	private static LayerCache instance = null;
	private final Map<Long,LayerModel> map;

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private LayerCache() {
		this.map = new HashMap<>();
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static LayerCache getInstance() {
		if( instance==null) {
			synchronized(LayerCache.class) {
				instance = new LayerCache();
			}
		}
		return instance;
	}
	
	public void addLayer(LayerModel model) {map.put(model.getId(),model); }
	/**
	 * When we get the layer from the cache, make sure that the features are populated.
	 * This amounts to a lazy initialization.
	 */
	public LayerModel getLayer(long id) {
		LayerModel model = map.get(id);
		return model;
	}

	public void removeLayer(LayerModel model) {map.remove(model.getId()); }
	public void removeLayer(long id) {map.remove(id); }
}
