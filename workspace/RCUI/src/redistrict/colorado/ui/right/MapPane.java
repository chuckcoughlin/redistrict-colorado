/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.io.IOException;
import java.util.logging.Logger;

import org.openjump.io.ShapefileReader;

import javafx.css.Style;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;
import redistrict.colorado.ui.navigation.LayerNavigationPane;

/**
 * Plot a shapefile. Parent is an AnchorPane.
 */
	public class MapPane extends BasicRightSideNode {
		private final static String CLSS = "MapPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private LayerNavigationPane navPane = new LayerNavigationPane();
		private Label headerLabel = new Label("Map Canvas");
		private LayerModel model;
		
		public MapPane() {
			super(ViewMode.LAYER,DisplayOption.MAP);
			this.model = hub.getSelectedLayer();
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			
			try {
				MapCanvas canvas = new MapCanvas(UIConstants.SCENE_WIDTH, UIConstants.SCENE_HEIGHT);
				Pane pane = new	Pane(canvas.getCanvas());
			} 
			catch (Exception e) {
					e.printStackTrace();
			}
			
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
			if( model.getFeatures()==null ) {
				try {
					model.setFeatures(ShapefileReader.read(model.getShapefilePath()));
				}
				catch( Exception ex) {
					model.setFeatures(null);
					String msg = String.format("%s: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
					LOGGER.warning(msg);
					EventBindingHub.getInstance().setMessage(msg);
				}
			}
		}
		
		private void initMap() {
			try {
				FileDataStore store = FileDataStoreFinder.getDataStore(this.getClass().getResource("countries.shp"));
				SimpleFeatureSource featureSource = store.getFeatureSource();
				map = new MapContent();
				map.setTitle("Quickstart");
				Style style = SLD.createSimpleStyle(featureSource.getSchema());
				FeatureLayer layer = new FeatureLayer(featureSource, style);
				map.addLayer(layer);
				map.getViewport().setScreenArea(new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
