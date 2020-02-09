/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.openjump.coordsys;

import java.util.ArrayList;
import java.util.List;

/**
 * The direction of positive increments in the coordinate value for a coordinate system axis. This
 * direction is exact in some cases, and is approximate in other cases.
 *
 * <p>Some coordinate systems use non-standard orientations. For example, the first axis in South
 * African grids usually points West, instead of East. This information is obviously relevant for
 * algorithms converting South African grid coordinates into Lat/Long.
 */
public enum AxisDirection
{
	OTHER,
	NORTH,
	NORTH_NORTH_EAST,
	NORTH_EAST,
	EAST_NORTH_EAST,
	EAST,
	EAST_SOUTH_EAST,
	SOUTH_EAST,
	SOUTH_SOUTH_EAST,
	SOUTH,
	SOUTH_SOUTH_WEST,
	SOUTH_WEST,
	WEST_SOUTH_WEST,
	WEST,
	WEST_NORTH_WEST,
	NORTH_WEST,
	NORTH_NORTH_WEST,
	DOWN,
	UP,
	GEOCENTRIC_X,
	GEOCENTRIC_Y,
	GEOCENTRIC_Z,
	PAST,
	FUTURE,
	COLUMN_NEGATIVE,
	COLUMN_POSITIVE,
	ROW_NEGATIVE,
	ROW_POSITIVE,
	DISPLAY_LEFT,
	DISPLAY_RIGHT,
	DISPLAY_DOWN,
	DISPLAY_UP
	;

	/**
	 * @return direction names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (AxisDirection role : AxisDirection.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
