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
	
	public void getRacialVoteDilution () {
	}
	
	public void getVotingPowerImbalance() {
		
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
