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
 * Compare plans based on the compactness of the districts.
 */
public class CompactnessGate extends Gate {
	private final TextFlow info;
	public CompactnessGate() {
		this.info = new TextFlow();
		Text t1 = new Text("To measure compactness, we calculate the Isoperimetric Quotient. ");
		Text t2 = new Text("Basically we divide the area by the square of the length of the perimeter. ");
		Text t3 = new Text("But we want a grand total, so we add together the reciprocals of this for each district,");
		Text t4 = new Text("and then take the reciprocal of that. This gives us a weighted average. We want this score to be ");
		Text t5 = new Text("maximized");
		t5.setStyle("-fx-font-weight: bold");
		Text t6 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6);
	}
	public TextFlow getInfo() { return this.info; }
	public String getTitle() { return "Compactness"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.COMPACTNESS_WEIGHT_KEY);}
	public GateType getType() { return GateType.COMPACTNESS; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.COMPACTNESS_WEIGHT_KEY,weight);}
}
