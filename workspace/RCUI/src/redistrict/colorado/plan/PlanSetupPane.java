/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.ArrayList;
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
import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PartisanMetric;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.gate.Gate;
import redistrict.colorado.gate.GateCache;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.StringEditorCellFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
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
	private static final GuiUtil guiu = new GuiUtil();
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 400.;
	private final static double TABLE_OFFSET_TOP = 230.;
	// Column names for the table
	private final static String KEY_NAME = "Name";
	private final static String KEY_FAIR = "Fair";
	private final static String KEY_UNFAIR = "Unfair";
	private final static String KEY_TYPE = "Type";
	private final static String KEY_WEIGHT = "Weight";
	
	private Label headerLabel = new Label("Analysis Setup");
	private final SavePane savePane = new SavePane(this);
	private final Button info;
	protected final SettingsInformation infoDialog;
	private AnalysisModel model;
	private final GridPane grid;
	private final Label affiliationLabel = new Label("Affiliation: ");
	private final Label demographicsLabel = new Label("Demographics: ");
	private final Label countyBoundariesLabel = new Label("County Boundaries: ");
	private final Label competitivenessLabel = new Label("Competitive Threshold: ");
	private final Label partisanAsymmetryLabel = new Label("Partisan Asymmetry Metric: ");
	private final TextField competitivenessField = new TextField();
	private final ComboBox<String> affiliationCombo;
	private final ComboBox<String> countyCombo;
	private final ComboBox<String> demographicCombo;
	private final ComboBox<String> partisanCombo;
	private final ObservableList<NameValue> items;  // Array displayed in table
	private final TableView<NameValue> table;
	private final TableEventHandler cellHandler;


	public PlanSetupPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_SETUP);
		this.model = EventBindingHub.getInstance().getAnalysisModel();
		this.items = FXCollections.observableArrayList();
		this.cellHandler = new TableEventHandler();
		this.infoDialog = new SettingsInformation();
		info = new Button("",guiu.loadImage("images/information.png"));
		info.setOnAction( new EventHandler<ActionEvent>() {
	        @Override public void handle( ActionEvent e ) {
	        	showDialog(); 
	        }
	    } );
		info.setId(ComponentIds.BUTTON_INFO);
		
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
        affiliationCombo  = new ComboBox<>();
        countyCombo = new ComboBox<>();
        demographicCombo = new ComboBox<>();
        partisanCombo = new ComboBox<>();
        affiliationCombo.setPrefWidth(COL1_WIDTH);
        countyCombo.setPrefWidth(COL1_WIDTH);
        demographicCombo.setPrefWidth(COL1_WIDTH);
        partisanCombo.setPrefWidth(COL1_WIDTH);
        
	    Tooltip tt = new Tooltip("The threshold is the vote differential between parties ~ percent. Valid ranges is 1. to 60.");
	    Tooltip.install(competitivenessLabel, tt);
        
		table = new TableView<NameValue>();
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
		grid.add(countyBoundariesLabel, 0, 2);
		grid.add(countyCombo, 1, 2);
		grid.add(partisanAsymmetryLabel, 0, 3);
		grid.add(partisanCombo, 1, 3);
		grid.add(competitivenessLabel, 0, 4);
		grid.add(competitivenessField, 1, 4);
		
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		TableColumn<NameValue,String> column;
		NameValueCellValueFactory valueFactory = new NameValueCellValueFactory();
		valueFactory.setFormat(KEY_WEIGHT, "%2.1f");
		valueFactory.setFormat(KEY_UNFAIR, "%2.3f");
		valueFactory.setFormat(KEY_FAIR, "%2.3f");
		StringEditorCellFactory cellFactory = new StringEditorCellFactory();

		column = new TableColumn<>(KEY_NAME);
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(valueFactory);
		table.getColumns().add(column);

		column = new TableColumn<>(KEY_WEIGHT);
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);
		
		column = new TableColumn<>(KEY_UNFAIR);
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);
		
		column = new TableColumn<>(KEY_FAIR);
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
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
		getChildren().add(info);
		setRightAnchor(info,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setTopAnchor(info,TABLE_OFFSET_TOP);
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
		List<String> boundaries = Database.getInstance().getDatasetTable().getDatasetNamesForRole(DatasetRole.BOUNDARIES);
		countyCombo.getItems().clear();
		countyCombo.getItems().addAll(boundaries);
		partisanCombo.getItems().clear();
		partisanCombo.getItems().addAll(PartisanMetric.labels());
	}
	/**
	 * Update the table's combo boxes from the database and caches. If the model has no datasets, read them from the database.
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
			DatasetModel countyModel = DatasetCache.getInstance().getDataset(model.getCountyBoundariesId());
			if( countyModel!=null ) {
				countyCombo.getSelectionModel().select(countyModel.getName());
			}
			List<GateProperty> gateProperties = Database.getInstance().getGateTable().getGateProperties();
			for( GateProperty gp:gateProperties ) {
				if(gp.getType().equals(GateType.COMPOSITE) ) continue;
				Gate gate = GateCache.getInstance().getGate(gp.getType());
				NameValue nv = new NameValue(gate.getTitle());
				nv.setValue(KEY_TYPE, gp.getType());
				nv.setValue(KEY_WEIGHT, gp.getWeight());
				nv.setValue(KEY_FAIR, gp.getFairValue());
				nv.setValue(KEY_UNFAIR, gp.getUnfairValue());
				items.add(nv);
			}
			PartisanMetric metric = model.getPartisanMetric();
			partisanCombo.getSelectionModel().select(PartisanMetric.labelForMetric(metric));
		}
	}
	private void configureTable() {
		competitivenessField.setText(String.valueOf(model.getCompetitiveThreshold()));
		table.setItems(items);
	}
	
	public void showDialog() {
		try {
			// Trying to do this twice throws the exception
			infoDialog.initOwner(getScene().getWindow());
		}
		catch(IllegalStateException ignore) {}
        infoDialog.showAndWait();
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
				name = countyCombo.getSelectionModel().getSelectedItem();
				DatasetModel cbModel = DatasetCache.getInstance().getDataset(name);
				if( cbModel!=null ) {
					if( model.getCountyBoundariesId()!=cbModel.getId()) {
						model.setCountyBoundariesId(cbModel.getId());
						model.updateCountyFeatures();
						for(PlanModel plan:hub.getPlans()) {
							plan.setMetrics(null);
							Database.getInstance().getPlanTable().clearMetrics(plan.getId());
						}
					}
				}
				name = partisanCombo.getSelectionModel().getSelectedItem();
				PartisanMetric metric = PartisanMetric.metricForLabel(name);
				model.setPartisanMetric(metric);
				LOGGER.info(String.format("%s.save: metric = %s",CLSS,metric.name()));
				try {
					model.setCompetitiveThreshold(Double.parseDouble(competitivenessField.getText()));
				}
				catch(NumberFormatException nfe) {
					LOGGER.severe(String.format("%s.save: Failed to save competetiveness field %s (%s)",CLSS,competitivenessField.getText(),nfe.getLocalizedMessage()));
				}

				// Update model in the database
				Database.getInstance().getPreferencesTable().updateAnalysisModel(model);
				LOGGER.info(String.format("%s.save = %s",CLSS,competitivenessField.getText()));
				// Update gate properties
				List<NameValue> properties = table.getItems();
				for(NameValue nv:properties) {
					GateType type = (GateType)nv.getValue(KEY_TYPE);
					GateProperty gp = new GateProperty(type,
							(Double)nv.getValue(KEY_WEIGHT),
							(Double)nv.getValue(KEY_FAIR),
							(Double)nv.getValue(KEY_UNFAIR) );
					Database.getInstance().getGateTable().updateGateProperties(gp);	
				}
			}
		}
	}
	// ================================================= Event Handler ============================================
	public class TableEventHandler implements EventHandler<TableColumn.CellEditEvent<NameValue,String>>  {
		/**
		 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
		 * Loss of focus is not enough.
		 */
		@Override
		public void handle(CellEditEvent<NameValue, String> event) {
			int row = event.getTablePosition().getRow();
			String column = event.getTableColumn().getText();
			String newValue = event.getNewValue();
			List<NameValue> items = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));

			NameValue item = items.get(row);
			try {
				item.setValue(column,Double.parseDouble(newValue));
			}
			catch(NumberFormatException nfe) {
				LOGGER.warning(String.format("%s.handle %s=%s is not a double (%s)",CLSS,column,newValue,nfe.getLocalizedMessage()));

			}
		}
	}
	
	

}
