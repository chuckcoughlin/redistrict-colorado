/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;

public class FCBooleanValueFactory implements Callback<TableColumn.CellDataFeatures<FeatureConfiguration,Boolean>,ObservableValue<Boolean>> {
	private final static String CLSS = "FCBooleanValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. 
	 */
	@Override
	public ObservableValue<Boolean> call(CellDataFeatures<FeatureConfiguration, Boolean> cdf) {
		FeatureConfiguration fc = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		Property<Boolean> property = new SimpleBooleanProperty();
		if( name.equalsIgnoreCase("Visible")) {
			//LOGGER.info(String.format("%s.getValue: %s = %s",CLSS,name,fc.isVisible()?"true":"false"));
			property.setValue(fc.isVisible());
		}
		return property;
	}
}
