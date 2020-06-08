/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.Ethnicity;
import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.PowerSummary;
import redistrict.colorado.core.VotingPower;
import redistrict.colorado.core.VotingPowerAnalyzer;
import redistrict.colorado.db.Database;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.UIConstants;

/**
 * Evaluate plans for the presence od vote dilution.
 */
public class RacialVoteDilutionGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_ETHNICITY = "Ethnicity";
	private final static String KEY_MAD = "MAD";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_BLACK = "Black";
	private final static String KEY_HISPANIC = "Hispanic";
	private final static String KEY_WHITE = "White";
	
	private final Label aggregateLabel = new Label("Mean Absolute Deviation across Districts by Ethnicity");
	private final Label detailLabel = new Label("Normalized Voting Power by Ethnicity for each District (uniformity implies dilution)");
	private final Map<Long,VotingPowerAnalyzer> planAnalyzers; 
	
	public RacialVoteDilutionGate() {
		this.planAnalyzers = new HashMap<>();
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To evaluate vote dilution, for each ethnic group in each district take the ratio of votes by the group to vote margin. ");
		Text t2 = new Text("Scale this value by the overall vote population to margin ratio. Take the log of the result.");
		Text t3 = new Text("Compute the mean absolute deviation (MAD) across the districts. A MAD value near zero implies dilution. Compare ethnic groups.");
		Text t4 = new Text("Take the minimum value and record the associated ethnic group.");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getScoreAttribute() { return KEY_MAD; };
	public String getTitle() { return "Racial Vote Dilution"; } 
	public GateType getType() { return GateType.RACIAL_VOTE_DILUTION; }
 	/**
	 * Compute the voting power across ethnicities for each district. Save in the district scores.
	 * Also compute the composite.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("RacialVoteDilutionGate.evaluating: ...");

		for(PlanModel plan:plans) {
			VotingPowerAnalyzer vpa = new VotingPowerAnalyzer(plan.getName(),plan.getMetrics());
			double mad = vpa.getRacialVoteDilution();
			Ethnicity group = vpa.getDilutedGroup();;

			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_PLAN, plan.getName());
			nv.setValue(KEY_MAD, mad);
			nv.setValue(KEY_ETHNICITY, group.name());
			scoreMap.put(plan.getId(),nv);
			planAnalyzers.put(plan.getId(), vpa);
		}
		Collections.sort(plans,compareByScore); 
		Collections.reverse(plans);   // Because minimum is best.
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	
	// Create contents that allow viewing the details of the calculation
	@Override
	protected Node getResultsContents() { 
		GateProperty gp = Database.getInstance().getGateTable().getGateProperty(getType());
		VBox pane =  new VBox(10);
		pane.setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		pane.setFillWidth(true);
		
		aggregateLabel.setId(ComponentIds.LABEL_SCORE);
		pane.getChildren().add(aggregateLabel);

		// Aggregate table
		TableView<NameValue> aggregateTable = new TableView<>();
		double height = UIConstants.TABLE_ROW_HEIGHT*(1.5+sortedPlans.size());
		aggregateTable.setPrefSize(AGGREGATE_TABLE_WIDTH, height);
		aggregateTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		TableColumn<NameValue,String> column;
		NameValueCellValueFactory factory = new NameValueCellValueFactory();
		factory.setFormat(KEY_BLACK, "%2.2f");
		factory.setFormat(KEY_HISPANIC, "%2.2f");
		factory.setFormat(KEY_WHITE, "%2.2f");
		column = new TableColumn<>(KEY_NAME);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.4));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_BLACK);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.2));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_HISPANIC);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.2));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_WHITE);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.2));
		aggregateTable.getColumns().add(column);
		ObservableList<NameValue> aitems = FXCollections.observableArrayList();
		for(PlanModel plan:sortedPlans ) {
			// Get the analyzer and create a list of MAD values by ethnicity/aitems
			VotingPowerAnalyzer vpa = planAnalyzers.get(plan.getId());
			aitems.add(vpa.getDilutions());
		}
		aggregateTable.setItems(aitems);
		pane.getChildren().add(aggregateTable);
		
		detailLabel.setId(ComponentIds.LABEL_SCORE);
		pane.getChildren().add(detailLabel);
		
		// Detail table
		TableView<List<NameValue>> detailTable = new TableView<>();
		TableColumn<List<NameValue>,String> col;
		TableColumn<List<NameValue>,String> subcol;
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		fact.setFormat(KEY_BLACK, "%2.2f");
		fact.setFormat(KEY_HISPANIC, "%2.2f");
		fact.setFormat(KEY_WHITE, "%2.2f");
		
		int colno = 0;
		int maxrows = 0;  // Max districts among plans
		double widthFactor = 1./(4*sortedPlans.size());
		for(PlanModel plan:sortedPlans ) {
			int ndistricts = planAnalyzers.get(plan.getId()).getNDistricts();
			if(ndistricts>maxrows) maxrows = ndistricts;
			// These columns have no cells, just sub-columns.
			col = new TableColumn<>(plan.getName());
			col.setPrefWidth(DIALOG_WIDTH);
			detailTable.getColumns().add(col);
			subcol = new TableColumn<>(KEY_NAME);
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_BLACK);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_HISPANIC);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_WHITE);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			colno++;
		}
		
		ObservableList<List<NameValue>> ditems = FXCollections.observableArrayList();
		for( int row=0;row<maxrows;row++ ) {
			List<NameValue> values = new ArrayList<>();
			for(PlanModel plan:sortedPlans ) {
				VotingPowerAnalyzer vpa = planAnalyzers.get(plan.getId());
				PowerSummary ps = vpa.getSummary();
				VotingPower.setNormalizationFactor(ps.getTotalPopulation()/ps.getTotalMargin());
				
				List<NameValue> details = vpa.getDetails();
				Collections.sort(details,compareByName);
				if(details.size()>row ) {
					values.add(details.get(row));
				}
				else {
					values.add(NameValue.EMPTY);
				}
			}
			ditems.add(values);
		}
		
		detailTable.setItems(ditems);
		pane.getChildren().add(detailTable);

		return pane;
	}
}
