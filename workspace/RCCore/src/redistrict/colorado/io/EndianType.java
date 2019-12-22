/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.io;

import java.util.ArrayList;
import java.util.List;

/**
 * "endian" refers to byte order.
 */
public enum EndianType
{
	BIG,
	LITTLE
	;

	/**
	 * @return endian types in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (EndianType type : EndianType.values())
		{
			names.add(type.name());
		}
		return names;
	}
}
