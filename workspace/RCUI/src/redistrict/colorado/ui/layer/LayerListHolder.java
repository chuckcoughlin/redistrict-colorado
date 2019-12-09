/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.layer;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;



public class LayerListHolder extends VBox {
	public static final double FRAME_HEIGHT = 2000;
	Label layerLabel = new Label("===== Layer Definitions =======");
	
	public LayerListHolder() {
		this.setPrefHeight(FRAME_HEIGHT);
		this.getChildren().add(layerLabel);
	}
}
