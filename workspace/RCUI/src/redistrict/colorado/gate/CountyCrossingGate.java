/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateType;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;

/**
 * Compare plans based on the number of times district boundaries
 * cross county lines.
 */
public class CountyCrossingGate extends Gate {
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
	public String getTitle() { return "County Line Crossings"; }
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.COUNTY_CROSSING_WEIGHT_KEY);}
	public GateType getType() { return GateType.COUNTY_CROSSINGS; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.COUNTY_CROSSING_WEIGHT_KEY,weight);}

    /**
     * Handle a click on the name text. Popup a dialog.
     */
    public class MouseEventHandler implements EventHandler<MouseEvent> {
    	@Override public void handle(MouseEvent e) {
    		/*
    		if( e.getSource() instanceof Label ) {
    			Label source = (Label)e.getSource();
    			if(source.getUserData().toString().equals(NAME) ) {
    				TextInputDialog dialog = new TextInputDialog(model.getName());
    				dialog.setTitle("Plan Name Dialog");
    				dialog.setHeaderText("Enter a unique name for the plan");
    				dialog.setContentText("Name:");
    			}
    		}
    		*/
    	}
    }
}
