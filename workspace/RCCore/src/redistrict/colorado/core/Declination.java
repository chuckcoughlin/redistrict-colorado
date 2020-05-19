package redistrict.colorado.core;

import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import javafx.scene.paint.Color;

/**
 * Declination calculator.
 * Translated from Python
 * See: https://observablehq.com/@sahilchinoy/gerrymandering-the-declination-function
 */
public class Declination {
	private final static String CLSS = "Declination";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Color planColor = null;
	private double declination = 0.;
	private final double[]demFractions;
	private double repMean = 0.;
	private double demMean = 0.;
	private int demSeats = 0;
	private int repSeats = 0;
	private final int size;
	
	/**
	 * @param fracs ordered list of Democratic vote percentages.
	 */
	public Declination(double[] fracs) {
		this.demFractions = fracs;
		this.size = fracs.length;
	}
	
	public double getDeclination() { return this.declination; }
	public double[] getDemFractions() { return this.demFractions; }
	public int getDemSeats() { return demSeats; }
	public int getRepSeats() { return repSeats; }
	// Democratic % in districts where Republicans lose
	public double getDemMean() { return demMean; }
	// Democratic % in districts where Republicans win
	public double getRepMean() { return repMean; }
	public Color getColor() { return this.planColor; }
	public void setColor(Color clr) { this.planColor = clr; }
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
		repSeats = 0;  // Number of republican-majority districts
		for(double frac:demFractions ) {
			if( frac>0.5 ) break;
			repSeats++;
		}
		demSeats = seats - repSeats;
		
		Mean mean = new Mean();
		repMean = mean.evaluate(demFractions,0,repSeats);
		demMean = mean.evaluate(demFractions,repSeats,demSeats);
		
		double theta = Math.atan((1-2*repMean)*demFractions.length/repSeats);
		double gamma = Math.atan((2*demMean-1)*demFractions.length/(demSeats));
		// Convert to range [-1,1]
		declination =  2.0*(gamma-theta)/Math.PI;
		LOGGER.info(String.format("%s.generate: %1.2f %d dem (%2.1f), %d rep (%2.1f)" ,CLSS, declination,demSeats,demMean,repSeats,repMean));
		return true;
	}
}
