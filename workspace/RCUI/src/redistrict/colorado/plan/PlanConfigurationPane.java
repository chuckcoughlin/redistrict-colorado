/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.SavePane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

public class PlanConfigurationPane extends BasicRightSideNode implements EventHandler<ActionEvent> {
	private final static String CLSS = "DatasetConfigurationPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static double GRID0_WIDTH = 100.;    // Grid widths
	private final static double GRID1_WIDTH = 300.;
	private final static double GRID2_WIDTH = 40.;
	private final static double RECTANGLE_SIDE = 20;
	private final GridPane grid;
	private final ColorPicker colorPicker;
	private Label headerLabel = new Label("Plan Definition");
	private final SavePane savePane = new SavePane(this);
	private final Label nameLabel = new Label("Name: ");
	private final Label descriptionLabel = new Label("Description: ");
	private final Label boundaryLabel = new Label("Boundary: ");
	private final Label fillLabel = new Label("Fill color: ");
	private final TextField nameField;
	private final TextField descriptionField;
	private final ComboBox<String> boundaryCombo;
	private PlanModel model;
	

	public PlanConfigurationPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_DEFINITION);
		this.model = EventBindingHub.getInstance().getSelectedPlan();
		
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		this.nameField = new TextField();
		nameField.setOnAction(this);
        this.descriptionField = new TextField();
        this.boundaryCombo = new ComboBox<>();
        boundaryCombo.setPrefWidth(GRID1_WIDTH);
	    Tooltip tt = new Tooltip("Select the dataset that provides the district boundaries for the plan.");
	    Tooltip.install(boundaryLabel, tt);
	    tt = new Tooltip("Define the color that identifies this plan in the bar charts showing results.");
	    Tooltip.install(fillLabel, tt);
        this.colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("text-field"); 
        
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
		grid.add(boundaryLabel, 0, 2);
		grid.add(boundaryCombo, 1, 2);
		grid.add(fillLabel, 0, 3);
		grid.add(colorPicker, 1, 3);
		
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);

		getChildren().add(savePane);
		setLeftAnchor(savePane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(savePane,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(savePane,0.);
		
		configureComboBox();
		configureDefinition();
	}
	
	private void configureDefinition() {
		// If the model is non-null, fill in all the fields
		if( model!=null ) {
			LOGGER.info(String.format("%s.configureDefinition: model is %s", CLSS,model.getName()));
			nameField.setText(model.getName());
			descriptionField.setText(model.getDescription());
			colorPicker.setValue(model.getFill());
			DatasetModel boundary = model.getBoundary();
			if( boundary!=null) {
				boundaryCombo.getSelectionModel().select(model.getBoundary().getName());
			}
		}
	}
	private void configureComboBox() {
		List<String> boundaries = Database.getInstance().getDatasetTable().getDatasetNamesForRole(DatasetRole.BOUNDARIES);
		boundaryCombo.getItems().clear();
		boundaryCombo.getItems().addAll(boundaries);
	}
	
	/**
	 * Respond to button presses, including "Save"
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		// On a save, update the model object, the database and then the hub.
		if( source instanceof Button && ((Button)source).getId().equals(ComponentIds.BUTTON_SAVE)) {
			if(model!=null) {
				model.setName(nameField.getText());
				model.setDescription(descriptionField.getText());
				model.setFill(colorPicker.getValue());
				DatasetModel dm = DatasetCache.getInstance().getDataset(boundaryCombo.getSelectionModel().getSelectedItem());
				model.setBoundary(dm);
				Database.getInstance().getPlanTable().updatePlan(model);
				EventBindingHub.getInstance().unselectPlan();     // Force fire
				EventBindingHub.getInstance().setSelectedPlan(model);
			}
		}

	}
	// ====================================== BasicRightSideNode =====================================
	@Override
	public void updateModel() {
		this.model = EventBindingHub.getInstance().getSelectedPlan();
		configureDefinition();
	}

}
