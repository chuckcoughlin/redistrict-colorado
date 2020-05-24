package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Seats-votes curve generator.
 * Calculated using uniform partisan swing. Apply overall fraction to changes at district level.
 * NOOTE: The reference used a randomized "smoothing" of the curve. We were not able to get this to work.
 * Translated from Python
 * See: https://github.com/jeffreyshen19/Seats-Votes-Curves/blob/master/generator/uniform_partisan_swing.py
 */

public class VoteSeatCurve {
	private final static String CLSS = "VoteSeatCurve";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static double INCREMENT = 0.01;  // X-Axis values
	private final List<PlanFeature> votesByDistrict;
	private final List<SeatVote> seatsVotesDem;
	private final List<SeatVote> seatsVotesRep;
	private final NormalDistribution normal;
	private double totalVotes;
	private final int ndistricts;
	private final double totalSeats;
	
	public VoteSeatCurve(List<PlanFeature>feats) {
		this.votesByDistrict = feats;
		this.ndistricts = votesByDistrict.size();
		this.seatsVotesDem = new ArrayList<>();
		this.seatsVotesRep = new ArrayList<>();
		this.normal = new NormalDistribution(0.,.05);  // 5% deviation.
		this.totalVotes = 0.;
		this.totalSeats = ndistricts;
	}
	
	// The seat-vote objects in these lists contain percentages.
	public List<SeatVote> getSeatVotesDemocratic() { return this.seatsVotesDem; }
	public List<SeatVote> getSeatVotesRepublican() { return this.seatsVotesRep; }
	// Metrics - In each case a positive results means that the plan favors republicans
	// No seat-votes lists contain cumulative values
	public double getSeatImbalance() { 
		// Note the vote when the seats exceed 50%
		for( SeatVote sv: seatsVotesRep) {
			if( sv.getVotes()>0.5 ) return sv.getSeats();
		}
		return 1.0; 
	}
	public double getVoteImbalance() { 
		// Note the vote when the seats exceed 50%
		for( SeatVote sv: seatsVotesRep) {
			if( sv.getSeats()>0.5 ) return sv.getVotes();
		}
		return 1.0;
	}
	public double getTotalVotes() { return this.totalVotes; }
	
	
	public void generate()  {  
		seatsVotesRep.clear();
		seatsVotesDem.clear();
		totalVotes = 0.;
		
		double totalDVotes = 0.;
		double totalRVotes = 0.;

		//  Read all district data
		for(PlanFeature feat:votesByDistrict) {
			totalVotes += feat.getDemocrat()+feat.getRepublican();
			totalDVotes += feat.getDemocrat();
			totalRVotes += feat.getRepublican();
		}

		// These are the overall proportions that we use to modify the individual districts.
		double repVoteShare = (double)(totalRVotes) / totalVotes;
		double demVoteShare = (double)(totalDVotes) / totalVotes;

	
		// Generate the Republican curve
		// The uniform partisan swing
		double swing = repVoteShare - demVoteShare;
		for(double frac=0;frac<=1.0;frac+=INCREMENT) {
			// Arrays to handle overflow situations
			boolean[] districtOverflowRep = new boolean[ndistricts];
			boolean[] districtOverflowDem = new boolean[ndistricts];
			Arrays.fill(districtOverflowDem, false);
			Arrays.fill(districtOverflowRep, false);
			int excessDem = 0;  // Seats
			int excessRep = 0;
			double[] updatedVotesDem = new double[ndistricts];
			double[] updatedVotesRep = new double[ndistricts];
			Arrays.fill(updatedVotesDem, 0.);
			Arrays.fill(updatedVotesRep, 0.);
			
			//  Iterate over districts
			double repSeats = 0;
			double demSeats = 0;
			double totalRepVotes = 0;
			int idistrict = 0;
			for(PlanFeature feat:votesByDistrict) {
				double repVotes = feat.getRepublican();
				double demVotes = feat.getDemocrat();
				double total = repVotes + demVotes;
				double variance = Math.abs(normal.sample()*repVotes);
				variance = 0.;     // Ignore any random "smoothing"
				updatedVotesRep[idistrict] = repVotes*frac + variance;
				updatedVotesDem[idistrict] = total - updatedVotesRep[idistrict] + swing*total;
				
				if( updatedVotesRep[idistrict] > total ) {
					districtOverflowRep[idistrict] = true;
					excessRep += 1;
				}
				if( updatedVotesDem[idistrict] < 0. ) {
					districtOverflowDem[idistrict] = true;
					excessDem += 1;
				}
				idistrict++;
 			}
			// Distribute any excess votes to other districts
			idistrict = 0;
			totalRepVotes = 0;
			for(PlanFeature feat:votesByDistrict) {
				if( !districtOverflowRep[idistrict]) {
					updatedVotesRep[idistrict] += INCREMENT*totalVotes*excessRep/(totalSeats - excessRep);
				}
				if( !districtOverflowDem[idistrict]) {
					updatedVotesDem[idistrict] -= INCREMENT*totalVotes*excessDem/(totalSeats - excessDem);
				}
				
				if(updatedVotesRep[idistrict]>=updatedVotesDem[idistrict]) repSeats+=1;
				if(updatedVotesDem[idistrict]>=updatedVotesRep[idistrict]) demSeats+=1;
				totalRepVotes += updatedVotesRep[idistrict];
				idistrict++;
			}
			seatsVotesRep.add(new SeatVote(repSeats/totalSeats,totalRepVotes/totalVotes));
			seatsVotesDem.add(new SeatVote(demSeats/totalSeats,(totalVotes-(totalRepVotes+swing))/totalVotes));
		}
		
		// Repeat for the Democratic curve
		// The uniform partisan swing (inverse of previous)
		for(double frac=0;frac<=1.0;frac+=INCREMENT) {
			// Arrays to handle overflow situations
			boolean[] districtOverflowRep = new boolean[ndistricts];
			boolean[] districtOverflowDem = new boolean[ndistricts];
			Arrays.fill(districtOverflowDem, false);
			Arrays.fill(districtOverflowRep, false);
			int excessDem = 0;  // Seats
			int excessRep = 0;
			double[] updatedVotesDem = new double[ndistricts];
			double[] updatedVotesRep = new double[ndistricts];
			Arrays.fill(updatedVotesDem, 0.);
			Arrays.fill(updatedVotesRep, 0.);
			//  Iterate over districts
			double repSeats = 0;
			double demSeats = 0;
			double totalDemVotes = 0;
			int idistrict = 0;
			for(PlanFeature feat:votesByDistrict) {
				double repVotes = feat.getRepublican();
				double demVotes = feat.getDemocrat();
				double total = repVotes + demVotes;
				double variance = normal.sample()*demVotes;
				variance = 0.;     // Ignore any random "smoothing"
				updatedVotesDem[idistrict] = demVotes*frac + variance;
				updatedVotesRep[idistrict] = total - updatedVotesDem[idistrict] - swing*total;
						
				if( updatedVotesDem[idistrict] > total ) {
					districtOverflowDem[idistrict] = true;
					excessDem += 1;
				}
				if( updatedVotesRep[idistrict] < 0. ) {
					districtOverflowRep[idistrict] = true;
					excessRep += 1;
				}
				idistrict++;
		 	}
			// Distribute any excess votes to other districts
			idistrict = 0;
			for(PlanFeature feat:votesByDistrict) {
				if( !districtOverflowDem[idistrict]) {
					updatedVotesDem[idistrict] += INCREMENT*totalVotes*excessDem/(totalSeats - excessDem);
				}
				if( !districtOverflowDem[idistrict]) {
					updatedVotesRep[idistrict] -= INCREMENT*totalVotes*excessRep/(totalSeats - excessRep);
				}
						
				if(updatedVotesRep[idistrict]>=updatedVotesDem[idistrict]) repSeats+=1;
				if(updatedVotesDem[idistrict]>=updatedVotesRep[idistrict]) demSeats+=1;
				totalDemVotes += updatedVotesDem[idistrict];
				idistrict++;
			}
			seatsVotesDem.add(new SeatVote(demSeats/totalSeats,totalDemVotes/totalVotes));
			seatsVotesRep.add(new SeatVote(repSeats/totalSeats,(totalVotes-(totalDemVotes+swing))/totalVotes));
		}
		// Finally sort the lists by votes
		Collections.sort(seatsVotesRep,compareByVote);  // 
		for(int index=0;index<seatsVotesRep.size();index++)  {
			SeatVote sv = seatsVotesRep.get(index);
			LOGGER.info(String.format(" %d, %2.2f %2.2f",index,sv.getVotes(),sv.getSeats()));
		}
		Collections.sort(seatsVotesDem,compareByVote);  // 
	}
	
	// Compare SeatVotes based on vote attribute in ascending order
	protected Comparator<SeatVote> compareByVote = new Comparator<SeatVote>() {
		@Override
		public int compare(SeatVote sv1, SeatVote sv2) {
			return (sv1.getVotes()> sv2.getVotes()?1:0);
		}
	};
}
