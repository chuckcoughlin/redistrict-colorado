/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/**
 * Specify allowable uses of map overlays.
 */
public enum LayerRole
{
	BOUNDARIES,
	DEMOGRAPHICS,
	AFFILIATIONS
	;


	/**
	 * @return  a comma-separated list of all layer roles in a single String.
	 */
	public static String names()
	{
		StringBuffer names = new StringBuffer();
		for (LayerRole type : LayerRole.values())
		{
			names.append(type.name()+", ");
		}
		return names.substring(0, names.length()-2);
	}
}
