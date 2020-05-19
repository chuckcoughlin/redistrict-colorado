package redistrict.colorado.core;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 * Declination calculator.
 * Translated from Python
 * See: https://github.com/jeffreyshen19/Seats-Votes-Curves/blob/master/generator/uniform_partisan_swing.py
 */
public class Declination {
	private double declination = 0.;
	private final double[]demFractions;
	private final int size;
	
	/**
	 * @param fracs ordered list of Democratic vote percentages.
	 */
	public Declination(double[] fracs) {
		this.demFractions = fracs;
		this.size = fracs.length;
	}
	
	public double getDeclination() { return this.declination; }
	public int getSize() { return this.size; }
	
	/**
	 * Calculate the declination and series to plot;
	 * @return true if computation was successful
	 */
	public boolean generate() {  
		// Undefined if each party does not win at least one seat
		int seats = demFractions.length;
		if( seats==0 ) return false;
		if( demFractions[0] > 0.5  || demFractions[demFractions.length-1]<0.5 ) return false;
		int repCount = 0;  // Number of democrat-majority districts
		for(double frac:demFractions ) {
			if( frac<0.5 ) break;
			repCount++;
		}
		Mean mean = new Mean();
		double repMean = mean.evaluate(demFractions,0,repCount);
		double demMean = mean.evaluate(demFractions,repCount,demFractions.length-repCount);
		
		double theta = Math.atan((1-2*repMean)*demFractions.length/repCount);
		double gamma = Math.atan((2*demMean-1)*demFractions.length/(demFractions.length-repCount));
		// Convert to range [-1,1]
		declination =  2.0*(gamma-theta)/Math.PI;
		return true;
	}
}
