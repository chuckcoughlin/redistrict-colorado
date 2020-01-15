package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
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
			CheckBoxTableCell<FeatureConfiguration, Boolean> checkboxCell = new CheckBoxTableCell<FeatureConfiguration, Boolean>();
			LOGGER.info(String.format("%s:TableCell.call: %s %d children",CLSS,p.getText(),checkboxCell.getChildrenUnmodifiable().size()));
			//checkboxCell.getGraphic().setOnMousePressed(new MouseHandler());
			CheckBox graphic = new CheckBox(null);
			checkboxCell.setGraphic(graphic);
			graphic.setOnMouseReleased(new MouseHandler());
			cell = checkboxCell;
		}
		return cell;
	}
	
	// ======================================== Event Handler ========================================
	public class MouseHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			LOGGER.info(String.format("%s.handle: MOUSE EVENT",CLSS));
			
		}
		
	}
	//public class CheckboxListener implements Callback<Integer,ObservableValue<Boolean>> {
	public class CheckboxListener implements ChangeListener<EventHandler<MouseEvent>> {
		/*
		@Override
		public ObservableValue<Boolean> call(Integer val) {
			LOGGER.info(String.format("%s.call: %d",CLSS,val.intValue()));
			if( val.intValue()==0) return new SimpleBooleanProperty(Boolean.FALSE);
			return new SimpleBooleanProperty(Boolean.TRUE);
		}

		@Override
		public void changed(ObservableValue<? extends Callback<Integer, ObservableValue<Boolean>>> before,
				Callback<Integer, ObservableValue<Boolean>> during, Callback<Integer, ObservableValue<Boolean>> after) {
			LOGGER.info(String.format("%s.changed: %s",CLSS,after.toString()));
			
		}
*/
		@Override
		public void changed(ObservableValue<? extends EventHandler<MouseEvent>> arg0, EventHandler<MouseEvent> arg1,
				EventHandler<MouseEvent> arg2) {
			LOGGER.info(String.format("%s.changed: %s",CLSS,arg2.toString()));
			
		}
		
	}

}
