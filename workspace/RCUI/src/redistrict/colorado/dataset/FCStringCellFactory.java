package redistrict.colorado.dataset;

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
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.StandardAttributes;

/**
 * Render a string type cell in the FeatureConfiguration table
 */
public class FCStringCellFactory implements Callback<TableColumn<FeatureConfiguration, String>, TableCell<FeatureConfiguration, String>>,
														EventHandler<TableColumn.CellEditEvent<FeatureConfiguration, String>> { 
	private final static String CLSS = "FCStringCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	public FCStringCellFactory() {
	}
	
	@Override
	public TableCell<FeatureConfiguration, String> call(TableColumn<FeatureConfiguration, String> p) {
		//LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<FeatureConfiguration, String> cell = null;
		if(p.getText().equalsIgnoreCase("Alias")) {
			ComboBoxTableCell<FeatureConfiguration, String> comboCell = new ComboBoxTableCell<FeatureConfiguration, String>();
			comboCell.setConverter(new FCStringConverter());
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( String alias:StandardAttributes.names()) {
				list.add(alias);
			}
			cell = comboCell;
		}
		else if(p.getText().equalsIgnoreCase("Type")) {
			ComboBoxTableCell<FeatureConfiguration, String> comboCell = new ComboBoxTableCell<FeatureConfiguration, String>();
			comboCell.setConverter(new FCStringConverter());
			ObservableList<String> list = comboCell.getItems();
			list.clear();
			for( AttributeType type:AttributeType.basicTypes()) {
				list.add(type.name());
			}
			cell = comboCell;
		}
		else {
			TextFieldTableCell<FeatureConfiguration, String> textCell = new TextFieldTableCell<FeatureConfiguration, String>();
			textCell.setConverter(new FCStringConverter());
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<FeatureConfiguration, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
	public class FCStringConverter extends StringConverter<String> {
		 
		public FCStringConverter() {
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
