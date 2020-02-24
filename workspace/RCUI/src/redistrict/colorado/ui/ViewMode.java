/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * These are the modes for the UI. The left side of the split pane reflects this.
 */
public enum ViewMode
{
	PLAN,
	LAYER,
	DISTRICT,
	UNSELECTED
	;

	/**
	 * @return view modes in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (ViewMode mode : ViewMode.values())
		{
			names.add(mode.name());
		}
		return names;
	}
}
