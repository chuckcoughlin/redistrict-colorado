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
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanDataset;

/**
 * Render a String cell in the PlanLayer table
 */
public class PLStringCellFactory implements Callback<TableColumn<PlanDataset, String>, TableCell<PlanDataset, String>>,
											EventHandler<TableColumn.CellEditEvent<PlanDataset, String>> { 
	private final static String CLSS = "PLStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public PLStringCellFactory() {
	}
	
	@Override
	public TableCell<PlanDataset, String> call(TableColumn<PlanDataset, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<PlanDataset, String> cell = null;
		if(p.getText().equalsIgnoreCase("Role")) {
			ComboBoxTableCell<PlanDataset, String> comboCell = new ComboBoxTableCell<PlanDataset, String>();
			comboCell.setConverter(new PLStringConverter(DatasetRole.BOUNDARIES));
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( String role:DatasetRole.names()) {
				list.add(role);
			}
			cell = comboCell;
		}
		else {
			TextFieldTableCell<PlanDataset, String> textCell = new TextFieldTableCell<PlanDataset, String>();
			textCell.setConverter(new PLStringConverter(DatasetRole.BOUNDARIES));
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<PlanDataset, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class PLStringConverter extends StringConverter<String> {
		private final DatasetRole role;
		 
		public PLStringConverter(DatasetRole r) {
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