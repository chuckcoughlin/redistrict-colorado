package redistrict.colorado.gate;

import java.util.List;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import redistrict.colorado.ui.NameValue;

/**
 * Render a string type cell in the FeatureMetric table
 */
public class NameValueListLimitCellFactory implements Callback<TableColumn<List<NameValue>, String>, TableCell<List<NameValue>, String>>,
														EventHandler<TableColumn.CellEditEvent<List<NameValue>, String>> { 
	private final static String CLSS = "NameValueLimitCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final double limit;

	public NameValueListLimitCellFactory(double lim) {
		this.limit = lim;
	}
	/*
	 * @return a TableCell that responds to the out-of-bounds values by making the cell red.
	 */
	@Override
	public TableCell<List<NameValue>, String> call(TableColumn<List<NameValue>, String> p) {
		return new LimitCell();
	}
	

	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<List<NameValue>, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}

	public class LimitCell extends TableCell<List<NameValue>, String> {
	    @Override
	    protected void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item!=null) {
	        	setText(item);
	        	if( Math.abs(Double.parseDouble(item))>limit) {
	        		setTextFill(Color.RED);
	        	}
	        }
	    }
	}
}
