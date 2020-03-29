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
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.NameValue;
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on the compactness of the districts.
 */
public class CompactnessGate extends Gate {
	// For the results popup
	private final static double DIALOG_HEIGHT = 500.; 
	private final static double DIALOG_WIDTH = 600.;
	private final double HEX_QUOTIENT = 0.07216878;   // 3*SQRT(3)/72
	private final Label detailLabel = new Label("Normalized Isoperimetric Quotients by District:");
	private final Map<Long,List<NameValue>> districtScores; 
	
	public CompactnessGate() {
		this.districtScores = new HashMap<>();
		xAxis.setUpperBound(0.5);
		xAxis.setAutoRanging(false);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure compactness, we calculate the ");
		Text t2 = new Text("Isoperimetric Quotient");
		t2.setStyle("-fx-font-style: italic");
		Text t3 = new Text(". This is obtained by dividing the area by the square of the length of the perimeter. ");
		Text t4 = new Text(". We then normalize by dviding by the quotient for a hexagon, leaving the theoretical maximum at 1.0. ");
		Text t5 = new Text("In order to obtain a grand total, we add together the reciprocals of this for each district,");
		Text t6 = new Text("and then take the reciprocal of that. This gives us a weighted average. We want this score to be ");
		Text t7 = new Text("maximized");
		t6.setStyle("-fx-font-weight: bold");
		Text t8 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8);
		return info;
	}

	public String getTitle() { return "Compactness"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.COMPACTNESS_WEIGHT_KEY);}
	public GateType getType() { return GateType.COMPACTNESS; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.COMPACTNESS_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return true; }
 	/**
	 * Compute composite the isoperimetric quotient for each plan. 
	 * The list of plans will be sorted in place by score, best score
	 * is first. Save the details for each feature for viewing in the popup.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("CompactnessGate.evaluating: ...");
		for(PlanModel plan:plans) {
			List<NameValue> quotients = new ArrayList<>();
			double sum = 0.0;
			for(PlanFeature feat:plan.getMetrics()) {
				double iq = feat.getArea()/(feat.getPerimeter()*feat.getPerimeter());
				iq = iq/HEX_QUOTIENT;
				quotients.add(new NameValue(feat.getName(),iq));
				sum += 1/iq;
			}
			sum = sum/plan.getMetrics().size();
			scoreMap.put(plan.getId(), 1/sum);
			districtScores.put(plan.getId(), quotients);
		}
		Collections.sort(plans,compareByScore);  // use .reversed() when minimized is good
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
		NameValueCellValueFactory factory = new NameValueCellValueFactory();
		column = new TableColumn<>("Plan");
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		column = new TableColumn<>("Score");
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(0.5));
		aggregateTable.getColumns().add(column);
		ObservableList<NameValue> aitems = FXCollections.observableArrayList();
		for(PlanModel plan:sortedPlans ) {
			// There is a single row containing the overall score
			aitems.add(new NameValue(plan.getName(),scoreMap.get(plan.getId())));
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
		
		int colno = 0;
		int maxrows = 0;  // Max districts among plans
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
			subcol.prefWidthProperty().bind(column.widthProperty().multiply(0.5));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>("IsoQuotient");
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(column.widthProperty().multiply(0.5));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			colno++;
		}
		
		ObservableList<List<NameValue>> ditems = FXCollections.observableArrayList();
		for( int row=0;row<maxrows;row++ ) {
			List<NameValue> values = new ArrayList<>();
			for(PlanModel plan:sortedPlans ) {
				List<NameValue> scores = districtScores.get(plan.getId());
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
