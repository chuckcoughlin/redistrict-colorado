package redistrict.colorado.layer;

import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import redistrict.colorado.db.FeatureConfiguration;
import redistrict.colorado.ui.ColorTableCell;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FeatureConfigurationCellFactory implements Callback<TableColumn<FeatureConfiguration, String>, TableCell<FeatureConfiguration, String>>,
														EventHandler<TableColumn.CellEditEvent<FeatureConfiguration, String>> { 
	private final static String CLSS = "FeatureConfigurationCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	
	public FeatureConfigurationCellFactory() {
	}
	
	@Override
	public TableCell<FeatureConfiguration, String> call(TableColumn<FeatureConfiguration, String> p) {
		LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<FeatureConfiguration, String> cell = null;
		if(p.getText().equalsIgnoreCase("Visible")) {
			CheckBoxTableCell<FeatureConfiguration, String> checkboxCell = new CheckBoxTableCell<FeatureConfiguration, String>();
			cell = checkboxCell;
		}
		else if(p.getText().equalsIgnoreCase("Type")) {
			ComboBoxTableCell<FeatureConfiguration, String> comboCell = new ComboBoxTableCell<FeatureConfiguration, String>();
			comboCell.setConverter(new FeatureAttributeStringConverter(AttributeType.ATTRIBUTE_TYPE));
			cell = comboCell;
		}
		else if(p.getText().equalsIgnoreCase("Color")) {
			cell = new ColorTableCell<FeatureConfiguration>(p);
		}
		else {
			TextFieldTableCell<FeatureConfiguration, String> textCell = new TextFieldTableCell<FeatureConfiguration, String>();
			textCell.setConverter(new FeatureAttributeStringConverter(AttributeType.STRING));
			cell = textCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<FeatureConfiguration, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}
}
