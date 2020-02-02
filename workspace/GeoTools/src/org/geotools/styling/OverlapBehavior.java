/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.styling;

import java.util.ArrayList;
import java.util.List;

/**
 * Specify how to behave when multiple raster images in
 * a layer overlap each other.
 */
public enum OverlapBehavior
{
	AVERAGE,
	RANDOM,
	LATEST_ON_TOP,
	EARLIEST_ON_TOP,
	UNSPECIFIED
	;

	/**
	 * @return behavior names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (OverlapBehavior role : OverlapBehavior.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
