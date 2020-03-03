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
public class VotingPowerImbalanceGate extends Gate {
	public VotingPowerImbalanceGate() {
		
	}
	public String getTitle() { return "Voting Power Imbalance"; } 
}
