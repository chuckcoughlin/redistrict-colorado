/**  
 * Copyright (C) 2019 Charles Coughlin
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;

public class PlanListController extends AnchorPane 
							    implements EventReceiver<ActionEvent>,ChangeListener<PlanModel>  {
	private final static String CLSS = "PlanListController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Plans");
	private PlanButtonPane buttons = new PlanButtonPane();
	private ListView<PlanModel> planList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	private final EventBindingHub hub;
	
	public PlanListController() {
		this.auxEventHandler = new PlanListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		this.hub = EventBindingHub.getInstance();
		planList = new ListView<PlanModel>();
		planList.setCellFactory(new PlanRowFactory());
		planList.getSelectionModel().selectedItemProperty().addListener(this);
		planList.setMinWidth(UIConstants.LIST_PANEL_WIDTH);
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(planList);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(planList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(planList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(buttons,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(planList,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(planList,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(buttons,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(buttons,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		buttons.setDeleteDisabled(true);
		buttons.registerEventReceiver(this.auxEventDispatcher);
		updateUIFromDatabase();
	}
	
	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class PlanListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String id = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,id));
			if( id.equalsIgnoreCase(ComponentIds.BUTTON_ADD))       {
				Database.getInstance().getPlanTable().createPlan();
				updateUIFromDatabase();
			}
			// Delete the selected layer, then refresh
			else if( id.equalsIgnoreCase(ComponentIds.BUTTON_DELETE)) {
				PlanModel selectedModel = planList.getSelectionModel().getSelectedItem();
				if( selectedModel!=null) {
					Database.getInstance().getPlanTable().deletePlan(selectedModel.getId());
					planList.getItems().remove(selectedModel);
					updateUIFromDatabase();
				}
			}
			// Compare the selected models for fairness
			else if( id.equalsIgnoreCase(ComponentIds.BUTTON_ANALYZE)) {
				List<PlanModel> plans = new ArrayList<>();
				for(PlanModel model:planList.getItems()) {
					if(model.isActive()) plans.add(model);
				}
				// TODO: Trigger panel
			}
		}
	}
	/**
	 * Query the Layer table and update the list accordingly. Retain the same selection, if any.
	 */
	private void updateUIFromDatabase() {
		PlanModel selectedModel = planList.getSelectionModel().getSelectedItem();
		long selectedId = UIConstants.UNSET_KEY;
		if( selectedModel!=null ) selectedId = selectedModel.getId();
		selectedModel = null;
		
		List<PlanModel> plans = Database.getInstance().getPlanTable().getPlans();
		planList.getItems().clear();
		for(PlanModel model:plans) {
			planList.getItems().add(model);
			if( model.getId()==selectedId) selectedModel = model;
		}
		buttons.setDeleteDisabled(selectedModel==null);	
	}

	/**
	 * Listen for changes to the selected layer based on actions in the list.
	 */
	@Override
	public void changed(ObservableValue<? extends PlanModel> source, PlanModel oldValue, PlanModel newValue) {
		LOGGER.info(String.format("%s.changed: selected = %s", CLSS,(newValue==null?"null":newValue.getName())));
		buttons.setDeleteDisabled(newValue==null);
		//hub.setSelectedPlan(newValue);
	}
}
