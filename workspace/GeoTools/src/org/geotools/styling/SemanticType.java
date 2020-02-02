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
 * Identifies the more general "type" of geometry that this style is meant to act upon. In the
 * current OGC SE specifications, this is an experimental element.
 */
public enum SemanticType
{
	POINT,
	LINE,
	POLYGON,
	TEXT,
	RASTER,
	ANY
	;

	/**
	 * @return type names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (SemanticType role : SemanticType.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
