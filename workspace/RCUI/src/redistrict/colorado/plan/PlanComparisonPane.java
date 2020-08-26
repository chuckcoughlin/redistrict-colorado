/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.gate.Gate;
import redistrict.colorado.gate.GateCache;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Show an array of Gates with results of plan comparison analysis.
 * @author chuckc
 *
 */
public class PlanComparisonPane extends BasicRightSideNode {
	private final static String CLSS = "PlanComparisonPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final double HEADER_HEIGHT = 20.;
	private static final double COL_PERCENT_WIDTH = 20.;
	private static final double ROW_PERCENT_HEIGHT = 30.;
	private static final double COLUMN_PAD = 40.;
	private Label headerLabel = new Label("District Plan Comparison");
	private List<PlanModel> models;
	private final GridPane grid;

	public PlanComparisonPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_COMPARISON);
		models = EventBindingHub.getInstance().getActivePlans();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		int nmodels = models.size();
		      
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints();
		//col0.setPercentWidth(COL_PERCENT_WIDTH);
		col0.setFillWidth(false);
		col0.setPrefWidth(Gate.WIDTH+COLUMN_PAD);
		ColumnConstraints col1 = new ColumnConstraints();
		//col1.setPercentWidth(COL_PERCENT_WIDTH);
		col1.setFillWidth(false);
		col1.setPrefWidth(Gate.WIDTH+COLUMN_PAD);
		ColumnConstraints col2 = new ColumnConstraints();
		//col2.setPercentWidth(COL_PERCENT_WIDTH);
		col2.setFillWidth(false);
		col2.setPrefWidth(Gate.WIDTH+COLUMN_PAD);
		grid.getColumnConstraints().addAll(col0,col1,col2);
		
		grid.getRowConstraints().clear();
		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(ROW_PERCENT_HEIGHT);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(ROW_PERCENT_HEIGHT);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(ROW_PERCENT_HEIGHT);
		grid.getRowConstraints().addAll(row0,row1,row2); 
		populateGrid();
		GridPane.setMargin(grid,new Insets(20,0,0,0));  // top right bottom left
	
		getChildren().add(grid);
		setTopAnchor(grid,HEADER_HEIGHT*(nmodels+1));
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(grid,0.);
	}

	// Place gates from the GateCache into the grid.
	private void populateGrid() {
		GateCache cache = GateCache.getInstance();
		grid.getChildren().clear();
		Gate composite = cache.getGate(GateType.COMPOSITE);
		grid.add(composite,0,0);
		grid.add(cache.getGate(GateType.POPULATION_BALANCE),1, 0);  // column row
		grid.add(cache.getGate(GateType.COMPACTNESS), 2, 0);
		
		grid.add(cache.getGate(GateType.COMPETIVENESS), 0, 1);
		grid.add(cache.getGate(GateType.PROPORTIONALITY), 1, 1);
		grid.add(cache.getGate(GateType.COUNTY_CROSSINGS), 2, 1);
		
		grid.add(cache.getGate(GateType.PARTISAN_ASYMMETRY), 0, 2);
		grid.add(cache.getGate(GateType.RACIAL_VOTE_DILUTION), 1, 2);
		grid.add(cache.getGate(GateType.VOTING_POWER_IMBALANCE), 2, 2);

	}

	/**
	 * We use the cache to make sure we are dealing with the same objects.
	 */
	@Override
	public void updateModel() {
		models = EventBindingHub.getInstance().getActivePlans();
		for(Gate gate:GateCache.getInstance().getBasicGates()) {
			gate.evaluate(models);
		}
		// Do the composite last as it relies on the others
		Gate composite = GateCache.getInstance().getGate(GateType.COMPOSITE);
		composite.evaluate(models);
	}
}
