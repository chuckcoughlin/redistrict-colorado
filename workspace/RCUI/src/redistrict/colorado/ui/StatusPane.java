/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;


import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.bind.EventBindingHub;

/**
 * Hold a label and status field. Subscribe to the generic application-wide status message.
 * This is a panel that appears under the split panel.
 */
public class StatusPane extends FlowPane implements ChangeListener<String> {
	private static final String CLSS = "StatusPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	private final Label statusLabel = new Label("Status:");
	private final Label message = new Label("");  // Most recent message
	
	public StatusPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.setPrefHeight(40.);
		EventBindingHub.getInstance().addMessageListener(this);

		this.getChildren().add(statusLabel);
		this.getChildren().add(message);
		
		setMargin(statusLabel,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
	
	// ============================= ChangeListener ==========================
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
		LOGGER.info(String.format("%s.changed: %s,%s,%s",CLSS,arg0,arg1,arg2));
		message.setText(arg2);
	}
}
