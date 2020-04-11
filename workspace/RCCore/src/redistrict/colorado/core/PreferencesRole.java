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
 * These values categorize entries in the Preferences table.
 */
public enum PreferencesRole
{
	ID,
	KEY,
	MINIMUM,
	MAXIMUM,
	WEIGHT
	;

	/**
	 * @return preference types in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (PreferencesRole type : PreferencesRole.values())
		{
			names.add(type.name());
		}
		return names;
	}
}
