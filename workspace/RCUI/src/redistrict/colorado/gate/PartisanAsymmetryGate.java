/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.inference.TestUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.chart.DeclinationChart;
import redistrict.colorado.chart.VoteSeatChart;
import redistrict.colorado.core.Declination;
import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.NameValue;
import redistrict.colorado.core.PartisanMetric;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.VoteSeatCurve;
import redistrict.colorado.db.Database;
import redistrict.colorado.table.NameValueCellValueFactory;
import redistrict.colorado.table.NameValueLimitCellFactory;
import redistrict.colorado.table.NameValueListCellValueFactory;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.InfoDialog;
import redistrict.colorado.ui.UIConstants;

/**
 * Compare plans based on one of several measures of partisan bias
 */
public class PartisanAsymmetryGate extends Gate {
	private final static String KEY_DECLINATION = "Declination"; 
	private final static String KEY_GAP = "Efficiency Gap"; 
	private final static String KEY_MARGIN = "Margin"; 
	private final static String KEY_MM_DIFF = "Median-Mean ~%"; 
	private final static String KEY_PROBABILITY = "Probability"; 
	private final static String KEY_SCORE = "Score";  // Value appropriate to metric
	private final static String KEY_SEAT_BIAS = "Seat Bias"; 
	private final static String KEY_NAME = "Name";
	private final static String KEY_PLAN = "Plan";
	private final static String KEY_PARTY = "Advantaged Party";
	private final static String KEY_DEM_WASTED = "Wasted Dem";
	private final static String KEY_REP_WASTED = "Wasted Rep";
	private final static String KEY_WINNING_PARTY = "Winning Party";
	private Label aggregateLabel = new Label();
	private Label detailLabel = new Label();
	
	private final List<Declination> declinations = new ArrayList<>();
	private final List<VoteSeatCurve> voteSeatCurves = new ArrayList<>();
	
	public PartisanAsymmetryGate() {
		xAxis.setAutoRanging(true);
	}

	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		PartisanMetric metric = EventBindingHub.getInstance().getAnalysisModel().getPartisanMetric();
		switch(metric) {
			case DECLINATION:
				updateDeclinationInfo(info);
				break;
			case EFFICIENCY_GAP: 
				updateEfficiencyGapInfo(info);
				break;
			case LOPSIDED_WINS:
				updateLopsidedWinsInfo(info);
				break;
			case MEAN_MEDIAN:
				updateMeanMedianInfo(info);
				break;
			case PARTISAN_BIAS:
				updatePartisanBiasInfo(info);;
				break;
		}
		return info;
	}
	private void updateDeclinationInfo(TextFlow info) {
		Text t1 = new Text("The declination function treats asymmetry in the vote distribution as indicative of gerrymandering.");
		Text t2 = new Text("When plotted the function shows a geometric angle that can be easily visualized. ");
		Text t3 = new Text("In our usage a negative angle indicates an unfair Democratic advantage and a positive angle indicates a Republican advantage. ");
		Text t4 = new Text("An angle of more than 0.3 radians indicates probable manipulation. We want this score to be ");
		Text t5 = new Text("minimized");
		t5.setStyle("-fx-font-weight: bold");
		Text t6 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6);
	}
	private void updateEfficiencyGapInfo(TextFlow info) {
		Text t1 = new Text("Efficiency gap is the sum of the differences of wasted votes for the two parties divided by the total number of votes. A wasted vote is a ");
		Text t3 = new Text("vote that does not help elect a candidate (over 50% for the winning side, all votes for the losing side). We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t3,t4,t5);
	}
	private void updateLopsidedWinsInfo(TextFlow info) {
		Text t1 = new Text("Use the students-t test to analyze the distributions in vote-margin between districts won by the two parties.");
		Text t2 = new Text("A statistically significant difference may be an indication of gerrymandering.");
		Text t3 = new Text("The result is the probability that the distributions could have occurred by chance. We want this value to be ");
		Text t4 = new Text("maximized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(". This metric should not be used with less than 30 districts.");
		info.getChildren().addAll(t1,t2,t3,t4,t5);
	}
	private void updateMeanMedianInfo(TextFlow info) {
		Text t1 = new Text("Mean-median is a measure of vote bias. ");
		Text t2 = new Text("The mean-median difference is a party’s median vote share minus its mean vote share across all districts. ");
		Text t3 = new Text("The difference is expressed as a percentage of total voted.  We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5);
	}
	private void updatePartisanBiasInfo(TextFlow info) {
		Text t1 = new Text("Partisan bias is a measure of seat bias. ");
		Text t2 = new Text("It is the difference between 50% of the seats and the number of seats that would be won with 50% of the votes.");
		Text t3 = new Text("We want this score to be ");
		Text t4 = new Text("minimized");
		t4.setStyle("-fx-font-weight: bold");
		Text t5 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5);
	}
	@Override
	public void showDialog() {
		try {
			// Trying to do this twice throws the exception
			infoDialog = new InfoDialog(this);
			infoDialog.initOwner(getScene().getWindow());
		}
		catch(IllegalStateException ignore) {}
        infoDialog.showAndWait();
    }
	// This is the attribute of the name-value dictionary used to compare plans
	public String getScoreAttribute() { 
		String key = KEY_SCORE;
		PartisanMetric metric = EventBindingHub.getInstance().getAnalysisModel().getPartisanMetric();
		switch(metric) {
			case DECLINATION: 	 key = KEY_DECLINATION; break;
			case EFFICIENCY_GAP: key = KEY_GAP;   break;
			case LOPSIDED_WINS:  key = KEY_PROBABILITY;   break;
			case MEAN_MEDIAN:    key = KEY_MM_DIFF;  break;
			case PARTISAN_BIAS:  key = KEY_SEAT_BIAS;  break;
		}
		return key;
	} 
	public String getTitle() { return "Partisan Asymmetry"; } 
	public GateType getType() { return GateType.PARTISAN_ASYMMETRY; }

	@Override
	public void evaluate(List<PlanModel> plans) { 
		PartisanMetric metric = EventBindingHub.getInstance().getAnalysisModel().getPartisanMetric();
		LOGGER.info("PartisanAsymmetryGate.evaluating: ..."+metric.name());
		switch(metric) {
			case DECLINATION:
				evaluateDeclination(plans);
				break;
			case EFFICIENCY_GAP:
				evaluateEfficiencyGap(plans);
				break;
			case LOPSIDED_WINS:
				evaluateLopsidedWins(plans);
				break;
			case MEAN_MEDIAN:
				evaluateMeanMedian(plans);
				break;
			case PARTISAN_BIAS:
				evaluatePartisanBias(plans);
				break;
		}
	}
	/**
	 * Create sorted list of both democratic and republican votes.
	 * Then compute the declination.
	 */
	private void evaluateDeclination(List<PlanModel> plans) {
		declinations.clear();
		for(PlanModel plan:plans) {
			double[] demFractions = new double[plan.getMetrics().size()];
			int index = 0;
			for(PlanFeature feat:plan.getMetrics()) {
				demFractions[index] = feat.getDemocrat()/(feat.getRepublican()+feat.getDemocrat());
				index++;
			}
			Arrays.sort(demFractions);
			Declination decl = new Declination(demFractions);
			decl.setColor(plan.getFill());
			decl.generate();
			declinations.add(decl);
			
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_DECLINATION, Math.abs(decl.getDeclination()));
			nv.setValue(KEY_PLAN, plan.getName());
			String party = "Democrat";
			if( decl.getDeclination() > 0. ) party = "Republican";
			nv.setValue(KEY_PARTY,party); // Party with advantage
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByPlanScore);  // 
		Collections.reverse(plans);
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	private void evaluateEfficiencyGap(List<PlanModel> plans) {
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
			nv.setValue(KEY_PARTY, (wastedDem>wastedRep?"Republican":"Democrat")); // Party with least votes
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByPlanScore);  // 
		Collections.reverse(plans);
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	/**
	 * http://gerrymander.princeton.edu/info/
	 */
	/**
	 * Create sorted list of both democratic and republican vote margins.
	 * Then compute the student-t.
	 */
	private void evaluateLopsidedWins(List<PlanModel> plans) {
		for(PlanModel plan:plans) {	
			SummaryStatistics demStats = new SummaryStatistics();
			SummaryStatistics repStats = new SummaryStatistics();

			for(PlanFeature feat:plan.getMetrics()) {
				double margin = feat.getDemocrat()-feat.getRepublican();
				if( feat.getDemocrat()>feat.getRepublican()) {
					demStats.addValue(margin);
				}
				else {
					repStats.addValue(margin);
				}
			}
			// pVal is the probability that the differences appeared by chance
			double pVal = TestUtils.tTest(demStats, repStats);
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_PROBABILITY, pVal);
			nv.setValue(KEY_PLAN, plan.getName());
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByPlanScore);  // 
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	/**
	 * The score is the median-mean expressed as % of total votes
	 * Compute overall results.
	 */
	private void evaluateMeanMedian(List<PlanModel> plans) {
		voteSeatCurves.clear();
		for(PlanModel plan:plans) {
			VoteSeatCurve vsc = new VoteSeatCurve(plan.getMetrics());
			vsc.generate();
			voteSeatCurves.add(vsc);
			
			Median median = new Median();
			Mean mean = new Mean();
			double population = 0;
			double data[] = new double[plan.getMetrics().size()];
					
			int index = 0;
			for(PlanFeature feat:plan.getMetrics()) {
				data[index] = feat.getDemocrat();
				population = population + feat.getDemocrat() + feat.getRepublican();
				index++;
			}
			double meanValue = mean.evaluate(data);
			double medianValue = median.evaluate(data);
			double val = medianValue - meanValue;
			val = val/population;
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_MM_DIFF, Math.abs(100.*val));
			nv.setValue(KEY_PLAN, plan.getName());
			String party = "Democrat";
			if( val > 0.0 ) party = "Republican";
			nv.setValue(KEY_PARTY,party); // Party with advantage
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByPlanScore);  
		Collections.reverse(plans);
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	private void evaluatePartisanBias(List<PlanModel> plans) {
		voteSeatCurves.clear();
		for(PlanModel plan:plans) {
			VoteSeatCurve vsc = new VoteSeatCurve(plan.getMetrics());
			vsc.generate();
			voteSeatCurves.add(vsc);
			
			NameValue nv = new NameValue(plan.getName());
			nv.setValue(KEY_SEAT_BIAS, vsc.getSeatImbalance());
			nv.setValue(KEY_PLAN, plan.getName());
			String party = "Democrat";
			if( vsc.getSeatImbalance() > 0.5 ) party = "Republican";
			nv.setValue(KEY_PARTY,party); // Party with advantage
			scoreMap.put(plan.getId(),nv);
		}
		Collections.sort(plans,compareByPlanScore);  // 
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}
	
	
	@Override
	protected Node getResultsContents() {
		VBox pane =  new VBox(10);
		pane.setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		pane.setFillWidth(true);
		aggregateLabel.setId(ComponentIds.LABEL_SCORE);
		pane.getChildren().add(aggregateLabel);
		
		PartisanMetric metric = EventBindingHub.getInstance().getAnalysisModel().getPartisanMetric();
		LOGGER.info("PartisanAsymmetryGate.getResultsContents: ..."+metric.name());
		String colTitle = getScoreAttribute();
		
		// Aggregate table
		TableView<NameValue> aggregateTable = new TableView<>();
		double height = UIConstants.TABLE_ROW_HEIGHT*(1.5+sortedPlans.size());
		aggregateTable.setPrefSize(AGGREGATE_TABLE_WIDTH, height);
		aggregateTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		GateProperty gp = Database.getInstance().getGateTable().getGateProperty(getType());
		double threshold = gp.getUnfairValue();
		
		double ncols = 3.;
		if( metric.equals(PartisanMetric.LOPSIDED_WINS) ) ncols = 2.;

		TableColumn<NameValue,String> column;
		NameValueCellValueFactory factory = new NameValueCellValueFactory();
		NameValueLimitCellFactory limFactory = new NameValueLimitCellFactory( threshold);
		if( metric.equals(PartisanMetric.MEAN_MEDIAN) || metric.equals(PartisanMetric.PARTISAN_BIAS) )  {
			limFactory = new NameValueLimitCellFactory( (double)(50.-threshold), (double)(50.+threshold));
		}
		factory.setFormat(colTitle, "%2.1f");
		if( metric.equals(PartisanMetric.DECLINATION)) factory.setFormat(colTitle, "%1.2f");
		else if( metric.equals(PartisanMetric.LOPSIDED_WINS)) factory.setFormat(colTitle, "%1.2f");
		column = new TableColumn<>(KEY_PLAN);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(1./ncols));

		aggregateTable.getColumns().add(column);
		column = new TableColumn<>(colTitle);
		column.setCellFactory(limFactory);
		column.setCellValueFactory(factory);
		column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(1./ncols));
		aggregateTable.getColumns().add(column);
		if( !metric.equals(PartisanMetric.LOPSIDED_WINS)) {
			column = new TableColumn<>(KEY_PARTY);
			column.setCellValueFactory(factory);
			column.prefWidthProperty().bind(aggregateTable.widthProperty().multiply(1./ncols));
			aggregateTable.getColumns().add(column);
		}
		ObservableList<NameValue> aitems = FXCollections.observableArrayList();
		for(PlanModel plan:sortedPlans ) {
			aitems.add(scoreMap.get(plan.getId()));
		}
		aggregateTable.setItems(aitems);
		pane.getChildren().add(aggregateTable);

		switch(metric) {
			case DECLINATION:
				aggregateLabel.setText("Declination ~ radians / Advantaged Party");
				detailLabel.setText("Declination Comparison");
				getResultsForDeclination(pane);
				break;
			case EFFICIENCY_GAP: 
				aggregateLabel.setText("Efficiency Gap ~ % / Party with Least Wasted Votes");
				detailLabel.setText("Wasted Votes by Party");
				getResultsForEfficiencyGap(pane);
				break;
			case LOPSIDED_WINS:
				aggregateLabel.setText("Lopsided Wins ~ Probability that Vote-Margins are Random");
				detailLabel.setText("Vote-Margins");
				getResultsForLopsidedWins(pane);
				break;
			case MEAN_MEDIAN:
				aggregateLabel.setText("Mean-Median ~ Median Mean ~ votes, Me / Advantaged Party");
				detailLabel.setText("Vote-Seat Comparison");
				getResultsForMeanMedian(pane);
				break;
			case PARTISAN_BIAS:
				aggregateLabel.setText("Partisan Bias ~ number of extra seats for 50% of votes/ Advantaged Party");
				detailLabel.setText("Vote-Seat Comparison");
				getResultsForPartisanBias(pane);
				break;
		}
		return pane;
	}
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	private void getResultsForDeclination(VBox pane) {
		DeclinationChart dc = new DeclinationChart(declinations);
		pane.getChildren().add(dc.getChart());
	}
	// Create contents that allow viewing the details of the calculation
	private void getResultsForEfficiencyGap(VBox pane) { 
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
	}
	// Create contents that allow viewing the details of the calculation
	private void getResultsForLopsidedWins(VBox pane) { 
		detailLabel.setId(ComponentIds.LABEL_SCORE);
		pane.getChildren().add(detailLabel);
		
		// Detail table
		TableView<List<NameValue>> detailTable = new TableView<>();
		TableColumn<List<NameValue>,String> col;
		TableColumn<List<NameValue>,String> subcol;
		NameValueListCellValueFactory fact = new NameValueListCellValueFactory();
		fact.setFormat(KEY_MARGIN, "%5.0f");

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
			subcol = new TableColumn<>(KEY_MARGIN);
			subcol.setCellValueFactory(fact);
			subcol.prefWidthProperty().bind(detailTable.widthProperty().multiply(widthFactor));
			subcol.setUserData(colno);
			col.getColumns().add(subcol);
			subcol = new TableColumn<>(KEY_WINNING_PARTY);
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
					nv.setValue(KEY_MARGIN,Math.abs(feat.getDemocrat()-feat.getRepublican()));
					if( feat.getDemocrat()>feat.getRepublican() ) {
						nv.setValue(KEY_WINNING_PARTY,"Democrat");
					}
					else {
						nv.setValue(KEY_WINNING_PARTY,"Republican");
					}					scores.add(nv);
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
	}
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	private void getResultsForMeanMedian(VBox pane) {
		VoteSeatChart vc = new VoteSeatChart(voteSeatCurves);
		pane.getChildren().add(vc.getChart());
	}
	/**
	 * Sort the districts by name and save the % democrat score.
	 * Compute overall results.
	 */
	private void getResultsForPartisanBias(VBox pane) {
		VoteSeatChart vc = new VoteSeatChart(voteSeatCurves);
		pane.getChildren().add(vc.getChart());
	}
}
