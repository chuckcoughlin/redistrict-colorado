/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;

import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import redistrict.colorado.core.FeatureConfiguration;

public class FCColorValueFactory implements Callback<TableColumn.CellDataFeatures<FeatureConfiguration,Color>,ObservableValue<Color>> {
	private final static String CLSS = "FCColorValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	/** 
	 * Key values off of column names. 
	 */
	@Override
	public ObservableValue<Color> call(CellDataFeatures<FeatureConfiguration, Color> cdf) {
		FeatureConfiguration fc = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		ObjectProperty<Color> property = new SimpleObjectProperty<Color>();
		if( name.equalsIgnoreCase("Background")) {
			Color c = fc.getBackground();
			//String clr = String.format("0x%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue());
			property.setValue(c);
		}
		return property;
	}
}
