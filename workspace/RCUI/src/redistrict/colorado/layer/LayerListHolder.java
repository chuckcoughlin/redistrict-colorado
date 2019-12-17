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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.UIConstants;



public class LayerListHolder extends AnchorPane implements EventReceiver<ActionEvent>  {
	private final static String CLSS = "LayerListHolder";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("===== Layer Definitions =======");
	private ButtonPane buttons = new ButtonPane();
	private ListView<String> layerList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	
	
	public LayerListHolder() {
		this.auxEventHandler = new LayerListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		layerList = new ListView<String>();
		headerLabel.setPrefHeight(40.);
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(layerList);
		//layerList.setMinHeight(100);
		//this.setPrefHeight(UIConstants.SCENE_HEIGHT/4);
		setTopAnchor(headerLabel,20.);
		setTopAnchor(layerList,100.);
		setBottomAnchor(buttons,10.);
		setLeftAnchor(headerLabel,10.);
		setRightAnchor(headerLabel,10.);
		setLeftAnchor(layerList,10.);
		setRightAnchor(layerList,10.);
		setLeftAnchor(buttons,10.);
		setRightAnchor(buttons,10.);
		
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
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,((Node)event.getSource()).getId()));
		}
	}
}
