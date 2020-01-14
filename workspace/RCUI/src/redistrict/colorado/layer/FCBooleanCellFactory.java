package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;

/**
 * Render a boolean cell in the FeatureConfiguration table
 */
public class FCBooleanCellFactory implements Callback<TableColumn<FeatureConfiguration, Boolean>, TableCell<FeatureConfiguration, Boolean>> { 
	private final static String CLSS = "FCBooleanCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	
	
	@Override
	public TableCell<FeatureConfiguration, Boolean> call(TableColumn<FeatureConfiguration, Boolean> p) {

		TableCell<FeatureConfiguration, Boolean> cell = null;
		if(p.getText().equalsIgnoreCase("Visible")) {
			LOGGER.info(String.format("%s:TableCell.call: %s",CLSS,p.getText()));
			CheckBoxTableCell<FeatureConfiguration, Boolean> checkboxCell = new CheckBoxTableCell<FeatureConfiguration, Boolean>();
			cell = checkboxCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================

	public class CheckboxListener implements Callback<Integer,ObservableValue<Boolean>> {

		@Override
		public ObservableValue<Boolean> call(Integer val) {
			LOGGER.info(String.format("%s.call: %d",CLSS,val.intValue()));
			if( val.intValue()==0) return new SimpleBooleanProperty(Boolean.FALSE);
			return new SimpleBooleanProperty(Boolean.TRUE);
		}
/*
		@Override
		public void changed(ObservableValue<? extends Callback<Integer, ObservableValue<Boolean>>> before,
				Callback<Integer, ObservableValue<Boolean>> during, Callback<Integer, ObservableValue<Boolean>> after) {
			LOGGER.info(String.format("%s.changed: %s",CLSS,after.toString()));
			
		}
			*/
	}

}
