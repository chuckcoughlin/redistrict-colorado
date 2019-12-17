/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
/**
 * Implement a standard event dispatcher for use by a chain that
 * simply dispatches the event to its list of targets.
 */
public class BasicEventDispatcher<T extends Event> implements EventDispatcher {
	private final EventHandler<T> handler;
	
	public BasicEventDispatcher(EventHandler<T> h) {
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
