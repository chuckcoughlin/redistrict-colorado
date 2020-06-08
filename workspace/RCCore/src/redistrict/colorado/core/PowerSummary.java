/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/*
 * Hold totals across all the ethnic groups considered for voting power
 * calculations.
 */
public class PowerSummary {
	private double totalPopulation;
	private double totalSeats;
	private double totalMargin;
	private double totalVotes;
	
	public PowerSummary()  {
		this.totalPopulation = 0.;
		this.totalSeats = 0.;
		this.totalMargin = 0.;
		this.totalVotes = 0;
	}
	
	public double getAverageMargin() { return this.totalMargin/this.totalMargin; }
	public double getTotalMargin() { return this.totalMargin; }
	public double getTotalPopulation() { return this.totalPopulation; }
	public double getTotalSeats() { return this.totalSeats; }         // = ndistricts
	public double getTotalVotes() { return this.totalVotes; }
	
	public void incrementPopulation(double count) { this.totalPopulation += count; }
	public void incrementSeats(double count) { this.totalSeats += count; }
	public void incrementVotes(double count) { this.totalVotes += count; }
	public void incrementVoteMargin(double count) { this.totalMargin += count; } 
}
