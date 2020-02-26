/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import java.util.logging.Logger;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A navigation pane is shown under the panes on the right-side of the split.
 * This abstract base class sets a common size. It has pan buttons and a zoom
 * slider. 
 */
public abstract class AbstractNavigationPane extends FlowPane {
	protected static final String CLSS = "AbstractNavigationPane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 32.;
	protected final Slider zoomSlider;
	protected final GuiUtil guiu = new GuiUtil();
	
	public AbstractNavigationPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		final Pane spacer = new Pane();
	    spacer.setMinSize(100, 1);
	    this.getChildren().add(spacer);
		this.zoomSlider = new Slider();
		this.getChildren().add(zoomSlider);
		
		//setMargin(navigationLabel,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
}
