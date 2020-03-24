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
 * Compare plans based on the populations of each district.  Values must be within 
 * 1% of each other. The explanation is from autoredistrict.org.
 */
public class PopulationEqualityGate extends Gate {
	private final TextFlow info;
			
	public PopulationEqualityGate() {
		this.info = new TextFlow();
		Text t1 = new Text("To measure population balance, the program calculates the statistical variance");
		Text t2 = new Text(" of the populations of the districts. Since population selection is a Bernoulli process,");
		Text t3 = new Text(" the populations will take on a Normal distribution. So the variance of the population ");
		Text t4 = new Text(" is just the square of the standard deviation. We want this score to be ");
		Text t5 = new Text("minimized");
		t5.setStyle("-fx-font-weight: bold");
		Text t6 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6);
	}
	public TextFlow getInfo() { return this.info; }
	public String getTitle() { return "Population Equality"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.POPULATION_EQUALITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY,weight);}
}
