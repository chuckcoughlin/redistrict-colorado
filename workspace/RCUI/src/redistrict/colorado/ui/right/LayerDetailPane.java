/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openjump.feature.Feature;
import org.openjump.io.ShapefileReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.layer.FeatureDataFactory;
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
		private final ObservableList<Feature> items;
		private LayerNavigationPane navPane = new LayerNavigationPane();
		private final Label headerLabel = new Label("Layer Details");
		private final TableView<Feature> table;
		private final CheckBox showAllColumns;  // Including the hidden ones
		
		public LayerDetailPane() {
			super(ViewMode.LAYER,DisplayOption.DETAIL);
			this.model = hub.getSelectedLayer();
			this.items = FXCollections.observableArrayList();
			this.showAllColumns = new CheckBox("Show All");
			this.table = new TableView<Feature>();
	        table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
	        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	        
			showAllColumns.setIndeterminate(false);
			headerLabel.getStyleClass().add("list-header-label");
			getChildren().add(headerLabel);
			getChildren().add(showAllColumns);
			getChildren().add(table);
			getChildren().add(navPane);
			setTopAnchor(headerLabel,0.);
			setTopAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
			setTopAnchor(table,UIConstants.BUTTON_PANEL_HEIGHT);
			setLeftAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
			setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			setBottomAnchor(table,UIConstants.BUTTON_PANEL_HEIGHT);
			setBottomAnchor(navPane,0.);
			setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
			setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
			updateModel();
		}

		@Override
		public void updateModel() {
			LayerModel selectedModel = hub.getSelectedLayer();
			if( selectedModel!=null) {
				this.model = selectedModel;
				LOGGER.info(String.format("%s.updateModel: Model is %s", CLSS,model.getName()));
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
					Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), model.getFeatures().getFeatureSchema().getAttributeNames());
				}
				navPane.updateTextForModel();
				table.getColumns().clear();
				items.clear();
				for(Feature feat:model.getFeatures().getFeatures()) {
					items.add(feat);
				}
				
				TableColumn<Feature,String> column;
		        Map<String,String> aliasMap = new HashMap<>();
		        
		        FeatureDataFactory factory = new FeatureDataFactory(aliasMap);
		        List<FeatureConfiguration> configurations = Database.getInstance().getFeatureAttributeTable().getFeatureAttributes(model.getId());
		        boolean showAll = showAllColumns.isSelected();
		        
		        for(FeatureConfiguration fc:configurations) {
		        	if(fc.isVisible()||showAll ) {
		        		column = new TableColumn<>(fc.getAlias());
		        		column.setCellValueFactory(factory);
		        		//LOGGER.info(String.format("%s.updateModel: Added column %s", CLSS,fc.getAlias()));
		        		table.getColumns().add(column);
		        	}
		        }
		        LOGGER.info(String.format("%s.updateModel: Table has %d rows", CLSS,items.size()));
		        table.setItems(items);
			}
		}
	
	
}
