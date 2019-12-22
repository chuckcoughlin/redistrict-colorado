/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Specify allowable types of map overlays.
 */
public enum LayerType
{
	GOOGLE_MAP,
	SHAPEFILE
	;

	/**
	 * @return layer types in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (LayerType type : LayerType.values())
		{
			names.add(type.name());
		}
		return names;
	}
}
