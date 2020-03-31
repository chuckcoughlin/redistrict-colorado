/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.ui.NameValue;

public class NameValueCellValueFactory implements Callback<TableColumn.CellDataFeatures<NameValue,String>,ObservableValue<String>> {
	/** 
	 * Key values off of column names. The data item is a name-value.
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<NameValue, String> cdf) {
		NameValue nv = cdf.getValue();
		StringProperty property = new SimpleStringProperty();
		String columnName = cdf.getTableColumn().getText();
		if( columnName.equalsIgnoreCase("Plan") ) {
			property.setValue(nv.getName());
		}
		else if( columnName.contains("Mean") ) {
			property.setValue(String.format("%2.6f",nv.getMean()));
		}
		else if( columnName.contains("Score") ) {
			property.setValue(String.format("%2.6f",nv.getValue()));
		}
		else if( columnName.contains("Deviation") ) {
			property.setValue(String.format("%2.6f",nv.getStandardDeviation()));
		}
		return property;
	}
}
