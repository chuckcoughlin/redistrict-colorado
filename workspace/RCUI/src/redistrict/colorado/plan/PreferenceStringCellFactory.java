package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import redistrict.colorado.gate.Gate;

/**
 * Render a String cell in the PlanLayer table
 */
public class PreferenceStringCellFactory implements Callback<TableColumn<Gate, String>, TableCell<Gate, String>>,
											EventHandler<TableColumn.CellEditEvent<Pair<String,String>, String>> { 
	private final static String CLSS = "PreferenceStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public PreferenceStringCellFactory() {
	}
	
	@Override
	public TableCell<Gate, String> call(TableColumn<Gate, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<Gate, String> cell = null;
		TextFieldTableCell<Gate, String> textCell = new TextFieldTableCell<>();
		textCell.setConverter(new PreferenceStringConverter());
		cell = textCell;
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<Pair<String,String>, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class PreferenceStringConverter extends StringConverter<String> {
		 
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