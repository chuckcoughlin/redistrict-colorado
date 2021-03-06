/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.dataset;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.openjump.feature.AttributeType;
import org.openjump.feature.FeatureSchema;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Window;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.table.FCBooleanCellFactory;
import redistrict.colorado.table.FCBooleanValueFactory;
import redistrict.colorado.table.FCColorCellFactory;
import redistrict.colorado.table.FCColorValueFactory;
import redistrict.colorado.table.FCStringCellFactory;
import redistrict.colorado.table.FCStringValueFactory;
import redistrict.colorado.table.TableCellCallback;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

public class DatasetEditPane extends BasicRightSideNode implements EventHandler<ActionEvent> {
	private final static String CLSS = "DatasetConfigurationPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double GRID0_WIDTH = 100.;    // Grid widths
	private final static double GRID1_WIDTH = 300.;
	private final static double GRID2_WIDTH = 40.;
	private static final double CONFIGURATION_TABLE_HEIGHT = 500; // Preferred
	private static final double CONFIGURATION_TABLE_WIDTH  = 600; 
	private final static double TABLE_OFFSET_TOP = 200.;
	private static final GuiUtil guiu = new GuiUtil();
	private final GridPane grid;
	private Label headerLabel = new Label("Dataset Configuration");
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
	private DatasetModel model;
	private final ObservableList<FeatureConfiguration> items;
	private final TableView<FeatureConfiguration> table;
	private final TableEventHandler cellHandler;
	

	public DatasetEditPane() {
		super(ViewMode.DATASET,DisplayOption.DATASET_DEFINITION);
		this.model = EventBindingHub.getInstance().getSelectedDataset();
		this.items = FXCollections.observableArrayList();
		this.cellHandler = new TableEventHandler();

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
        roleChooser.getItems().addAll(DatasetRole.names());
        this.indicator = new Label("",guiu.loadImage("images/ball_gray.png"));
        this.grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints(GRID0_WIDTH);
		col0.setHalignment(HPos.LEFT);
		ColumnConstraints col1 = new ColumnConstraints(GRID1_WIDTH,GRID1_WIDTH,Double.MAX_VALUE);
		col1.setHalignment(HPos.LEFT);
		col1.setHgrow(Priority.ALWAYS);
		ColumnConstraints col2 = new ColumnConstraints(GRID2_WIDTH);
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
		
		table = new TableView<FeatureConfiguration>();
		table.setEditable(true);
		table.setPrefSize(CONFIGURATION_TABLE_WIDTH, CONFIGURATION_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		TableColumn<FeatureConfiguration,String> column;
		FCStringValueFactory valueFactory = new FCStringValueFactory();
		FCStringCellFactory cellFactory = new FCStringCellFactory();

		column = new TableColumn<>("Name");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(valueFactory);
		table.getColumns().add(column);

		column = new TableColumn<>("Alias");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);

		column = new TableColumn<>("Type");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(true);
		column.setCellFactory(cellFactory);
		column.setCellValueFactory(valueFactory);
		column.setOnEditCommit(cellHandler);
		table.getColumns().add(column);

		TableColumn<FeatureConfiguration,Boolean> bcol;
		bcol = new TableColumn<>("Visible");
		bcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		bcol.setResizable(true);
		bcol.setEditable(true);
		bcol.setCellValueFactory(new FCBooleanValueFactory());
		bcol.setCellFactory(new FCBooleanCellFactory(new BooleanCommitHandler()));
		table.getColumns().add(bcol);

		TableColumn<FeatureConfiguration,Color> ccol;
		ccol = new TableColumn<>("Background");
		ccol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		ccol.setResizable(true);
		ccol.setEditable(true);
		ccol.setCellFactory(new FCColorCellFactory());
		ccol.setCellValueFactory(new FCColorValueFactory());
		ccol.setOnEditCommit(new ColorCommitHandler());
		table.getColumns().add(ccol);

		column = new TableColumn<>("Rank");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
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
		updateFeatures();
		configureTable();
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
	        		indicator = new Label("",guiu.loadImage("images/ball_red.png"));
	        	}
	        	else {
	        		indicator = new Label("",guiu.loadImage("images/ball_green.png"));
	        	}
	        }
		}
	}
	
	private void configureTable() {	
        table.setItems(items);
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
		// If the shapefile path is changed, then update the feature list.
		else if( source instanceof Button && ((Button)source).getId().equals(ComponentIds.BUTTON_SAVE)) {
			if(model!=null) {
				model.setName(nameField.getText());
				model.setDescription(descriptionField.getText());
				model.setRole(DatasetRole.valueOf(roleChooser.getValue()));

				if( !pathField.getText().equals(model.getShapefilePath())) {
					model.setShapefilePath(pathField.getText());
					model.setFeatures(null);  // Force re-read next time features are used
				}
				Database.getInstance().getDatasetTable().updateDataset(model);
				
				// Update attributes in the model
				Database.getInstance().getFeatureAttributeTable().updateFeatureAttributes(items);
				Database.getInstance().getAttributeAliasTable().updateAliasTable(model.getId(),items);
				EventBindingHub.getInstance().unselectDataset();     // Force fire
				EventBindingHub.getInstance().setSelectedDataset(model);

			}
			updateFeatures();
			configureTable();
		}
	}

	/**
	 * Update the table feature list from the model. If the model has no features, read them from the shapefile.
	 * If the dataset is the aggregating kind, set the aggregating column in the schema.
	 * If we add, delete or change the district column,force a re-read of the shapefile.
	 * A shapefile re-read will update the geometries.
	 */
	private void updateFeatures() {
		if( model!=null ) {
			boolean reread = false;
			items.clear();
			
			String districtColumn = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.DISTRICT.name());
			if( (districtColumn==null && model.getDistrictColumn()!=null) ||
				(districtColumn!=null && !districtColumn.equalsIgnoreCase(model.getDistrictColumn())) ) {
					reread = true;
					model.setDistrictColumn(districtColumn);
			}
			if( model.getFeatures()!=null){
				List<FeatureConfiguration> configs = Database.getInstance().getFeatureAttributeTable().getFeatureAttributes(model.getId());
				for(FeatureConfiguration fc:configs) {
					items.add(fc);
				}
			}
			if( reread ) {
				try {
					String idColumn = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.ID.name());
					model.setFeatures(ShapefileReader.read(model.getShapefilePath(),idColumn,districtColumn));
				}
				catch(Exception ex) {
					LOGGER.warning(String.format("%s.updateFeatures: Exception re-reading shapefile (%s)",CLSS,ex.getLocalizedMessage()));
				}
			}
		}
	}
	// ====================================== BasicRightSideNode =====================================
	@Override
	public void updateModel() {
		this.model = EventBindingHub.getInstance().getSelectedDataset();
		configureDefinition();
		updateFeatures();
		configureTable();
	}

	// ================================================= Event Handler ============================================
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
			List<FeatureConfiguration> featConfigs = event.getTableView().getItems();
			LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
			FeatureConfiguration item = featConfigs.get(row);
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
	public class ColorCommitHandler implements EventHandler<TableColumn.CellEditEvent<FeatureConfiguration,Color>> {
		@Override
		public void handle(CellEditEvent<FeatureConfiguration, Color> event) {
			FeatureConfiguration fc = event.getRowValue();
			if( event.getTableColumn().getText().equalsIgnoreCase("background")) {
				LOGGER.info(String.format("%s.handle: color %s",CLSS,event.getNewValue().toString()));
				fc.setBackground(event.getNewValue());
			}
		}
	}
	public class BooleanCommitHandler implements TableCellCallback<Boolean> {
		@Override
		public void update(String column,int row,Boolean value) {
			//LOGGER.info(String.format("%s.handle: boolean %s %d = %s",CLSS,column,row,value.toString()));
			FeatureConfiguration fc = items.get(row);
			if( column.equalsIgnoreCase("visible")) {
				fc.setVisible(value.booleanValue());
			}
		}
	}
}
