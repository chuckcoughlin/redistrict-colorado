/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * These are the choices for the type of display to show on the right-side pane
 */
public enum DisplayOption
{
	FEATURE_MAP,
	LAYER_CONFIGURATION,
	MODEL_DETAIL,
	MODEL_MAP,
	PLAN_CONFIGURATION,
	PLAN_COMPARISON,
	PLAN_METRICS,
	NONE
	;

	/**
	 * @return view modes in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (DisplayOption mode : DisplayOption.values())
		{
			names.add(mode.name());
		}
		return names;
	}
}
