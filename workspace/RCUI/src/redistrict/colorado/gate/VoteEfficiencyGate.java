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
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure wasted votes, we count the number of votes above the amount necessary to win, ");
		Text t2 = new Text("for each district and each party. The more wasted votes an election had, the less competitive ");
		Text t3 = new Text("it was. We want this score to be ");
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
