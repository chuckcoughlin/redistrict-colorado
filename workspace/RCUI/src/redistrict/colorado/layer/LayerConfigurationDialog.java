/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.io.File;
import java.util.logging.Logger;

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
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.ui.UIConstants;

public class LayerConfigurationDialog extends Dialog<LayerModel> implements EventHandler<ActionEvent>{
	private final static String CLSS = "LayerConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final LayerModel model;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");
	private final Button fileButton = new Button("Shapefile: ");
	private final Label roleLabel = new Label("Role: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final TextField pathField;
	private final ComboBox<String> roleChooser;

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
    
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints(COL0_WIDTH);
		col0.setHalignment(HPos.LEFT);
		ColumnConstraints col1 = new ColumnConstraints(COL1_WIDTH,COL1_WIDTH,Double.MAX_VALUE);
		col1.setHalignment(HPos.LEFT);
		col1.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col0,col1); 
		grid.add(nameLabel,0, 0);
		grid.add(nameField, 1, 0);
		grid.add(descriptionLabel, 0, 1);
		grid.add(descriptionField, 1, 1);
		grid.add(fileButton, 0, 2);
		grid.add(pathField, 1, 2);
		grid.add(roleLabel, 0, 3);
		grid.add(roleChooser, 1, 3);
		
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
					model.setShapefilePath(pathField.getText());
					model.setRole(LayerRole.valueOf(roleChooser.getValue()));
					return model;
				}
				return null;
			}
		});
	}
	@Override
	public void handle(ActionEvent arg0) {
		FileChooser fc = new FileChooser();
		Window window = new Popup();
		File file = fc.showOpenDialog(window); 
		LOGGER.info(String.format("File is %s",(file==null?"null":file.getAbsolutePath()))); 
        if (file != null) {      
        	pathField.setText(file.getAbsolutePath()); 
        } 
		
	}
}
