/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import redistrict.colorado.core.LayerModel;

/**
 * This navigation pane is shown on the right-side of the split under the layer map.
 * .
 */
public class LayerSavePane extends AbstractSavePane implements ChangeListener<LayerModel>,EventHandler<ActionEvent> {

	
	public LayerSavePane() {
		hub.addLayerListener(this);
		save.setOnAction(this);
	}

	/**
	 * One of the right-side selections has changed. Compose a message to display. This is only called if the
	 * layer is edited.
	 */
	@Override
	public void changed(ObservableValue<? extends LayerModel> source, LayerModel oldValue, LayerModel newValue) {
		LOGGER.info(String.format("%s.changed: got %s", CLSS,newValue.getName()));
	}
	
	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
}
