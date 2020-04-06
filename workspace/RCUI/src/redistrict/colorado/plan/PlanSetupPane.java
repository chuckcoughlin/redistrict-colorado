/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.db.PreferencesTable;
import redistrict.colorado.gate.Gate;
import redistrict.colorado.gate.GateCache;
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
	private final static double COL1_WIDTH = 400.;
	private final static double TABLE_OFFSET_TOP = 210.;
	private Label headerLabel = new Label("Analysis Setup");
	private final SavePane savePane = new SavePane(this);
	private AnalysisModel model;
	private final GridPane grid;
	private final Label affiliationLabel = new Label("Affiliation: ");
	private final Label demographicsLabel = new Label("Demographics: ");
	private final Label competitivenessLabel = new Label("Competitive Threshold: ");
	private final Label efficiencyGapLabel = new Label("Efficiency Gap Threshold: ");
	private final Label populationEqualityLabel = new Label("Population Equality Threshold: ");
	private final TextField competitivenessField = new TextField();
	private final TextField efficiencyGapField = new TextField();
	private final TextField populationEqualityField = new TextField();
	private final ComboBox<String> affiliationCombo;
	private final ComboBox<String> demographicCombo;
	private final ObservableList<Gate> items;  // Array displayed in table
	private final TableView<Gate> table;
	private final TableEventHandler cellHandler;


	public PlanSetupPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_SETUP);
		this.model = EventBindingHub.getInstance().getAnalysisModel();
		this.items = FXCollections.observableArrayList();
		this.cellHandler = new TableEventHandler();
		
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
        affiliationCombo  = new ComboBox<>();
        demographicCombo = new ComboBox<>();
        affiliationCombo.setPrefWidth(COL1_WIDTH);
        demographicCombo.setPrefWidth(COL1_WIDTH);
        
	    Tooltip tt = new Tooltip("The threshold is the vote differential between parties ~ percent. Valid ranges is 1. to 60.");
	    Tooltip.install(competitivenessLabel, tt);
	    tt = new Tooltip("The threshold marks the maximum efficiency gap considered to be non-gerrymandered.");
	    Tooltip.install(efficiencyGapLabel, tt);
	    tt = new Tooltip("This value is the maximum allowed differential between a district's population and the mean.");
	    Tooltip.install(populationEqualityLabel, tt);
        
		table = new TableView<Gate>();
		table.setEditable(true);
		//table.setPrefSize(SETUP_TABLE_WIDTH,SETUP_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints(COL0_WIDTH);
		col0.setHalignment(HPos.LEFT);
		col0.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
		ColumnConstraints col1 = new ColumnConstraints(COL1_WIDTH);
		col1.setHalignment(HPos.LEFT);
		col1.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
		col1.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col0,col1); 
		grid.add(affiliationLabel,0, 0);
		grid.add(affiliationCombo, 1, 0);
		grid.add(demographicsLabel, 0, 1);
		grid.add(demographicCombo, 1, 1);
		grid.add(competitivenessLabel, 0, 2);
		grid.add(competitivenessField, 1, 2);
		grid.add(efficiencyGapLabel, 0, 3);
		grid.add(efficiencyGapField, 1, 3);
		grid.add(populationEqualityLabel, 0, 4);
		grid.add(populationEqualityField, 1, 4);
		
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		TableColumn<Gate,String> column;
		PreferenceStringValueFactory valueFactory = new PreferenceStringValueFactory();
		PreferenceStringCellFactory cellFactory = new PreferenceStringCellFactory();

		column = new TableColumn<>("Metric");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.7));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(valueFactory);
		table.getColumns().add(column);

		column = new TableColumn<>("Weight");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);

		getChildren().add(table);
		setTopAnchor(table,TABLE_OFFSET_TOP);
		setLeftAnchor(table,4*UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(table,4*UIConstants.BUTTON_PANEL_HEIGHT);
		getChildren().add(savePane);
		setLeftAnchor(savePane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(savePane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(savePane,0.);
       
		configureComboBoxes();
		updateDatasets();
		configureTable();
	}
	
	private void configureComboBoxes() {
		List<String> affiliations = Database.getInstance().getDatasetTable().getDatasetNamesForRole(DatasetRole.AFFILIATIONS);
		affiliationCombo.getItems().clear();
		affiliationCombo.getItems().addAll(affiliations);
		List<String> demographics = Database.getInstance().getDatasetTable().getDatasetNamesForRole(DatasetRole.DEMOGRAPHICS);
		demographicCombo.getItems().clear();
		demographicCombo.getItems().addAll(demographics);
	}
	/**
	 * Update the table's dataset list from the model. If the model has no datasets, read them from the database.
	 * Next update the weights.
	 */
	private void updateDatasets() {
		if( model!=null ) {
			items.clear();
			DatasetModel affModel = DatasetCache.getInstance().getDataset(model.getAffiliationId());
			if( affModel!=null ) {
				affiliationCombo.getSelectionModel().select(affModel.getName());
			}
			DatasetModel demModel = DatasetCache.getInstance().getDataset(model.getDemographicId());
			if( demModel!=null ) {
				demographicCombo.getSelectionModel().select(demModel.getName());
			}
			items.addAll(GateCache.getInstance().getBasicGates());
		}
	}
	private void configureTable() {
		competitivenessField.setText(Database.getInstance().getPreferencesTable().getParameter(PreferencesTable.COMPETITIVENESS_THRESHOLD_KEY));
		efficiencyGapField.setText(Database.getInstance().getPreferencesTable().getParameter(PreferencesTable.EFFICIENCY_GAP_THRESHOLD_KEY));
		populationEqualityField.setText(Database.getInstance().getPreferencesTable().getParameter(PreferencesTable.POPULATION_EQUALITY_THRESHOLD_KEY));
		table.setItems(items);
	}

	// ====================================== BasicRightSideNode =====================================
	@Override
	public void updateModel() {
		this.model = EventBindingHub.getInstance().getAnalysisModel();
		configureComboBoxes();
		updateDatasets();
		configureTable();
	}

	/**
	 * On a "save", update the model object, the database and then the hub. If either affiliation or
	 * demographics datasets change, invalidate the cached  model metrics.
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if( source instanceof Button && ((Button)source).getId().equals(ComponentIds.BUTTON_SAVE)) {
			if(model!=null) {
				EventBindingHub hub = EventBindingHub.getInstance();
				// Update the model from UI elements
				String name = affiliationCombo.getSelectionModel().getSelectedItem();
				DatasetModel affModel = DatasetCache.getInstance().getDataset(name);
				if( affModel!=null ) {
					if( model.getAffiliationId()!=affModel.getId()) {
						model.setAffiliationId(affModel.getId());
						model.updateAffiliationFeatures();
						for(PlanModel plan:hub.getPlans()) {
							plan.setMetrics(null);
							Database.getInstance().getPlanTable().clearMetrics(plan.getId());
						}
					}

				}
				
				name = demographicCombo.getSelectionModel().getSelectedItem();
				DatasetModel demModel = DatasetCache.getInstance().getDataset(name);
				if( demModel!=null ) {
					if( model.getDemographicId()!=demModel.getId()) {
						model.setDemographicId(demModel.getId());
						model.updateDemographicFeatures();
						for(PlanModel plan:hub.getPlans()) {
							plan.setMetrics(null);
							Database.getInstance().getPlanTable().clearMetrics(plan.getId());
						}
					}
				}
				// Update model in the database
				Database.getInstance().getPreferencesTable().updateAnalysisModel(model);
				LOGGER.info(String.format("%s.save = %s",CLSS,competitivenessField.getText()));
				Database.getInstance().getPreferencesTable().setParameter(PreferencesTable.COMPETITIVENESS_THRESHOLD_KEY, competitivenessField.getText());
				Database.getInstance().getPreferencesTable().setParameter(PreferencesTable.EFFICIENCY_GAP_THRESHOLD_KEY, efficiencyGapField.getText());
				Database.getInstance().getPreferencesTable().setParameter(PreferencesTable.POPULATION_EQUALITY_THRESHOLD_KEY, populationEqualityField.getText());
				
			}
		}
	}
	// ================================================= Event Handler ============================================
	public class TableEventHandler implements EventHandler<TableColumn.CellEditEvent<Gate,String>>  {
		/**
		 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
		 * Loss of focus is not enough.
		 */
		@Override
		public void handle(CellEditEvent<Gate, String> event) {
			int row = event.getTablePosition().getRow();
			String column = event.getTableColumn().getText();
			String newValue = event.getNewValue();
			List<Gate> items = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
			
			Gate item = items.get(row);
			if( column.equalsIgnoreCase("Weight") ) {
				try {
					item.setWeight(Double.parseDouble(newValue));
				}
				catch(NumberFormatException nfe) {
					LOGGER.warning(String.format("%s.handle %s is not a double (%s)",CLSS,newValue,nfe.getLocalizedMessage()));
				}
			}
		}
	}
	
	

}
