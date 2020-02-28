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
 * Specify allowable uses of map overlays.
 */
public enum LayerRole
{
	BOUNDARIES,
	DEMOGRAPHICS,
	AFFILIATIONS,
	PRIMARY
	;

	/**
	 * @return layer roles in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (LayerRole role : LayerRole.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
