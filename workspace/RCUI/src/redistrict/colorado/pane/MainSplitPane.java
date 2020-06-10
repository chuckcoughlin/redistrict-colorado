/**  
 * Copyright (C) 2019-2020 Charles Coughlin
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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.dataset.DatasetEditPane;
import redistrict.colorado.dataset.DatasetDetailPane;
import redistrict.colorado.dataset.DatasetListController;
import redistrict.colorado.district.DistrictTreeController;
import redistrict.colorado.district.DistrictMapPane;
import redistrict.colorado.plan.PlanComparisonPane;
import redistrict.colorado.plan.PlanEditPane;
import redistrict.colorado.plan.PlanPropertiesPane;
import redistrict.colorado.plan.PlanListController;
import redistrict.colorado.plan.PlanSetupPane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Create the main split panel. The left side is a stack of three options. The right side
 * has more. Options are dependent on selections from the left.
 */
public class MainSplitPane extends SplitPane implements ChangeListener<ViewMode> {
	private static final String CLSS = "MainSplitPane";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static int N_CHILDREN_RIGHT = 11;
	private final static int N_CHILDREN_LEFT = 3;
	private final EventHandler<ActionEvent> eventHandler;
	private final StackPane left;
	private final StackPane right;
	private final Node[] leftChildren;
	private final BasicRightSideNode[] rightChildren;
	private final RightSideController rightController;
	private final DistrictTreeController districtTreeController;
	
	public MainSplitPane() {
		this.eventHandler = new SplitPaneEventHandler();
		left = new StackPane();
		right = new StackPane();
		districtTreeController = new DistrictTreeController();
		leftChildren = new Node[N_CHILDREN_LEFT];
		leftChildren[0] = new PlanListController();
		leftChildren[1] = new DatasetListController();
		leftChildren[2] = districtTreeController;
		
		rightChildren = new BasicRightSideNode[N_CHILDREN_RIGHT];
		rightChildren[0] = new SplashScreen();
		rightChildren[1] = new DatasetMapPane(ViewMode.DATASET,DisplayOption.MODEL_MAP);
		rightChildren[2] = new DatasetMapPane(ViewMode.PLAN,DisplayOption.MODEL_MAP);
		rightChildren[3] = new DatasetDetailPane();
		rightChildren[4] = new DatasetEditPane();
		rightChildren[5] = new PlanMapPane();
		rightChildren[6] = new PlanSetupPane();
		rightChildren[7] = new PlanComparisonPane();
		rightChildren[8] = new PlanPropertiesPane();
		rightChildren[9] = new PlanEditPane();
		rightChildren[10] = new DistrictMapPane();
		
		this.rightController = new RightSideController(rightChildren);
		this.init();
	}
	
	private void init() {
		left.setCursor(Cursor.HAND);
		for(int i=0;i<N_CHILDREN_LEFT;i++) {
			left.getChildren().add(leftChildren[i]);
			if( i==0 )leftChildren[0].toFront();
			else leftChildren[i].setVisible(false);
		}
	
		right.setCursor(Cursor.OPEN_HAND);
		for(int i=0;i<N_CHILDREN_RIGHT;i++) {
			right.getChildren().add(rightChildren[i]);
			if( i==0 )rightChildren[0].toFront();    // SplashScreen
			else rightChildren[i].setVisible(false);
		}
			
		getItems().addAll(left,right);
		EventBindingHub.getInstance().addModeListener(this);
		
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
			if( id.equalsIgnoreCase(ComponentIds.MENU_DATASET)) mode = ViewMode.DATASET;
			else if( id.equalsIgnoreCase(ComponentIds.MENU_DISTRICT)) mode = ViewMode.DISTRICT;
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
	 * If the hub has an appropriate value, then display the proper right-side also. If there is
	 * no appropriate value, then display the splash screen. See constructor for index values.
	 * NOTE: We do not iterate over left.getChildren() due to ConcurrentModification exceptions.
	 */
	private synchronized void updateUIForViewMode(ViewMode mode) {
		EventBindingHub hub = EventBindingHub.getInstance();
		int leftPane = 0; //
		int rightPane = 0;  // Splash screen
		if( mode.equals(ViewMode.PLAN)) {
			leftPane = 0;
			if(hub.isPlanSelected()) rightPane = 7; // Plan metrics
		}
		if( mode.equals(ViewMode.DATASET)) {
			leftPane = 1;
			if(hub.isDatasetSelected()) rightPane = 1; // Dataset map
		}
		else if( mode.equals(ViewMode.DISTRICT)) {
			leftPane = 2;
			districtTreeController.populateDatasets();
		}
		int index = 0;
		while(index<leftChildren.length){
			if( index==leftPane) {
				leftChildren[index].toFront();
				leftChildren[index].setVisible(true);
			}
			else {
				leftChildren[index].setVisible(false);
			}
			index++;
		}
		index = 0;
		while(index<rightChildren.length){
			if( index==rightPane) {
				rightChildren[index].toFront();
				rightChildren[index].setVisible(true);
			}
			else {
				rightChildren[index].setVisible(false);
			}
			index++;
		}
	}
}
