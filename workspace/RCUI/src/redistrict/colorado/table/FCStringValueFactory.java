/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.table;

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;

public class FCStringValueFactory implements Callback<TableColumn.CellDataFeatures<FeatureConfiguration,String>,ObservableValue<String>> {
	private final static String CLSS = "FCStringValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. 
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<FeatureConfiguration, String> cdf) {
		FeatureConfiguration fc = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		//LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		StringProperty property = new SimpleStringProperty();
		if( name.equalsIgnoreCase("Name")) {
			property.setValue(fc.getName());
		}
		else if( name.equalsIgnoreCase("Alias")) {
			if( !fc.getAlias().equalsIgnoreCase("NONE")) {
				property.setValue(fc.getAlias());
			}
		}
		else if( name.equalsIgnoreCase("Type")) {
			property.setValue(fc.getAttributeType().name());
		}
		else if( name.equalsIgnoreCase("Rank")) {
			property.setValue(String.valueOf(fc.getRank()));
		}
		return property;
	}
}
