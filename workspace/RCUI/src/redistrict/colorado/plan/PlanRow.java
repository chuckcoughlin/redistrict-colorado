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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.LeftSelectionEvent;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This is the UI element for a list view that represents a Plan.
 */
public class PlanRow extends ListCell<PlanModel>   {
	private static final String CLSS = "PlanRow";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL1_WIDTH = 40.;
	private final static double COL2_WIDTH = 120.;
	private final static double COL3_WIDTH = 65.;
	private final static double COL4_WIDTH = 40.;
	private final static double COL5_WIDTH = 80.;
	private static final GuiUtil guiu = new GuiUtil();
	private final static String ACTIVE = "active";
	private final static String ATTRIBUTES = "attributes";
	private final static String COMBO = "combo";
	private final static String NAME = "name";
	private GridPane grid = new GridPane();
	private long id = -1;      // model identifier
	private String name = "";  // model name
    private final Label tag;   // Identifies the pane class
    private final CheckBox active;
    private final ComboBox<String> combo;
    private final Label nameLabel;
    private final Button attributes;
    private final EditEventHandler handler;
    private final MouseEventHandler clickHandler;
    
	public PlanRow() {
		handler = new EditEventHandler();
		clickHandler = new MouseEventHandler();
		setPrefWidth(UIConstants.LIST_PANEL_WIDTH);
		tag = new Label("",guiu.loadImage("images/plans.png"));
	    active = new CheckBox("Active:");
	    active.setUserData(ACTIVE);
	    combo = new ComboBox<String>();
	    combo.setUserData(COMBO);
	    nameLabel = new Label("");
	    nameLabel.setUserData(NAME);
	    nameLabel.setOnMouseClicked(clickHandler);
	    attributes =new Button("Attributes");
	    attributes.setId(ComponentIds.BUTTON_ATTRIBUTES);
	    attributes.setUserData(ATTRIBUTES);
	    Tooltip tt = new Tooltip("Display a table of aggregated feature attributes for this plan.");
	    Tooltip.install(attributes, tt);
        configureGrid();        
        configureLabels();
        addLabelsToGrid();
        addControlsToGrid(); 
        
        List<String> items = Database.getInstance().getDatasetTable().getDatasetNames(DatasetRole.BOUNDARIES);
        active.setOnAction(handler);
        combo.setOnAction(handler);
        combo.getItems().addAll(items);
        setContent(getItem());
        attributes.setOnAction(handler);
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
        grid.getColumnConstraints().add(new ColumnConstraints(COL5_WIDTH)); 					// metrics

    }
	
	private void configureLabels() {
		tag.getStyleClass().add(UIConstants.LIST_CELL_ICON_CLASS);
		nameLabel.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
    }
	
    private void addLabelsToGrid() {
        grid.add(tag, 0, 0);                             
    }
    private void addControlsToGrid() {
    	grid.add(active, 3,0);
    	grid.add(nameLabel, 1, 0);
        grid.add(combo, 2, 0);                    
        grid.add(attributes, 4, 0);        
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
        name = model.getBoundary().getName();
        combo.getSelectionModel().select(name);
        active.setSelected(model.isActive());      
    }

    
    /**
     * Handle an event from one of the buttons.
     */
    public class EditEventHandler implements EventHandler<ActionEvent> {
    	@Override public void handle(ActionEvent e) {
    		String data = ACTIVE;
    		if( e.getSource() instanceof Button ) {
    			Button source = (Button)e.getSource();
    			data = source.getUserData().toString();
    		}

    		EventBindingHub hub = EventBindingHub.getInstance();
    		PlanModel model = getItem();
    		hub.setSelectedPlan(model);
    		LOGGER.info(String.format("%s.handle: processing event from %s (%s)", CLSS,data,model.getBoundary().getName()));
    		if( data.equals(ATTRIBUTES)) {
    			hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.PLAN_FEATURES));
    		}
    		else if( data.equals(ACTIVE)) {
    			model.setActive(active.isSelected());
    			Database.getInstance().getPlanTable().updatePlan(model);
    			hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.NONE));
    		}
    		else if( data.equals(COMBO)) {
    			String datasetName = combo.getSelectionModel().getSelectedItem();
    			DatasetModel dm = DatasetCache.getInstance().getDataset(datasetName);
    			if(dm!=null) model.setBoundary(dm);
    			Database.getInstance().getPlanTable().updatePlan(model);
    		}
    	}
    }
    /**
     * Handle a click on the name text. Popup a dialog.
     */
    public class MouseEventHandler implements EventHandler<MouseEvent> {
    	@Override public void handle(MouseEvent e) {
    		if( e.getSource() instanceof Label ) {
    			Label source = (Label)e.getSource();
    			if(source.getUserData().toString().equals(NAME) ) {
    				PlanModel model = getItem();
    				TextInputDialog dialog = new TextInputDialog(model.getName());
    				dialog.setTitle("Plan Name Dialog");
    				dialog.setHeaderText("Enter a unique name for the plan");
    				dialog.setContentText("Name:");

    				Optional<String> result = dialog.showAndWait();
    				if (result.isPresent()){
    				    model.setName(result.get());
    				    source.setText(result.get());
    				}
    				Database.getInstance().getPlanTable().updatePlan(model);
    			}
    		}
    	}
    }
}
