/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pref.PreferenceKeys;
import redistrict.colorado.ui.ColorizingOption;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a map corresponding to a plan. Plot a Google Map as a backdrop.
 * Parent is an AnchorPane.
 */
public class PlanMapPane extends BasicRightSideNode {
	private final static String CLSS = "PlanMapPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final PlanMapConfigurationPane headerPane;
	private PlanModel model;
	private final PlanMapRenderer map;

	public PlanMapPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_MAP);
		this.model = hub.getSelectedPlan();
		this.headerPane = new PlanMapConfigurationPane("Map");
		getChildren().add(headerPane);

		String key = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
		GoogleMapView view = new GoogleMapView(key,GoogleMapView.PLAN_PATH);
		view.setMinWidth(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN);
		view.setMinHeight(UIConstants.SCENE_HEIGHT-2.*UIConstants.BUTTON_PANEL_HEIGHT);
		getChildren().add(view);
		setTopAnchor(view,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(view,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(view,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(view,0.);
		
		setTopAnchor(headerPane,0.);
		setLeftAnchor(headerPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);

		map = new PlanMapRenderer(view);
		updateModel();
	}

	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			model = selectedModel;
			LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
			map.updateModel(model);
			headerPane.setPlan(model);
		}
		ColorizingOption option = hub.getSelectedColorOption();
		headerPane.setColorizingOption(option);
	}
}
