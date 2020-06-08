/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.HashMap;
import java.util.Map;

/*
 * Hold values for use in table cells. By convention, the hash map keys
 * match table column names.
 */
public class NameValue {
	private final String name;
	private final Map<String,Object> values;
	public static NameValue EMPTY = new NameValue("");
	
	public NameValue(String name) {
		this.name = name;
		this.values = new HashMap<>();
	}
	
	public String getName() { return this.name; }
	public Object getValue(String key) { return this.values.get(key); }
	public void setValue(String key,Object value) { this.values.put(key, value); }
}
