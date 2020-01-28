/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.styling;

import java.util.ArrayList;
import java.util.List;

/**
 * Specify allowable contrast methods.
 */
public enum ContrastMethod
{
	NORMALIZE,
	HISTOGRAM,
	LOGARITHMIC,
	EXPONENTIAL,
	NONE
	;

	/**
	 * @return method names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (ContrastMethod role : ContrastMethod.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
