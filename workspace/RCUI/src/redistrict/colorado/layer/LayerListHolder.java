/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.db.LayerModel;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;



public class LayerListHolder extends AnchorPane implements EventReceiver<ActionEvent>  {
	private final static String CLSS = "LayerListHolder";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Layers");
	private ButtonPane buttons = new ButtonPane();
	
	private ListView<LayerModel> layerList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	
	
	public LayerListHolder() {
		this.auxEventHandler = new LayerListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		layerList = new ListView<LayerModel>();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(layerList);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(layerList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(layerList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(buttons,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(layerList,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(layerList,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(buttons,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(buttons,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		buttons.setDeleteDisabled(true);
		buttons.registerEventReceiver(this.auxEventDispatcher);
		
	}


	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class LayerListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String id = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,id));
			if( id.equalsIgnoreCase(ComponentIds.BUTTON_ADD))       {
				
			}
			// Delete the selected layer, then refresh
			else if( id.equalsIgnoreCase(ComponentIds.BUTTON_DELETE)) {
				
			}

		}
	}
	
	/**
	 * Listen for changes to the selected layer.
	 */
}
