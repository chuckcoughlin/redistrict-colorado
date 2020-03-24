/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.GateType;

/**
 * The gate cache is a Singleton that holds references to all of
 * the currently instantiated gate instances. Gates are keyed by type.
 */
public class GateCache {
	private final static String CLSS = "GateCache";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	private static GateCache instance = null;
	private final Map<GateType,Gate> map;

	/**
	 * Constructor is private per Singleton pattern.
	 * Create all gates on the first attempt to access.
	 */
	private GateCache() {
		this.map = new HashMap<>();
		map.put(GateType.POPULATION_EQUALITY, new PopulationEqualityGate());
		
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static GateCache getInstance() {
		if( instance==null) {
			synchronized(GateCache.class) {
				instance = new GateCache();
			}
		}
		return instance;
	}
	/**
	 * When we get the gate from the cache.
	 */
	public Gate getGate(GateType type) {
		Gate gate = map.get(type);
		return gate;
	}
	
	
	/**
	 * The order of the list is the order types are 
	 * defined in the enumeration.
	 */
	public List<Gate> getGates() {
		List<Gate> gates = new ArrayList<>();
		for(GateType type:GateType.values()) {
			gates.add(map.get(type));
		}
		return gates;
	}
}
