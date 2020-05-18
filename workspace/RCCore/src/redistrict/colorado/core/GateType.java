/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Specify the kinds of gates that we consider
 */
public enum GateType
{
	COMPACTNESS,
	COMPETIVENESS,
	COMPOSITE,
	COUNTY_CROSSINGS,
	POPULATION_BALANCE,
	PARTISAN_ASYMMETRY,
	PROPORTIONALITY,
	VOTING_POWER
	;

	/**
	 * @return the basic gate types in a list. "Composite"
	 *         relies on the others and is therefore excluded.
	 */
	public static List<GateType> basicTypes() {
		List<GateType> types = new ArrayList<>();
		for (GateType type : GateType.values()){	
			if(!type.equals(COMPOSITE) ) types.add(type);
		}
		return types;
	}
	
	/**
	 * @return gate type names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (GateType type : GateType.values())
		{
			names.add(type.name());
		}
		return names;
	}
}
