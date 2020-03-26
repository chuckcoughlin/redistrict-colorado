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
 * Compare plans based on the contiguity of the districts.
 */
public class ContiguousGate extends Gate {
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure contiguity, or the amount of disconnected population, we count the total population ");
		Text t2 = new Text("that is not connected to the most populated fully-connected region. We want this score to be ");
		Text t3 = new Text("minimized");
		t3.setStyle("-fx-font-weight: bold");
		Text t4 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getTitle() { return "Contiguity"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.CONTIGUITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.CONTIGUITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.CONTIGUITY_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return false; }
}
