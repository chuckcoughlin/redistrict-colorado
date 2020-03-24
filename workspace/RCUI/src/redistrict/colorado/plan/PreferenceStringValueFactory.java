/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import javafx.util.Pair;

public class PreferenceStringValueFactory implements Callback<TableColumn.CellDataFeatures<Pair<String,String>,String>,ObservableValue<String>> {
	private final static String CLSS = "PLStringValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. 
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<Pair<String,String>, String> cdf) {
		Pair<String,String> pl = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		//LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		StringProperty property = new SimpleStringProperty();
		if( name.equalsIgnoreCase("Weight")) {
			property.setValue(pl.getKey());
		}
		else {
			property.setValue(pl.getValue());
		}
		return property;
	}
}
