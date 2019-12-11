/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.common;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
/**
 * Modify the standard event dispatching by creating a chain that
 * simply dispatches the event to a pre-determined list of targets.
 * Each target must register. Targets may be anywhere on the stage.
 * Dispatch order is from the end of the chain to the beginning.
 */
public class RCEventDispatcher<T extends Event> implements EventDispatcher {
	private final EventHandler<T> handler;
	
	public RCEventDispatcher(EventHandler<T> h) {
		this.handler = h;
	}



	
	/**
	 * Dispatching consists of transferring the event to the handler.
	 * The chain is superfluous at this point.
	 */
	@Override
	public Event dispatchEvent(Event event, EventDispatchChain ignore) {
		try {
			handler.handle((T)event);
		}
		catch(ClassCastException cce) {
			
		}
		return event;
	}

}
