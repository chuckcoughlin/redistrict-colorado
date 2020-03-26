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
 * Maximized the number of districts that are balanced in party affiliations
 */
public class CompetitiveDistrictsGate extends Gate {
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("This metric is simply a count of the number of districts where the probable ");
		Text t2 = new Text("difference between parties is less than 15%. We want this count to be ");
		Text t3 = new Text("maximized");
		t3.setStyle("-fx-font-weight: bold");
		Text t4 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getTitle() { return "Competitive Districts"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.COMPETITIVENESS_WEIGHT_KEY);}
	public GateType getType() { return GateType.COMPETIVENESS; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.COMPETITIVENESS_WEIGHT_KEY,weight);}
}
