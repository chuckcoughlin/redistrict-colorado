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
import redistrict.colorado.core.HarmonicMean;
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PlanFeature;
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
 * Verify that the number of probable district outcomes aligns with the 
 * ethnic population as a whole.
 */
public class VotingPowerImbalanceGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_POWER = "VotingPower";
	private final static String KEY_SCORE = "Score";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_BLACK = "Black";
	private final static String KEY_HISPANIC = "Hispanic";
	private final static String KEY_WHITE = "White";
	
	private final Label aggregateLabel = new Label("Harmonic Mean of Normalized Voting Power by Ethnicity");
	private final Label detailLabel = new Label("Voting Power by Ethnicity for each District");
	private final Map<Long,VotingPowerAnalyzer> planAnalyzers; 
	
	public VotingPowerImbalanceGate() {
		this.planAnalyzers = new HashMap<>();
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Voting power is the ability to elect a candidate of one's choosing, ");
		Text t2 = new Text("that is, the ability to effect the outcome of an election. We want to make sure that this power in not diluted by ");
		Text t3 = new Text("artifically spredding votes of one ethnicity across multiple districts. For a single district, voting power is ");
		Text t4 = new Text("the fraction of the population for a given ethnicity times the total votes cast divided by the vote margin.");
		Text t5 = new Text(" We normalize by the overall population to mairgin ratio then take the harmonic mean for each ethniticy over ");
		Text t6 = new Text("all districts. We want to minimize how much this varies between ethnicities, ");
		Text t7 = new Text("so we take take difference between the ethnicity with the most power and the ethnicity with the least. ");
		Text t8= new Text("We want this score to be ");
		Text t9 = new Text("minimized");
		t9.setStyle("-fx-font-weight: bold");
		Text t10 = new Text("."); 
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
		return info;
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "Voting Power Imbalance"; } 
	public GateType getType() { return GateType.VOTING_POWER_IMBALANCE; }
 	/**
	 * Compute the voting power across ethnicities for each district. Save in the district scores.
	 * Also compute the composite.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("VotingPowerImbalanceGate.evaluating: ...");

		for(PlanModel plan:plans) {
			VotingPowerAnalyzer vpa = new VotingPowerAnalyzer(plan.getName(),plan.getMetrics());
			PowerSummary ps = vpa.getSummary();
			VotingPower.setNormalizationFactor(ps.getTotalPopulation()/ps.getTotalMargin());
			
			List<NameValue> powers = vpa.getVotingPowerDetails();
			int ndistricts = powers.size();
			// Create arrays to hold voting power values
			double[] black = new double[ndistricts];
			double[] hispanic = new double[ndistricts];
			double[] white = new double[ndistricts];
			
			int index = 0;
			for(NameValue nv:powers) {
				black[index] = ((VotingPower)(nv.getValue(Ethnicity.BLACK.name()))).getNormalizedVotePower();
				hispanic[index] = ((VotingPower)(nv.getValue(Ethnicity.HISPANIC.name()))).getNormalizedVotePower();
				white[index] = ((VotingPower)(nv.getValue(Ethnicity.WHITE.name()))).getNormalizedVotePower();
				index++;
			}
			
			// Find max difference
			double blackMean = HarmonicMean.evaluate(black);
			double hispanicMean = HarmonicMean.evaluate(hispanic);
			double whiteMean = HarmonicMean.evaluate(white);
			double max = blackMean;
			double min = blackMean;
			if( hispanicMean>max ) max = hispanicMean;
			if( whiteMean>max )    max = whiteMean;
			if( hispanicMean<min ) min = hispanicMean;
			if( whiteMean<min )    min = whiteMean;
			

			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_PLAN, plan.getName());
			nv.setValue(KEY_BLACK, blackMean);
			nv.setValue(KEY_HISPANIC, hispanicMean);
			nv.setValue(KEY_WHITE, whiteMean);
			nv.setValue(KEY_SCORE, max - min);
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
		double threshold = gp.getUnfairValue();
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
			// There is a single row containing the overall scores
			aitems.add(scoreMap.get(plan.getId()));
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
				
				List<NameValue> details = vpa.getVotingPowerDetails();
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
