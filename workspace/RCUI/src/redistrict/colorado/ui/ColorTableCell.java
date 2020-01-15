package redistrict.colorado.ui;


import java.util.logging.Logger;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class ColorTableCell<T> extends TableCell<T, Color> {
	private final static String CLSS = "ColorTableCell";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final ColorPicker colorPicker;

	public ColorTableCell(TableColumn<T, Color> column) {
		this.colorPicker = new ColorPicker();
		this.colorPicker.editableProperty().bind(column.editableProperty());
		this.colorPicker.disableProperty().bind(column.editableProperty().not());
		this.colorPicker.setOnShowing(event -> {
			final TableView<T> tableView = getTableView();
			tableView.getSelectionModel().select(getTableRow().getIndex());
			tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);	    
		});
		this.colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(isEditing()) {
				commitEdit(newValue);
			}
		});		
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

/*
	protected void updateItem(String item, boolean empty) {
		try {
			int val = Integer.parseInt(item, 16);
			int r = val & 255; int g = (val >> 8) & 255; int b = (val >> 16) & 255; int alpha = (val >> 24) & 255;
			Color clr = Color.rgb(r,g,b,alpha);
			updateItem(clr,empty);
		}
		catch(NumberFormatException nfe) {}
		
	}
*/
	@Override
	protected void updateItem(Color item, boolean empty) {
		super.updateItem(item, empty);	

		setText(null);	
		if(empty) {	    
			setGraphic(null);
		} else {	    
			this.colorPicker.setValue(item);
			this.setGraphic(this.colorPicker);
		} 
	}
}
