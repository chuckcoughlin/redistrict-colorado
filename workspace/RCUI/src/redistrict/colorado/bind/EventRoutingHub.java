/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import redistrict.colorado.ui.ViewMode;

/**
 * This singleton class holds observable properties that allow scattered elements
 * of the UI to bind with each other (albeit indirectly). The singleton nature 
 * provides easy access to the instance from anywhere.
 * 
 * The listeners must support ChangeListener of the appropriate class. Nodes can also bind to this.
 * 
 * The entities that are tracked include:
 * 	ViewMode - plans, layers or routes
 */
public class EventRoutingHub {
	private static EventRoutingHub instance = null;
	private final SimpleObjectProperty<ViewMode> mode;
	/**
	 * Constructor is private per Singleton pattern.
	 */
	private EventRoutingHub() {
		this.mode = new SimpleObjectProperty<ViewMode>();
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static EventRoutingHub getInstance() {
		if( instance==null) {
			synchronized(EventRoutingHub.class) {
				instance = new EventRoutingHub();
			}
		}
		return instance;
	}
	
	// Application view mode is determined from the choose list.
	public ViewMode getMode() { return mode.get(); }
	// Run after current activity to avoid ConcurrentModification exception
	public void setMode(ViewMode mt) { 
		mode.set(mt); 
	}
	public void addModeListener(ChangeListener<ViewMode> listener) {
		mode.addListener(listener);
	}
	public SimpleObjectProperty<ViewMode> modeProperty(){
	    return mode;
	}
	
}
