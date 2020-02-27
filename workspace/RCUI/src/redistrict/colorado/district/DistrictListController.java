/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
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

public class DistrictListController extends AnchorPane implements EventReceiver<ActionEvent> {
	private final static String CLSS = "DistrictListController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Districts");
	private ListView<String> regionList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	
	public DistrictListController() {
		this.auxEventHandler = new RegionListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		regionList = new ListView<String>();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(regionList);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(regionList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(regionList,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(regionList,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(regionList,UIConstants.LIST_PANEL_RIGHT_MARGIN);
	}
	
	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class RegionListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,((Node)event.getSource()).getId()));
		}
	}
}