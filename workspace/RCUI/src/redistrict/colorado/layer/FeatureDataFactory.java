/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;

import java.util.Map;
import java.util.logging.Logger;

import org.openjump.feature.Feature;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class FeatureDataFactory implements Callback<TableColumn.CellDataFeatures<Feature,String>,ObservableValue<String>> {
	private final static String CLSS = "FeatureDataFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final Map<String,String> nameForAliasMap;
	
	/**
	 * The map converts the column headings (aliases) to attribute names
	 * @param map
	 */
	public FeatureDataFactory(Map<String,String> map) {
		this.nameForAliasMap = map;
	}
	
	/** 
	 * Key values off of column names. The column names are user-defined aliases.
	 * Access the feature by the actual name.
	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<Feature, String> cdf) {
		Feature feature = cdf.getValue();
		StringProperty property = new SimpleStringProperty();
		String alias = cdf.getTableColumn().getText();
		String name = nameForAliasMap.get(alias);
		LOGGER.info(String.format("%s.call cfd= %s, alias = %s, name= %s",CLSS,cdf,alias,name));
		if( name==null) name = alias;
		property.setValue(feature.getAttribute(name).toString());

		return property;
	}
}
