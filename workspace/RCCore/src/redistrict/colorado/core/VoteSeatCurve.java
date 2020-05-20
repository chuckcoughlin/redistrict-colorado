package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Seats-votes curve generator.
 * Calculated using uniform partisan swing. Apply overall fraction to changes at district level.
 * Translated from Python
 * See: https://github.com/jeffreyshen19/Seats-Votes-Curves/blob/master/generator/uniform_partisan_swing.py
 */

public class VoteSeatCurve {
	private final static String CLSS = "VoteSeatCurve";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static double INCREMENT = 0.01;  // X-Axis values
	private final List<PlanFeature> votingByDistrict;
	private final List<SeatVote> seatsVotesDem;
	private final List<SeatVote> seatsVotesRep;
	private double republicanSeatImbalance = 0;
	private double republicanVoteImbalance = 0;
	private final NormalDistribution normal;
	
	public VoteSeatCurve(List<PlanFeature>feats) {
		this.votingByDistrict = feats;
		this.seatsVotesDem = new ArrayList<>();
		this.seatsVotesRep = new ArrayList<>();
		this.normal = new NormalDistribution(0.,5.);  // 5% deviation.
	}
	
	// The seat-vote objects in these lists contain percentages.
	public List<SeatVote> getSeatVotesDemocratic() { return this.seatsVotesDem; }
	public List<SeatVote> getSeatVotesRepublican() { return this.seatsVotesRep; }
	// Metrics - In each case a positive results means that the plan favors republicans
	public double getSeatImbalance() { return republicanSeatImbalance; }
	public double getVoteImbalance() { return republicanVoteImbalance; }
	
	
	public void generate()  {  
		List<SeatVote> seatsVotesRep = new ArrayList<>();
		List<SeatVote> seatsVotesDem = new ArrayList<>();
		
		double totalVotes = 0.;
		double totalDVotes = 0.;
		double totalRVotes = 0.;
		double totalSeats = votingByDistrict.size();

		//  Read all district data
		for(PlanFeature feat:votingByDistrict) {
			totalVotes += feat.getDemocrat()+feat.getRepublican();
			totalDVotes += feat.getDemocrat();
			totalRVotes += feat.getRepublican();
		}

		// These are the overall proportions that we use to modify the individual districts.
		double repVoteShare = (double)(totalRVotes) / totalVotes;
		double demVoteShare = (double)(totalDVotes) / totalVotes;

	
		// Generate the Republican curve
		// The uniform partisan swing
		double swing = demVoteShare/repVoteShare;
		for(double frac=0;frac<=1.0;frac+=INCREMENT) {
			//  Iterate over districts
			double repSeats = 0;
			double totalRepVotes = 0;
			for(PlanFeature feat:votingByDistrict) {
				double repVotes = feat.getRepublican();
				double demVotes = feat.getDemocrat();
				double variance = normal.sample()*repVotes;
				repVotes += variance;
				repVotes = repVotes * frac;
				demVotes = totalVotes - repVotes + swing*variance;
				if( repVotes>demVotes ) repSeats++;
				totalRepVotes += repVotes;
 			}
			seatsVotesRep.add(new SeatVote(repSeats/totalSeats,totalRepVotes/totalVotes));
		}
		
		// Repeat for the Democratic curve
		// The uniform partisan swing (inverse of previous)
		swing = repVoteShare/demVoteShare;
		for(double frac=0;frac<=1.0;frac+=INCREMENT) {
			//  Iterate over districts
			double demSeats = 0;
			double totalDemVotes = 0;
			for(PlanFeature feat:votingByDistrict) {
				double repVotes = feat.getRepublican();
				double demVotes = feat.getDemocrat();
				double variance = normal.sample()*demVotes;
				demVotes += variance;
				demVotes = demVotes * frac;
				repVotes = totalVotes - demVotes + swing*variance;
				if( demVotes>repVotes ) demSeats++;
				totalDemVotes += demVotes;
 			}
			seatsVotesDem.add(new SeatVote(demSeats/totalSeats,totalDemVotes/totalVotes));
		}
			
	}
}
