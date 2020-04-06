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
public class VoteEfficiencyGate extends Gate {
	private final static String KEY_SCORE = "Score";
	private final double DEFAULT_THRESHOLD = 8.0;
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("The efficiency gap is the sum of the differences of wasted votes for the two parties divied by the total number of votes. A wasted vote is a ");
		Text t3 = new Text("vote that does not help elect a candidate (Over 50% for the winning side, all votes for the losing side. We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5);
		return info;
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "Vote Efficiency"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.VOTE_EFFICIENCY_WEIGHT_KEY);}
	public GateType getType() { return GateType.VOTING_EFFICIENCY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.VOTE_EFFICIENCY_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return false; }
}
