/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is the base shape for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends Rectangle {
	public Gate() {
		this.setHeight(200.);
		this.setWidth(200.);
		this.setFill(Color.web("#88d197"));
	}
	public abstract String getTitle(); 
}
