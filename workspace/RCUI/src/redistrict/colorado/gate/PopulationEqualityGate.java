/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import redistrict.colorado.core.GateType;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;

/**
 * Compare plans based on the populations of each district.  Values must be within 
 * 1% of each other.
 */
public class PopulationEqualityGate extends Gate {
	public PopulationEqualityGate() {
		
	}
	public String getTitle() { return "Population Equality"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.POPULATION_EQUALITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY,weight);}
}
