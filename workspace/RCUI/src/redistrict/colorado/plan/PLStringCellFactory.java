package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.core.PlanLayer;

/**
 * Render a String cell in the PlanLayer table
 */
public class PLStringCellFactory implements Callback<TableColumn<PlanLayer, String>, TableCell<PlanLayer, String>>,
											EventHandler<TableColumn.CellEditEvent<PlanLayer, String>> { 
	private final static String CLSS = "PLStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public PLStringCellFactory() {
	}
	
	@Override
	public TableCell<PlanLayer, String> call(TableColumn<PlanLayer, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<PlanLayer, String> cell = null;
		if(p.getText().equalsIgnoreCase("Role")) {
			ComboBoxTableCell<PlanLayer, String> comboCell = new ComboBoxTableCell<PlanLayer, String>();
			comboCell.setConverter(new PLStringConverter(LayerRole.BOUNDARIES));
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( String role:LayerRole.names()) {
				list.add(role);
			}
			cell = comboCell;
		}
		else {
			TextFieldTableCell<PlanLayer, String> textCell = new TextFieldTableCell<PlanLayer, String>();
			textCell.setConverter(new PLStringConverter(LayerRole.BOUNDARIES));
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<PlanLayer, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class PLStringConverter extends StringConverter<String> {
		private final LayerRole role;
		 
		public PLStringConverter(LayerRole r) {
			this.role = r;
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