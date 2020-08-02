/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import redistrict.colorado.core.PlanFeature;

/**
 * Render shape as referenced by a plan.
 */
public class OverlayColorGenerator {
	private final static String CLSS = "OverlayColorGenerator";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public String getAffiliationColor(double maxRep, double maxDem,PlanFeature feature) {
		double total = feature.getDemocrat() + feature.getRepublican();
		double minDem = 1. - maxRep;
		double minRep = 1. - maxDem;
		double dem = feature.getDemocrat()/total;
		dem = (dem - minDem)/(maxDem - minDem);
		double rep = feature.getRepublican()/total;
		rep = (rep - minRep)/(maxRep - minRep);
		String color = String.format("#%02X%02X%02X",(int)(255.*rep),0,(int)(255.*dem));
		LOGGER.warning(String.format("%s.getAffiliationColor: %s %2.2f,%2.2f %s",CLSS,feature.getName(),dem,rep,color));
		return color;
	}
	// Return a gray color representing the fraction of minorities
	// Scale the value to a range of 1 - 0 (all white to all non-white)
	public String getDemographicsColor(double minWhite,double maxWhite,PlanFeature feature) {
		double val = feature.getWhite()/feature.getPopulation();
		val = (val - minWhite)/(maxWhite - minWhite);
		int c = (int)Math.round(255.*val);
		String color = String.format("#%02X%02X%02X",c,c,c);
		LOGGER.warning(String.format("%s.getDemographicsColor: %s %d %s",CLSS,feature.getName(),c,color));
		return color;	
	}
}
