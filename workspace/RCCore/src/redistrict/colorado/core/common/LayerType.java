/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core.common;

/**
 * Specify allowable types of map overlays.
 */
public enum LayerType
{
	GOOGLE_MAP,
	SHAPEFILE
	;


	/**
	 * @return  a comma-separated list of all layer types in a single String.
	 */
	public static String names()
	{
		StringBuffer names = new StringBuffer();
		for (LayerType type : LayerType.values())
		{
			names.append(type.name()+", ");
		}
		return names.substring(0, names.length()-2);
	}
}
