/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.navigation;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import redistrict.colorado.core.LayerModel;

/**
 * This navigation pane is shown on the right-side of the split under panes dealing with layers.
 * It listens for left-side selections and identifies the layer.
 */
public class LayerNavigationPane extends AbstractNavigationPane implements ChangeListener<LayerModel> {

	
	public LayerNavigationPane() {
		hub.addLayerListener(this);
	}

	/**
	 * One of the right-side selections has changed. Compose a message to display.
	 */
	@Override
	public void changed(ObservableValue<? extends LayerModel> arg0, LayerModel arg1, LayerModel arg2) {
		// TODO Auto-generated method stub
		
	}
}
