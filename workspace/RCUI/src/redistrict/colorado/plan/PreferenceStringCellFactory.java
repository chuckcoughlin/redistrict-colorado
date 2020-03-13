package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import redistrict.colorado.core.DatasetRole;

/**
 * Render a String cell in the PlanLayer table
 */
public class PreferenceStringCellFactory implements Callback<TableColumn<Property, String>, TableCell<Property, String>>,
											EventHandler<TableColumn.CellEditEvent<Property, String>> { 
	private final static String CLSS = "PreferenceStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public PreferenceStringCellFactory() {
	}
	
	@Override
	public TableCell<Property, String> call(TableColumn<Property, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<Property, String> cell = null;
		if(p.getText().equalsIgnoreCase("Role")) {
			ComboBoxTableCell<Property, String> comboCell = new ComboBoxTableCell<Property, String>();
			comboCell.setConverter(new PreferenceStringConverter());
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( String role:DatasetRole.names()) {
				list.add(role);
			}
			cell = comboCell;
		}
		else {
			TextFieldTableCell<Property, String> textCell = new TextFieldTableCell<Property, String>();
			textCell.setConverter(new PreferenceStringConverter());
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<Property, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class PreferenceStringConverter extends StringConverter<String> {
		 
		public String fromRole(DatasetRole rle) {
			return rle.name();
		}
		
		@Override
		public String fromString(String string) {
			return string;
		}
		@Override
		public String toString(String string) {
			return string;
		}
	}
}