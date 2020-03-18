/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This pane allows configuration of parameters used in plan comparisons.
 * These parameters include weightings, and mappings. 
 * @author chuckc
 *
 */
public class PlanSetupPane extends BasicRightSideNode 
									implements EventHandler<ActionEvent> {
	private final static String CLSS = "PlanSetupPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private final static double TABLE_OFFSET_TOP = 150.;
	private Label headerLabel = new Label("Plan Setup");
	private final SavePane savePane = new SavePane(this);
	private PlanModel model;
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");

	private final TextField nameField;
	private final TextField descriptionField;
	private final ObservableList<Property> items;  // Array displayed in table
	private final TableView<Property> table;
	private final TableEventHandler cellHandler;


	public PlanSetupPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_SETUP);
		this.model = EventBindingHub.getInstance().getSelectedPlan();
		this.items = FXCollections.observableArrayList();
		this.cellHandler = new TableEventHandler();
		
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
        nameField = new TextField();
        descriptionField = new TextField();
        
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints(COL0_WIDTH);
		col0.setHalignment(HPos.LEFT);
		ColumnConstraints col1 = new ColumnConstraints(COL1_WIDTH,COL1_WIDTH,Double.MAX_VALUE);
		col1.setHalignment(HPos.LEFT);
		col1.setHgrow(Priority.ALWAYS);
		ColumnConstraints col2 = new ColumnConstraints(COL2_WIDTH);
		col2.setHalignment(HPos.CENTER);
		grid.getColumnConstraints().addAll(col0,col1,col2); 
		grid.add(nameLabel,0, 0);
		grid.add(nameField, 1, 0);
		grid.add(descriptionLabel, 0, 1);
		grid.add(descriptionField, 1, 1);
		
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		table = new TableView<Property>();
		table.setEditable(true);
		table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		TableColumn<Property,String> column;
		PreferenceStringValueFactory valueFactory = new PreferenceStringValueFactory();
		PreferenceStringCellFactory cellFactory = new PreferenceStringCellFactory();

		column = new TableColumn<>("Name");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.7));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(valueFactory);
		table.getColumns().add(column);

		column = new TableColumn<>("Role");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);

		getChildren().add(table);
		setTopAnchor(table,TABLE_OFFSET_TOP);
		setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(table,UIConstants.BUTTON_PANEL_HEIGHT);
		getChildren().add(savePane);
		setLeftAnchor(savePane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(savePane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(savePane,0.);
       
		configureDefinition();
		updateDatasets();
		configureTable();
	}
	
	private void configureDefinition() {
		if( model!=null ) {
			nameField.setText(model.getBoundary().getName());
		}
	}
	/**
	 * Update the table's dataset list from the model. If the model has no datasets, read them from the database.
	 */
	private void updateDatasets() {
		if( model!=null ) {
			items.clear();
			/*
			try {
				model.setLayers(Database.getInstance().getPlanLayerTable().getDatasetRoles(model.getId()));
				LOGGER.info(String.format("%s.updateDatasets: There are %d dataset definitions", CLSS,model.getLayers().size()));
				for(PlanDataset player:model.getLayers()) {
					items.add(player);
				}
			}
			catch( Exception ex) {
				model.setLayers(null);
				String msg = String.format("%s.updateDatasets: Failed to read dataset definitions (%s)",CLSS,ex.getLocalizedMessage());
				LOGGER.warning(msg);
				ex.printStackTrace();
				EventBindingHub.getInstance().setMessage(msg);
			}
			// For purposes of the table, append layers not part of the model.
			List<PlanDataset> unused = Database.getInstance().getPlanLayerTable().getUnusedDatasetRoles(model.getId());
			for(PlanDataset player:unused) {
				items.add(player);
			}
			*/
		}
	}
	private void configureTable() {	
        table.setItems(items);
	}

	// ====================================== BasicRightSideNode =====================================
	@Override
	public void updateModel() {
		this.model = EventBindingHub.getInstance().getSelectedPlan();
		this.headerLabel.setText("Configure "+model.getBoundary().getName());
		configureDefinition();
		updateDatasets();
		configureTable();
	}

	/**
	 * On a "save", update the model object, the database and then the hub.
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if( source instanceof Button && ((Button)source).getId().equals(ComponentIds.BUTTON_SAVE)) {
			if(model!=null) {
				model.getBoundary().setName(nameField.getText());
				Database.getInstance().getPlanTable().updatePlan(model);
				// Update layer roles in the model
				/*
				List<Property> players = model.
				players.clear();
				for(Property player:items) {
					//if(!player.getRole().equals(DatasetRole.NONE)) players.add(player);
				}
				// Update layer roles in the database
				Database.getInstance().getPlanLayerTable().synchronizePlanDatasets(model);
				EventBindingHub.getInstance().unselectPlan();     // Force fire
				EventBindingHub.getInstance().setSelectedPlan(model);
				*/
			}
		}
	}
	// ================================================= Event Handler ============================================
	public class TableEventHandler implements EventHandler<TableColumn.CellEditEvent<Property,String>>  {
		/**
		 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
		 * Loss of focus is not enough.
		 */
		@Override
		public void handle(CellEditEvent<Property, String> event) {
			int row = event.getTablePosition().getRow();
			String column = event.getTableColumn().getText();
			String newValue = event.getNewValue();
			List<Property> items = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
			Property item = items.get(row);
			if( column.equalsIgnoreCase("Name") ) {
				item.setValue(newValue);
			}

		}
	}
	
	

}
