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

/**
 * Compare plans based on the number of times district boundaries
 * cross county lines.
 */
public class CountyCrossingGate extends Gate {
	private final static String KEY_SCORE = "Score";
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("This is essentially a trade-off with compactness. To measure split reduction, ");
		Text t2 = new Text("we count the number of different districts in each county, and subtract the number of counties.");
		Text t3 = new Text("We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5);
		return info;
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "County Line Crossings"; }
	public GateType getType() { return GateType.COUNTY_CROSSINGS; }
}
