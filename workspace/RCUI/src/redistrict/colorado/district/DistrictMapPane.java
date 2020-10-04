/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pref.PreferenceKeys;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a single district graphically. Add a google map backdrop.
 * Parent is an AnchorPane.
 */
	public class DistrictMapPane extends BasicRightSideNode {
		private final static String CLSS = "DistrictMapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private final Label headerLabel = new Label("Region Map");
		private final DistrictMapRenderer map;
		private DatasetModel model;
		private String region;
		
		public DistrictMapPane() {
			super(ViewMode.DISTRICT,DisplayOption.MODEL_MAP);
			this.model = hub.getSelectedDataset();
			this.region = hub.getSelectedRegion();
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			String key = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
			if( key==null || key.length()<GoogleMapView.MIN_KEY_LENGTH ) {
	    		EventBindingHub.getInstance().setMessage(
	    				"The application must be configured with a valid Google Maps API key before maps can be displayed.");
	    	}
			GoogleMapView view = new GoogleMapView(key,GoogleMapView.DISTRICT_PATH);
			view.setMinWidth(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN);
			view.setMinHeight(UIConstants.SCENE_HEIGHT);
			
			getChildren().add(view);
			setTopAnchor(view,UIConstants.BUTTON_PANEL_HEIGHT);
			setLeftAnchor(view,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(view,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(view,0.);
			
			map = new DistrictMapRenderer(view);
			updateModel();
		}
		
		@Override
		public void updateModel() {
			DatasetModel selectedModel = hub.getSelectedDataset();
			if( selectedModel!=null) {
				String region = hub.getSelectedRegion();
				model = selectedModel;
				headerLabel.setText(model.getName());
				LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
				map.updateModel(model,region);
			}
		}
}
