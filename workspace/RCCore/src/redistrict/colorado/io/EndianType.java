/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.io;

/**
 * "endian" refers to byte order.
 */
public enum EndianType
{
	BIG,
	LITTLE
	;


	/**
	 * @return  a comma-separated list of all endian states in a single String.
	 */
	public static String names()
	{
		StringBuffer names = new StringBuffer();
		for (EndianType type : EndianType.values())
		{
			names.append(type.name()+", ");
		}
		return names.substring(0, names.length()-2);
	}
}
