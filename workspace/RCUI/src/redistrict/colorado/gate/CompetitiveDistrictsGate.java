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
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.NameValue;
import redistrict.colorado.ui.TwoPartyValue;
import redistrict.colorado.ui.UIConstants;

/**
 * Maximized the number of districts that are balanced in party affiliations
 */
public class CompetitiveDistrictsGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final double DEFAULT_THRESHOLD = 15.0;   //
	private final Label aggregateLabel = new Label("Competitive Districts");
	private final Label detailLabel = new Label("District Results by Party ~ %");

	public CompetitiveDistrictsGate() {
		xAxis.setAutoRanging(true);
	}

	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("This metric is simply a count of districts where the probable ");
		Text t2 = new Text("difference between parties is less than a configurable threshold. We want this count to be ");
		Text t3 = new Text("maximized");
		t3.setStyle("-fx-font-weight: bold");
		Text t4 = new Text(". The threshold is specified on the setup page as \"Competitiveness Treshold\".");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getTitle() { return "Competitive Districts"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.COMPETITIVENESS_WEIGHT_KEY);}
	public GateType getType() { return GateType.COMPETIVENESS; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.COMPETITIVENESS_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return true; }


	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("CompetitiveDistrictsGate.evaluating: ...");

		double threshold = DEFAULT_THRESHOLD;
		try {
			String val = Database.getInstance().getPreferencesTable().getParameter(PreferencesTable.COMPETITIVENESS_THRESHOLD_KEY);
			if( !val.isEmpty()) threshold = Double.parseDouble(val);
		}
		catch(NumberFormatException nfe) {
			LOGGER.warning("CompetitiveDistrictsGate.evaluating: Error converting threshold to double. Using 15%. ("+nfe.getLocalizedMessage()+")");
		}
		for(PlanModel plan:plans) {
			double competitiveCount = 0.;
			for(PlanFeature feat:plan.getMetrics()) {
				double totalVoters = 0.;
				totalVoters += feat.getDemocrat();
				totalVoters += feat.getRepublican();
				double percent = 100.*Math.abs((feat.getDemocrat()-feat.getRepublican())/totalVoters);
				if( percent<threshold ) {
					competitiveCount++;
				}
			}
			scoreMap.put(plan.getId(),new NameValue(plan.getName(),competitiveCount));
		}
		Collections.sort(plans,compareByScore);  // 
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	// Create contents that allow viewing the details of the calculation
	@Override
	protected Node getResultsContents() { 
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
		column = new TableColumn<>("Plan");
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>("Count");
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
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
		TableView<List<TwoPartyValue>> detailTable = new TableView<>();
		TableColumn<List<TwoPartyValue>,String> col;
		TableColumn<List<TwoPartyValue>,String> subcol;
		TwoPartyListCellValueFactory fact = new TwoPartyListCellValueFactory();

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
			subcol = new TableColumn<>("Name");
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>("% Dem");
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>("% Rep");
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			colno++;
		}

		ObservableList<List<TwoPartyValue>> ditems = FXCollections.observableArrayList();
		for( int row=0;row<maxrows;row++ ) {
			List<TwoPartyValue> values = new ArrayList<>();
			for(PlanModel plan:sortedPlans ) {
				List<TwoPartyValue> scores = new ArrayList<>();
				for(PlanFeature feat:plan.getMetrics()) {
					double totalVoters = 0.;
					totalVoters += feat.getDemocrat();
					totalVoters += feat.getRepublican();
					values.add(new TwoPartyValue(feat.getName(),100.*feat.getDemocrat()/totalVoters,100.*feat.getRepublican()/totalVoters));
				}
				Collections.sort(scores,compare2ByName);
				if(scores.size()>row ) {
					values.add(scores.get(row));
				}
				else {
					values.add(TwoPartyValue.EMPTY);
				}
			}
			ditems.add(values);
		}

		detailTable.setItems(ditems);
		pane.getChildren().add(detailTable);

		return pane;
	}
}
