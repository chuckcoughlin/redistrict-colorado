/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/*
 * Hold population parameters for an ethnic group, in a 
 * particular district. 
 */
public class VotingPower {
	private static double normalizationFactor = 1.0; // By default no normalization
	private double voteCount;     // Assume same as population
	private double voteMargin;    // Victory margin
	
	public VotingPower(double count,double margin)  {
		this.voteCount = count;
		this.voteMargin = margin;
	}
	
	public VotingPower()  {
		this.voteCount = 0.;
		this.voteMargin = 0.;
	}
	// Presumably we normalize by total population/total vote margin
	public static void setNormalizationFactor(double fact) { normalizationFactor = fact; }
	public double getVoteCount() { return this.voteCount; }
	public double getVoteMargin() { return this.voteMargin; }
	public double getNormalizedVotePower() { return (this.voteCount/this.voteMargin)/normalizationFactor; }
	public void setVoteCount(double count) { this.voteCount=count;}
	public void setVoteMargin(double margin) { this.voteMargin=margin;}
}