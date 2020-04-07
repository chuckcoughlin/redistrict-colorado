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
import redistrict.colorado.table.NameValue;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.table.NameValueListLimitCellFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.UIConstants;

/**
 * Maximized the number of districts that are balanced in party affiliations
 */
public class CompetitiveDistrictsGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_COUNT = "Count";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_DEM_PCNT = "% Dem";
	private final static String KEY_REP_PCNT = "% Rep";
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
	public String getScoreAttribute() { return KEY_COUNT; };
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

		double threshold = getThreshold(PreferencesTable.COMPETITIVENESS_THRESHOLD_KEY,DEFAULT_THRESHOLD);

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
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_COUNT, competitiveCount);
			nv.setValue(KEY_PLAN, plan.getName());
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
		double threshold = getThreshold(PreferencesTable.COMPETITIVENESS_THRESHOLD_KEY,DEFAULT_THRESHOLD);
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
		factory.setFormat(KEY_COUNT, "%2.0f");
		column = new TableColumn<>(KEY_PLAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_COUNT);
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
		TableView<List<NameValue>> detailTable = new TableView<>();
		TableColumn<List<NameValue>,String> col;
		TableColumn<List<NameValue>,String> subcol;
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		NameValueListLimitCellFactory limFactory = new NameValueListLimitCellFactory( 50.-threshold/2.,50.+threshold/2.0);
		fact.setFormat(KEY_DEM_PCNT, "%2.2f");
		fact.setFormat(KEY_REP_PCNT, "%2.2f");

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
			subcol = new TableColumn<>(KEY_DEM_PCNT);
			subcol.setCellFactory(limFactory);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_REP_PCNT);
			subcol.setCellFactory(limFactory);
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
					double totalVoters = 0.;
					totalVoters += feat.getDemocrat();
					totalVoters += feat.getRepublican();
					NameValue nv = new NameValue(feat.getName());
					nv.setValue(KEY_DEM_PCNT, 100.*feat.getDemocrat()/totalVoters);
					nv.setValue(KEY_REP_PCNT, 100.*feat.getRepublican()/totalVoters);
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
