/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A navigation pane is shown under the panes on the right-side of the split.
 * This abstract base class sets a common size. It has pan buttons and a zoom
 * slider. 
 */
public class NavigationPane extends FlowPane {
	protected static final String CLSS = "NavigationPane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	private static final double LMARGIN = 200.;
	protected final Slider zoomSlider;
	protected final GuiUtil guiu = new GuiUtil();
	
	public NavigationPane(EventHandler<ActionEvent> handler,ChangeListener<Number> listener) {
		super(Orientation.HORIZONTAL,HGAP,VGAP);
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		this.zoomSlider = new Slider();
		zoomSlider.setMin(0.8);
		zoomSlider.setMax(10.);
		zoomSlider.valueProperty().addListener(listener);  // Scale
		this.getChildren().add(zoomSlider);
		
		setMargin(zoomSlider,new Insets(VGAP,HGAP,VGAP,LMARGIN));
	}
}
