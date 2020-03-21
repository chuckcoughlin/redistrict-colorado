package redistrict.colorado.dataset;

import java.util.logging.Logger;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.ui.BooleanTableCell;
import redistrict.colorado.ui.TableCellCallback;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FCBooleanCellFactory implements Callback<TableColumn<FeatureConfiguration, Boolean>, TableCell<FeatureConfiguration, Boolean>>,
											 TableCellCallback<Boolean> { 
	private final static String CLSS = "FCBooleanCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final TableCellCallback<Boolean> callback;

	
	public FCBooleanCellFactory(TableCellCallback<Boolean> c) {
		this.callback = c;
	}
	
	@Override
	public TableCell<FeatureConfiguration, Boolean> call(TableColumn<FeatureConfiguration, Boolean> p) {
		TableCell<FeatureConfiguration, Boolean> cell = null;
		
		if(p.getText().equalsIgnoreCase("Visible")) {
			//LOGGER.info(String.format("%s:TableCell.call: col = %s",CLSS,p.getText()));
			TableCell<FeatureConfiguration, Boolean> bCell = new BooleanTableCell<FeatureConfiguration>(p.getText(),this);
			cell = bCell;
		}
		return cell;
	}
	

	// ================================= TableCellCallback ===================================
	/**
	 * Propagate the change up a level.
	 */
	@Override
	public void update(String columnName, int row, Boolean newValue) {
		callback.update(columnName, row, newValue);
	}
}
