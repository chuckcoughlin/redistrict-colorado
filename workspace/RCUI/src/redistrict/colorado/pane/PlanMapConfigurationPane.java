/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;


import java.util.logging.Logger;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.plan.ColorizingLegend;
import redistrict.colorado.ui.ColorizingOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * This is the header pane for a plan map (right side).  
 */
public class PlanMapConfigurationPane extends AnchorPane {
	protected static final String CLSS = "PlanMapConfigurationPane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double COMBO_WIDTH = 40.;
	private static final double LEGEND_WIDTH = 40.;
	private static final double HGAP = 8.;
	private Label headerLabel = new Label("Title");
	private final ColorizingLegend legend;
	private final ComboBox<String> colorizingOptionCombo;

	protected final GuiUtil guiu = new GuiUtil();
	
	public PlanMapConfigurationPane(String title) {
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		headerLabel.getStyleClass().add("list-header-label");
		setText(title);
		this.getChildren().add(headerLabel);
		
		colorizingOptionCombo  = new ComboBox<>();
		colorizingOptionCombo.setPrefWidth(COMBO_WIDTH);
		colorizingOptionCombo.getItems().clear();
		colorizingOptionCombo.getItems().addAll(ColorizingOption.names());
		this.getChildren().add(colorizingOptionCombo);
		
		
		this.legend = new ColorizingLegend();
		legend.setPrefWidth(LEGEND_WIDTH);
		this.getChildren().add(legend);
		
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,HGAP);
		setRightAnchor(headerLabel,3*HGAP+COMBO_WIDTH+LEGEND_WIDTH);
		
		setTopAnchor(colorizingOptionCombo,0.);
		setRightAnchor(headerLabel,2*HGAP+LEGEND_WIDTH);
		setTopAnchor(legend,HGAP/2.);
		setRightAnchor(headerLabel,HGAP);
	}
	
	public void setText(String title) { this.headerLabel.setText(title); }
}
