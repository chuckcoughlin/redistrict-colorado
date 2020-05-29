/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/*
 * Population parameters for an ethnic group, 
 */
public class EthnicGroup {

	private final Ethnicity ethnicity;
	private double votes;         // Assume same as population
	private double voteDilution;
	private double voteMargin;    // Victory margin
	private double votesWasted;
	
	public EthnicGroup(Ethnicity eth)  {
		this.ethnicity = eth;
		this.votes = 0.;
		this.voteDilution = 0.;
		this.voteMargin = 0.;
		this.votesWasted = 0.;
	}
	
	public Ethnicity getEthnicity() { return this.ethnicity; }
	public double getVoteDilution() { return this.voteDilution; }
	public double getVoteMargin() { return this.voteMargin; }
	public double getVotes() { return this.votes; }
	public void incrementVotes(double count) { this.votes+=count;}
	public void incrementVoteMargin(double count) { this.voteMargin+=count;}
	public void incrementWastedVotes(double count) { this.votesWasted+=count;}
	public void setVoteDilution(double val) { this.voteDilution = val; }
}
