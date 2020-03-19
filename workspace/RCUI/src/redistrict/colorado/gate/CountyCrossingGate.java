/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Compare plans based on the number of times district boundaries
 * cross county lines.
 */
public class CountyCrossingGate extends Gate {
	public CountyCrossingGate() {
		
	}
	public String getTitle() { return "County Line Crossings"; }
	
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
