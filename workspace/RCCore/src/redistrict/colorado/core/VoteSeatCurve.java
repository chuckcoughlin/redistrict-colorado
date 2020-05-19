package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Seats-votes curve generator.
 * Calculated using uniform partisan swing
 * Translated from Python
 * See: https://github.com/jeffreyshen19/Seats-Votes-Curves/blob/master/generator/uniform_partisan_swing.py
 */

public class VoteSeatCurve {
	private final static String CLSS = "VoteSeatCurve";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static double ITERATIONS = 1000;
	private static double INTREMENT = 0.01; // Percentage increase each district each iteration
	private final List<PlanFeature> votingByDistrict;
	private final List<SeatVote> seatsVotesDem;
	private final List<SeatVote> seatsVotesRep;
	
	public VoteSeatCurve(List<PlanFeature>feats) {
		this.votingByDistrict = feats;
		this.seatsVotesDem = new ArrayList<>();
		this.seatsVotesRep = new ArrayList<>();
	}
	
	public List<SeatVote> getSeatVotesDemocratic() { return this.seatsVotesDem; }
	public List<SeatVote> getSeatVotesRepublican() { return this.seatsVotesRep; }
	
	
	public void generate()  {  
		List<SeatVote> seatsVotesRep = new ArrayList<>();
		seatsVotesRep.add(new SeatVote(0.,0.));
		List<SeatVote> seatsVotesDem = new ArrayList<>();
		seatsVotesDem.add(new SeatVote(0,0));
		
		int totalVotes = 0;
		int totalDemVotes = 0;
		int totalRepVotes = 0;
		int ndistricts = votingByDistrict.size();

		//  Read all district data
		for(PlanFeature feat:votingByDistrict) {
			totalVotes += feat.getDemocrat()+feat.getRepublican();
			totalDemVotes += feat.getDemocrat();
			totalRepVotes += feat.getRepublican();
		}

		double repVoteShare = (double)(totalRepVotes) / totalVotes;
		double demVoteShare = (double)(totalDemVotes) / totalVotes;

		double diff = repVoteShare - demVoteShare;

		// Generate curve
		double share = repVoteShare;
		int counter = 0;
		while( share <= 1. ) {
			int totalRepSeats = 0;
			int totalDemSeats = 0;

			// simulate 1000 elections
    		for(int iteration=0;iteration < ITERATIONS;iteration++ ) {
    			boolean[] districtOverflowedDem = new boolean[ndistricts];
    			boolean[] districtOverflowedRep = new boolean[ndistricts];
    			double [] updatedValsDem = new double[ndistricts];
    			double [] updatedValsRep = new double[ndistricts];
    			for(int i=0;i<ndistricts;i++ ) {
    				districtOverflowedRep[i] = false;
    				districtOverflowedDem[i] = false;
    				updatedValsRep[i] = 0.;
    		        updatedValsDem[i] = 0.;
    			}
    			int excessDem = 0;
    			int excessRep = 0;
        

    			int k = 0;
    			for(PlanFeature feat:votingByDistrict) {
    				updatedValsRep[k] = feat.getRepublican() + counter * SWING_CONST + SWING_CONST * random.randint(-5, 5)
            updatedValsDem[k] = 1 - updatedValsRep[k] + diff

            if updatedValsRep[k] > 1:
                excessRep += 1
                districtOverflowedRep[k] = True

            if updatedValsDem[k] < 0:
                excessDem += 1
                districtOverflowedDem[k] = True

        for k, district in enumerate(votingByDistrict):
            #Overflow mechanic: distribute excess votes to the other districts
            if districtOverflowedRep[k] is False:
                updatedValsRep[k] += SWING_CONST * (excessRep / (len(votingByDistrict) - excessRep))

            if districtOverflowedDem[k] is False:
                updatedValsDem[k] -= SWING_CONST * (excessDem / (len(votingByDistrict) - excessDem))

            if updatedValsRep[k] > 0.50:
                totalRepSeats += 1
            if updatedValsDem[k] > 0.50:
                totalDemSeats += 1
    share += INCREMENT;
    counter += 1;

    if share <= 1:
        seatsVotesRep.append({"seats": float(totalRepSeats) / (len(votingByDistrict) * 1000.0), "votes": i})
        seatsVotesDem.insert(0, {"seats": float(totalDemSeats) / (len(votingByDistrict) * 1000.0), "votes": 1 - i + diff})
		}
		}  // End iteration (1% difference in vote
		share = demVoteShare;
		counter = 0;

		while( share <= 1) {
    totalDemSeats = 0
    totalRepSeats = 0

    for j in range(0,1000): #simulate 1000 elections
        districtOverflowedRep = [False] * len(votingByDistrict);
        districtOverflowedDem = [False] * len(votingByDistrict);
        excessDem = 0
        excessRep = 0
        updatedValsRep = [0] * len(votingByDistrict);
        updatedValsDem = [0] * len(votingByDistrict);

        for k, district in enumerate(votingByDistrict):
            updatedValsDem[k] = district["percentDem"] + counter * SWING_CONST + SWING_CONST * random.randint(-5, 5) + diff
            updatedValsRep[k] = 1 - (updatedValsDem[k] - diff)

            if updatedValsRep[k] > 1:
                excessRep += 1
                districtOverflowedRep[k] = True

            if updatedValsDem[k] < 0:
                excessDem += 1
                districtOverflowedDem[k] = True

        for k, district in enumerate(votingByDistrict):
            #Overflow mechanic: distribute excess votes to the other districts
            if districtOverflowedRep[k] is False:
                updatedValsRep[k] -= INCREMENT * (excessRep / (len(votingByDistrict) - excessRep))

            if districtOverflowedDem[k] is False:
                updatedValsDem[k] += INCREMENT * (excessDem / (len(votingByDistrict) - excessDem))

            if updatedValsRep[k] > 0.50:
                totalRepSeats += 1
            if updatedValsDem[k] > 0.50:
                totalDemSeats += 1

    i += INCREMENT
    counter += 1

    if i <= 1:
        seatsVotesDem.append({"seats": float(totalDemSeats) / (len(votingByDistrict) * 1000.0), "votes": i + diff})
        seatsVotesRep.insert(0, {"seats": float(totalRepSeats) / (len(votingByDistrict) * 1000.0), "votes": 1 - i})
		}
	// Add endpoints

	seatsVotesRep.append({"seats": 1, "votes": 1})
	seatsVotesDem.append({"seats": 1, "votes": 1})

		}
}
