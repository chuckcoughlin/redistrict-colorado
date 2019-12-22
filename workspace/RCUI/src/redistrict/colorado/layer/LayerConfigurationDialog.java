/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.db.LayerModel;

public class LayerConfigurationDialog extends Dialog<LayerModel> {
	private final LayerModel model;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");
	private final Label roleLabel = new Label("Role: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final ComboBox<String> roleChooser;

	public LayerConfigurationDialog(LayerModel m) {
		this.model = m;
        setTitle("LayerModel Editor");
        setHeaderText("Edit properties of a layer.");
        setResizable(true);
        
        nameField = new TextField(model.getName());
        descriptionField = new TextField(model.getDescription()); 
        roleChooser = new ComboBox<>();
        roleChooser.getItems().addAll(LayerRole.names());
        roleChooser.getSelectionModel().select(model.getRole().name());
    
        grid = new GridPane();
		grid.add(nameLabel, 1, 1);
		grid.add(nameField, 2, 1);
		grid.add(descriptionLabel, 1, 2);
		grid.add(descriptionField, 2, 2);
		grid.add(roleLabel, 1, 3);
		grid.add(roleChooser, 2, 3);

		getDialogPane().setContent(grid);
		getDialogPane().getButtonTypes().add(buttonCancel);
		getDialogPane().getButtonTypes().add(buttonOK);

		setResultConverter(new Callback<ButtonType, LayerModel>() {
			@Override
			public LayerModel call(ButtonType b) {
				if (b == buttonOK) {
					model.setName(nameField.getText());
					model.setDescription(descriptionField.getText());
					model.setRole(LayerRole.valueOf(roleChooser.getValue()));
					return model;
				}
				return null;
			}
		});
	}
	/*
    Optional<String> result = dialog.showAndWait();
    String selectedv = "cancelled.";
             
    if (result.isPresent()) {
     
        selected = result.get();
    }
     
    actionStatus.setText("Selection: " + selected);
    */
}
