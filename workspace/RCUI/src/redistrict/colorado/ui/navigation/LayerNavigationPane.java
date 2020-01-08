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
	 * One of the right-side selections has changed. Compose a message to display. This is only called if the
	 * layer is edited.
	 */
	@Override
	public void changed(ObservableValue<? extends LayerModel> source, LayerModel oldValue, LayerModel newValue) {
		LOGGER.info(String.format("%s.changed: got %s", CLSS,newValue.getName()));
		updateTextForModel();
	}

	@Override
	public void updateTextForModel() {
		LayerModel model = hub.getSelectedLayer();
		if( model!=null ) {
			navigationLabel.setText(String.format("Layer: %s",hub.getSelectedLayer().getName()));
		}
	}
}
