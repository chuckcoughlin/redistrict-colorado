/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/*
 * Hold values for use in seat-votes curves. 
 * By convention the values are both percentages.
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
