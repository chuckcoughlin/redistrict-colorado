/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

/**
 * Compare plans based on the populations of each district are within 
 * 1% of each other.
 */
public class VoteEffeciencyGate extends Gate {
	public VoteEffeciencyGate() {
		
	}
	public String getTitle() { return "Vote Efficiency"; } 
}