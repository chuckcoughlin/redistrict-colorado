/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.LeftSelectionEvent;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This is the UI element for a list view that represents a Plan.
 */
public class PlanRow extends ListCell<PlanModel> implements ChangeListener<Toggle>   {
	private static final String CLSS = "PlanRow";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL1_WIDTH = 40.;
	private final static double COL2_WIDTH = 120.;
	private final static double COL3_WIDTH = 65.;
	private final static double COL4_WIDTH = 40.;
	private final static double COL5_WIDTH = 50.;
	private final static double COL6_WIDTH = 80.;
	private final static double ROW1_HEIGHT = 40.;
	private static final GuiUtil guiu = new GuiUtil();
	private final static String ACTIVE = "active";
	private final static String EDIT = "edit";
	private final static String NAME = "name";
	private final static String MAP_DATA = "map";
	private final static String PROPERTIES_DATA = "properties";
	private GridPane grid = new GridPane();
	private long id = -1;      // model identifier
    private final Label tag;   // Identifies the pane class
    private final Label name;
    private final Label description;
    private final CheckBox active;
    private final Button edit;
    private final ToggleButton propertiesButton;
    private final ToggleButton mapButton;
    private final ToggleGroup toggleGroup;
    private final EditEventHandler handler;
    private final PlanChangeListener listener;
    
	public PlanRow() {
		handler = new EditEventHandler();
		listener= new PlanChangeListener();
		EventBindingHub.getInstance().addPlanListener(listener);
		setPrefWidth(UIConstants.LIST_PANEL_WIDTH);
		tag = new Label("",guiu.loadImage("images/plans.png"));
		name = new Label();
	    description = new Label();
	    active = new CheckBox("Active:");
	    active.setUserData(ACTIVE);
	    edit = new Button("",guiu.loadImage("images/edit.png"));
	    edit.setUserData(EDIT);
	    toggleGroup = new ToggleGroup();
	    mapButton =new ToggleButton("Map");
	    mapButton.setUserData(MAP_DATA);
	    mapButton.setToggleGroup(toggleGroup);
	    propertiesButton =new ToggleButton("Properties");
	    propertiesButton.setUserData(PROPERTIES_DATA);
	    propertiesButton.setToggleGroup(toggleGroup);
	    Tooltip tt = new Tooltip("Show the boundary map for this button.");
	    Tooltip.install(mapButton, tt);
	    tt = new Tooltip("Display a table of aggregated feature attributes for this plan.");
	    Tooltip.install(propertiesButton, tt);
        
        configureGrid();
        configureLabels();
        configureControls();
        addLabelsToGrid();
        addControlsToGrid(); 
        
        active.setOnAction(handler);
        propertiesButton.setOnAction(handler);
        edit.setOnAction(handler);
        
        toggleGroup.selectedToggleProperty().addListener(this);
        setContent(getItem());

    } 

	private void configureControls() {
		edit.getStyleClass().add(UIConstants.LIST_CELL_BUTTON_CLASS);
    }
	
	private void configureGrid() {
        grid.setHgap(0);
        grid.setVgap(4);
        grid.setPadding(new Insets(10, 0, 10, 0));  // top, left, bottom,right
        grid.getColumnConstraints().add(new ColumnConstraints(COL1_WIDTH)); 					// tag
        ColumnConstraints col2 = new ColumnConstraints(COL2_WIDTH,COL2_WIDTH,Double.MAX_VALUE); // combo
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(col2);
        grid.getColumnConstraints().add(new ColumnConstraints(COL3_WIDTH)); 					// active
        grid.getColumnConstraints().add(new ColumnConstraints(COL4_WIDTH)); 					// edit
        grid.getColumnConstraints().add(new ColumnConstraints(COL5_WIDTH)); 					// map
        grid.getColumnConstraints().add(new ColumnConstraints(COL6_WIDTH)); 					// metrics
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
        grid.add(propertiesButton, 5, 0);        
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
        setGraphic(grid);
        if( model==null) return;
        id = model.getId();
        name.setText(model.getName());
        description.setText(model.getDescription());
        active.setSelected(model.isActive());      
    }
    
    public class PlanChangeListener implements ChangeListener<PlanModel> {
		@Override
		public void changed(ObservableValue<? extends PlanModel> source, PlanModel oldModel, PlanModel newModel) {
			if(newModel!=null && newModel.getId()==id) {
				//LOGGER.info(String.format("%s.changed: name = %s",CLSS,newModel.getName()));
				name.setText(newModel.getName());
				description.setText(newModel.getDescription());
			}
		}
	}
    // ============================================ Change Listener(Toggle) ==================================================
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
			EventBindingHub hub = EventBindingHub.getInstance();
			hub.setSelectedPlan(getItem());
			Object data = newValue.getUserData();
			if( data==null ) data = "null";
    		if( data.equals(PROPERTIES_DATA)) {
    			hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.PLAN_FEATURES));
    		}
    		else if( data.toString().equalsIgnoreCase(MAP_DATA)) {
				hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.MODEL_MAP));
			}
    	
			
		}
	}
	
    /**
     * Handle an event from one of the buttons - either the active checkbox, or edit button.
     */
    public class EditEventHandler implements EventHandler<ActionEvent> {
    	@Override public void handle(ActionEvent e) {
    		EventBindingHub hub = EventBindingHub.getInstance();
    		PlanModel model = getItem();
    		hub.setSelectedPlan(model);
    		LOGGER.info(String.format("%s.handle: processing event from %s", CLSS,(model==null?"null":(model.getBoundary()==null?"null":model.getBoundary().getName()))));
    		if( e.getSource() instanceof Button ) {
    			Button source = (Button)e.getSource();
    			String data = source.getUserData().toString();
    			if( data.equals(EDIT)) {
       	         hub.setSelectedPlan(model);
       	         hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.PLAN_DEFINITION));
    			}
    		}
    		else if(e.getSource() instanceof CheckBox) {
    			model.setActive(active.isSelected());
    			Database.getInstance().getPlanTable().updatePlan(model);
    			hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.NONE));
    		}
    	}
    } 
}
