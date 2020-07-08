/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.dataset;
import java.util.logging.Logger;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a graphical layer that corresponds to a dataset. Parent is an AnchorPane.
 */
	public class DatasetMapPane extends BasicRightSideNode  {
		private final static String CLSS = "DatasetMapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private final Label headerLabel = new Label("District Map");
		private final DatasetMapRenderer map;
		private DatasetModel model;
		
		// This same screen is used in two contexts.
		public DatasetMapPane(ViewMode vm, DisplayOption dopt) {
			super(vm,dopt);
			this.model = hub.getSelectedDataset();
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);

			Canvas canvas = new Canvas(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN, 
					                   UIConstants.SCENE_HEIGHT-2.*UIConstants.BUTTON_PANEL_HEIGHT);
			getChildren().add(canvas);
			setTopAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
			setLeftAnchor(canvas,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(canvas,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(canvas,0.);
			
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			map = new DatasetMapRenderer(canvas);
			updateModel();
		}
		
		@Override
		public void updateModel() {
			DatasetModel selectedModel = hub.getSelectedDataset();
			if( selectedModel!=null) {
				model = selectedModel;
				headerLabel.setText(model.getName());
				LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
				map.updateModel(model);
			}
		}

}
