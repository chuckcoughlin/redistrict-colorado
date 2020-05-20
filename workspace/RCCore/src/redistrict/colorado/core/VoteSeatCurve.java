package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Seats-votes curve generator.
 * Calculated using uniform partisan swing
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
	private final NormalDistribution normal;
	
	public VoteSeatCurve(List<PlanFeature>feats) {
		this.votingByDistrict = feats;
		this.seatsVotesDem = new ArrayList<>();
		this.seatsVotesRep = new ArrayList<>();
		this.normal = new NormalDistribution(0.,5.);  // 5% deviation.
	}
	
	public List<SeatVote> getSeatVotesDemocratic() { return this.seatsVotesDem; }
	public List<SeatVote> getSeatVotesRepublican() { return this.seatsVotesRep; }
	
	
	public void generate()  {  
		List<SeatVote> seatsVotesRep = new ArrayList<>();
		seatsVotesRep.add(new SeatVote(0.,0.));
		List<SeatVote> seatsVotesDem = new ArrayList<>();
		seatsVotesDem.add(new SeatVote(0,0));
		
		double totalVotes = 0.;
		double totalDVotes = 0.;
		double totalRVotes = 0.;

		//  Read all district data
		for(PlanFeature feat:votingByDistrict) {
			totalVotes += feat.getDemocrat()+feat.getRepublican();
			rVotest
		}

		// These are the proportions that we use to modify the individual districts.
		double repVoteShare = (double)(totalRepVotes) / totalVotes;
		double demVoteShare = (double)(totalDemVotes) / totalVotes;

	
		// Generate the Republican curve
		for(double frac=0;frac<=1.0;frac+=INCREMENT) {
			//  Read all district data
			double repSeats = 0;
			double totalRepVotes = 0;
			for(PlanFeature feat:votingByDistrict) {
				double repVotes = feat.getRepublican();
				double demVotes = feat.getDemocrat();
				double sum += repVotes + demVotes;
				double variance = normal.sample()*repVotes;
				repVotes += variance;
				repVotes = repVotes * frac;
				demVotes = totalVotes - repVotes;
				if( repVotes>demVotes ) repSeats++;
				totalRepVotes += repVotes;
 			}
			seatsVotesRep.add(new SeatVote(repSeats,totalRepVotes/totalVotes));
		}
			
	}
}
