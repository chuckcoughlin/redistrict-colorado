/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.region;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RegionListHolder extends VBox {
	Label regionLabel = new Label("===== Region Definitions =======");
	public static final double FRAME_HEIGHT = 2000;
	public RegionListHolder() {
		this.setPrefHeight(FRAME_HEIGHT);
		this.getChildren().add(regionLabel);
	}
}
