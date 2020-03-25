/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.gate.CompactnessGate;
import redistrict.colorado.gate.CompetitiveDistrictsGate;
import redistrict.colorado.gate.CompositeGate;
import redistrict.colorado.gate.ContiguousGate;
import redistrict.colorado.gate.CountyCrossingGate;
import redistrict.colorado.gate.Gate;
import redistrict.colorado.gate.GateCache;
import redistrict.colorado.gate.PopulationEqualityGate;
import redistrict.colorado.gate.ProportionalityGate;
import redistrict.colorado.gate.VoteEfficiencyGate;
import redistrict.colorado.gate.VotingPowerGate;
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
	private Label headerLabel = new Label("Plan Comparison");
	private List<PlanModel> models;
	private final GridPane grid;
	private final Legend legend;

	public PlanComparisonPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_COMPARISON);
		models = EventBindingHub.getInstance().getActivePlans();
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		int nmodels = models.size();
		this.legend = new Legend();
		legend.setAlignment(Pos.CENTER);
		getChildren().add(legend);
		setTopAnchor(legend,HEADER_HEIGHT);
		setLeftAnchor(legend,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(legend,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
        
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(30.);
		grid.getColumnConstraints().add(col0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(30.);
		grid.getColumnConstraints().addAll(col1); 
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(25.);
		grid.getColumnConstraints().addAll(col2);
		
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
		grid.add(new ContiguousGate(), 2, 0);
		grid.add(new VotingPowerGate(), 0, 1);
		grid.add(new ProportionalityGate(), 1, 1);
		grid.add(new CompetitiveDistrictsGate(), 2, 1);
		grid.add(new VoteEfficiencyGate(), 0, 2);
		grid.add(new CountyCrossingGate(), 1, 2);
		grid.add(new CompositeGate(), 2, 2);
		GridPane.setMargin(grid,new Insets(20,0,0,0));  // top right bottom left
	
		getChildren().add(grid);
		setTopAnchor(grid,HEADER_HEIGHT*(nmodels+1));
		setLeftAnchor(grid,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(grid,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(grid,0.);
		
		updateModel();  // Perform the calculations
	}


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
