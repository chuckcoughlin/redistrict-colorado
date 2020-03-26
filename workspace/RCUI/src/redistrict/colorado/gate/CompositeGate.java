/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateType;

/**
 * Compare plans based on the populations of each district are within 
 * 1% of each other.
 */
public class CompositeGate extends Gate {
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text t1 = new Text(" Each of these scores will have vastly different ranges. For instance, compactness varies from 0 to 1, ");
		Text t2 = new Text("while population imbalance could be in the tens of thousands. But we want each score to be \"weighed\" ");
		Text t3 = new Text("about the same, or, rather, in proportion to where the sliders are set. So we have to get them all on ");
		Text t4 = new Text("the same scale. We call this \"normalizing\" the scores.\n\n");
		Text t5 = new Text("To normalize a score, we first sort the population according to one criteria, then we replace each score ");
		Text t6 = new Text("with their \"rank\" in the sorted list, and divide by the size of the population. We use this as the new, ");
		Text t7 = new Text("normalized score for that criteria. Another way to say this is that we replace a raw score with it's ");
		Text t8 = new Text("\"percentile\". We do this one at a time for all criteria.\n\n");
		Text t9 = new Text("Then we multiply each score by where its corresponding slider is set, and then again by where the ");
		Text t10= new Text("geometry/fairness slider is set (starting from left or right, depending on whether it's a geometry score ");
		Text t11= new Text("or a fairness score). We then add these all together, and this gives us a final single-number score for a map." );
		info.getChildren().addAll(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
		return info;
	}
	public String getTitle() { return "Composite Score"; }
	public double getWeight() { return 0.; }
	public GateType getType() { return GateType.COMPOSITE; }
	public void setWeight(double weight) {}
	public boolean useMaximum() { return true; }
}
