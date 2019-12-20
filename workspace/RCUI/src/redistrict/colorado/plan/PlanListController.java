/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
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

public class PlanListController extends AnchorPane implements EventReceiver<ActionEvent> {
	private final static String CLSS = "PlanListController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Plans");
	private ButtonPane buttons = new ButtonPane();
	private ListView<String> planList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	
	public PlanListController() {
		this.auxEventHandler = new PlanListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		planList = new ListView<String>();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(planList);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(planList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(planList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(buttons,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(planList,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(planList,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(buttons,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(buttons,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		buttons.setDeleteDisabled(true);
		buttons.registerEventReceiver(this.auxEventDispatcher);;
	}
	
	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class PlanListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,((Node)event.getSource()).getId()));
		}
	}
}
