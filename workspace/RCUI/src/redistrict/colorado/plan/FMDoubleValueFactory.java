/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureMetric;

public class FMDoubleValueFactory implements Callback<TableColumn.CellDataFeatures<FeatureMetric,Number>,ObservableValue<Number>> {
	private final static String CLSS = "FMDoubleValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. The only one that is a String is name 
	 */
	@Override
	public ObservableValue<Number> call(CellDataFeatures<FeatureMetric, Number> cdf) {
		FeatureMetric fm = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		//LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		DoubleProperty property = new SimpleDoubleProperty();
		if( name.equalsIgnoreCase("Area")) {
			property.setValue(fm.getArea());
		}
		return property;
	}
}
