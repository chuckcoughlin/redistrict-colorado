/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * These are the choices for coloring the districts of a plan map
 */
public enum ColorizingOption
{
	AFFILIATION,
	DEMOGRAPHICS,
	NONE
	;

	/**
	 * @return view modes in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (ColorizingOption mode : ColorizingOption.values())
		{
			names.add(mode.name());
		}
		return names;
	}
}
