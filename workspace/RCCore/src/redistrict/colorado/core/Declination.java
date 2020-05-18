package redistrict.colorado.core;

/**
 * Seats-votes curve generator
 * Calculated using uniform partisan swing
 * Translated from Python
 * See: https://github.com/jeffreyshen19/Seats-Votes-Curves/blob/master/generator/uniform_partisan_swing.py
 */

public class Declination {
	
	public Declination() {
		
	}
	
	public void generate()  
	{  
/*
""" Compute the declination of an election.
   """
   Rwin = sorted(filter(lambda x: x <= 0.5, vals))
   Dwin = sorted(filter(lambda x: x > 0.5, vals))
   # Undefined if each party does not win at least one seat
   if len(Rwin) < 1 or len(Dwin) < 1:
      return False
   theta = np.arctan((1-2*np.mean(Rwin))*len(vals)/len(Rwin))
   gamma = np.arctan((2*np.mean(Dwin)-1)*len(vals)/len(Dwin))
   # Convert to range [-1,1]
   # A little extra precision just in case.
   return 2.0*(gamma-theta)/3.1415926535

	*/
    
	}
}
