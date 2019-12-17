/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
/**
 * Modify the standard event dispatching by creating a chain that
 * simply dispatches the event to a pre-determined list of targets.
 * Each target must register. Targets may be anywhere on the stage.
 * Dispatch order is from the end of the chain to the beginning.
 * 
 * The internal list is not thread-safe. Our use case assumes all
 * chains are setup in the beginning of the application and are 
 * static from then on.
 */
public class BasicEventDispatchChain<T extends Event> implements EventDispatchChain  {
	private List<BasicEventDispatcher<T>> dispatchers;
	
	public BasicEventDispatchChain() {
		dispatchers = new ArrayList<>();
	}

	public BasicEventDispatchChain<T> append(EventDispatcher ed) {
		if( ed instanceof BasicEventDispatcher) {
			dispatchers.add((BasicEventDispatcher<T>)ed);
		}
		return this;
	}


	public Event dispatchEvent(Event event) {
		for(BasicEventDispatcher<T> ed:dispatchers) {
			event = ed.dispatchEvent(event, this);
			if( event==null ) break;     // Event was consumed
		}
		return event;
	}

	/**
	 * Add a dispatcher to the beginning of the chain. This is much more
	 * expensive than appending.
	 * @return the original chain.
	 */

	public BasicEventDispatchChain<T> prepend(EventDispatcher ed) {
		try {
			if( ed instanceof BasicEventDispatcher) {
				dispatchers.add(0,(BasicEventDispatcher<T>)ed);
			}
		}
		catch(ClassCastException cce) {

		}
		return this;
	}

}
