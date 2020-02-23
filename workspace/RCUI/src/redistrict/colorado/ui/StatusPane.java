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
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 * Hold a label and status field. Subscribe to the generic application-wide status message.
 * 
 */
public class StatusPane extends FlowPane {
	private static final String CLSS = "StatusPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	private final Label statusLabel = new Label("Status:");
	private final Label message = new Label("");  // Most recent message
	private final GuiUtil guiu = new GuiUtil();
	private final EventHandler<ActionEvent> eventHandler;
	
	public StatusPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.setPrefHeight(40.);
		this.eventHandler = new StatusPaneEventHandler();

		this.getChildren().add(statusLabel);
		this.getChildren().add(message);
		
		setMargin(statusLabel,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
	
	/**
	 * One of the buttons has been pressed. The source of the event is the button.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class StatusPaneEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,((Node)event.getSource()).getId()));
		}
	}

}
