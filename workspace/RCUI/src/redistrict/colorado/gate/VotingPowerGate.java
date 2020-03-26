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
 * Compare plans based on the populations of each district are within 
 * 1% of each other.
 */
public class VotingPowerGate extends Gate {
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("We define voting power as the ability to elect a candidate of one's choosing. ");
		Text t2 = new Text("Another way to state this is the ability to effect the outcome of one or more elections. ");
		Text t3 = new Text("For a single district, this can be summarized by taking the margin of victory (in votes) and ");
		Text t4 = new Text("dividing it by the total votes cast. To total this up by ethnicity, we take the weighted sum of this over ");
		Text t5 = new Text("all elections. For example, for hispanics, we take the total number of votes in an election, multiply by the ");
		Text t6 = new Text("fraction of that district that is hispanic, and total that up over all districts. Then we do the same for margin ");
		Text t7 = new Text("of victory. Then we divide the margin of victory total by the votes cast total, and that gives us an estimate ");
		Text t8 = new Text("of the average voting power for that ethnicity. We want to minimize how much this varies between ethnicities, ");
		Text t9 = new Text("so we take the average of this over the entire population, and calculate the mean absolute deviation ");
		Text t10= new Text("(M.A.D.) of the ethnicities from this. This gives us a summary of how uneven voting power is distributed ");
		Text t11= new Text("among the ethnicities. We want this score to be");
		Text t12 = new Text("minimized");
		t12.setStyle("-fx-font-weight: bold");
		Text t13 = new Text("."); 
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
		return info;
	}
	public String getTitle() { return "Voting Power"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.VOTING_POWER_WEIGHT_KEY);}
	public GateType getType() { return GateType.VOTING_POWER; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.VOTING_POWER_WEIGHT_KEY,weight);}
}
