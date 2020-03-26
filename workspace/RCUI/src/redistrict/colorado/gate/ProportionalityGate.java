/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateType;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;

/**
 * Verify that the number of probable district outcomes aligns with the 
 * population as a whole.
 */
public class ProportionalityGate extends Gate {
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Seats / votes asymmetry is the total absolute deviation from a symmetric seats / votes curve.");
		Text t2 = new Text( "We want this score to be ");
		Text t3 = new Text("minimized");
		t3.setStyle("-fx-font-weight: bold");
		Text t4 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getTitle() { return "Proportionality"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.POPULATION_EQUALITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY,weight);}
}
