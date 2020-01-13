package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FCBooleanCellFactory implements Callback<TableColumn<FeatureConfiguration, Boolean>, TableCell<FeatureConfiguration, Boolean>>,
														EventHandler<TableColumn.CellEditEvent<FeatureConfiguration, Boolean>> { 
	private final static String CLSS = "FCBooleanCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	
	public FCBooleanCellFactory() {
	}
	
	@Override
	public TableCell<FeatureConfiguration, Boolean> call(TableColumn<FeatureConfiguration, Boolean> p) {
		LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<FeatureConfiguration, Boolean> cell = null;
		if(p.getText().equalsIgnoreCase("Visible")) {
			CheckBoxTableCell<FeatureConfiguration, Boolean> checkboxCell = new CheckBoxTableCell<FeatureConfiguration, Boolean>();
			cell = checkboxCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<FeatureConfiguration, Boolean> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text.toString()));
	}
}
