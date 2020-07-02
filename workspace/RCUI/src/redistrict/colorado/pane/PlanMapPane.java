/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.pref.PreferenceKeys;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a map corresponding to a plan. Plot a Google Map as a backdrop.
 * Parent is an AnchorPane.
 */
public class PlanMapPane extends BasicRightSideNode implements EventHandler<ActionEvent>,ChangeListener<Number>{
	private final static String CLSS = "PlanMapPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Map");
	private PlanModel model;
	private final PlanMapRenderer map;

	public PlanMapPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_MAP);
		this.model = hub.getSelectedPlan();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);

		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);

		String key = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
		GoogleMapView view = new GoogleMapView(key,GoogleMapView.PLAN_PATH);
		view.setMinWidth(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN);
		view.setMinHeight(UIConstants.SCENE_HEIGHT-3*UIConstants.BUTTON_PANEL_HEIGHT);
		getChildren().add(view);
		setTopAnchor(view,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(view,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(view,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(view,UIConstants.BUTTON_PANEL_HEIGHT);

		map = new PlanMapRenderer(view);
		updateModel();
	}

	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			model = selectedModel;
			headerLabel.setText(model.getName());
			LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
			map.updateModel(model);
		}
	}

	/**
	 * The zoom slider on the navigation pane has changed value.
	 */
	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * An arrow button has been selected on the navigation pane
	 */
	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
}
