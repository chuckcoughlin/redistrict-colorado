/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.LayerNavigationPane;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a single layer graphically. Parent is an AnchorPane.
 */
	public class ModelMapPane extends BasicRightSideNode {
		private final static String CLSS = "ModelMapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private final LayerNavigationPane navPane = new LayerNavigationPane();
		private final Label headerLabel = new Label("Map");
		private final ModelMapRenderer map;
		private LayerModel model;
		
		
		public ModelMapPane() {
			super(ViewMode.LAYER,DisplayOption.MODEL_MAP);
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
			
			Canvas canvas = new Canvas(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN, 
					                   UIConstants.SCENE_HEIGHT-3*UIConstants.BUTTON_PANEL_HEIGHT);
			getChildren().add(canvas);
			setTopAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
			setLeftAnchor(canvas,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(canvas,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
			
			map = new ModelMapRenderer(canvas);
			updateModel();
		}
		
		@Override
		public void updateModel() {
			LayerModel selectedModel = hub.getSelectedLayer();
			if( selectedModel!=null) {
				model = selectedModel;;
				LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
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
