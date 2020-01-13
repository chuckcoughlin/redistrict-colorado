package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.ui.ColorTableCell;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FCColorCellFactory implements Callback<TableColumn<FeatureConfiguration, Color>, TableCell<FeatureConfiguration, Color>>,
														EventHandler<TableColumn.CellEditEvent<FeatureConfiguration, Color>> { 
	private final static String CLSS = "FCColorCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	
	public FCColorCellFactory() {
	}
	
	@Override
	public TableCell<FeatureConfiguration, Color> call(TableColumn<FeatureConfiguration, Color> p) {
		LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
		TableCell<FeatureConfiguration, Color> cell = null;
		if(p.getText().equalsIgnoreCase("Background")) {
			TableCell<FeatureConfiguration, Color> colorCell = new ColorTableCell(p);
			cell = colorCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<FeatureConfiguration, Color> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text.toString()));
	}
}
