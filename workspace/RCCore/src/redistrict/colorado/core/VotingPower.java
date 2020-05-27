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
	private final List<PlanFeature> districtPopulations;
	private final int ndistricts;
	
	public VotingPower(List<PlanFeature>feats) {
		this.districtPopulations = feats;
		this.ndistricts = districtPopulations.size();
	}
	
	//returns population-weighted mean absolute deviation.
	public double getRacialVoteDilution() {
    	
    	double[][] ddd = calcDemographicStatistics();
    	if( ddd.length == 0 || ddd[0].length == 0) {
    		return 0;
    	}
    	double tot = 0;
    	double tot_score = 0;
    	for( int i = 0; i < ddd.length-1; i++) {
    		double pop = ddd[i][0];
    		double score = ddd[i][1];
    		score = Math.log(score);
    		tot_score += Math.abs(score)*pop;
    		tot += pop;
    	}
    	tot_score /= tot;
    	return tot_score;
    }
	
	public void getVotingPowerImbalance() {
		
	}
	
	/**
	 *  Calculate demographic statistics.
	 * @return an array of EthnicGroup objects
	 */
	private double[][] calcDemographicStatistics() {
    	String[] dem_col_names = MainFrame.mainframe.project.demographic_columns_as_array();
		
		double[] pop_by_dem = new double[dem_col_names.length];
		for( int i = 0; i < pop_by_dem.length; i++) { pop_by_dem[i] = 0; }
		double[] votes_by_dem = new double[dem_col_names.length];
		for( int i = 0; i < votes_by_dem.length; i++) { votes_by_dem[i] = 0; }
		double[] vote_margins_by_dem = new double[dem_col_names.length];
		for( int i = 0; i < vote_margins_by_dem.length; i++) { vote_margins_by_dem[i] = 0; }
		double[][] demo = getDemographicsByDistrict();
		double[][] demo_pct = new double[demo.length][];
		for( int i = 0; i < demo_pct.length; i++) {
			double total = 0;
			for( int j = 0; j < demo[i].length; j++) {
				pop_by_dem[j] += demo[i][j];
				total += demo[i][j];
			}
			total = 1.0/total;
			demo_pct[i] = new double[demo[i].length];
			for( int j = 0; j < demo[i].length; j++) {
				demo_pct[i][j] = demo[i][j]*total;
			}
		}
		
		//---insert vote and vote margin finding
		for( int i = 0; i < districts.size(); i++) {
			try {
			District d = districts.get(i);

			//double[][] result = d.getElectionResults();
			double[][] result = new double[2][];//d.getElectionResults();
			result[0] = d.getAnOutcome();
			result[1] = District.popular_vote_to_elected(result[0], i,0);
			
			if( result[0].length == 0) {
				return new double[][]{new double[]{},new double[]{}};
			}

			double total_votes = result[0][0]+result[0][1];
			if( total_votes == 0) {
				total_votes = 1;
			}
			
			for( int j = 0; j < dem_col_names.length; j++) {
				votes_by_dem[j] += total_votes*demo_pct[i][j];
				vote_margins_by_dem[j] += vote_gap_by_district[i]*demo_pct[i][j];
			}	
			} catch (Exception ex) {
				System.out.println("ex stats 1 "+ex);
				ex.printStackTrace();
			}
		}
		//--end insert vote and vote margin finding
		
		double tot_pop = 0;
		double tot_vote = 0;
		double tot_margin = 0;
		for( int i = 0; i < dem_col_names.length; i++) {
			tot_pop += pop_by_dem[i];
			tot_vote += votes_by_dem[i];
			tot_margin += vote_margins_by_dem[i];
		}
		if( tot_margin == 0) {
			tot_margin = 1;
		}
		if( tot_vote == 0) {
			tot_vote = 1;
		}
		double ravg = 1.0 / (tot_margin / tot_vote);
		
		//String[] ecolumns = new String[]{"Ethnicity","Population","Vote dilution","% Wasted votes","Votes","Victory margins"};
		double[][] edata = new double[dem_col_names.length+1][];
		for( int i = 0; i < dem_col_names.length; i++) {
			edata[i] = new double[]{
					pop_by_dem[i],
					(ravg*vote_margins_by_dem[i]/votes_by_dem[i]),
					(vote_margins_by_dem[i]/votes_by_dem[i]),
					(votes_by_dem[i]),
					(vote_margins_by_dem[i]),
			};
		}
		edata[dem_col_names.length] = new double[]{
				tot_pop,
				1,
				(1.0/ravg),
				(tot_vote),
				(tot_margin),
		};
		
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

}
