package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Voting power analysis across ethnic groups and districts
 * Adapted from Autoredistrict.org
 * MAD - mean absolute deviation
 * 		sum absolute differences from mean. divide by count 
 */
public class VotingPowerAnalyzer {
	private final static String CLSS = "VotingPower";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final Map<Ethnicity,List<VotingPower>> emap;
	private final String name; // Plan name
	private final List<NameValue> vplist;
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
		this.vplist = new ArrayList<>();
		this.summary = new PowerSummary();
		populateDemographics(feats);
	}
	
	public int getNDistricts() { return this.ndistricts; }
	public PowerSummary getSummary() { return this.summary; }
	
	/**
	 * The normalization factor should be set from the state at-large,
	 * but we leave it up to the caller to do so.
	 * @return  the minimum mean absolute deviation of the 
	 * 			vote dilution across ethnic groups.
	 */
	public double getRacialVoteDilution() {
		dilutions = new HashMap<>();
    	for(Ethnicity ethnicity:Ethnicity.getEthnicities()) {
    		List<VotingPower> powers = emap.get(ethnicity);
    		double scores[] = new double[powers.size()];
    		int index = 0;
    		for(VotingPower vp:powers) {
    			double power = vp.getNormalizedVotePower();
    			scores[index] = Math.log(power);   // Previously we took the abs 
				index++;	
    		}
    		Double value = MeanAbsoluteDeviation.evaluate(scores);
    		dilutions.put(ethnicity, value);
    	}
    	// Compute the minimum among ethnicities
    	double dilution = Double.MAX_VALUE;
    	for( Ethnicity ethnicity:dilutions.keySet()) {
    		Double val = dilutions.get(ethnicity);
    		if( val<dilution ) {
				dilution = val;
				this.mostDilute = ethnicity;
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
	
	
	/**
	 * @return a list by district of VotingPowers fro each ethnicity
	 */
	public List<NameValue> getVotingPowerDetails() { return this.vplist; }
	
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
			vplist.add(nv);
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
