/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;

import javafx.scene.Node;
import javafx.scene.control.Label;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;
import redistrict.colorado.ui.navigation.LayerNavigationPane;

/**
 * Plot a single layer graphically. Parent is an AnchorPane.
 */
	public class MapPane extends BasicRightSideNode {
		private final static String CLSS = "MapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private LayerNavigationPane navPane = new LayerNavigationPane();
		private Label headerLabel = new Label("Map Canvas");
		private LayerModel model;
		private MapRenderer map;
		
		public MapPane() {
			super(ViewMode.LAYER,DisplayOption.MAP);
			this.model = hub.getSelectedLayer();
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			
			map = new MapRenderer(UIConstants.SCENE_WIDTH, UIConstants.SCENE_HEIGHT);
			Node canvas = map.getCanvas();
			getChildren().add(canvas);

			getChildren().add(navPane);
			setTopAnchor(headerLabel,0.);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			setLeftAnchor(canvas,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(canvas,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			
			setBottomAnchor(navPane,0.);
			setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			updateModel();
		}
		
		@Override
		public void updateModel() {
			LayerModel selectedModel = hub.getSelectedLayer();
			if( selectedModel!=null) {
				model = selectedModel;;
				LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
				navPane.updateTextForModel();
				if( model.getFeatures()==null ) {
					try {
						model.setFeatures(ShapefileReader.read(model.getShapefilePath()));
						map.updateModel(model);
					}
					catch( Exception ex) {
						model.setFeatures(null);
						String msg = String.format("%s: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
						LOGGER.warning(msg);
						EventBindingHub.getInstance().setMessage(msg);
					}
				}
				map.updateModel(model);
			}
		}
}
