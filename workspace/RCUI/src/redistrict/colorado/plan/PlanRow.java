/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.LeftSelectionEvent;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.layer.LayerRow.EditEventHandler;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This is the UI element for a list view that represents a Plan.
 */
public class PlanRow extends ListCell<PlanModel>  {
	private static final String CLSS = "PlanRow";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL1_WIDTH = 40.;
	private final static double COL2_WIDTH = 120.;
	private final static double COL3_WIDTH = 65.;
	private final static double COL4_WIDTH = 40.;
	private final static double COL5_WIDTH = 70.;
	private final static double ROW1_HEIGHT = 40.;
	private static final GuiUtil guiu = new GuiUtil();
	private final static String METRICS = "metrics";
	private GridPane grid = new GridPane();
    private final Label tag;   // Identifies the pane class
    private final Label name;
    private final Label description;
    private final CheckBox active;
    private final Button edit;
    private final Button metrics;
    private final EditEventHandler handler;
    
	public PlanRow() {
		handler = new EditEventHandler();
		setPrefWidth(UIConstants.LIST_PANEL_WIDTH);
		tag = new Label("",guiu.loadImage("images/plans.png"));
		name = new Label();
	    description = new Label();
	    active = new CheckBox("Active:");
	    edit = new Button("",guiu.loadImage("images/edit.png"));
	    metrics =new Button("Metrics");
	    metrics.setUserData(METRICS);

        configureGrid();        
        configureLabels();
        addLabelsToGrid();
        configureControls();
        addControlsToGrid(); 
        
        active.setOnAction(handler);
        edit.setOnAction(handler);
        metrics.setOnAction(handler);
    } 
	private void configureControls() {
		edit.getStyleClass().add(UIConstants.LIST_CELL_BUTTON_CLASS);
    }
	private void configureGrid() {
        grid.setHgap(0);
        grid.setVgap(4);
        grid.setPadding(new Insets(10, 0, 10, 0));  // top, left, bottom,right
        grid.getColumnConstraints().add(new ColumnConstraints(COL1_WIDTH)); 					// tag
        ColumnConstraints col2 = new ColumnConstraints(COL2_WIDTH,COL2_WIDTH,Double.MAX_VALUE); // name
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(col2);
        grid.getColumnConstraints().add(new ColumnConstraints(COL3_WIDTH)); 					// active
        grid.getColumnConstraints().add(new ColumnConstraints(COL4_WIDTH)); 					// edit
        grid.getColumnConstraints().add(new ColumnConstraints(COL5_WIDTH)); 					// metrics
        grid.getRowConstraints().add(new RowConstraints(ROW1_HEIGHT)); // column 0 is 40 wide
    }
	
	private void configureLabels() {
		tag.getStyleClass().add(UIConstants.LIST_CELL_ICON_CLASS);
        name.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
        description.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS);
    }
	
    private void addLabelsToGrid() {
        grid.add(tag, 0, 0);                    
        grid.add(name, 1, 0);        
        grid.add(description, 1,1,2,1);
        

    }
    private void addControlsToGrid() {
    	grid.add(active, 2,0);
        grid.add(edit, 3, 0);                    
        grid.add(metrics, 4, 0);        
    }
	
    @Override
    public void updateItem(PlanModel model, boolean empty) {
        super.updateItem(model, empty);
        if (empty) {
            clearContent();
        } else {
            setContent(model); 
        }
    }
    // Empty cells have no corresponding PlanModel
    private void clearContent() {
        setText(null);
        setGraphic(null);
    }
 
    private void setContent(PlanModel model) {
        setText(null);
        name.setText(model.getName());
        description.setText(model.getDescription());
        active.setSelected(model.isActive());      
        setGraphic(grid);
    }

    
    /**
     * Handle an event from the "edit" button. Display an edit pane in the right-side.
     */
    public class EditEventHandler implements EventHandler<ActionEvent> {
    	@Override public void handle(ActionEvent e) {
    		LOGGER.info(String.format("%s.handle: processing event from %s", CLSS,e.getSource()));
    		EventBindingHub hub = EventBindingHub.getInstance();
    		PlanModel model = getItem();
            hub.setSelectedPlan(model);
    		hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.PLAN_CONFIGURATION));
    	}
    }
}
