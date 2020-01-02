/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.LeftSelectionEvent;


/**
 * Control the display of screens in the right-side of the split pane.
 * Listen for user-selections originating on the left.
 */
public class RightSideController implements ChangeListener<LeftSelectionEvent> {
	private final static String CLSS = "RightSideController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final BasicRightSideNode[] nodes;
	
	private final EventBindingHub hub;
	
	
	public RightSideController(BasicRightSideNode[] brnodes) {
		this.hub = EventBindingHub.getInstance();
		this.nodes = brnodes;
		hub.addLeftSideSelectionListener(this);
	}
	



	/**
	 * Listen for changes to the selected layer based on our subscription to the hub.
	 */
	@Override
	public void changed(ObservableValue<? extends LeftSelectionEvent> source, LeftSelectionEvent oldValue,LeftSelectionEvent newValue) {
		if( newValue==null ) return;
		for(BasicRightSideNode node:nodes) {
			if( node.getMode().equals(newValue.getMode()) && 
				node.getOption().equals(newValue.getOption()) ) {
				node.setVisible(true);
				node.toFront();
				LOGGER.info(String.format("%s.changed: selected = %s,%s", CLSS,newValue.getMode().name(),newValue.getOption().name()));
				node.updateModel();
			}
			else {
				node.setVisible(false);
			}
		}
		
		
	}
}
