/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.BasicEventDispatchChain;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.EventSource;
import redistrict.colorado.bind.LeftSelectionEvent;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * The panel contains add and delete buttons plus a button to trigger the analysis.
 * 
 * We provide a method to enable/disable the delete button.
 */
public class PlanButtonPane extends AnchorPane implements EventSource<ActionEvent>, EventHandler<ActionEvent>, ChangeListener<PlanModel> {
	private static final String CLSS = "ButtonPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final double HGAP = 6.;
	private final Button addButton;
	private final Button analyzeButton;
	private final Button deleteButton;
	private final GuiUtil guiu = new GuiUtil();
	private final EventHandler<ActionEvent> eventHandler;
	private final BasicEventDispatchChain<ActionEvent> eventChain;
	
	public PlanButtonPane() {
		EventBindingHub.getInstance().addPlanListener(this);
		this.setPrefHeight(UIConstants.BUTTON_PANEL_HEIGHT);
		this.eventHandler = new ButtonPaneEventHandler();
		this.eventChain   = new BasicEventDispatchChain<ActionEvent>();
		addButton = new Button("",guiu.loadImage("images/add.png"));
		addButton.setId(ComponentIds.BUTTON_ADD);
		addButton.setOnAction(eventHandler);
		
		deleteButton = new Button("",guiu.loadImage("images/delete.png"));
		deleteButton.setId(ComponentIds.BUTTON_DELETE);
		deleteButton.setDisable(true);
		deleteButton.setOnAction(eventHandler);
		
		analyzeButton = new Button("Analyze\n Active");
		analyzeButton.setId(ComponentIds.BUTTON_ANALYZE);
		analyzeButton.setAlignment(Pos.CENTER_RIGHT);
		analyzeButton.setDisable(true);
		analyzeButton.setOnAction(this);
		analyzeButton.setId(ComponentIds.BUTTON_ANALYZE);

		this.getChildren().add(addButton);
		this.getChildren().add(deleteButton);
		this.getChildren().add(analyzeButton);
		
		setTopAnchor(addButton,HGAP);
		setTopAnchor(deleteButton,HGAP);
		setTopAnchor(analyzeButton,HGAP);
		setLeftAnchor(addButton,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(deleteButton,2*UIConstants.BUTTON_PANEL_HEIGHT);
		setRightAnchor(analyzeButton,UIConstants.BUTTON_PANEL_HEIGHT);
		
		updateUI();
	}
	
	public void setDeleteDisabled(boolean flag) { deleteButton.setDisable(flag); }
	
	/**
	 * One of the buttons has been pressed. The source of the event is the button.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class ButtonPaneEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,((Node)event.getSource()).getId()));
			eventChain.dispatchEvent(event);
		}
	}

	@Override
	public void registerEventReceiver(BasicEventDispatcher<ActionEvent> bed) {
		eventChain.append(bed);	
	}

	/**
	 * The "analyze" button has been selected locally. Display analysis screen.
	 * @param event
	 */
	@Override
	public void handle(ActionEvent event) {
		EventBindingHub hub = EventBindingHub.getInstance();
		hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.PLAN,DisplayOption.PLAN_COMPARISON));
	}
	
	// =========================== ChangeListener<PlanModel> =============================================
	/**
	 * There has been some change to a plan. Check to see if any are active - even if the new model is null
	 */
	@Override
	public void changed(ObservableValue<? extends PlanModel> source, PlanModel oldModel, PlanModel newModel) {
		updateUI();
	}

	/**
	 * Set active plans and enable/disable analyze button appropriately.
	 */
	private void updateUI() {
		EventBindingHub hub = EventBindingHub.getInstance();
		List<PlanModel> activePlans = new ArrayList<>();
		List<PlanModel> plans = Database.getInstance().getPlanTable().getPlans();
		boolean hasActive = false;
		for(PlanModel model:plans) {
			if( model.isActive()) {
				hasActive = true;
				activePlans.add(model);
			}
		}
		hub.setActivePlans(activePlans);
		analyzeButton.setDisable(!hasActive);
	}
}
