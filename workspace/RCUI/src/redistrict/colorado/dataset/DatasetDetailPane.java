/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.dataset;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Display a table containing feature details of a particular shapefile.
 */
public class DatasetDetailPane extends BasicRightSideNode {
	private final static String CLSS = "DatasetDetailPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	public static final double DETAIL_TABLE_HEIGHT = 600; // Preferred
	public static final double DETAIL_TABLE_WIDTH  = 400; 
	private DatasetModel model;
	private final ObservableList<Feature> items;
	private final Label headerLabel = new Label("Layer Details");
	private final TableView<Feature> table;
	private final CheckBox showAllColumns;  // Including the hidden ones
	private final EventHandler<ActionEvent> eventHandler;

	public DatasetDetailPane() {
		super(ViewMode.DATASET,DisplayOption.MODEL_DETAIL);
		this.model = hub.getSelectedDataset();
		this.items = FXCollections.observableArrayList();
		this.showAllColumns = new CheckBox("Show All");
		this.table = new TableView<Feature>();
		this.eventHandler = new ActionEventHandler();
		table.setPrefSize(DETAIL_TABLE_WIDTH, DETAIL_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		showAllColumns.setIndeterminate(false);
		showAllColumns.setOnAction(eventHandler);
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(showAllColumns);
		getChildren().add(table);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
		setLeftAnchor(showAllColumns,UIConstants.BUTTON_PANEL_HEIGHT/5);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setTopAnchor(table,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(table,0.);
		updateModel();
	}

	@Override
	public void updateModel() {
		DatasetModel selectedModel = hub.getSelectedDataset();
		if( selectedModel!=null) {
			this.model = selectedModel;
			headerLabel.setText(model.getName());
			LOGGER.info(String.format("%s.updateModel: Model is %s", CLSS,model.getName()));
			table.getColumns().clear();
			items.clear();
			FeatureCollection collection = model.getFeatures();
			if( collection!=null ) {
				for(Feature feat:model.getFeatures().getFeatures()) {
					items.add(feat);
				}
			}

			TableColumn<Feature,String> column;
			Map<String,String> aliasMap = Database.getInstance().getFeatureAttributeTable().getNamesForFeatureAliases(model.getId());
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
	
	/**
	 * The checkbox has been selected. Update the model
	 */
	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			updateModel();
		}
	}

}
