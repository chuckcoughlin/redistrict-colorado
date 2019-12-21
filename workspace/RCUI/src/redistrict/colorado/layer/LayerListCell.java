package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import redistrict.colorado.db.LayerModel;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

public class LayerListCell extends ListCell<LayerModel> implements ChangeListener<Toggle> {
	private static final String CLSS = "LayerListCell";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final GuiUtil guiu = new GuiUtil();
	private GridPane grid = new GridPane();
    private final Label tag;   // Identifies the pane class
    private final Label name;
    private final Label description;
    private final Label shapefilePath;
    private final Label displayOrder;
    private final Label role;
    private final Button edit;
    private final ToggleButton mapButton;
    private final ToggleButton detailButton;
    private final ToggleGroup toggleGroup;
    private final EditEventHandler cellHandler;
    
	public LayerListCell() {
		cellHandler = new EditEventHandler();
		tag = new Label("",guiu.loadImage("images/layers.png"));
		name = new Label();
	    description = new Label();
	    shapefilePath = new Label();
	    displayOrder = new Label();
	    role = new Label();
	    edit = new Button("",guiu.loadImage("images/edit.png"));
	    mapButton =    new ToggleButton("",guiu.loadImage("images/earth.png"));
	    detailButton = new ToggleButton("",guiu.loadImage("images/table.png"));
	    toggleGroup = new ToggleGroup();
	    mapButton.setToggleGroup(toggleGroup);
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
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
    }
	
	private void configureLabels() {
		tag.getStyleClass().add(UIConstants.LIST_CELL_ICON_CLASS);
        name.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
        description.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS);
        shapefilePath.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS);
        displayOrder.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS);
        role.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS_SMALL);
    }
	
    private void addLabelsToGrid() {
        grid.add(tag, 0, 0, 1, 1);                    
        grid.add(name, 1, 0);        
        grid.add(description, 2, 0);
        grid.add(shapefilePath, 2, 1);
        grid.add(displayOrder, 3, 1);
        grid.add(role, 4, 1);
    }
    private void addControlsToGrid() {
        grid.add(edit, 3, 0);                    
        grid.add(mapButton, 4, 0);        
        grid.add(detailButton, 5, 1);
    }
	
    @Override
    public void updateItem(LayerModel model, boolean empty) {
        super.updateItem(model, empty);
        if (empty) {
            clearContent();
        } else {
            addContent(model);
        }
    }
    // Empty cells have no corresponding LayerModel
    private void clearContent() {
        setText(null);
        setGraphic(null);
    }
 
    private void addContent(LayerModel model) {
        setText(null);
        //icon.setText(GeocachingIcons.getIcon(cache).toString());
        name.setText(model.getName());
        description.setText(model.getDescription());
        shapefilePath.setText(model.getDescription());
        displayOrder.setText(String.valueOf(model.getDisplayOrder()));
        role.setText(model.getRole().name());
        setStyleClassDependingOnSelectedState(model);        
        setGraphic(grid);
    }
 
    private void setStyleClassDependingOnSelectedState(LayerModel cache) {
    	/*
        if (selected) {
            addClasses(this, UIConstants.LIST_CELL_SELECTED_CLASS);
            removeClasses(this, UIConstants.LIST_CELL_NOT_SELECTED_CLASS);
        } 
        else {
            addClasses(this, UIConstants.LIST_CELL_NOT_SELECTED_CLASS);
            removeClasses(this, UIConstants.LIST_CELL_SELECTED_CLASS);
        }
        */
    }
    
    /**
     * Handle an event from the "edit" button. Display a popup edit window.
     */
    public class EditEventHandler implements EventHandler<ActionEvent> {
    	@Override public void handle(ActionEvent e) {
            LOGGER.info(String.format("%s.handle: got edit event", CLSS));
        }
    }

    /**
     * The toggle group has changed. 
     * @param source
     * @param oldValue
     * @param newValue
     */
	@Override
	public void changed(ObservableValue<? extends Toggle> source, Toggle oldValue, Toggle newValue) {
		LOGGER.info(String.format("%s.changed: toggle button event", CLSS));
		
	}
}
