package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.ui.BooleanTableCell;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FCBooleanCellFactory implements Callback<TableColumn<FeatureConfiguration, Boolean>, TableCell<FeatureConfiguration, Boolean>>,
											EventHandler<TableColumn.CellEditEvent<FeatureConfiguration, Boolean>> { 
	private final static String CLSS = "FCBooleanCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	
	public FCBooleanCellFactory() {
		LOGGER.info(String.format("%s:Constructor:",CLSS));
	}
	
	@Override
	public TableCell<FeatureConfiguration, Boolean> call(TableColumn<FeatureConfiguration, Boolean> p) {
		TableCell<FeatureConfiguration, Boolean> cell = null;
		LOGGER.info(String.format("%s:TableCell.call: p= %s",CLSS,p.getText()));
		if(p.getText().equalsIgnoreCase("Visible")) {
			LOGGER.info(String.format("%s:TableCell.call:",CLSS));
			TableCell<FeatureConfiguration, Boolean> bCell = new BooleanTableCell<FeatureConfiguration>(p);
			cell = bCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================


	@Override
	public void handle(CellEditEvent<FeatureConfiguration, Boolean> val) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,val.toString()));
		
	}
}
