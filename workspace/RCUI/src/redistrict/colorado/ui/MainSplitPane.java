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
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.EventRoutingHub;
import redistrict.colorado.layer.LayerConfigurationPage;
import redistrict.colorado.layer.LayerListController;
import redistrict.colorado.plan.PlanListController;
import redistrict.colorado.region.RegionListController;

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
	private final Node[] leftChildren;
	private ViewMode currentViewMode = ViewMode.PLAN;  // Initially
	
	public MainSplitPane() {
		this.eventHandler = new SplitPaneEventHandler();
		left = new StackPane();
		right = new StackPane();
		leftChildren = new Node[3];
		leftChildren[0] = new PlanListController();
		leftChildren[1] = new LayerListController();
		leftChildren[2] = new RegionListController();
		
		this.init();
	}
	
	private void init() {
		left.setCursor(Cursor.HAND);
		left.getChildren().add(leftChildren[0]);
		left.getChildren().add(leftChildren[1]);
		left.getChildren().add(leftChildren[2]);
		leftChildren[0].toFront();
		leftChildren[1].setVisible(false);
		leftChildren[2].setVisible(false);
		
		right.setCursor(Cursor.OPEN_HAND);
		right.getChildren().addAll(new MapCanvas(),new LayerConfigurationPage());
		// The rectangle is a place holder for when there is no selection in the left pane.
		Rectangle rect = new Rectangle(UIConstants.SCENE_WIDTH, UIConstants.SCENE_HEIGHT, Color.ANTIQUEWHITE);
		right.getChildren().addAll(rect);
			
		getItems().addAll(left,right);
		EventRoutingHub.getInstance().addModeListener(this);
		
		// Set min height before starting to lose button panel.
		left.setMinHeight(UIConstants.STACK_PANE_MIN_HEIGHT);
		right.setMinHeight(UIConstants.STACK_PANE_MIN_HEIGHT);
		
		left.setPrefWidth(800.);
		right.setMinWidth(UIConstants.STACK_PANE_MIN_WIDTH);
	}
	
	/**
	 * The menu bar view option determines which pane shows in the stack. 
	 * NOTE: This not currently hooked up.
	 */
	public class SplitPaneEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String id = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,id));
			ViewMode mode = ViewMode.PLAN;
			if( id.equalsIgnoreCase(ComponentIds.MENU_LAYER))      mode = ViewMode.LAYER;
			else if( id.equalsIgnoreCase(ComponentIds.MENU_REGION))mode = ViewMode.REGION;
			updateUIForViewMode(mode);
		}
	}
	
	/**
	 * The source is a bindable SimpleObjectProperty of the hub. Values are ViewMode.
	 * Run later (still on UI Thread) to avoid concurrent modification of property
	 */
	@Override
	public void changed(ObservableValue<? extends ViewMode> source, ViewMode oldValue, ViewMode newValue) {
		LOGGER.info(String.format("%s.changed: new value=%s",CLSS,newValue.name()));
		updateUIForViewMode(newValue);
	}
	
	/**
	 * Display the panes in the stack that correspond to the selected ViewMode and screen option.
	 * NOTE: We do not iterate over left.getChildren() due to ConcurrentModification exceptions.
	 */
	private synchronized void updateUIForViewMode(ViewMode mode) {
		currentViewMode = mode;
		int pane = 0;
		if( mode.equals(ViewMode.LAYER))pane = 1;
		else if( mode.equals(ViewMode.REGION))pane = 2;
		int index = 0;
		while(index<leftChildren.length){
			if( index==pane) {
				leftChildren[index].toFront();
				leftChildren[index].setVisible(true);
			}
			else {
				leftChildren[index].setVisible(false);
			}
			index++;
		}


	}
	
}
