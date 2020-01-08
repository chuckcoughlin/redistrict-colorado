/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.logging.Logger;

import org.openjump.io.ShapefileReader;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.layer.LayerTableView;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;
import redistrict.colorado.ui.navigation.LayerNavigationPane;

/**
 * Display the shapefile demographic information in table form.
 */
	public class LayerDetailPane extends BasicRightSideNode {
		private final static String CLSS = "LayerDetailPane";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private LayerModel model;
		private LayerNavigationPane navPane = new LayerNavigationPane();
		private final Label headerLabel = new Label("Layer Details");
		private final LayerTableView table;
		private final CheckBox showAllColumns;  // Including the hidden ones
		
		public LayerDetailPane() {
			super(ViewMode.LAYER,DisplayOption.DETAIL);
			this.model = hub.getSelectedLayer();
			this.showAllColumns = new CheckBox("Show All");
			this.table = new LayerTableView(model);
			showAllColumns.setIndeterminate(false);
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			getChildren().add(showAllColumns);
			getChildren().add(navPane);
			setTopAnchor(headerLabel,0.);
			setTopAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
			setLeftAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(navPane,0.);
			setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			updateModel();
		}

		@Override
		public void updateModel() {
			model = hub.getSelectedLayer();
			if( model!=null) {
				table.setModel(model);
				table.populate(showAllColumns.isSelected());
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
		}
	
	
}
