/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.Collections;
import java.util.List;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;

/**
 * Compare plans based on the compactness of the districts.
 */
public class CompactnessGate extends Gate {

	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text("To measure compactness, we calculate the ");
		Text t2 = new Text("Isoperimetric Quotient");
		t2.setStyle("-fx-font-style: italic");
		Text t3 = new Text(". This is obtained by dividing the area by the square of the length of the perimeter. ");
		Text t4 = new Text("In order to obtain a grand total, we add together the reciprocals of this for each district,");
		Text t5 = new Text("and then take the reciprocal of that. This gives us a weighted average. We want this score to be ");
		Text t6 = new Text("maximized");
		t6.setStyle("-fx-font-weight: bold");
		Text t7 = new Text(".");
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7);
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
	 * is first.
	 */
	@Override
	public void evaluate(List<PlanModel> plans) {
		LOGGER.info("CompactnessGate.evaluating: ...");
		for(PlanModel plan:plans) {
			double sum = 0.0;
			for(PlanFeature feat:plan.getMetrics()) {
				double iq = feat.getArea()/(feat.getPerimeter()*feat.getPerimeter());
				sum += 1/iq;
			}
			scoreMap.put(plan.getId(), 1/sum);
		}
		Collections.sort(plans,compareByScore);  // use .reversed() when minimized is good
		sortedPlans.clear();
		sortedPlans.addAll(plans);
		updateChart();
	}

}
