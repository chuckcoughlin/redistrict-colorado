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

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.table.NameValue;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueLimitCellFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.table.NameValueListLimitCellFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on the populations of each district are within 
 * 1% of each other.
 */
public class RacialVoteDilutionGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_MAD = "MAD";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_BLACK = "Black";
	private final static String KEY_HISPANIC = "Hispanic";
	private final static String KEY_WHITE = "White";
	
	private final Label aggregateLabel = new Label("Voting Power by Ethnicity and Mean Absoute Deviation");
	private final Label detailLabel = new Label("Voting Power by Ethnicity for each District");
	private final Map<Long,List<NameValue>> districtScores;  
	
	public RacialVoteDilutionGate() {
		this.districtScores = new HashMap<>();
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Voting power is the ability to elect a candidate of one's choosing, ");
		Text t2 = new Text("that is the ability to effect the outcome of an election. We want to make sure that this power in not diluted by ");
		Text t3 = new Text("artifically spredding votes of one ethnicity across multiple districts. For a single district, voting power is the margin of victory (in votes) ");
		Text t4 = new Text("divided by the total votes cast multiplied by the fraction of the population for a given ethnicity. We take the sum of this over ");
		Text t5 = new Text("all districts. We want to minimize how much this varies between ethnicities, ");
		Text t6 = new Text("so we take the average of this over the entire population, and calculate the mean absolute deviation ");
		Text t7= new Text("(M.A.D.) of the ethnicities from this. This gives us a summary of how uneven voting power is distributed ");
		Text t8= new Text("among the ethnicities. We want this score to be");
		Text t9 = new Text("minimized");
		t9.setStyle("-fx-font-weight: bold");
		Text t10 = new Text("."); 
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
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
		LOGGER.info("VotingPowerGate.evaluating: ...");

		for(PlanModel plan:plans) {
			List<NameValue> powers = new ArrayList<>();
			double voteMargin = 0.;
			double totalVotes = 0.;
			double weightedBlack  = 0.;
			double weightedHispanic  = 0.;
			double weightedWhite  = 0.;
			for(PlanFeature feat:plan.getMetrics()) {
				double population = feat.getPopulation();
				double votes = feat.getDemocrat()+feat.getRepublican();
				double margin = Math.abs(feat.getDemocrat()-feat.getRepublican());
				double black = feat.getBlack()/population;
				double hispanic = feat.getHispanic()/population;
				double white = feat.getWhite()/population;
				totalVotes += votes;
				voteMargin += margin;
				weightedBlack += black*margin/votes;
				weightedHispanic += hispanic*margin/votes;
				weightedWhite += white*margin/votes;
				
				NameValue nv = new NameValue(feat.getName());
				nv.setValue(KEY_BLACK,black*margin/votes);
				nv.setValue(KEY_HISPANIC,hispanic*margin/votes);
				nv.setValue(KEY_WHITE,white*margin/votes);
				powers.add(nv);
			}
			double mean = voteMargin/totalVotes;
			double mad = (Math.abs(mean-weightedBlack) + Math.abs(mean-weightedHispanic) + Math.abs(mean-weightedWhite))/3.;

			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_PLAN, plan.getName());
			nv.setValue(KEY_BLACK, weightedBlack);
			nv.setValue(KEY_HISPANIC, weightedHispanic);
			nv.setValue(KEY_WHITE, weightedWhite);
			nv.setValue(KEY_MAD, mad);
			scoreMap.put(plan.getId(),nv);
			districtScores.put(plan.getId(), powers);
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
		factory.setFormat(KEY_MAD, "%2.4f");
		column = new TableColumn<>(KEY_PLAN);
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
		column = new TableColumn<>(KEY_MAD);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.2));
		aggregateTable.getColumns().add(column);
		ObservableList<NameValue> aitems = FXCollections.observableArrayList();
		for(PlanModel plan:sortedPlans ) {
			// There is a single row containing the overall score
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
		fact.setFormat(KEY_MAD, "%2.4f");
		
		int colno = 0;
		int maxrows = 0;  // Max districts among plans
		double widthFactor = 1./(4*sortedPlans.size());
		for(PlanModel plan:sortedPlans ) {
			int ndistricts = districtScores.get(plan.getId()).size();
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
				List<NameValue> scores = districtScores.get(plan.getId());
				Collections.sort(scores,compareByName);
				if(scores.size()>row ) {
					values.add(scores.get(row));
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
