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

/**
 * The table data consists of a list of lists of name value entries. Rely on UserData to specify
 * which of the lists of lists we are referencing.
 * @author chuckc
 *
 */
public class NameValueListCellValueFactory implements Callback<TableColumn.CellDataFeatures<List<NameValue>,String>,ObservableValue<String>> {
	private final static String CLSS = "NameValueCellValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. The value of the CDF is a list of name-values.
	 * Use the index set as user data to determine which to use.
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<List<NameValue>, String> cdf) {
		List<NameValue> list = cdf.getValue();
		int index = (Integer)cdf.getTableColumn().getUserData();
		NameValue nv = list.get(index);
		StringProperty property = new SimpleStringProperty();
		String columnName = cdf.getTableColumn().getText();
		if( columnName.equalsIgnoreCase("Name") ) {
			property.setValue(nv.getName());
		}
		else {
			property.setValue(String.format("%2.6f",nv.getValue()));
		}
		return property;
	}
}
