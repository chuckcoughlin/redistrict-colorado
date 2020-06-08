/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.table;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.NameValue;

public class NameValueCellValueFactory implements Callback<TableColumn.CellDataFeatures<NameValue,String>,ObservableValue<String>> {
	private final static String CLSS = "NameValueCellValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	public final static String DEFAULT_FORMAT = "%s";
	private final Map<String,String> formats;
	
	 public NameValueCellValueFactory() {
		 this.formats = new HashMap<>();
	 }
	
	 public void setFormat(String key,String fmt) { formats.put(key, fmt); }
	/** 
	 * Key values off of column names. The data item is a name-value.
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<NameValue, String> cdf) {
		NameValue nv = cdf.getValue();
		StringProperty property = new SimpleStringProperty();
		String columnName = cdf.getTableColumn().getText();
		String format = formats.get(columnName);
		
		if( columnName.equalsIgnoreCase("Name") ) {
			property.setValue(nv.getName());
		}
		else {
			Object val = nv.getValue(columnName);
			if( val==null ) val = nv.getValue(columnName.toUpperCase());
			if( val==null ) {
				LOGGER.warning(String.format("%s.call: NameValue has no attribute %s", CLSS,columnName));
				property.setValue("");
			}
			else if( format==null  ) {
				if(!(val instanceof String)) LOGGER.warning(String.format("%s.call: No format specified for column %s", CLSS,columnName));
				property.setValue(String.format(DEFAULT_FORMAT,val.toString()));
			}
			else {
				//LOGGER.info(String.format("%s.call: %s format = %s, type= %s", CLSS,columnName,format,val.getClass().getCanonicalName()));
				property.setValue(String.format(format,val));
			}
		}
		return property;
	}
}
