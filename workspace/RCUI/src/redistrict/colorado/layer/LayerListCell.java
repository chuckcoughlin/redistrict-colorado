package redistrict.colorado.layer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import redistrict.colorado.db.LayerModel;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

public class LayerListCell extends ListCell<LayerModel> {
	private static final GuiUtil guiu = new GuiUtil();
	private GridPane grid = new GridPane();
    private final Label tag;   // Identifies the pane class
    private final Label name;
    private final Label description;
    private final Button edit;
    
	public LayerListCell() {
		tag = new Label("",guiu.loadImage("images/table.png"));
		name = new Label();
	    description = new Label();
	    edit = new Button("",guiu.loadImage("images/edit.png"));
        configureGrid();        
        configureLabels();
        addLabelsToGrid();
        configureControls();
        addControlsToGrid();            
    } 
	private void configureControls() {
		edit.getStyleClass().add(UIConstants.LIST_CELL_BUTTON_CLASS);
    }
	private void configureGrid() {
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
    }
	
	private void configureLabels() {
		tag.getStyleClass().add(UIConstants.LIST_CELL_ICON_CLASS);
        name.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
        description.getStyleClass().add(UIConstants.LIST_CELL_FIELD_CLASS);
    }
	
    private void addLabelsToGrid() {
        grid.add(tag, 0, 0, 1, 1);                    
        grid.add(name, 1, 0);        
        grid.add(description, 2, 0);
    }
    private void addControlsToGrid() {
        grid.add(edit, 3, 0);                    
        //grid.add(name, 1, 0);        
        //grid.add(description, 1, 1);
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
}
