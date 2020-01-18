/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import javafx.geometry.HPos;
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
import javafx.util.Callback;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

public class PlanConfigurationDialog extends Dialog<PlanModel> {
	private final static String CLSS = "PlanConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private static final GuiUtil guiu = new GuiUtil();
	private final PlanModel model;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);
	private final GridPane grid;
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");

	private final Label roleLabel = new Label("Role: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final ComboBox<String> roleChooser;


	public PlanConfigurationDialog(PlanModel m) {
		this.model = m;
        setTitle("PlanModel Editor");
        setHeaderText(String.format("Define Plan: %s.",m.getName()));
        setResizable(true);
        
        nameField = new TextField(model.getName());
        descriptionField = new TextField(model.getDescription());
        //this.table = new TableView<PlanLayer>();
        
        roleChooser = new ComboBox<>();
        roleChooser.getItems().addAll(LayerRole.names());
        //roleChooser.getSelectionModel().select(model.getRole().name());
        /*
        if( model.getLayers()==null){
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
        */
        
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
		grid.add(descriptionField, 0, 2, 2, 1);

		DialogPane dialog = this.getDialogPane();
		dialog.setContent(grid);
		dialog.getButtonTypes().add(buttonCancel);
		dialog.getButtonTypes().add(buttonOK);
		dialog.getStyleClass().add(UIConstants.LAYER_EDITOR_CLASS);

		setResultConverter(new Callback<ButtonType, PlanModel>() {
			@Override
			public PlanModel call(ButtonType b) {
				if (b == buttonOK) {
					model.setName(nameField.getText());
					model.setDescription(descriptionField.getText());
					return model;
				}
				return null;
			}
		});
	}
}
