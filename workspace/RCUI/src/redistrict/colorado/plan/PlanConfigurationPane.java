/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.PlanLayer;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.layer.FCBooleanCellFactory;
import redistrict.colorado.layer.FCBooleanValueFactory;
import redistrict.colorado.layer.FCColorCellFactory;
import redistrict.colorado.layer.FCColorValueFactory;
import redistrict.colorado.layer.FCStringCellFactory;
import redistrict.colorado.layer.FCStringValueFactory;
import redistrict.colorado.layer.LayerConfigurationPane.BooleanCommitHandler;
import redistrict.colorado.layer.LayerConfigurationPane.ColorCommitHandler;
import redistrict.colorado.layer.LayerConfigurationPane.TableEventHandler;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

public class PlanConfigurationPane extends BasicRightSideNode implements EventHandler<ActionEvent> {
	private final static String CLSS = "PlanConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private final static double COL_TEXT_WIDTH = 100.;
	private final static double TABLE_OFFSET_TOP = 150.;
	private static final GuiUtil guiu = new GuiUtil();
	private Label headerLabel = new Label("Plan Configuration");
	private final SavePane savePane = new SavePane(this);
	private PlanModel model;
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");

	private final TextField nameField;
	private final TextField descriptionField;
	private final ObservableList<PlanLayer> items;
	private final TableView<PlanLayer> table;
	private final TableEventHandler cellHandler;


	public PlanConfigurationPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_CONFIGURATION);
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
		
		table = new TableView<PlanLayer>();
		table.setEditable(true);
		table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		TableColumn<PlanLayer,String> column;
		PLStringValueFactory valueFactory = new PLStringValueFactory();
		PLStringCellFactory cellFactory = new PLStringCellFactory();

		column = new TableColumn<>("Name");
		column.setMinWidth(COL_TEXT_WIDTH);
		column.setEditable(false);
		column.setCellValueFactory(valueFactory);
		table.getColumns().add(column);

		column = new TableColumn<>("Role");
		column.setMinWidth(COL_TEXT_WIDTH);
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
		updateLayers();
		configureTable();
	}
	
	private void configureDefinition() {
		if( model!=null ) {
			nameField.setText(model.getName());
			descriptionField.setText(model.getDescription());
		}
	}
	/**
	 * The table holds a list of all definied layers.
	 */
	private void updateLayers() {
		
	}
	private void configureTable() {	
        table.setItems(items);
	}

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * "Save" on the embedded pane.
	 */
	@Override
	public void handle(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	// ================================================= Event Handler ============================================
	public class TableEventHandler implements EventHandler<TableColumn.CellEditEvent<PlanLayer,String>>  {
		/**
		 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
		 * Loss of focus is not enough.
		 */
		@Override
		public void handle(CellEditEvent<PlanLayer, String> event) {
			int row = event.getTablePosition().getRow();
			String column = event.getTableColumn().getText();
			String newValue = event.getNewValue();
			List<PlanLayer> items = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
			PlanLayer item = items.get(row);
			if( column.equalsIgnoreCase("Name") ) {
				item.setAlias(newValue);
			}
			else if( column.equalsIgnoreCase("Role") ) {
				try {
					item.setAttributeType(AttributeType.valueOf(newValue));
				}
				catch(IllegalArgumentException iae) {
					LOGGER.warning(String.format("%s.handle %s: Bad value for AttributeType - %s (%s)",CLSS,newValue,iae.getLocalizedMessage()));
				}
			}

		}
	}

}
