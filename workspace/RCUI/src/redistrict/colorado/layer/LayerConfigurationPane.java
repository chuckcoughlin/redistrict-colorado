/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.openjump.feature.AttributeType;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Window;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

public class LayerConfigurationPane extends BasicRightSideNode implements EventHandler<ActionEvent>{
	private final static String CLSS = "LayerConfigurationPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private final static double 
	private static final GuiUtil guiu = new GuiUtil();
	private final GridPane grid;
	private Label headerLabel = new Label("Layer Configuration");
	private final SavePane savePane = new SavePane(this);
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");
	private final Button fileButton = new Button("Shapefile: ");
	private final Label roleLabel = new Label("Role: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final TextField pathField;
	private final ComboBox<String> roleChooser;
	private Label indicator;
	private LayerModel model;
	private final ObservableList<FeatureConfiguration> items;
	private final TableView<FeatureConfiguration> table;

	public LayerConfigurationPane() {
		super(ViewMode.LAYER,DisplayOption.LAYER_CONFIGURATION);
		this.model = EventBindingHub.getInstance().getSelectedLayer();
		LOGGER.info(String.format("%s.CONSTRUCTOR:", CLSS));
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		this.nameField = new TextField();
		nameField.setOnAction(this);
        this.descriptionField = new TextField();
        fileButton.setOnAction(this);
        fileButton.setId(ComponentIds.BUTTON_SHAPEFILE);
        this.pathField = new TextField();
        pathField.setEditable(false);
        this.roleChooser = new ComboBox<>();
        roleChooser.getItems().addAll(LayerRole.names());
        this.indicator = new Label("",guiu.loadImage("images/ball_gray.png"));
        this.grid = new GridPane();
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
		grid.add(fileButton, 0, 2);
		grid.add(pathField, 1, 2);
		grid.add(roleLabel, 0, 3);
		grid.add(roleChooser, 1, 3);
		grid.add(indicator, 2, 3);
		
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		getChildren().add(savePane);
		setLeftAnchor(savePane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(savePane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(savePane,0.);
		
		table = new TableView<FeatureConfiguration>();
		table.setEditable(true);
		table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		getChildren().add(table);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		configureDefinition();
		configureTable();
       
        
/*
		setResultConverter(new Callback<ButtonType, LayerModel>() {
			@Override
			public LayerModel call(ButtonType b) {
				if (b == buttonOK) {
					model.setName(nameField.getText());
					model.setDescription(descriptionField.getText());
					model.setRole(LayerRole.valueOf(roleChooser.getValue()));
					// If path changes, re-analyze file
					if( !model.getShapefilePath().equalsIgnoreCase(pathField.getText())) {
						model.setShapefilePath(pathField.getText());
						try {
							model.setFeatures(ShapefileReader.read(pathField.getText()));
							LOGGER.info(String.format("%s.onSave: Shapefile has %d records, %d attributes", CLSS,model.getFeatures().getFeatures().size(),model.getFeatures().getFeatureSchema().getAttributeCount()));
						}
						catch( Exception ex) {
							model.setFeatures(null);
							String msg = String.format("%s.onSave: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
							LOGGER.warning(msg);
							EventBindingHub.getInstance().setMessage(msg);
						}
						Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), model.getFeatures().getFeatureSchema().getAttributeNames());
					}
					return model;
				}
				return null;
			}
		});
		*/
	}
	
	private void configureDefinition() {
		// If the model is non-null, fill in all the fields
		if( model!=null ) {
			LOGGER.info(String.format("%s.configureDefinition: model is %s", CLSS,model.getName()));
			nameField.setText(model.getName());
			descriptionField.setText(model.getDescription());
			pathField.setText(model.getShapefilePath());
	        roleChooser.getSelectionModel().select(model.getRole().name());
			if( model.getShapefilePath()!=null && !model.getShapefilePath().isEmpty() ) {
	        	if( model.getFeatures()==null){
	        		try {
						model.setFeatures(ShapefileReader.read(model.getShapefilePath()));
						LOGGER.info(String.format("%s.configureDefinition: Shapefile has %d records, %d attributes", CLSS,model.getFeatures().getFeatures().size(),model.getFeatures().getFeatureSchema().getAttributeCount()));
						Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), model.getFeatures().getFeatureSchema().getAttributeNames());
					}
					catch( Exception ex) {
						model.setFeatures(null);
						String msg = String.format("%s.configureDefinition: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
						LOGGER.warning(msg);
						ex.printStackTrace();
						EventBindingHub.getInstance().setMessage(msg);
					}
	        	}
	        	if( model.getFeatures()==null){
	        		indicator = new Label("",guiu.loadImage("images/ball_red.png"));
	        	}
	        	else {
	        		indicator = new Label("",guiu.loadImage("images/ball_green.png"));
	        	}
	        }
		}
	}
	
	private void configureTable() {	
	}
	/**
	 * Respond to button presses, including "Save"
	 */
	@Override
	public void handle(ActionEvent event) {
		// Find the file
		Object source = event.getSource();
		if( source.equals(fileButton)) {
			FileChooser fc = new FileChooser();
			Window window = new Popup();
			File file = fc.showOpenDialog(window); 
			LOGGER.info(String.format("File is %s",(file==null?"null":file.getAbsolutePath()))); 
			if (file != null) {      
				pathField.setText(file.getAbsolutePath()); 
			} 
		}
		// On a save, update the model object, the database and then the hub.
		else if( source instanceof Button && ((Button)source).getId().equals(ComponentIds.BUTTON_SAVE)) {
			if(model!=null) {
				model.setName(nameField.getText());
				model.setDescription(descriptionField.getText());
				model.setShapefilePath(pathField.getText());
				model.setRole(LayerRole.valueOf(roleChooser.getValue()));
				Database.getInstance().getLayerTable().updateLayer(model);
				EventBindingHub.getInstance().setSelectedLayer(model);
			}
		}

	}
	// ====================================== BasicRightSideNode =====================================
	@Override
	public void updateModel() {
		this.model = EventBindingHub.getInstance().getSelectedLayer();
		configureDefinition();
		configureTable();
		
	}
	
	// ======================================== Event Handler ========================================
	public class TableEventHandler implements EventHandler<TableColumn.CellEditEvent<FeatureConfiguration,String>>  {
		/**
		 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
		 * Loss of focus is not enough.
		 */
		@Override
		public void handle(CellEditEvent<FeatureConfiguration, String> event) {
			int row = event.getTablePosition().getRow();
			String column = event.getTableColumn().getText();
			String newValue = event.getNewValue();
			List<FeatureConfiguration> items = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
			FeatureConfiguration item = items.get(row);
			if( column.equalsIgnoreCase("Alias") ) {
				item.setAlias(newValue);
			}
			else if( column.equalsIgnoreCase("Type") ) {
				try {
					item.setAttributeType(AttributeType.valueOf(newValue));
				}
				catch(IllegalArgumentException iae) {
					LOGGER.warning(String.format("%s.handle %s: Bad value for AttributeType - %s (%s)",CLSS,newValue,iae.getLocalizedMessage()));
				}
			}
			else if( column.equalsIgnoreCase("Visible") ) {  // Handled by listener
			}
			else if( column.equalsIgnoreCase("Color") ) {	 // Handled by listener
			}
			else if( column.equalsIgnoreCase("Rank") ) {
				try {
					int rank = Integer.parseInt(newValue);
					item.setRank(rank);
				}
				catch(NumberFormatException nfe) {}
			}

		}
	}

}
