/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * The table data consists of a list of lists of name value entries. Rely on UserData to specify
 * which of the lists of lists we are referencing.
 * @author chuckc
 *
 */
public class NameValueListCellValueFactory implements Callback<TableColumn.CellDataFeatures<List<NameValue>,String>,ObservableValue<String>> {
	private final static String CLSS = "NameValueListCellValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	public final static String DEFAULT_FORMAT = "%s";
	private final Map<String,String> formats;
	
	 public NameValueListCellValueFactory() {
		 this.formats = new HashMap<>();
	 }
	 public void setFormat(String key,String fmt) { formats.put(key, fmt); }
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
		String format = formats.get(columnName);
		
		if( columnName.equalsIgnoreCase("Name") ) {
			property.setValue(nv.getName());
		}
		else {
			Object val = nv.getValue(columnName);
			if( val==null ) {
				LOGGER.warning(String.format("%s.call: NameValue has no attribute %s", CLSS,columnName));
				property.setValue("");
			}
			else if( format==null ) {
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
