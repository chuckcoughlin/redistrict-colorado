package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Voting power analysis across ethnic groups and districts
 * Adapted from Autoredistrict.org
 * See: http://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence 
 */
public class VotingPowerAnalyzer {
	private final static String CLSS = "VotingPower";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final Map<Ethnicity,List<VotingPower>> emap;
	private final String name; // Plan name
	private final List<NameValue> details;
	private PowerSummary summary = null;
	private final int ndistricts;
	// Cached from getRacialVoteDilution
	private Ethnicity mostDilute = null;   
	private Map<Ethnicity,Double> dilutions = null;
	
	/**
	 * Constructor: Populate the demographics arrays.
	 * @param feats
	 */
	public VotingPowerAnalyzer(String nam,List<PlanFeature>feats) {
		this.name = nam;
		this.ndistricts = feats.size();
		this.emap = new HashMap<>();
		this.details = new ArrayList<>();
		this.summary = new PowerSummary();
		populateDemographics(feats);
	}
	
	public List<NameValue> getDetails() { return this.details; }
	public int getNDistricts() { return this.ndistricts; }
	public PowerSummary getSummary() { return this.summary; }
	
	/**
	 * @return  the minimum mean absolute deviation of the 
	 * 			vote dilution across ethnic groups.
	 */
	public double getRacialVoteDilution() {
		dilutions = new HashMap<>();
    	double dilution = Double.MAX_VALUE;   // MAD for most diluted ethnicity
    	for(Ethnicity ethnicity:Ethnicity.getEthnicities()) {
    		List<VotingPower> powers = emap.get(ethnicity);
    		double score = Double.MAX_VALUE;
    		for(VotingPower vp:powers) {
    			double power = vp.getNormalizedVotePower()*summary.getTotalMargin()/summary.getTotalPopulation();
    			score = Math.abs(Math.log(power));
				dilutions.put(ethnicity, score);
    			if( score<dilution ) {
    				dilution = score;
    				this.mostDilute = ethnicity;
    			}
    		}
    	}
    	return dilution;
    }
	
	/**
	 * If getRacialVoteDilution() has never been called, it will be 
	 * as the result here is a side-effect.
	 * @return the name of the group that is most diluted.
	 */
	public Ethnicity getDilutedGroup() {
		if( mostDilute==null) getRacialVoteDilution();
		return mostDilute;
	}
	
	/**
	 * If getRacialVoteDilution() has never been called, it will be 
	 * as the result here is a side-effect.
	 * @return a name-values that contain the MAD for each ethnicity
	 */
	public NameValue getDilutions() {
		if( dilutions==null) getRacialVoteDilution();
		NameValue nv = new NameValue(name);
		for(Ethnicity key:dilutions.keySet()) {
			nv.setValue(key.name(), dilutions.get(key));
		}
		return nv;
	}
	
	public double getVotingPowerImbalance() {
		try {
			//double[][] demo = getDemographicsByDistrict();
			double[] winners_by_ethnicity = new double[Ethnicity.count()];
			summary = new PowerSummary();

			for( int j = 0; j < Ethnicity.count(); j++) {
				winners_by_ethnicity[j] = 0;
			}


			//double[] targets = popularVoteToElected(summary,-1,false);
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
	 * Tally demographics in preparation for calculations..
	 * @return an array of EthnicGroup objects - black, hispanic, white
	 */
	private void populateDemographics(List<PlanFeature>feats) {
		List<VotingPower> blacks = new ArrayList<>();
		List<VotingPower> hispanics = new ArrayList<>();
		List<VotingPower> whites = new ArrayList<>();
		emap.put(Ethnicity.BLACK,blacks);
		emap.put(Ethnicity.HISPANIC,hispanics);
		emap.put(Ethnicity.WHITE,whites);
		
		for( PlanFeature feat:feats ) {  // Iterating over districts
			summary.incrementPopulation(feat.getPopulation()); 
			summary.incrementSeats(1.0);   // One seat per district
			summary.incrementVotes(feat.getRepublican() + feat.getDemocrat());
			double margin = Math.abs(feat.getRepublican() - feat.getDemocrat());
			if(margin<1 ) margin = 1.0; // prevent possibility of divide by zero
			summary.incrementVoteMargin(margin);
			NameValue nv = new NameValue(feat.getName());
			VotingPower vp = new VotingPower(feat.getBlack(),margin);
			blacks.add(vp);
			nv.setValue(Ethnicity.BLACK.name(), vp);
			vp = new VotingPower(feat.getHispanic(),margin);
			hispanics.add(vp);
			nv.setValue(Ethnicity.HISPANIC.name(), vp);
			vp = new VotingPower(feat.getWhite(),margin);
			whites.add(vp);
			nv.setValue(Ethnicity.WHITE.name(), vp);
			
			// Preserve the details
			details.add(nv);
		}
    }

    
	/** KullbackLeiblerDivergence - unused
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
}
