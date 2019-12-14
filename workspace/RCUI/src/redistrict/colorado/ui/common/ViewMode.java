/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.common;

/**
 * These are the modes for the UI. The left side of the split pane reflects this.
 */
public enum ViewMode
{
	PLAN,
	LAYER,
	REGION
	;


	/**
	 * @return  a comma-separated list of all mode types in a single String.
	 */
	public static String names()
	{
		StringBuffer names = new StringBuffer();
		for (ViewMode type : ViewMode.values())
		{
			names.append(type.name()+", ");
		}
		return names.substring(0, names.length()-2);
	}
}
