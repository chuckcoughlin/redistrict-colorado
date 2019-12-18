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
import javafx.scene.layout.FlowPane;
import redistrict.colorado.bind.BasicEventDispatchChain;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventSource;

/**
 * Hold the add and delete buttons, Insets are top,right,bottom,left. 
 * There may be multiple instances of this class, so we use the event
 * chain to properly inform our parent of the button presses.
 * 
 * We provide a method to enable/disable the delete button.
 */
public class ButtonPane extends FlowPane implements EventSource<ActionEvent> {
	private static final String CLSS = "ButtonPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	private final Button addButton;
	private final Button deleteButton;
	private final GuiUtil guiu = new GuiUtil();
	private final EventHandler<ActionEvent> eventHandler;
	private final BasicEventDispatchChain<ActionEvent> eventChain;
	
	public ButtonPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.setPrefHeight(40.);
		this.eventHandler = new ButtonPaneEventHandler();
		this.eventChain   = new BasicEventDispatchChain<ActionEvent>();
		addButton = new Button("",guiu.loadImage("images/add.png"));
		addButton.setId(ComponentIds.BUTTON_ADD);
		addButton.setOnAction(eventHandler);
		
		deleteButton = new Button("",guiu.loadImage("images/delete.png"));
		deleteButton.setId(ComponentIds.BUTTON_DELETE);
		deleteButton.setDisable(true);
		deleteButton.setOnAction(eventHandler);

		this.getChildren().add(addButton);
		this.getChildren().add(deleteButton);
		
		setMargin(addButton,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
	
	public void setDeleteDisabled(boolean flag) { this.deleteButton.setDisable(flag); }
	
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

	@Override
	public void registerEventReceiver(BasicEventDispatcher<ActionEvent> bed) {
		eventChain.append(bed);	
	}

}
