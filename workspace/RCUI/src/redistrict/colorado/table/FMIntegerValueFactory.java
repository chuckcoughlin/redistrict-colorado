/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.table;

import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.PlanFeature;

public class FMIntegerValueFactory implements Callback<TableColumn.CellDataFeatures<PlanFeature,Number>,ObservableValue<Number>> {
	private final static String CLSS = "FMDoubleValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. The only one that is a String is name 
	 */
	@Override
	public ObservableValue<Number> call(CellDataFeatures<PlanFeature, Number> cdf) {
		PlanFeature fm = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		//LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		IntegerProperty property = new SimpleIntegerProperty();
		if( name.equalsIgnoreCase("Area")) {
			property.setValue(fm.getArea());
		}
		else if( name.equalsIgnoreCase("Perimeter")) {
			property.setValue(fm.getPerimeter());
		}
		else if( name.equalsIgnoreCase("Population")) {
			property.setValue(fm.getPopulation());
		}
		else if( name.equalsIgnoreCase("Black")) {
			property.setValue(fm.getBlack());
		}
		else if( name.equalsIgnoreCase("Hispanic")) {
			property.setValue(fm.getHispanic());
		}
		else if( name.equalsIgnoreCase("White")) {
			property.setValue(fm.getWhite());
		}
		else if( name.equalsIgnoreCase("Democrat")) {
			property.setValue(fm.getDemocrat());
		}
		else if( name.equalsIgnoreCase("Republican")) {
			property.setValue(fm.getRepublican());
		}
		else if( name.equalsIgnoreCase("Crossings")) {
			property.setValue(fm.getCrossings());
		}
		return property;
	}
}
