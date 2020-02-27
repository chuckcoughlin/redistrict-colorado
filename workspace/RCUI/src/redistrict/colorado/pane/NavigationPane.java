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
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * A navigation pane is shown under the panes on the right-side of the split.
 * This abstract base class sets a common size. It has pan buttons and a zoom
 * slider. 
 */
public class NavigationPane extends AnchorPane {
	protected static final String CLSS = "NavigationPane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double HGAP = 8.;
	private static final double MARGIN = 100.;
	private final Button leftButton;
	private final Button rightButton;
	private final Button upButton;
	private final Button downButton;
	protected final Slider zoomSlider;
	protected final GuiUtil guiu = new GuiUtil();
	
	public NavigationPane(EventHandler<ActionEvent> handler,ChangeListener<Number> listener) {
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		leftButton = new Button("",guiu.loadImage("images/arrow_left_blue.png"));
		leftButton.setId(ComponentIds.BUTTON_LEFT);
		leftButton.setOnAction(handler);
		this.getChildren().add(leftButton);
		
		rightButton = new Button("",guiu.loadImage("images/arrow_right_blue.png"));
		rightButton.setId(ComponentIds.BUTTON_RIGHT);
		rightButton.setOnAction(handler);
		this.getChildren().add(rightButton);
		
		upButton = new Button("",guiu.loadImage("images/arrow_up_blue.png"));
		upButton.setId(ComponentIds.BUTTON_UP);
		upButton.setOnAction(handler);
		this.getChildren().add(upButton);
		
		downButton = new Button("",guiu.loadImage("images/arrow_down_blue.png"));
		downButton.setId(ComponentIds.BUTTON_DOWN);
		downButton.setOnAction(handler);
		this.getChildren().add(downButton);
		
		this.zoomSlider = new Slider();
		zoomSlider.setMin(0.8);
		zoomSlider.setMax(10.);
		zoomSlider.valueProperty().addListener(listener);  // Scale
		this.getChildren().add(zoomSlider);
		
		setTopAnchor(leftButton,HGAP);
		setTopAnchor(rightButton,HGAP);
		setTopAnchor(upButton,HGAP);
		setTopAnchor(downButton,HGAP);
		setLeftAnchor(leftButton,MARGIN+UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(rightButton,MARGIN+2*UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(upButton,MARGIN+3*UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(downButton,MARGIN+4*UIConstants.BUTTON_PANEL_HEIGHT);
		setTopAnchor(zoomSlider,HGAP);
		setRightAnchor(zoomSlider,MARGIN+UIConstants.BUTTON_PANEL_HEIGHT);
	}
}
