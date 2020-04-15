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

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.plan.Legend;
import redistrict.colorado.table.NameValue;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;

/**
 * Compare plans based on the harmonic mean of the normalized
 * and weighted composite of all other plans. 
 */
public class CompositeGate extends Gate {
	private final static double DIALOG_HEIGHT = 550.; 
	private final static double DIALOG_WIDTH = 600.;
	private final static String KEY_NAME = "Name";
	private final static String KEY_SCORE = "Score";
	private final static String KEY_FAIR = "Fair";
	private final static String KEY_UNFAIR = "Unfair";
	private final static String KEY_WEIGHT = "Weight";
	private final Label detailLabel = new Label("Individual Metric Results"); 
	private Legend legend;
	private final Map<Long,List<NameValue>> planScores; // List is ordered by gate type

	public CompositeGate() {
		this.planScores = new HashMap<>();
		this.setAlignment(Pos.CENTER_LEFT);
	}

	/**
	 * Initialize the main UI. This is the only class where we override
	 */
	protected void init() {
		double width = WIDTH + 70;  // Empirical
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(width+1);
		header.getStyleClass().add("graph-header");

		this.legend = new Legend();
		legend.setAlignment(Pos.CENTER_LEFT);
		legend.setPadding(new Insets(10, 120, 10, 10));  // top, right,bottom,left
		body.setAlignment(Pos.CENTER);
		body.setPrefWidth(width-40);
		body.setMaxWidth(USE_PREF_SIZE);
		rectangle = new Rectangle(width,HEIGHT);
		rectangle.getStyleClass().add("graph-rectangle");
		StackPane.setAlignment(header, Pos.TOP_CENTER);
		StackPane.setAlignment(legend, Pos.CENTER_LEFT);
		StackPane.setAlignment(rectangle, Pos.CENTER);
		StackPane.setAlignment(info, Pos.BOTTOM_CENTER);
		body.getChildren().addAll(rectangle,legend,header,info);
		info.setPadding(new Insets(0,0,8,250));  // top,right,bottom,left
		getChildren().addAll(body);	
	}

	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("The composite score is a number from 0-10. Each of the constituent scores is constrained to limits on the setup page.  ");
		Text t2 = new Text(" A score at the \"unfair\" limit is assigned a value of zero. A score at ");
		Text t3 = new Text("\"fair\" limit is assigned a 10. Scores in between are evaluated proportionally. ");
		Text t4 = new Text("The final metric is a weighed mean of the individual scores." );
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "Composite Score"; }
	public GateType getType() { return GateType.COMPOSITE; }

	/**
	 * Compute the overall score, place into the score-map. Along the way we save
	 * results for the individual gates for display in the detail table.
	 * This must evaluate after al the other gates.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("CompositeGate.evaluating: ...");
		Mean mean = new Mean();
		// First normalize the weightings - the weightings must total to 1.
		List<GateProperty> properties = Database.getInstance().getGateTable().getGateProperties();
		for(PlanModel plan:plans) {
			double [] scores = new double[properties.size()];
			double [] weights = new double[properties.size()];
			// Now tally the individual normalized score.
			int row = 0;
			for(Gate gate:GateCache.getInstance().getBasicGates()) {
				GateProperty prop = Database.getInstance().getGateTable().getGateProperty(gate.getType());
				NameValue nv = new NameValue(gate.getTitle());
				double weight = prop.getWeight();
				double unfair = prop.getUnfairValue();
				double fair   = prop.getFairValue();
				nv.setValue(KEY_FAIR, fair);
				nv.setValue(KEY_UNFAIR, unfair);
				nv.setValue(KEY_WEIGHT, weight);
				double raw = gate.getScore(plan.getId());
				double fairness = 0.;
				nv.setValue(KEY_SCORE, raw);
				if( fair>unfair) {  // (large is good)
					if(raw<unfair) fairness = 0.;
					else if(raw>unfair) fairness = 10;
					else {
						fairness = 10.*(raw - unfair)/(fair-unfair);
					}
				}
				else {    // fair<unfair  (small is good)
					if(raw<fair) fairness = 10.;
					else if(raw>unfair) fairness = 0;
					else {
						fairness = 10.*(unfair - raw)/(unfair-fair);
					}
				}
				LOGGER.info(String.format("CompositeGate: evaluating %s (%2.2f->%2.2f)",gate.getTitle(), raw,fairness));
				scores[row] = fairness;
				weights[row]= weight;
				row++;
			}
			double score = mean.evaluate(scores,weights);
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_SCORE, score);
			scoreMap.put(plan.getId(), nv);
		}

		Collections.sort(plans,compareByScore);  // use .reversed() when minimized is good
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	// Create contents that allow viewing the details of the calculation.
	// Display a single table with weight, lower limit, upper limit, score for each plan.
	@Override
	protected Node getResultsContents() { 
		VBox pane =  new VBox(10);
		pane.setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		pane.setFillWidth(true);

		detailLabel.setId(ComponentIds.LABEL_SCORE);
		pane.getChildren().add(detailLabel);

		// Detail table
		TableView<List<NameValue>> detailTable = new TableView<>();
		TableColumn<List<NameValue>,String> col;
		TableColumn<List<NameValue>,String> subcol;
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		fact.setFormat(KEY_WEIGHT, "%2.1f");
		fact.setFormat(KEY_FAIR, "%2.1f");
		fact.setFormat(KEY_UNFAIR, "%2.1f");
		fact.setFormat(KEY_SCORE, "%2.1f");

		int colno = 0;
		double widthFactor = 1./(5*sortedPlans.size());

		for(PlanModel plan:sortedPlans ) {
			// These columns have no cells, just sub-columns.
			col = new TableColumn<>(plan.getName());
			col.setPrefWidth(DIALOG_WIDTH);
			detailTable.getColumns().add(col);
			subcol = new TableColumn<>(KEY_NAME);
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_WEIGHT);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_UNFAIR);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_FAIR);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_SCORE);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			colno++;
		}

		int maxrows = Database.getInstance().getGateTable().getGateProperties().size();
		ObservableList<List<NameValue>> ditems = FXCollections.observableArrayList();
		for( int row=0;row<maxrows;row++ ) {
			List<NameValue> values = new ArrayList<>();
			for(PlanModel plan:sortedPlans ) {
				List<NameValue> scores = planScores.get(plan.getId());
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


	// Update the legend and result labels based on computations
	// List is already sorted.
	protected void updateChart() {
		legend.display(sortedPlans);
		for(Long id:scoreMap.keySet() ) {
			NameValue nv = scoreMap.get(id);
			legend.setValue(id,GuiUtil.toDouble(nv.getValue(KEY_SCORE)));
		}

		LOGGER.info("CompositeGate.updateChart: complete.");
	}
}
