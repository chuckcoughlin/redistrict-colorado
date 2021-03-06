/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
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
public class PlanMapPane extends BasicRightSideNode implements EventHandler<ActionEvent> {
	private final static String CLSS = "PlanMapPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final PlanMapConfigurationPane headerPane;
	private PlanModel model;
	private final PlanMapRenderer renderer;

	public PlanMapPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_MAP);
		this.model = hub.getSelectedPlan();
		this.headerPane = new PlanMapConfigurationPane("Map",this);
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

		ColorizingOption opt = ColorizingOption.NONE;
		renderer = new PlanMapRenderer(view);
		hub.setSelectedColorOption(opt);
		renderer.setColorizingOption(opt);
		headerPane.setColorizingOption(opt);
		updateModel();
	}

	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			model = selectedModel;
			LOGGER.info(String.format("%s.updateModel: selected = %s", CLSS,model.getName()));
			renderer.updateModel(model);
			headerPane.updateModel(model);
		}
	}
	
	// ======================================= Event Handler ==========================================
	/**
	 * Respond to combo box selections in header panel
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if( source instanceof ComboBox ) {
			ComboBox<String>box = (ComboBox<String>)source;
			ColorizingOption opt = ColorizingOption.valueOf(box.getValue().toUpperCase());
			LOGGER.info(String.format("%s.handle: combo box = %s", CLSS,opt.name()));
			hub.setSelectedColorOption(opt);
			renderer.setColorizingOption(opt);
			headerPane.setColorizingOption(opt);
		}
	}
}
