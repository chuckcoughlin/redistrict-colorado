/**  
fddddddddddddddddddddedddddddddddddddddddd * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;


import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.ColorizingOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

/**
 * This is the header pane for a plan map (right side).  
 * Center the label. Place combo box on the left and 
 * legend on the right.
 */
public class PlanMapConfigurationPane extends AnchorPane {
	protected static final String CLSS = "PlanMapConfiguriationPane";
	protected static final Logger LOGGER = Logger.getLogger(CLSS);
	protected final EventBindingHub hub;
	private static final double COMBO_HEIGHT = 12.;
	private static final double COMBO_WIDTH = 150.;
	private static final double LEGEND_WIDTH = 240.;
	private static final double LEFT_MARGIN = 100.;
	private static final double SMALL_LABEL_HEIGHT = 15.;
	private static final double RIGHT_MARGIN = 50.;
	private static final double HGAP = 8.;
	private Label headerLabel = new Label("Title");
	private final ColorizingLegend legend;
	private final ComboBox<String> colorizingOptionCombo;
	private final Label colorizingLabel;
	private PlanModel plan = null;

	protected final GuiUtil guiu = new GuiUtil();
	
	public PlanMapConfigurationPane(String title,EventHandler<ActionEvent>handler) {
		this.hub = EventBindingHub.getInstance();
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		headerLabel.getStyleClass().add("list-header-label");
		this.getChildren().add(headerLabel);
		
		colorizingLabel = new Label("Colorizing:");
		colorizingLabel.getStyleClass().add("small-label");  // Height = 15
		colorizingOptionCombo  = new ComboBox<>();
		colorizingOptionCombo.getStyleClass().add("small-combo");
		colorizingOptionCombo.setMaxHeight(COMBO_HEIGHT);
		colorizingOptionCombo.setPrefHeight(COMBO_HEIGHT);
		colorizingOptionCombo.getItems().clear();
		colorizingOptionCombo.getItems().addAll(ColorizingOption.names());
		// Modify the combo box display for a smaller font
		colorizingOptionCombo.setButtonCell(new ListCell<String>(){
	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty); 
	            if(empty || item==null){
	                setStyle("-fx-font-size:10");
	            } 
	            else {
	                setStyle("-fx-font-size:10");
	                setText(item);
	            }
	        }
	    });
		// Modify the cell factory to change the size of the combo items
		colorizingOptionCombo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
		     @Override 
		     public ListCell<String> call(ListView<String> p) {
		         final ListCell<String> cell = new ListCell<String>() {
		             @Override 
		             protected void updateItem(String item, boolean empty) {
		                 super.updateItem(item, empty);
		                 if (item != null && !empty) {
		                     setText(item);
		                     setFont(Font.font(this.getFont().getName(),9.0));
		                 } 
		                 else {
		                	 setText(null);
		                 }
		            }
		       };
		       return cell;
		   }
		});
		colorizingOptionCombo.setOnAction(handler);
		this.getChildren().addAll(colorizingLabel,colorizingOptionCombo);
		setTopAnchor(colorizingLabel,0.);
		setTopAnchor(colorizingOptionCombo,SMALL_LABEL_HEIGHT);
		setLeftAnchor(colorizingLabel,LEFT_MARGIN);
		setLeftAnchor(colorizingOptionCombo,LEFT_MARGIN);
		
		this.legend = new ColorizingLegend();
		legend.setPrefWidth(LEGEND_WIDTH);
		
		this.getChildren().add(legend);
		setTopAnchor(legend,0.);
		setRightAnchor(legend,RIGHT_MARGIN);
		
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,3*HGAP+COMBO_WIDTH+LEFT_MARGIN);
		setRightAnchor(headerLabel,2*HGAP+LEGEND_WIDTH+RIGHT_MARGIN);
	}
	// Simply update the combo box without triggering a change
	public void setColorizingOption(ColorizingOption opt) {
		colorizingOptionCombo.setValue(opt.name());
		legend.setOption(opt);
	}
	
	// Specify the plan that this pane represents
	public void updateModel(PlanModel model) { 
		this.plan = model; 
		headerLabel.setText(model.getName());
		legend.updateModel(plan);
	}
	
	public void setText(String title) { this.headerLabel.setText(title); }
}
