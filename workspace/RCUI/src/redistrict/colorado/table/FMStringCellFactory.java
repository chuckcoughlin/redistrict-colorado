package redistrict.colorado.table;

import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import redistrict.colorado.core.PlanFeature;

/**
 * Render a string type cell in the FeatureMetric table
 */
public class FMStringCellFactory implements Callback<TableColumn<PlanFeature, String>, TableCell<PlanFeature, String>>,
														EventHandler<TableColumn.CellEditEvent<PlanFeature, String>> { 
	private final static String CLSS = "FMStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public FMStringCellFactory() {
	}
	
	@Override
	public TableCell<PlanFeature, String> call(TableColumn<PlanFeature, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<PlanFeature, String> cell = null;
		if(p.getText().equalsIgnoreCase("Type")) {
			ComboBoxTableCell<PlanFeature, String> comboCell = new ComboBoxTableCell<PlanFeature, String>();
			comboCell.setConverter(new FMStringConverter());
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( AttributeType type:AttributeType.basicTypes()) {
				list.add(type.name());
			}
			cell = comboCell;
		}
		else {
			TextFieldTableCell<PlanFeature, String> textCell = new TextFieldTableCell<PlanFeature, String>();
			textCell.setConverter(new FMStringConverter());
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<PlanFeature, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class FMStringConverter extends StringConverter<String> {
		 
		public FMStringConverter() {
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
