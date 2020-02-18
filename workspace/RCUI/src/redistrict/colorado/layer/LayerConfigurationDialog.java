/**  
 * Copyright (C) 2019 Charles Coughlin
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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Callback;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

public class LayerConfigurationDialog extends Dialog<LayerModel> implements EventHandler<ActionEvent>{
	private final static String CLSS = "LayerConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private static final GuiUtil guiu = new GuiUtil();
	private final LayerModel model;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");
	private final Button fileButton = new Button("Shapefile: ");
	private final Button fieldButton = new Button("Configure: ");
	private final Label roleLabel = new Label("Role: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final TextField pathField;
	private final ComboBox<String> roleChooser;
	private final Label indicator;

	public LayerConfigurationDialog(LayerModel m) {
		this.model = m;
        setTitle("LayerModel Editor");
        setHeaderText(String.format("Define layer: %s.",m.getName()));
        setResizable(true);
        
        nameField = new TextField(model.getName());
        descriptionField = new TextField(model.getDescription());
        fileButton.setOnAction(this);
        pathField = new TextField(model.getShapefilePath());
        roleChooser = new ComboBox<>();
        roleChooser.getItems().addAll(LayerRole.names());
        roleChooser.getSelectionModel().select(model.getRole().name());
        if( model.getShapefilePath()==null || model.getShapefilePath().isEmpty() ) {
        	indicator = new Label("",guiu.loadImage("images/ball_gray.png"));
        }
        else {
        	if( model.getFeatures()==null){
        		try {
					model.setFeatures(ShapefileReader.read(model.getShapefilePath()));
					LOGGER.info(String.format("%s.onInit: Shapefile has %d records, %d attributes", CLSS,model.getFeatures().getFeatures().size(),model.getFeatures().getFeatureSchema().getAttributeCount()));
					Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), model.getFeatures().getFeatureSchema().getAttributeNames());
				}
				catch( Exception ex) {
					model.setFeatures(null);
					String msg = String.format("%s.onInit: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
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
        fieldButton.setDisable(model.getFeatures()==null);
        fieldButton.setOnAction(this);
        
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
		grid.add(descriptionField, 1, 1, 2, 1);
		grid.add(fileButton, 0, 2);
		grid.add(pathField, 1, 2,  2, 1);
		grid.add(roleLabel, 0, 3);
		grid.add(roleChooser, 1, 3);
		grid.add(indicator, 2, 3);
		grid.add(fieldButton, 0, 4);
		
		DialogPane dialog = this.getDialogPane();
		dialog.setContent(grid);
		dialog.getButtonTypes().add(buttonCancel);
		dialog.getButtonTypes().add(buttonOK);
		dialog.getStyleClass().add(UIConstants.LAYER_EDITOR_CLASS);

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
	}
	/**
	 * Respond to button presses
	 */
	@Override
	public void handle(ActionEvent event) {
		// Find the file
		if( event.getSource().equals(fileButton)) {
			FileChooser fc = new FileChooser();
			Window window = new Popup();
			File file = fc.showOpenDialog(window); 
			LOGGER.info(String.format("File is %s",(file==null?"null":file.getAbsolutePath()))); 
			if (file != null) {      
				pathField.setText(file.getAbsolutePath()); 
			} 
		}
		// Configure field headers in the detail table
		else if( event.getSource().equals(fieldButton)) {
			List<FeatureConfiguration> configs = Database.getInstance().getFeatureAttributeTable().getFeatureAttributes(model.getId());
			Dialog<List<FeatureConfiguration>> dialog = new FAConfigurationDialog(configs);
            Optional<List<FeatureConfiguration>> result = dialog.showAndWait();
            if (result.isPresent()) {
            	boolean success = Database.getInstance().getFeatureAttributeTable().updateFeatureAttributes(configs);
            	LOGGER.info(String.format("%s.handle: returned from dialog %s", CLSS,(success?"successfully":"with error")));
            }
		}

	}
}
