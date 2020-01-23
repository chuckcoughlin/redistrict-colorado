/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import javafx.event.Event;

/**
 * In order to consume an event, the class must make its dispatcher available. 
 */
public interface EventReceiver<T extends Event> {	
	public BasicEventDispatcher<T> getAuxillaryEventDispatcher();
}
