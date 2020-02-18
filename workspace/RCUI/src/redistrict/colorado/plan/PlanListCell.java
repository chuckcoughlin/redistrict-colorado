/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

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
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This is the UI element for a list view that represents a Plan.
 */
public class PlanListCell extends ListCell<PlanModel> implements ChangeListener<Toggle> {
	private static final String CLSS = "PlanListCell";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL1_WIDTH = 40.;
	private final static double COL2_WIDTH = 100.;
	private final static double COL3_WIDTH = 65.;
	private final static double COL4_WIDTH = 40.;
	private final static double COL5_WIDTH = 55.;
	private final static double COL6_WIDTH = 55.;
	private final static double ROW1_HEIGHT = 40.;
	private static final GuiUtil guiu = new GuiUtil();
	private final static String MAP_DATA = "map";
	private final static String DETAIL_DATA = "detail";
	private GridPane grid = new GridPane();
    private final Label tag;   // Identifies the pane class
    private final Label name;
    private final Label description;
    private final CheckBox active;
    private final Button edit;
    private final ToggleButton mapButton;
    private final ToggleButton detailButton;
    private final ToggleGroup toggleGroup;
    private final EditEventHandler cellHandler;
    
	public PlanListCell() {
		cellHandler = new EditEventHandler();
		setPrefWidth(UIConstants.LIST_PANEL_WIDTH);
		tag = new Label("",guiu.loadImage("images/plans.png"));
		name = new Label();
	    description = new Label();
	    active = new CheckBox("Active:");
	    edit = new Button("",guiu.loadImage("images/edit.png"));
	    toggleGroup = new ToggleGroup();
	    mapButton =new ToggleButton("Map");
	    //mapButton.setGraphic(guiu.loadImage("images/earth.png"));
	    mapButton.setUserData(MAP_DATA);
	    mapButton.setToggleGroup(toggleGroup);
	    detailButton = new ToggleButton("Detail");
	    //detailButton.setGraphic(guiu.loadImage("images/table.png"));
	    detailButton.setUserData(DETAIL_DATA);
	    detailButton.setToggleGroup(toggleGroup);
        configureGrid();        
        configureLabels();
        addLabelsToGrid();
        configureControls();
        addControlsToGrid(); 
        
        edit.setOnAction(cellHandler);
        toggleGroup.selectedToggleProperty().addListener(this);
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
        grid.getColumnConstraints().add(new ColumnConstraints(COL5_WIDTH)); 					// map
        grid.getColumnConstraints().add(new ColumnConstraints(COL6_WIDTH)); 					// detail
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
        grid.add(mapButton, 4, 0);        
        grid.add(detailButton, 5, 0);
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
     * Handle an event from the "edit" button. Display a popup edit window.
     */
    public class EditEventHandler implements EventHandler<ActionEvent> {
    	@Override public void handle(ActionEvent e) {
            //LOGGER.info(String.format("%s.handle: processing edit event", CLSS));
            PlanModel model = getItem();
            Dialog<PlanModel> dialog = new PlanConfigurationDialog(model);
            Optional<PlanModel> result = dialog.showAndWait();
            if (result.isPresent()) {
            	setContent(model);
            	boolean success = Database.getInstance().getPlanTable().updatePlan(model);
            	LOGGER.info(String.format("%s.EditEventHandler: returned from dialog %s", CLSS,(success?"successfully":"with error")));
            }
        }
    }

    /**
     * The toggle group has changed. Inform the binding hub. If the user clicks on a button in a non-selected row,
     * the model will have changed also.
     * @param source
     * @param oldValue
     * @param newValue
     */
	@Override
	public void changed(ObservableValue<? extends Toggle> source, Toggle oldValue, Toggle newValue) {
		if( newValue==null ) {
			LOGGER.info(String.format("%s.changed: toggle button no new value", CLSS));
		}
		else {
			EventBindingHub.getInstance().setSelectedPlan(getItem());
			Object data = newValue.getUserData();
			if( data==null ) data = "null";
			if( data.toString().equalsIgnoreCase(MAP_DATA)) {
				EventBindingHub.getInstance().setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.MAP));
			}
			else if(data.toString().equalsIgnoreCase(DETAIL_DATA)) {
				EventBindingHub.getInstance().setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.DETAIL));
			}
			//LOGGER.info(String.format("%s.changed: toggle button = %s", CLSS,data.toString()));
		}
	}
}
