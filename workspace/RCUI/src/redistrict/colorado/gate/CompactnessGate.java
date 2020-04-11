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
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.HarmonicMean;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;
import redistrict.colorado.table.NameValue;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on the compactness of the districts.
 */
public class CompactnessGate extends Gate {
	// For the results popup
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_DEVIATION = "Std Deviation";
	private final static String KEY_SCORE = "Score";
	private final static String KEY_MEAN = "Mean";
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final Label aggregateLabel = new Label("Harmonic Mean of District Scores");
	private final Label detailLabel = new Label("Polsby-Popper Score (normalized Isoperimetric Quotient)");
	private final Map<Long,List<NameValue>> districtScores; 
	
	public CompactnessGate() {
		this.districtScores = new HashMap<>();
		xAxis.setUpperBound(0.5);
		xAxis.setAutoRanging(false);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure compactness, we calculate the ");
		Text t2 = new Text("Polsby-Popper Test");
		t2.setStyle("-fx-font-style: italic");
		Text t3 = new Text(" metric. This value is obtained by dividing the area of each district by the square of its perimeter, ");
		Text t4 = new Text("and normalizing by dviding by 4pi (the value if it were a circle). The metric has a maximum value of 1.0. ");
		Text t5 = new Text("In order to obtain a grand total, we average the reciprocals of this for each district,");
		Text t6 = new Text("and then take the reciprocal of that. This gives us the harmonic mean. We want this score to be ");
		Text t7 = new Text("maximized");
		t7.setStyle("-fx-font-weight: bold");
		Text t8 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8);
		return info;
	}
	public String getScoreAttribute() { return KEY_MEAN; };
	public String getTitle() { return "Compactness"; } 
	public GateType getType() { return GateType.COMPACTNESS; }
 	/**
	 * Compute the normalized isoperimetric quotient for each plan. 
	 * The list of plans will be sorted in place by score, best score
	 * is first. Save the details for each feature for viewing in the popup.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("CompactnessGate.evaluating: ...");
		StandardDeviation sd = new StandardDeviation();
		for(PlanModel plan:plans) {
			List<NameValue> quotients = new ArrayList<>();
			double vals[] = new double[plan.getMetrics().size()];
			int index = 0;
			for(PlanFeature feat:plan.getMetrics()) {
				double iq = feat.getArea()/(feat.getPerimeter()*feat.getPerimeter());
				iq = iq*(4.*Math.PI);
				NameValue nv = new NameValue(feat.getName());
				nv.setValue(KEY_SCORE,iq);
				quotients.add(nv);
				vals[index] = iq;
				index++;
			}
			NameValue score = new NameValue(plan.getName());
			score.setValue(KEY_PLAN,plan.getName());
			score.setValue(KEY_MEAN,HarmonicMean.evaluate(vals));
			score.setValue(KEY_DEVIATION,sd.evaluate(vals));
			scoreMap.put(plan.getId(),score);
			districtScores.put(plan.getId(),quotients);
		}
		Collections.sort(plans,compareByScore);
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
		factory.setFormat(KEY_DEVIATION, "%2.6f");
		factory.setFormat(KEY_MEAN, "%2.6f");
		factory.setFormat(KEY_SCORE, "%2.6f");
		column = new TableColumn<>(KEY_PLAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_MEAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_DEVIATION);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.33));
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
		fact.setFormat(KEY_SCORE, "%2.4f");
		
		int colno = 0;
		int maxrows = 0;  // Max districts among plans
		double widthFactor = 1./(2*sortedPlans.size());
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
			subcol = new TableColumn<>(KEY_SCORE);
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
