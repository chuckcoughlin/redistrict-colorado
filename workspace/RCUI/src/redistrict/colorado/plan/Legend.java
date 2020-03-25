/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.PlanModel;

/**
 * This pane shows the color scheme for each plan. There is no action supported
 * by these components.
 */
public class Legend extends GridPane   {
	private static final String CLSS = "Legend";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double COL1_WIDTH = 240.;
	private final static double COL2_WIDTH = 120.;
	private final static double RECT_HEIGHT = 10.;
	private final static double RECT_WIDTH = 80.;
    
	public Legend() {
        setHgap(0);
        setVgap(4);
        setPadding(new Insets(10, 0, 10, 0));  // top, left, bottom,right
        getColumnConstraints().add(new ColumnConstraints(COL1_WIDTH)); 							// name
        ColumnConstraints col2 = new ColumnConstraints(COL2_WIDTH,COL2_WIDTH,Double.MAX_VALUE); // color
        col2.setHgrow(Priority.ALWAYS);
        getColumnConstraints().add(col2);
        
		List<PlanModel> plans = EventBindingHub.getInstance().getActivePlans();
		int row = 0;
		for(PlanModel model:plans) {
			Label label = new Label(model.getName());
			label.setPrefWidth(220.);
			label.setAlignment(Pos.CENTER_RIGHT);
			Rectangle rect = new Rectangle(RECT_WIDTH,RECT_HEIGHT);
			rect.setFill(model.getFill());
			add(label, 0, row);                    
		    add(rect, 1, row);
			row++;
		}
    } 
}
