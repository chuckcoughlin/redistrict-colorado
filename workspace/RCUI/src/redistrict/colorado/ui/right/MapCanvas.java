/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;
import redistrict.colorado.ui.navigation.LayerNavigationPane;

/**
 * Display a shapefile without any additional attribution.
 */
	public class MapCanvas extends BasicRightSideNode {
		private final static String CLSS = "MapCanvas";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private LayerNavigationPane navPane = new LayerNavigationPane();
		private Label headerLabel = new Label("Map Canvas");
		private LayerModel model;
		
		public MapCanvas() {
			super(ViewMode.LAYER,DisplayOption.MAP);
			this.model = hub.getSelectedLayer();
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			getChildren().add(navPane);
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(navPane,0.);
			setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		}
		
		@Override
		public void updateModel() {
			model = hub.getSelectedLayer();
			navPane.updateTextForModel();	
		}
}
