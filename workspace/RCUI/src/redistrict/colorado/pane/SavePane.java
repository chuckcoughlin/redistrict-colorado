/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A save pane is shown under configuration panes on the right-side of the split.
 * It has a save button.
 */
public class SavePane extends AnchorPane {
	protected static final String CLSS = "SavePane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double HGAP = 8.;
	protected final Button save;
	protected final GuiUtil guiu = new GuiUtil();
	
	public SavePane(EventHandler<ActionEvent> handler) {
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		this.save = new Button("Save");
		save.setId(ComponentIds.BUTTON_SAVE);
		save.setOnAction(handler);
		this.getChildren().add(save);
		setTopAnchor(save,HGAP);
		setRightAnchor(save,UIConstants.BUTTON_PANEL_HEIGHT);
	}
}
