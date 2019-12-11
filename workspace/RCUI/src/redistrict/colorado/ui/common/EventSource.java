/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.common;

import javafx.event.Event;

/**
 * An event source is a component that creates events and distributes them
 * to registered receivers. 
 */
public interface EventSource<T extends Event> {
	
	public void registerEventReceiver(RCEventDispatcher<T> rce);
}
