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
 * "Well-known" attribute aliases
 */
public enum StandardAttributes
{
	ID,
	BLACK,
	DEMOCRAT,
	GEOMETRY,
	HISPANIC,
	POPULATION,
	REPUBLICAN,
	WHITE,
	NONE
	;

	/**
	 * @return attribute aliases in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (StandardAttributes role : StandardAttributes.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
