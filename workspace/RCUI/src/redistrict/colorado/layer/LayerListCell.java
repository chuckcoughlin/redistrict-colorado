package redistrict.colorado.layer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import redistrict.colorado.db.LayerModel;
import redistrict.colorado.ui.UIConstants;

public class LayerListCell extends ListCell<LayerModel> {
	private GridPane grid = new GridPane();
    private Label icon = new Label();
    private Label name = new Label();
    private Label description = new Label();
    private boolean selected = false;
    
	public LayerListCell() {
        configureGrid();        
        configureIcon();
        configureName();
        configureDescription();
        addControlsToGrid();            
    } 
	
	private void configureGrid() {
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
    }
	
	private void configureIcon() {
        icon.setFont(Font.font(UIConstants.LIST_CELL_FONT, FontWeight.BOLD, 24));
        icon.getStyleClass().add(UIConstants.LIST_CELL_ICON_CLASS);
    }
	private void configureName() {
        name.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
    }
	private void configureDescription() {
        name.getStyleClass().add(UIConstants.LIST_CELL_NAME_CLASS);
    }
	
    private void addControlsToGrid() {
        grid.add(icon, 0, 0, 1, 2);                    
        grid.add(name, 1, 0);        
        grid.add(description, 1, 1);
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
