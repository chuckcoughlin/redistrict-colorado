package redistrict.colorado.core;

import java.util.List;
import java.util.logging.Logger;

/**
 * Voting power analysis.
 * Adapted from Autoredistrict.org
 * See: http://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence 
 */

public class VotingPower {
	private final static String CLSS = "VotingPower";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final List<PlanFeature> districtDemographics;
	private final EthnicGroup[] edata;
	private final EthnicGroup blacks;
	private final EthnicGroup hispanics;
	private final EthnicGroup whites;
	private EthnicSummary summary = null;
	private final int ndistricts;
	
	public VotingPower(List<PlanFeature>feats) {
		this.districtDemographics = feats;
		this.ndistricts = districtDemographics.size();
		this. edata = new EthnicGroup[Ethnicity.count()];
		this.blacks = new EthnicGroup(Ethnicity.BLACK);
		this.hispanics = new EthnicGroup(Ethnicity.HISPANIC);
		this.whites = new EthnicGroup(Ethnicity.WHITE);
		edata[0] = blacks;
		edata[1] = hispanics;
		edata[2] = whites;
	}
	
	public EthnicSummary getSummary() { return this.summary; }
	
	/**
	 * @return population-weighted mean absolute deviation of the 
	 * 			vote dilution across ethnic groups.
	 */
	public double getRacialVoteDilution() {
    	
    	EthnicGroup[] groups = calcDemographicStatistics();
    	if( groups.length == 0 ) {
    		return 0;
    	}
    	double tot = 0;
    	double tot_score = 0;
    	for( int i = 0; i < groups.length-1; i++) {
    		double pop = groups[i].getVotes();
    		double score = groups[i].getVoteDilution(); 
    		score = Math.log(score);
    		tot_score += Math.abs(score)*pop;
    		tot += pop;
    	}
    	tot_score /= tot;
    	return tot_score;
    }
	
	/**
	 * @return the name of the group that is most diluted.
	 */
	public String getDilutedGroup() {
		double dilution = 0.;
		String name = "";
		for(EthnicGroup group:edata) {
			if( group.getVoteDilution()<dilution) {
				name = group.getEthnicity().name();
			}
		}
		return name;
	}
	
	
	public double getVotingPowerImbalance() {
		try {
			//double[][] demo = getDemographicsByDistrict();
			double[] winners_by_ethnicity = new double[Ethnicity.count()];
			summary = new EthnicSummary();

			for( int j = 0; j < Ethnicity.count(); j++) {
				winners_by_ethnicity[j] = 0;
			}

			for( PlanFeature feat:districtDemographics) {
				summary.incrementSeats(1.);  // One seat per district
				summary.incrementPopulation(feat.getPopulation());
			}

			double[] targets = popularVoteToElected(summary,-1,false);
/*
		double[] targets = new double[demo[0].length];
		//double total_seats = Settings.total_seats();
		for( int i = 0; i < targets.length; i++) {
			targets[i] = Math.round(total_seats*total_demo[i]/total_pop);
		}
*/
			double MAD = 0;
			double unrepresented = 0;
	/*	
		double pop_per_seat = total_pop/total_seats;
		double pop_per_seat_wrong = total_pop/(total_seats+1);
		if( Settings.quota_method == Settings.QUOTA_METHOD_HARE) {
			pop_per_seat_wrong = pop_per_seat;
		}
		double[] min_votes_needed_for_seat = this.getMinVotesNeededForSeat(demo, pop_per_seat_wrong);
		

		
		for( int i = 0; i < ndistricts; i++) {
			double[] demo_result = popularVoteToElected(demo[i], i, -1);
			for( int j = 0; j < demo_result.length; j++) {
				winners_by_ethnicity[j] += demo_result[j];
			}
		}

		
		
		for( int i = 0; i < Ethnicity.count(); i++) {
			if( min_votes_needed_for_seat[i] > pop_per_seat) {
				min_votes_needed_for_seat[i] = pop_per_seat;
			}
			//double winners = winners_by_ethnicity[i]*pop_per_seat;// + pop_per_seat - min_votes_needed_for_seat[i];
			//double unrepresented = total_demo[i]-winners;//targets[i]*pop_per_seat - winners;
		//	System.out.println("t: "+winners_by_ethnicity[i]+" "+targets[i]+" "+min_votes_needed_for_seat[i]);
			double unrepresented = (targets[i] - winners_by_ethnicity[i])*pop_per_seat;
			if( unrepresented < 0) {
				unrepresented = Math.abs(unrepresented);
				unrepresented += pop_per_seat - min_votes_needed_for_seat[i];
				if( unrepresented < 0) {
					unrepresented = 0;
				}
			} else if( unrepresented > 0) {
				unrepresented += min_votes_needed_for_seat[i] - pop_per_seat;
				if( unrepresented < 0) {
					unrepresented = 0;
				}
			}
			//unrepresented += min_votes_needed_for_seat[i]-pop_per_seat_wrong;
			//System.out.println("i "+i+" "+targets[i]+" "+winners_by_ethnicity[i]+" "+unrepresented+" "+ min_votes_needed_for_seat[i]);

			/*
				MAD -= unrepresented;
				MAD += pop_per_seat_wrong-min_votes_needed_for_seat[i];
			} else {
				if(unrepresented + min_votes_needed_for_seat[i]-pop_per_seat_wrong > 0) {
					unrepresented += min_votes_needed_for_seat[i]-pop_per_seat_wrong;///pop_per_seat_wrong;
				}
			}*/
			MAD += Math.abs(unrepresented)/2.0;//Math.abs(winners_by_ethnicity[i]*pop_per_seat - total_demo[i]);
			/*
			double unrepresented = total_demo[i]-winners_by_ethnicity[i]*pop_per_seat;
			double pct = ((double)min_votes_needed_for_seat[i])/pop_per_seat_wrong;
			//System.out.println("unr "+i+" "+unrepresented+" "+min_votes_needed_for_seat[i]+" "+pct);
			if( unrepresented < 0) {
				//System.out.println("overrepresented ");
				continue;
			}
			if( unrepresented > pop_per_seat*0.60) { //if should get another seat
				double amt = min_votes_needed_for_seat[i] - pop_per_seat_wrong;
				if( amt+unrepresented < 0) {
					continue;
				} else {
					unrepresented += amt;
				}
				//System.out.println("adjusted "+amt);
			}
			//System.out.println("adding "+unrepresented); 
			
			//(pop_per_seat - min_votes_needed_for_seat[i])
			 
			MAD += unrepresented;//Math.abs(winners_by_ethnicity[i]*pop_per_seat - total_demo[i]);
			*/
			/*
		}
		//System.out.println("returning "+MAD);
		*/
		return MAD;
    	} catch (Exception ex) {
    		System.out.println("Ex "+ex);
    		ex.printStackTrace();
    		return 0.;
    	}
	}
	
	/**
	 *  Calculate demographic statistics.
	 * @return an array of EthnicGroup objects - black, hispanic, white
	 */
	private EthnicGroup[] calcDemographicStatistics() {

		summary = new EthnicSummary();
		for( PlanFeature feat:districtDemographics ) {  // Iterating over districts
			summary.incrementPopulation(feat.getPopulation()); 
			blacks.incrementVotes(feat.getBlack());
			hispanics.incrementVotes(feat.getHispanic());
			whites.incrementVotes(feat.getWhite());
			
			double total_votes = feat.getRepublican() + feat.getDemocrat();
			summary.incrementVotes(total_votes);
			summary.incrementSeats(1);
			
			double margin = Math.abs(feat.getRepublican() - feat.getDemocrat());
			summary.incrementVoteMargin(margin);
			blacks.incrementVoteMargin(margin);
			hispanics.incrementVoteMargin(margin);
			whites.incrementVoteMargin(margin);
			
		}
		// Reciprical of average vote margin
		double ravg = summary.getTotalVotes()/summary.getTotalMargin();
		
		// Field names: "Ethnicity","Population","Vote dilution","% Wasted votes","Votes","Victory margins"};
		// Ethnic groups: 0==black, 1=hispanic, 2=white
		blacks.setVoteDilution(ravg*blacks.getVoteMargin()/blacks.getVotes());
		hispanics.setVoteDilution(ravg*hispanics.getVoteMargin()/hispanics.getVotes());
		whites.setVoteDilution(ravg*whites.getVoteMargin()/whites.getVotes());
		
		return edata;
    }

    
	/** KullbackLeiblerDivergence 
	 * @param p popular vote
	 * @param q election result
	 * @param regularization_factor, called with variously 1, 1.2, .01
	 * @return
	 */
    public double getKLDiv(double[] p, double[] q, double regularization_factor) {
        //regularize (see "regularization" in statistics)
        for( int i = 0; i < p.length; i++)
            p[i]+=regularization_factor;  
        for( int i = 0; i < q.length; i++)
            q[i]+=regularization_factor;  
        
        //get totals
        double totp = 0;
        for( int i = 0; i < p.length; i++)
            totp += p[i];  
        double totq = 0;
        for( int i = 0; i < q.length; i++)
            totq += q[i];  

        //make same ratio.
        double ratio = totp/totq;
        for( int i = 0; i < q.length; i++)
            q[i] *= ratio;  


        //normalize
        totp = 0;
        for( int i = 0; i < p.length; i++)
            totp += p[i];  
        for( int i = 0; i < p.length; i++)
            p[i] /= totp;
        totq = 0;
        for( int i = 0; i < q.length; i++)
            totq += q[i];  
        for( int i = 0; i < q.length; i++)
            q[i] /= totq;

        //get kldiv
        double div = 0;
        for( int i = 0; i < q.length; i++) {
        	if( p[i] == 0) {
        		continue;
        	}
        	double kl = p[i]*(Math.log(q[i]) - Math.log(p[i]));
            div += kl;
        }
        return -div;
    }
    /**
     * 
     * @param ds array of total votes by district
     * @param seats 
     * @param pop_per_seat_wrong
     * @param droop true for droop quota system, else hare. Should not matter for winner-take-all elections.
     * @return array of votes by district for the number of seats specified
     */
	private double[] popularVoteToElected(EthnicSummary summary, double pop_per_seat_wrong, boolean droop) {
		double[]res = new double[Ethnicity.count()];
		for( int j = 0; j < Ethnicity.count(); j++) {
			res[j] = 0;
		}

		double totvote = summary.getTotalPopulation();
		/*
		double unit = totvote / (seats + (droop ? 1 : 0));
		if( unit == 0) {
			unit = 1;
		}
		if( pop_per_seat_wrong > 0 && pop_per_seat_wrong > unit) {
			unit = pop_per_seat_wrong;
		}

		int seats_left = seats;
		for( int j = 0; j < ds.length; j++) {
			double mod = ds[j];
			while( mod >= unit) {
				res[j]++;
				seats_left--;
				mod -= unit;
			}
		}			

		while( seats_left > 0) {
			int n = -1;
			double max = -1;
			for( int j = 0; j < ds.length; j++) {
				if( n < 0 || ds[j]-unit*res[j] > max) {
					n = j;
					max = ds[j]-unit*res[j];
				}
			}
			if( max > 0) {
				res[n]++;
			} else {
				break;
			}
			seats_left--;
		}
		*/
		return res;
	}
}
