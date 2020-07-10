/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.core.PlanModel;

/**
 * This pane shows the color scheme and scores for each plan. 
 * There is no action supported by these components.
 */
public class ColorLegend extends GridPane   {
	private static final String CLSS = "ColorLegend";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final static double LABEL_WIDTH = 90.;  //220
	private final static double COL1_WIDTH = 100.;   // 240
	private final static double COL2_WIDTH = 40.;    // 120
	private final static double COL3_WIDTH = 50.;    // 80
	private final static double RECT_HEIGHT = 10.;
	private final static double RECT_WIDTH = 20.;
	private final Map<Long,Label> labelMap;
    
	public ColorLegend() {
		labelMap = new HashMap<>();
        setHgap(0);
        setVgap(4);
        setPadding(new Insets(10, 0, 10, 0));  // top, left, bottom,right
        getColumnConstraints().add(new ColumnConstraints(COL1_WIDTH)); 							// name
        ColumnConstraints col2 = new ColumnConstraints(COL2_WIDTH,COL2_WIDTH,Double.MAX_VALUE); // color
        col2.setHgrow(Priority.ALWAYS);
        getColumnConstraints().add(col2);
        getColumnConstraints().add(new ColumnConstraints(COL3_WIDTH));
    }
	
	public void display(List<PlanModel> plans) {
		getChildren().clear();
		int row = 0;
		for(PlanModel model:plans) {
			Label nameLabel = new Label(model.getName());
			nameLabel.setPrefWidth(LABEL_WIDTH);
			nameLabel.setAlignment(Pos.CENTER_RIGHT);
			Rectangle rect = new Rectangle(RECT_WIDTH,RECT_HEIGHT);
			rect.setFill(model.getFill());
			add(nameLabel, 0, row);                    
		    add(rect, 1, row);
		    Label scoreLabel = new Label("");
		    scoreLabel.getStyleClass().add("legend-score-label");
		    labelMap.put(model.getId(), scoreLabel);
		    add(scoreLabel,2,row);
			row++;
		}
	}
	
	public void setValue(long planId,double val) {
		Label lab = labelMap.get(planId);
		lab.setText(String.format("%2.1f",val));
	}
}
