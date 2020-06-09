package redistrict.colorado.core;

/* See: https://ryanharrison.co.uk/2013/10/04/java-calculate-the-harmonic-mean.html */

public class HarmonicMean {
	
	public static double evaluate(double[] data)  
	{  
		double sum = 0.0;

		for (int i = 0; i < data.length; i++) { 
			if( data[i]!=0.) sum += 1.0 / data[i]; 
		} 
		return data.length / sum; 
	}
}
