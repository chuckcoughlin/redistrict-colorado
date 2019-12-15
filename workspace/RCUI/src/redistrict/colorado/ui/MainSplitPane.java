/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.PropertyBindingHub;
import redistrict.colorado.layer.LayerConfigurationPage;
import redistrict.colorado.layer.LayerListHolder;
import redistrict.colorado.region.RegionListHolder;

/**
 * Create the main split panel. The left side is a stack of three options. The right side
 * has more. Options are dependent on selections from the left.
 */
public class MainSplitPane extends SplitPane implements ChangeListener<ViewMode> {
	private static final String CLSS = "MainSplitPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final EventHandler<ActionEvent> eventHandler;
	private final StackPane left;
	private final StackPane right;
	
	public MainSplitPane() {
		this.eventHandler = new SplitPaneEventHandler();
		left = new StackPane();
		right = new StackPane();

		this.init();
	}
	
	private void init() {
		left.setCursor(Cursor.HAND);
		left.setPrefHeight(UIConstants.FRAME_HEIGHT);
		left.getChildren().addAll(new LayerListHolder(),new RegionListHolder());
		left.getChildren().get(0).setVisible(true);
		left.getChildren().get(1).setVisible(false);
		
		right.setCursor(Cursor.OPEN_HAND);
		right.getChildren().addAll(new MapCanvas(),new LayerConfigurationPage());
		Rectangle rect = new Rectangle(UIConstants.SCENE_WIDTH, UIConstants.SCENE_HEIGHT, Color.RED);
		right.getChildren().addAll(rect);
			
		getItems().addAll(left,right);
		PropertyBindingHub.getInstance().addModeListener(this);
	}
	
	/**
	 * The menu bar can flip the current pane.
	 */
	public class SplitPaneEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String src = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,src));
			if( src.equalsIgnoreCase(ComponentIds.MENU_LAYER)) {
				left.getChildren().get(0).setVisible(true);
				left.getChildren().get(1).setVisible(false);
			}
			else if( src.equalsIgnoreCase(ComponentIds.MENU_REGION)) {
				left.getChildren().get(0).setVisible(false);
				left.getChildren().get(1).setVisible(true);
			}
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends ViewMode> source, ViewMode oldValue, ViewMode newValue) {
		LOGGER.info("Got a value!");
		
	}
}
