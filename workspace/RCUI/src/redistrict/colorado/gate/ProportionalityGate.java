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
import redistrict.colorado.ui.TwoPartyValue;
import redistrict.colorado.ui.UIConstants;

/**
 * Verify that the number of probable district outcomes aligns with the 
 * population as a whole.
 */
public class ProportionalityGate extends Gate {
	private final static double DIALOG_HEIGHT = 500.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static int BIASED_DEMOCRAT = -1;
	private final static int BIASED_REPUBLICAN = 1;
	private final Label aggregateLabel = new Label("Democrat/Republican Results: Seats/Overall/Ratio");
	private final Label detailLabel = new Label("Democrat/Republican Results ~ %"); 
	private final Map<Long,Integer> planBiased;
	
	public ProportionalityGate() {
		this.planBiased = new HashMap<>();
		xAxis.setAutoRanging(true);
	}
	
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("Show the difference in seats actually won versus the number of seats that would have been won in an \"at large\" election. ");
		Text t2 = new Text( "We want the difference to be  ");
		Text t3 = new Text("minimized");
		t3.setStyle("-fx-font-weight: bold"); 
		Text t4 = new Text(".  A party indicator is shown if the dominant party has more than the proportional fraction (less rounding error).");
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getTitle() { return "Proportionality"; } 
	public double getWeight() { return Database.getInstance().getPreferencesTable().getWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY);}
	public GateType getType() { return GateType.POPULATION_EQUALITY; }
	public void setWeight(double weight) {Database.getInstance().getPreferencesTable().setWeight(PreferencesTable.POPULATION_EQUALITY_WEIGHT_KEY,weight);}
	public boolean useMaximum() { return false; }
	
	protected Label getBarOverlayLabel(PlanModel model) {
		int biased = planBiased.get(model.getId());
		if( biased == BIASED_DEMOCRAT) {
			Label donkey = new Label("",guiu.loadImage("images/democrat.png"));
			donkey.setId(ComponentIds.BUTTON_INFO);
			return donkey;
		}
		else if( biased == BIASED_REPUBLICAN) {
			Label elephant = new Label("",guiu.loadImage("images/republican.png"));
			elephant.setId(ComponentIds.BUTTON_INFO);
			return elephant;
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
		LOGGER.info("ProportionalityGate.evaluating: ...");
		for(PlanModel plan:plans) {
			double voters = 0;
			double demSeats = 0;
			double nSeats = 0;
			double demVotes = 0;
			planBiased.put(plan.getId(), 0);
			for(PlanFeature feat:plan.getMetrics()) {
				double dem = feat.getDemocrat();
				double rep = feat.getRepublican();
				
				nSeats = nSeats+1;
				voters = voters + dem + rep;
				demVotes = demVotes + dem;
				if( dem>rep ) demSeats = demSeats + 1;
			}
			// Score map is the fractional seat differential of seats deserved - actual seats. (use the absolute value)
			double deserved = nSeats*demVotes/voters; 
			if( deserved - demSeats > 1 ) planBiased.put(plan.getId(), BIASED_REPUBLICAN);
			else if( deserved - demSeats < -1 ) planBiased.put(plan.getId(), BIASED_DEMOCRAT);
			scoreMap.put(plan.getId(),new NameValue(plan.getName(),deserved - demSeats));
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
		column = new TableColumn<>("Score");
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
		double widthFactor = 1./(2*sortedPlans.size());
		for(PlanModel plan:sortedPlans ) {
		
			// These columns have no cells, just sub-columns.
			col = new TableColumn<>(plan.getName());
			col.setPrefWidth(DIALOG_WIDTH);
			detailTable.getColumns().add(col);
			subcol = new TableColumn<>("Name");
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>("Party");
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
					values.add(new TwoPartyValue(feat.getName(),feat.getDemocrat(),feat.getRepublican()));
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
