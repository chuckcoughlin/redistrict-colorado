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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.ColorizingOption;

/**
 * This pane shows the limits and a color bar for the currently selected
 * map colorizing scheme and plan. The colorbar is fixed for demographics
 * and/or affiliation - only the end labels change. The legend is hidden for NONE. 
 */
public class ColorizingLegend extends GridPane   {
	private static final String CLSS = "ColorizingLegend";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private ColorizingOption option;
	private final Label optionLabel;
    
    private final static double COL_WIDTH = 50.;  //220
	private final static double LABEL_WIDTH = 50.;  //220
	private final static double RECT_HEIGHT = 10.;
	private final static double RECT_WIDTH = 20.;
    
	public ColorizingLegend() {
        setHgap(0);
        setVgap(2);
        setPadding(new Insets(2, 0, 2, 0));  // top, left, bottom,right
        
		optionLabel = new Label("");
		optionLabel.getStyleClass().add("small-label");

        getColumnConstraints().clear();
        ColumnConstraints col0 = new ColumnConstraints(COL_WIDTH);
		col0.setHalignment(HPos.CENTER);
        ColumnConstraints col1 = new ColumnConstraints(COL_WIDTH);
		col0.setHalignment(HPos.CENTER);
        ColumnConstraints col2 = new ColumnConstraints(COL_WIDTH);
		col2.setHalignment(HPos.CENTER);
		getColumnConstraints().addAll(col0,col1,col2);
        
		add(optionLabel, 0, 1); // row, column
        setOption(ColorizingOption.NONE.name());
    }
	
	public void display() {
		switch(this.option) {
			case NONE: 
				setVisible(false);
				break;
			case AFFILIATION: 
				optionLabel.setText("% Dem - %Rep");
				setVisible(true);
				break;
			case DEMOGRAPHICS: 
				optionLabel.setText("% White - % Non-white");
				setVisible(true);
				break;
		}
	}
	
	public void setOption(String text) {
		option = ColorizingOption.valueOf(text.toUpperCase());
		display();
	}
}
