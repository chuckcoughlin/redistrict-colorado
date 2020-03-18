/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;

/**
 * The dataset cache is a Singleton that holds references to all of
 * the currently instantiated dataset models. Models are keyed by Id.
 */
public class DatasetCache {
	private final static String CLSS = "DatasetCache";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	private static DatasetCache instance = null;
	private final Map<Long,DatasetModel> map;

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private DatasetCache() {
		this.map = new HashMap<>();
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static DatasetCache getInstance() {
		if( instance==null) {
			synchronized(DatasetCache.class) {
				instance = new DatasetCache();
			}
		}
		return instance;
	}
	
	public void addDataset(DatasetModel model) {map.put(model.getId(),model); }
	/**
	 * When we get the dataset from the cache, make sure that the features are populated.
	 * This amounts to a lazy initialization.
	 */
	public DatasetModel getDataset(long id) {
		DatasetModel model = map.get(id);
		return model;
	}
	
	/**
	 * Do a linear for dataset by name.
	 */
	public DatasetModel getDataset(String name) {
		for(DatasetModel model:map.values() ) {
			if(model.getName().equalsIgnoreCase(name)) return model;
		}
		return null;
	}
	
	public List<DatasetModel> getDatasetsInRole(DatasetRole role) {
		List<DatasetModel> datasets = new ArrayList<>();
		for(DatasetModel model:map.values()) {
			if( model.getRole().equals(role)) {
				datasets.add(model);
			}
		}
		return datasets;
	}

	public void removeDataset(DatasetModel model) {map.remove(model.getId()); }
	public void removeDataset(long id) {map.remove(id); }
}

