/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;


import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.ui.common.ComponentIds;
import redistrict.colorado.ui.common.EventReceiver;
import redistrict.colorado.ui.common.EventSource;
import redistrict.colorado.ui.common.GuiUtil;
import redistrict.colorado.ui.common.RCEventDispatchChain;
import redistrict.colorado.ui.common.RCEventDispatcher;

/**
 * Hold the add and delete buttons, Insets are top,right,bottom,left
 *
 */
public class ButtonPane extends FlowPane implements EventSource<ActionEvent>, EventReceiver<ActionEvent> {
	private static final String CLSS = "ButtonPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	private final Button addButton;
	private final Button deleteButton;
	private final Label statusLabel = new Label("Status:");
	private final Label message = new Label("");  // Most recent message
	private final GuiUtil guiu = new GuiUtil();
	private final EventHandler<ActionEvent> eventHandler;
	private final RCEventDispatchChain<ActionEvent> eventChain;
	private final RCEventDispatcher<ActionEvent> eventDispatcher;
	
	public ButtonPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.eventHandler = new ButtonPaneEventHandler();
		this.eventChain   = new RCEventDispatchChain<ActionEvent>();
		this.eventDispatcher = new RCEventDispatcher<ActionEvent>(eventHandler);
		addButton = new Button("",guiu.loadImage("images/add.png"));
		addButton.setId(ComponentIds.BUTTON_ADD);
		addButton.setOnAction(eventHandler);
		
		deleteButton = new Button("",guiu.loadImage("images/delete.png"));
		deleteButton.setId(ComponentIds.BUTTON_DELETE);
		deleteButton.setDisable(true);
		deleteButton.setOnAction(eventHandler);

		this.getChildren().add(addButton);
		this.getChildren().add(deleteButton);
		this.getChildren().add(statusLabel);
		this.getChildren().add(message);
		
		setMargin(addButton,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
	
	
	public RCEventDispatcher<ActionEvent> getRCEventDispatcher() { return eventDispatcher; }
	
	/**
	 * One of the buttons has been pressed. The source of the event is the button.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class ButtonPaneEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,((Node)event.getSource()).getId()));
			eventChain.dispatchEvent(event);
		}
	}


	// ================================= Event Source ======================
	@Override
	public void registerEventReceiver(RCEventDispatcher<ActionEvent> rce) {
		eventChain.append(rce);
	}
}
