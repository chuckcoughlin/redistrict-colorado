/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.pane.NavigationPane;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Plot a single feature graphically. Parent is an AnchorPane.
 */
public class FeatureMapPane extends BasicRightSideNode implements EventHandler<ActionEvent>,ChangeListener<Number>{
	private final static String CLSS = "FeatureMapPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private NavigationPane navPane = new NavigationPane(this,this);
	private Label headerLabel = new Label("Map");
	private DatasetModel model;
	private final FeatureMapRenderer map;

	public FeatureMapPane() {
		super(ViewMode.DATASET,DisplayOption.FEATURE_MAP);
		this.model = hub.getSelectedDataset();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);

		getChildren().add(navPane);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);

		setBottomAnchor(navPane,0.);
		setLeftAnchor(navPane,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(navPane,UIConstants.LIST_PANEL_RIGHT_MARGIN);

		Canvas canvas = new Canvas(UIConstants.SCENE_WIDTH-UIConstants.LIST_PANEL_LEFT_MARGIN-UIConstants.LIST_PANEL_RIGHT_MARGIN, 
				UIConstants.SCENE_HEIGHT-3*UIConstants.BUTTON_PANEL_HEIGHT);
		getChildren().add(canvas);
		setTopAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(canvas,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(canvas,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(canvas,UIConstants.BUTTON_PANEL_HEIGHT);

		map = new FeatureMapRenderer(canvas);
		updateModel();
	}

	@Override
	public void updateModel() {
		DatasetModel selectedModel = hub.getSelectedDataset();
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
