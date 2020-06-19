/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.NavigationPane;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a single district graphically. Add a google map backdrop.
 * Parent is an AnchorPane.
 */
	public class DistrictMapPane extends BasicRightSideNode implements EventHandler<ActionEvent>,ChangeListener<Number> {
		private final static String CLSS = "DistrictMapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private final NavigationPane navPane = new NavigationPane(this,this);
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
			
			getChildren().add(navPane);
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			setBottomAnchor(navPane,0.);
			setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			Canvas canvas = new Canvas(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN, 
					                   UIConstants.SCENE_HEIGHT-3*UIConstants.BUTTON_PANEL_HEIGHT);
			getChildren().add(canvas);
			setTopAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
			setLeftAnchor(canvas,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(canvas,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
			
			map = new DistrictMapRenderer(canvas);
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

		/**
		 * The zoom slider has been moved on the navigation pane.
		 */
		@Override
		public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * An arrow was selected on the navigation pane
		 */
		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
}
