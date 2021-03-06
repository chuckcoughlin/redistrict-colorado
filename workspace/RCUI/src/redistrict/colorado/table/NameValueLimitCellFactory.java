package redistrict.colorado.table;

import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import redistrict.colorado.core.NameValue;

/**
 * Render a string type cell in the FeatureMetric table
 */
public class NameValueLimitCellFactory implements Callback<TableColumn<NameValue, String>, TableCell<NameValue, String>>,
														EventHandler<TableColumn.CellEditEvent<NameValue, String>> { 
	private final static String CLSS = "NameValueLimitCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final boolean useRange;
	private final double limit;
	private final double minValue;
	private final double maxValue;

	public NameValueLimitCellFactory(double lim) {
		this.limit = lim;
		this.minValue = 0.;
		this.maxValue = 0.;
		this.useRange = false;
	}
	public NameValueLimitCellFactory(double minval, double maxval ) {
		this.limit = 0.;
		this.minValue = minval;
		this.maxValue = maxval;
		this.useRange = true;
	}
	/*
	 * @return a TableCell that responds to the out-of-bounds values by making the cell red.
	 */
	@Override
	public TableCell<NameValue, String> call(TableColumn<NameValue, String> p) {
		return new LimitCell();
	}
	

	// ======================================== Event Handler ========================================
	@Override
	public void handle(CellEditEvent<NameValue, String> text) {
		LOGGER.info(String.format("%s.handle: %s",CLSS,text));

	}

	public class LimitCell extends TableCell<NameValue, String> {
	    @Override
	    protected void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item!=null) {
	        	setText(item);
	        	if( useRange ) {
	        		double val = Double.parseDouble(item);
	        		if( val>minValue && val<maxValue ) {
		        		setTextFill(Color.GREEN);
		        	}
	        	}
	        	else {
	        		if( Math.abs(Double.parseDouble(item))>limit) {
	        			setTextFill(Color.RED);
	        		}
	        	}
	        }
	    }
	}
}
