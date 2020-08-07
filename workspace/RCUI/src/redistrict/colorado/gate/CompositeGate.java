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
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.plan.ColorLegend;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;

/**
 * Compare plans based on the harmonic mean of the normalized
 * and weighted composite of all other plans. 
 */
public class CompositeGate extends Gate {
	private final static double METRIC_COL_WIDTH = 300.;
	private final static double SCORE_COL_WIDTH = 80.;
	private final static String KEY_NAME = "Name";
	private final static String KEY_SCORE = "Score";
	private final static String KEY_FAIR = "Fair";
	private final static String KEY_SCALED = "Scaled";
	private final static String KEY_RAW = "Raw";
	private final static String KEY_UNFAIR = "Unfair";
	private final static String KEY_WEIGHT = "Weight";
	private final Label detailLabel = new Label("Individual Metric Results"); 
	private final Map<Long,List<NameValue>> planScores;
	private ColorLegend colorLegend;

	public CompositeGate() {
		this.planScores = new HashMap<>();
		this.setAlignment(Pos.CENTER_LEFT);
	}

	/**
	 * Initialize the main UI. This is the only class where we override
	 */
	protected void init() {
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(WIDTH+1);
		header.getStyleClass().add("graph-header");

		this.colorLegend = new ColorLegend();
		colorLegend.setAlignment(Pos.CENTER_LEFT);
		colorLegend.setOnMouseClicked(new ChartClickedHandler ());
		
		//legend.setPadding(new Insets(10, 120, 10, 10));  // top, right,bottom,left
		body.setAlignment(Pos.CENTER);
		body.setMaxWidth(USE_PREF_SIZE);
		rectangle = new Rectangle(WIDTH,HEIGHT);
		rectangle.getStyleClass().add("graph-rectangle");
		StackPane.setAlignment(header, Pos.TOP_CENTER);
		StackPane.setAlignment(colorLegend, Pos.CENTER);
		StackPane.setAlignment(rectangle, Pos.CENTER);
		StackPane.setAlignment(info, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(colorLegend, new Insets(0, 10, 5, 10));  // top right bottom left
		body.getChildren().addAll(rectangle,colorLegend,header,info);
		getChildren().addAll(body);	
		colorLegend.setOnMouseClicked(new ChartClickedHandler ());
	}

	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("The composite score is a number from 0-10. Each of the constituent scores is constrained to limits on the setup page.  ");
		Text t2 = new Text(" A score at the \"unfair\" limit is assigned a value of zero. A score at ");
		Text t3 = new Text("\"fair\" limit is assigned a 10. Scores in between are evaluated proportionally. ");
		Text t4 = new Text("The final metric is a weighted mean of the individual scores." );
		info.getChildren().addAll(t1,t2,t3,t4);
		return info;
	}
	public String getScoreAttribute() { return KEY_SCORE; };
	public String getTitle() { return "Composite Score"; }
	public GateType getType() { return GateType.COMPOSITE; }

	/**
	 * Compute the overall score, place into the score-map. Along the way we save
	 * results for the individual gates for display in the detail table.
	 * This must evaluate after all the other gates.
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
			List<NameValue> gateList = new ArrayList<>();
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
				nv.setValue(KEY_RAW, raw);
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
				nv.setValue(KEY_SCALED, fairness);
				LOGGER.info(String.format("CompositeGate: evaluating %s (%2.2f->%2.2f)",gate.getTitle(), raw,fairness));
				gateList.add(nv);
				scores[row] = fairness;
				weights[row]= weight;
				row++;
			}
			planScores.put(plan.getId(), gateList);
			double score = mean.evaluate(scores,weights);
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_SCORE, score);
			scoreMap.put(plan.getId(), nv);
		}

		Collections.sort(plans,compareByPlanName);  // use .reversed() when minimized is good
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

		// Detail table - first "plan" is the key
		TableView<List<NameValue>> detailTable = new TableView<>();
		TableColumn<List<NameValue>,String> col;
		TableColumn<List<NameValue>,String> subcol;
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		fact.setFormat(KEY_WEIGHT, "%2.1f");
		fact.setFormat(KEY_FAIR, "%2.1f");
		fact.setFormat(KEY_UNFAIR, "%2.1f");
		fact.setFormat(KEY_SCALED, "%2.1f");
		fact.setFormat(KEY_RAW, "%2.1f");
		fact.setFormat(KEY_SCORE, "%2.1f");

		int colno = 0;
		double widthFactor = 1./(sortedPlans.size());

		// Metric parameters title
		col = new TableColumn<>("Metrics");
		col.setPrefWidth(METRIC_COL_WIDTH);
		detailTable.getColumns().add(col);
		subcol = new TableColumn<>(KEY_NAME);
		subcol.setCellValueFactory(fact);
		subcol.setUserData(colno);
		col.getColumns().add(subcol);
		subcol = new TableColumn<>(KEY_WEIGHT);
		subcol.setCellValueFactory(fact);
		subcol.setUserData(colno);
		col.getColumns().add(subcol);
		subcol = new TableColumn<>(KEY_UNFAIR);
		subcol.setCellValueFactory(fact);
		subcol.setUserData(colno);
		col.getColumns().add(subcol);
		subcol = new TableColumn<>(KEY_FAIR);
		subcol.setCellValueFactory(fact);
		subcol.setUserData(colno);
		col.getColumns().add(subcol);
		colno++;
		
		// Now add plan raw/normalized scores
		for(PlanModel plan:sortedPlans ) {
			// These columns have no cells, just sub-columns.
			col = new TableColumn<>(plan.getName());
			col.setPrefWidth(SCORE_COL_WIDTH);
			detailTable.getColumns().add(col);
			subcol = new TableColumn<>(KEY_RAW);
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_SCALED);
			subcol.setCellValueFactory(fact);
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			colno++;
		}

		int gateRows = GateCache.getInstance().getBasicGates().size();
		ObservableList<List<NameValue>> ditems = FXCollections.observableArrayList();
		
		for( int row=0;row<gateRows;row++ ) {
			List<NameValue> values = new ArrayList<>();
			// Use the first plan as an example
			List<NameValue> gateList = planScores.get(sortedPlans.get(0).getId());
			Collections.sort(gateList,compareByName);
			if(gateList.size()>row ) {
				NameValue metric = gateList.get(row); 
				values.add(metric);  // Name, Weight,Fair,Unfair (all common)
			}
			
			for(PlanModel plan:sortedPlans ) {
				gateList = planScores.get(plan.getId());
				Collections.sort(gateList,compareByName);
				if(gateList.size()>row ) {
					values.add(gateList.get(row));
				}
				else {
					values.add(NameValue.EMPTY);
				}
			}
			ditems.add(values);
		}
		// Add an additional line that is the composite score
		// NOTE: NameValueListCellValueFactory complains about the nulls
		List<NameValue> values = new ArrayList<>();
		NameValue nv = new NameValue("Total");
		nv.setValue(KEY_FAIR, "");
		nv.setValue(KEY_UNFAIR, "");
		nv.setValue(KEY_WEIGHT, "");
		values.add(nv);
		for(PlanModel plan:sortedPlans ) {
			nv = scoreMap.get(plan.getId());
			// Move the score to the normalized column so the table finds it.
			nv.setValue(KEY_SCALED, nv.getValue(KEY_SCORE));
			values.add(nv);
		}
		ditems.add(values);

		detailTable.setItems(ditems);
		pane.getChildren().add(detailTable);
		return pane;
	}


	// Update the legend and result labels based on computations
	// List is already sorted.
	protected void updateChart() {
		colorLegend.display(sortedPlans);
		for(Long id:scoreMap.keySet() ) {
			NameValue nv = scoreMap.get(id);
			colorLegend.setValue(id,GuiUtil.toDouble(nv.getValue(KEY_SCORE)));
		}

		LOGGER.info("CompositeGate.updateChart: complete.");
	}
}
