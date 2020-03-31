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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
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
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on the populations of each district.  Values must be within 
 * 1% of each other. The explanation is from autoredistrict.org.
 */
public class PopulationEqualityGate extends Gate {
	private final static double DIALOG_HEIGHT = 500.; 
	private final static double DIALOG_WIDTH = 600.;
	private final double MAX_DIFFERENCE_FROM_MEAN = 1.0;   //
	private final Label detailLabel = new Label("Population Difference from Mean ~ %");
	private final Map<Long,List<NameValue>> districtScores; 
	private final Label redX;
	
	public PopulationEqualityGate() {
		this.districtScores = new HashMap<>();
		xAxis.setUpperBound(1.1);
		xAxis.setAutoRanging(false);
		redX = new Label("",guiu.loadImage("images/red_x.png"));
		redX.setId(ComponentIds.BUTTON_INFO);
		StackPane.setAlignment(redX, Pos.BOTTOM_LEFT);
		body.getChildren().add(redX);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure population balance, the program calculates the standard deviation");
		Text t2 = new Text(" of the populations of the districts normalized by the population and");
		Text t3 = new Text(" multiplied by 100 to give a result in percent. ");
		Text t4 = new Text(" We want this score to be ");
		Text t5 = new Text("minimized");
		t5.setStyle("-fx-font-weight: bold");
		Text t6 = new Text(". An alarm indicator is shown if any individual district has over a 1.0% deviation.");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6);
		return info; 
	}
	public String getTitle() { return "Population Equality"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.POPULATION_EQUALITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return false; }
	
 	/**
	 * Compute the standard deviation of the population across districts. 
	 * The individual scores are difference from the mean. There is a 
	 * maximum difference allowed of 1%.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("PopulationEqualityGate.evaluating: ...");
		redX.setVisible(false);
		StandardDeviation stdDeviation = new StandardDeviation();
		stdDeviation.setBiasCorrected(false);
		for(PlanModel plan:plans) {
			List<NameValue> populations = new ArrayList<>();
			double[] poparray = new double[plan.getMetrics().size()];
			double total = 0.0;
			for(PlanFeature feat:plan.getMetrics()) {
				total += feat.getPopulation();
			}
			double mean = total/plan.getMetrics().size();
			
			int i = 0;
			for(PlanFeature feat:plan.getMetrics()) {
				double pop = feat.getPopulation();
				double val = 100.*(pop-mean)/mean;
				if( Math.abs(val) > MAX_DIFFERENCE_FROM_MEAN) {
					//redX.setVisible(true);
				}
				populations.add(new NameValue(feat.getName(),val));
				poparray[i] = pop;
				i++;
			}
			
			double sd = 100.*stdDeviation.evaluate(poparray)/mean;
			if( Math.abs(sd) > MAX_DIFFERENCE_FROM_MEAN) redX.setVisible(true);
			scoreMap.put(plan.getId(),new NameValue(plan.getName(),sd));
			districtScores.put(plan.getId(), populations);
		}
		Collections.sort(plans,compareByScore);  // use .reversed() when minimized is good
		Collections.reverse(plans);   // Because minimum is best.
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

		// Aggregate table
		TableView<NameValue> aggregateTable = new TableView<>();
		double height = UIConstants.TABLE_ROW_HEIGHT*(1.5+sortedPlans.size());
		aggregateTable.setPrefSize(AGGREGATE_TABLE_WIDTH, height);
		aggregateTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		TableColumn<NameValue,String> column;
		NameValueLimitCellFactory limFactory = new NameValueLimitCellFactory( MAX_DIFFERENCE_FROM_MEAN);
		NameValueCellValueFactory factory = new NameValueCellValueFactory();
		column = new TableColumn<>("Plan");
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>("Score");
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
		NameValueListLimitCellFactory limitFactory = new NameValueListLimitCellFactory( MAX_DIFFERENCE_FROM_MEAN);
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		
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
			subcol = new TableColumn<>("Name");
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>("% from Mean");
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