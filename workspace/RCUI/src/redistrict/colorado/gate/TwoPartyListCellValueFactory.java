/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.ui.NameValue;
import redistrict.colorado.ui.TwoPartyValue;

/**
 * The table data consists of a list of lists of two party value entries. Rely on UserData to specify
 * which of the lists of lists we are referencing.
 * @author chuckc
 *
 */
public class TwoPartyListCellValueFactory implements Callback<TableColumn.CellDataFeatures<List<TwoPartyValue>,String>,ObservableValue<String>> {
	private final static String CLSS = "TwoPartyListCellValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. The value of the CDF is a list of name-values.
	 * Use the index set as user data to determine which to use.
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<List<TwoPartyValue>, String> cdf) {
		List<TwoPartyValue> list = cdf.getValue();
		int index = (Integer)cdf.getTableColumn().getUserData();
		TwoPartyValue nv = list.get(index);
		StringProperty property = new SimpleStringProperty();
		String columnName = cdf.getTableColumn().getText();
		if( columnName.equalsIgnoreCase("Name") ) {
			property.setValue(nv.getName());
		}
		// Spit out the name of the dominant party
		else if( columnName.equalsIgnoreCase("Party") ) {
			if( nv.getDemocrat()>nv.getRepublican())
				property.setValue("DEM");
			else if( nv.getRepublican()>nv.getDemocrat())
				property.setValue("REP");
			else {
				property.setValue("-tie-");
			}
		}
		else if( columnName.contains("dem") || columnName.contains("Dem") ) {
			property.setValue(String.format("%2.1f",nv.getDemocrat()));
		}
		else {
			property.setValue(String.format("%2.1f",nv.getRepublican()));
		}
		return property;
	}
}
