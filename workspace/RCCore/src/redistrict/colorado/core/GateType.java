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
	BOUNDARIES,
	DEMOGRAPHICS,
	AFFILIATIONS,
	NONE
	;

	/**
	 * @return gate types in a list.
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
