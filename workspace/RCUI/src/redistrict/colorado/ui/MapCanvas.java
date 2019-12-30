/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Display a shapefile without any additional attribution.
 */
	public class MapCanvas extends AnchorPane {
		private final static String CLSS = "MapCanvas";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private Label headerLabel = new Label("Map Canvas");
		
		public MapCanvas() {
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		}
	
}
