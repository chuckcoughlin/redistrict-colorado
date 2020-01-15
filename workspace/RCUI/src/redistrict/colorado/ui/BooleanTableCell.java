package redistrict.colorado.ui;


import java.util.logging.Logger;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;


public class BooleanTableCell<T> extends TableCell<T, Boolean> {
	private final static String CLSS = "BooleanTableCell";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final CheckBox checkBox;

	public BooleanTableCell(TableColumn<T, Boolean> column) {
		this.checkBox = new CheckBox(null);
		this.checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(isEditing()) {
				commitEdit(newValue);
			}
		});		
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);	

		setText(null);	
		if(empty) {	    
			setGraphic(null);
		} 
		else {	    
			this.checkBox.setSelected(item.booleanValue());
			this.setGraphic(this.checkBox);
		} 
	}
}
