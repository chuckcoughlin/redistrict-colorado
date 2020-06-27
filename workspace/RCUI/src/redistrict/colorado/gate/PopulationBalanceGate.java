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
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueLimitCellFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.table.NameValueListLimitCellFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on the populations of each district.  Values must be within 
 * 1% of each other. The explanation is from autoredistrict.org.
 * We call it "imbalance" to emphasize that we want to minimize.
 */
public class PopulationBalanceGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_SCORE = "Standard Deviation ~ %";
	private final static String SCORE_FORMAT = "%2.4f";
	private final double DEFAULT_THRESHOLD = 1.0;   // Max difference from mean
	private final Label aggregateLabel = new Label("Standard Deviation of Total District Populations");
	private final Label detailLabel = new Label("Population Difference from Mean ~ %");
	private final Map<Long,List<NameValue>> districtScores; 
	private final Map<Long,Boolean> planInError;
	
	public PopulationBalanceGate() {
		this.districtScores = new HashMap<>();
		this.planInError = new HashMap<>();
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Population imbalance is the standard deviation");
		Text t2 = new Text(" of the population of the individual districts, normalized by the total population and");
		Text t3 = new Text(" multiplied by 100, giving a result in percent. ");
		Text t4 = new Text(" We want this score to be ");
		Text t5 = new Text("minimized");
		t5.setStyle("-fx-font-weight: bold");
		Text t6 = new Text(". A red X indicator is shown if any individual district has over a 1.0% deviation.");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6);
		return info; 
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "Population Balance"; } 
	public GateType getType() { return GateType.POPULATION_BALANCE; }
	
	protected Label getBarOverlayLabel(PlanModel model) {
		boolean inError = planInError.get(model.getId());
		if( inError ) {
			Label redX = new Label("",guiu.loadImage("images/red_x.png"));
			redX.setId(ComponentIds.BUTTON_INFO);
			return redX;
		}
		else {
			return null;
		}

	}
 	/**
	 * Compute the standard deviation of the population across districts. 
	 * The individual scores are difference from the mean. There is a 
	 * maximum difference allowed of 1%.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("PopulationBalanceGate.evaluating: ...");
		GateProperty gp = Database.getInstance().getGateTable().getGateProperty(getType());
		double threshold = gp.getUnfairValue();
		StandardDeviation stdDeviation = new StandardDeviation();
		stdDeviation.setBiasCorrected(false);
		for(PlanModel plan:plans) {
			List<NameValue> populations = new ArrayList<>();
			double[] poparray = new double[plan.getMetrics().size()];
			double total = 0.0;
			planInError.put(plan.getId(), false);
			for(PlanFeature feat:plan.getMetrics()) {
				total += feat.getPopulation();
			}
			double mean = total/plan.getMetrics().size();
			LOGGER.info(String.format("PopulationBalanceGate.evaluating: %2.0f total, %2.0f mean",mean,total));
			int i = 0;
			for(PlanFeature feat:plan.getMetrics()) {
				double pop = feat.getPopulation();
				double val = 100.*(pop-mean)/mean;
				LOGGER.info(String.format("PopulationBalanceGate.evaluating: %2.0f pop, %2.2f val",pop,val));
				if( Math.abs(val) > threshold) {
					planInError.put(plan.getId(), true);
				}
				NameValue nv = new NameValue(feat.getName());
				nv.setValue(KEY_PLAN, plan.getName());
				nv.setValue(KEY_SCORE, val);
				populations.add(nv);
				poparray[i] = pop;
				i++;
			}
			// It's impossible for the std deviation to be out of range if 
			// none of the individual districts are out of range.
			double sd = 100.*stdDeviation.evaluate(poparray)/mean;
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_PLAN, plan.getName());
			nv.setValue(KEY_SCORE, sd);
			scoreMap.put(plan.getId(),nv);
			districtScores.put(plan.getId(), populations);
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
		NameValueLimitCellFactory limFactory = new NameValueLimitCellFactory( threshold);
		NameValueCellValueFactory factory = new NameValueCellValueFactory();
		factory.setFormat(KEY_SCORE, SCORE_FORMAT);
		column = new TableColumn<>(KEY_PLAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(KEY_SCORE);
		column.setCellFactory(limFactory);
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
		NameValueListLimitCellFactory limitFactory = new NameValueListLimitCellFactory(threshold);
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		fact.setFormat(KEY_SCORE, SCORE_FORMAT);
		
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
			subcol.setCellFactory(limitFactory);
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