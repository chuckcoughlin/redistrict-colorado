/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.navigation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import redistrict.colorado.core.DistrictModel;

/**
/**
 * This navigation pane is shown on the right-side of the split under panes dealing with regions.
 * It listens for left-side selections and identifies the current region.
 */
public class DistrictNavigationPane extends AbstractNavigationPane implements ChangeListener<DistrictModel> {

	
	public DistrictNavigationPane() {
		hub.addDistrictListener(this);
	}

	/**
	 * One of the right-side selections has changed. Compose a message to display.This is only called if the
	 * region is edited.
	 */
	@Override
	public void changed(ObservableValue<? extends DistrictModel> source, DistrictModel oldValue, DistrictModel newValue) {
		LOGGER.info(String.format("%s.changed: got %s", CLSS,newValue.getName()));
		updateTextForModel();
	}
	
	@Override
	public void updateTextForModel() {
		navigationLabel.setText(String.format("District: %s",hub.getSelectedDistrict().getName()));	
	}
}
