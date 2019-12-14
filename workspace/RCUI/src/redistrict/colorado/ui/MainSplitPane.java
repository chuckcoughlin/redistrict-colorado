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
import javafx.scene.SubScene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.ui.common.ComponentIds;
import redistrict.colorado.ui.common.GuiUtil;
import redistrict.colorado.ui.common.PropertyBindingHub;
import redistrict.colorado.ui.common.UIConstants;
import redistrict.colorado.ui.common.ViewMode;
import redistrict.colorado.ui.layer.LayerConfigurationPage;
import redistrict.colorado.ui.layer.LayerListHolder;
import redistrict.colorado.ui.region.RegionListHolder;

/**
 * Create the menu hierarchy for the menubar.
 * The leaf nodes are class MenuItem.
 */
public class MainSplitPane extends SplitPane implements ChangeListener<ViewMode> {
	private static final String CLSS = "MainSplitPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final EventHandler<ActionEvent> eventHandler;
	private final StackPane leftStack;
	private final StackPane rightStack;
	
	public MainSplitPane() {
		this.eventHandler = new SplitPaneEventHandler();
		leftStack = new StackPane();
		rightStack = new StackPane();

		this.init();
	}
	
	private void init() {
		ScrollPane left = new ScrollPane();
		left.fitToWidthProperty().set(true);
		left.setCursor(Cursor.HAND);
		//leftStack.setPrefHeight(FRAME_HEIGHT);
		leftStack.getChildren().addAll(new LayerListHolder(),new RegionListHolder());
		leftStack.getChildren().get(0).setVisible(true);
		leftStack.getChildren().get(1).setVisible(false);
		SubScene leftStackScene = new SubScene(leftStack,UIConstants.SCENE_WIDTH,UIConstants.SCENE_HEIGHT-2*UIConstants.BUTTON_PANEL_HEIGHT);    // Holds scroll area
		left.setContent(leftStackScene);
		
		
		ScrollPane right = new ScrollPane();
		right.pannableProperty().set(true);
		right.setCursor(Cursor.OPEN_HAND);
		rightStack.getChildren().addAll(new MapCanvas(),new LayerConfigurationPage());
		Rectangle rect = new Rectangle(UIConstants.SCENE_WIDTH, UIConstants.SCENE_HEIGHT, Color.RED);
		right.setContent(rect);
			
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
				leftStack.getChildren().get(0).setVisible(true);
				leftStack.getChildren().get(1).setVisible(false);
			}
			else if( src.equalsIgnoreCase(ComponentIds.MENU_REGION)) {
				leftStack.getChildren().get(0).setVisible(false);
				leftStack.getChildren().get(1).setVisible(true);
			}
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends ViewMode> source, ViewMode oldValue, ViewMode newValue) {
		LOGGER.info("Got a value!");
		
	}
}
