/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.navigation;


import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.bind.EventRoutingHub;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A navigation pane is shown under the panes on the right-side of the split.
 * This abstract base class sets a common size.
 */
public abstract class AbstractNavigationPane extends FlowPane {
	private static final String CLSS = "NavigationPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventRoutingHub hub;
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	protected final Label navigationLabel;
	protected final GuiUtil guiu = new GuiUtil();
	
	public AbstractNavigationPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.hub = EventRoutingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		this.navigationLabel = new Label("");
		this.getChildren().add(navigationLabel);
		
		setMargin(navigationLabel,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
}
