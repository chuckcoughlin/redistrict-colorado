/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.gate.CompactnessGate;
import redistrict.colorado.gate.CompetitiveDistrictsGate;
import redistrict.colorado.gate.PopulationEqualityGate;
import redistrict.colorado.gate.ProportionalityGate;
import redistrict.colorado.gate.VoteEffeciencyGate;
import redistrict.colorado.gate.VotingPowerImbalanceGate;
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
	private final static double COL0_WIDTH = 100.;    // margin
	private final static double COL1_WIDTH = 300.;
	private final static double COL2_WIDTH = 40.;
	private Label headerLabel = new Label("Plan Comparison");
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
        
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPercentWidth(50.);
		col0.setHalignment(HPos.CENTER);
		ColumnConstraints col1 = new ColumnConstraints(COL1_WIDTH,COL1_WIDTH,Double.MAX_VALUE);
		col1.setHalignment(HPos.LEFT);
		col1.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col0,col1); 
		
		grid.getRowConstraints().clear();
		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(30.);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(30.);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(30.);
		grid.getRowConstraints().addAll(row0,row1,row2); 
			
		grid.add(new PopulationEqualityGate(),0, 0);  // column row
		grid.add(new CompactnessGate(), 1, 0);
		grid.add(new VoteEffeciencyGate(), 0, 1);
		grid.add(new VotingPowerImbalanceGate(), 1, 1);
		grid.add(new ProportionalityGate(), 0, 2);
		grid.add(new CompetitiveDistrictsGate(), 1, 2);
	
		getChildren().add(grid);
		setTopAnchor(grid,UIConstants.DETAIL_HEADER_SPACING);
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(grid,0.);
	}


	@Override
	public void updateModel() {
		List<PlanModel> models = EventBindingHub.getInstance().getActivePlans();
		
	}
}
