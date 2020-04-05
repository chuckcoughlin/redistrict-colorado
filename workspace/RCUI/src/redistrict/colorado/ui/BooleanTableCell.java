package redistrict.colorado.ui;


import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import redistrict.colorado.table.TableCellCallback;


public class BooleanTableCell<T> extends TableCell<T, Boolean> {
	private final static String CLSS = "BooleanTableCell";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final String name;
	private final TableCellCallback<Boolean> callback;
	private final CheckBox checkBox;

	public BooleanTableCell(String column,TableCellCallback<Boolean> listener) {
		this.name = column;
		this.callback = listener;
		this.checkBox = new CheckBox(null);	
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		this.checkBox.selectedProperty().addListener(new BooleanListener());
		
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);	
		setText(null);
		if(empty) {	    
			setGraphic(null);
		} 
		else if(getTableRow()!=null){	    
			//LOGGER.info(String.format("%s.updateItem: %s %d", CLSS,item,getTableRow().getIndex()));
			this.checkBox.setSelected(item.booleanValue());
			this.setGraphic(this.checkBox);
			this.commitEdit(item);
		} 
	}
	
	
	public class BooleanListener implements ChangeListener<Boolean>  {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			//LOGGER.info(String.format("%s.changed: %s", CLSS,newValue.toString()));
			commitEdit(newValue);
			int row = getTableRow().getIndex();
			callback.update(name, row, newValue);
		}
		
	}
}
