/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

/**
 * Compare plans based on the number of times district boundaries
 * cross county lines.
 */
public class CountyCrossingGate extends Gate {
	public CountyCrossingGate() {
		
	}
	public String getTitle() { return "County Line Crossings"; } 
}
