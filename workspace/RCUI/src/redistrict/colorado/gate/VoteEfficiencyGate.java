/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import redistrict.colorado.db.PreferencesTable;
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
public class VoteEfficiencyGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_GAP = "Efficiency Gap";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_PARTY = "Party";
	private final static String KEY_DEM_WASTED = "Wasted Dem";
	private final static String KEY_REP_WASTED = "Wasted Rep";
	private final double DEFAULT_THRESHOLD = 8.0;   //
	private final Label aggregateLabel = new Label("Efficiency Gap ~ % / Party with Most Wasted Votes");
	private final Label detailLabel = new Label("Wasted Votes by Party");
	
	public VoteEfficiencyGate() {
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Efficiency gap is the sum of the differences of wasted votes for the two parties divided by the total number of votes. A wasted vote is a ");
		Text t3 = new Text("vote that does not help elect a candidate (over 50% for the winning side, all votes for the losing side). We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t3,t4,t5);
		return info;
	}
	public String getScoreAttribute() { return KEY_GAP; };
	public String getTitle() { return "Vote Efficiency"; } 
	public GateType getType() { return GateType.VOTING_EFFICIENCY; }
	
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("VoteEfficiencyGate.evaluating: ...");
		
		for(PlanModel plan:plans) {
			double totalVotes = 0.;
			double wastedDem = 0.;
			double wastedRep = 0.;
			for(PlanFeature feat:plan.getMetrics()) {

				double total = feat.getDemocrat()+feat.getRepublican();
				if( total<=0.) continue;
				if( feat.getDemocrat()>feat.getRepublican() ) {
					wastedDem += feat.getDemocrat() - total/2. - 1.;
					wastedRep += feat.getRepublican();
				}
				else {
					wastedRep += feat.getRepublican() - total/2. - 1.;
					wastedDem += feat.getDemocrat();
				}
				totalVotes += total;
				
			}
			double percent = 100.*Math.abs((wastedDem-wastedRep))/totalVotes;
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_GAP, percent);
			nv.setValue(KEY_PLAN, plan.getName());
			nv.setValue(KEY_PARTY, (wastedDem>wastedRep?"Democrat":"Republican")); // Party with most wasted votes
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByScore);  // 
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
		NameValueLimitCellFactory limFactory = new NameValueLimitCellFactory( threshold);
		factory.setFormat(KEY_GAP, "%2.1f");
		column = new TableColumn<>(KEY_PLAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_GAP);
		column.setCellFactory(limFactory);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_PARTY);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
		aggregateTable.getColumns().add(column);
		ObservableList<NameValue> aitems = FXCollections.observableArrayList();
		for(PlanModel plan:sortedPlans ) {
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
		fact.setFormat(KEY_DEM_WASTED, "%5.0f");
		fact.setFormat(KEY_REP_WASTED, "%5.0f");

		int colno = 0;
		int maxrows = 0;  // Max districts among plans
		double widthFactor = 1./(3*sortedPlans.size());
		for(PlanModel plan:sortedPlans ) {
			int ndistricts = plan.getMetrics().size();
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
			subcol = new TableColumn<>(KEY_DEM_WASTED);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_REP_WASTED);
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
				List<NameValue> scores = new ArrayList<>();
				for(PlanFeature feat:plan.getMetrics()) {
					NameValue nv = new NameValue(feat.getName());
					double total = feat.getDemocrat() + feat.getRepublican();
					if( feat.getDemocrat()>feat.getRepublican() ) {
						nv.setValue(KEY_DEM_WASTED,feat.getDemocrat() - total/2. - 1.);
						nv.setValue(KEY_REP_WASTED,feat.getRepublican());
					}
					else {
						nv.setValue(KEY_REP_WASTED,feat.getRepublican() - total/2. - 1.);
						nv.setValue(KEY_DEM_WASTED,feat.getDemocrat());
					}

					scores.add(nv);
				}
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
