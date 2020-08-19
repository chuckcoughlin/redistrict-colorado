package redistrict.colorado.core;

/**
 * Compute means, then sum the differences of individual values
 * and the mean. Divide by the count.
 */

public class MeanAbsoluteDeviation {
	
	public static double evaluate(double[] data)  
	{  
		double sum = 0.0;
		for (int i = 0; i < data.length; i++) { 
			sum += data[i]; 
		}
		double mean = sum/data.length;
		
		sum = 0.0;
		for (int i = 0; i < data.length; i++) { 
			sum += Math.abs(data[i]-mean); 
		}
		
		return sum/data.length; 
	}
}
