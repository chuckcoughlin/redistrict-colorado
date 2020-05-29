/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;

/**
 * These are the names of the ethnic (or other) groups considered. 
 * The limitation is what is included in the census.
 */
public enum Ethnicity
{
	BLACK,
	HISPANIC,
	WHITE
	;
  
	/**
	 * @return the basic asymmetry types in a list.
	 */
	public static List<Ethnicity> getEthnicities() {
		List<Ethnicity> types = new ArrayList<>();
		for (Ethnicity type : Ethnicity.values()){	
			types.add(type);
		}
		return types;
	}
	
	/**
	 * @return metric type names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (Ethnicity type : Ethnicity.values()) {
			names.add(type.name());
		}
		return names;
	}
	
	public static int count() { return Ethnicity.values().length; }
}
