/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.HashMap;
import java.util.Map;

/*
 * Hold values for use in table cells. By convention, the hash map keys
 * match table column names.
 */
public class SeatVote {
	private final double seats;
	private final double votes;

	
	public SeatVote(double seats,double votes) {
		this.seats = seats;
		this.votes = votes;
	}
	
	public double getSeats() { return this.seats; }
	public double getVotes() { return this.votes; }
}
