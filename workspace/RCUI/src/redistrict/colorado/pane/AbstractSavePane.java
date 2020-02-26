/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A save pane is shown under configuration panes on the right-side of the split.
 * This abstract base class sets a common size. It has a save button.
 */
public abstract class AbstractSavePane extends FlowPane {
	protected static final String CLSS = "AbstractSavePane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	protected final Button save;
	protected final GuiUtil guiu = new GuiUtil();
	
	public AbstractSavePane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		//final Pane spacer = new Pane();
	    //spacer.setMinSize(200, 1);
	    //this.getChildren().add(spacer);
		this.save = new Button("Save");
		save.setId(ComponentIds.BUTTON_SAVE);
		this.getChildren().add(save);
		setMargin(save,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
}
