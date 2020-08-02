/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.ColorizingOption;

/**
 * This pane shows the limits and a color bar for the currently selected
 * map colorizing scheme and plan. The colorbar is fixed for demographics
 * and/or affiliation - only the end labels change. The legend is hidden for NONE. 
 */
public class ColorizingLegend extends BorderPane   {
	private static final String CLSS = "ColorizingLegend";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final GridPane gridPane;
	private ColorizingOption option;
	private final Label optionLabel;
	private final Rectangle rectangle;
	private PlanModel plan = null;
	private final OverlayColorGenerator colorGenerator;
	private final Label leftLabel;
	private final Label centerLabel;
	private final Label rightLabel;
    
    private final static double COL_WIDTH = 60.;  
	private final static double LABEL_WIDTH = 80.;
	private final static double RECT_HEIGHT = 8.;
	public final static double RECT_WIDTH = 180.;
    
	public ColorizingLegend() {
		this.gridPane = new GridPane();
		this.colorGenerator = new OverlayColorGenerator();
		this.centerLabel = new Label("Center");
		this.leftLabel = new Label("Left");
		this.rightLabel = new Label("Right");
		centerLabel.getStyleClass().add("very-small-label");
		leftLabel.getStyleClass().add("very-small-label");
		rightLabel.getStyleClass().add("very-small-label");
		
		gridPane.setHgap(0);
		gridPane.setVgap(2);
		gridPane.setPadding(new Insets(2, 0, 2, 0));  // top, left, bottom,right
        
		optionLabel = new Label("");
		optionLabel.getStyleClass().add("small-label");
		
		rectangle = new Rectangle(RECT_WIDTH,RECT_HEIGHT);

        gridPane.getColumnConstraints().clear();
        ColumnConstraints col0 = new ColumnConstraints(COL_WIDTH);
		col0.setHalignment(HPos.LEFT);
        ColumnConstraints col1 = new ColumnConstraints(COL_WIDTH);
		col1.setHalignment(HPos.CENTER);
        ColumnConstraints col2 = new ColumnConstraints(COL_WIDTH);
		col2.setHalignment(HPos.RIGHT);
		gridPane.getColumnConstraints().addAll(col0,col1,col2);
       
		gridPane.add(optionLabel, 1, 0); // column,row,column span,row span
		gridPane.add(rectangle,0,1,3,1);
		gridPane.add(leftLabel, 0, 2);
		gridPane.add(centerLabel, 1, 2);
		gridPane.add(rightLabel, 2, 2);
		setCenter(gridPane);
		
        setOption(ColorizingOption.NONE);
    }
	
	public void display() {
		LinearGradient lg = null;
		Stop[] stops = null;
		if(plan==null) return;
		double left = 0.;
		double right = 0.;
		double center = 0.;
		switch(this.option) {
			case NONE: 
				setVisible(false);
				break;
			case AFFILIATION: 
				optionLabel.setText("%Dem/%Rep");
				stops = new Stop[] { new Stop(0, Color.BLUE), new Stop(RECT_WIDTH, Color.RED)};
				lg = new LinearGradient(0, 0, RECT_WIDTH, RECT_HEIGHT, false, CycleMethod.NO_CYCLE, stops);
				rectangle.setFill(lg);
				left = plan.getMaxDemocrat();
				right = plan.getMaxRepublican();
				leftLabel.setText(String.format("%2.0f", 100.*left));
				centerLabel.setText("|");
				rightLabel.setText(String.format("%2.0f", 100.*right));
				setVisible(true);
				break;
			case DEMOGRAPHICS: 
				optionLabel.setText("%White");
				stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(RECT_WIDTH, Color.BLACK)};
				lg = new LinearGradient(0, 0, RECT_WIDTH, RECT_HEIGHT, false, CycleMethod.NO_CYCLE, stops);
				rectangle.setFill(lg);
				left = plan.getMaxWhite();
				right = plan.getMinWhite();
				center = (left + right)/2.;
				leftLabel.setText(String.format("%2.0f", 100.*left));
				centerLabel.setText(String.format("%2.0f", 100.*center));
				rightLabel.setText(String.format("%2.0f", 100.*right));
				setVisible(true);
				break;
		}
	}
	public void updateModel(PlanModel model) {
		this.plan = model;
		display();
	}
	public void setOption(ColorizingOption opt) {
		LOGGER.info(String.format("%s.setOption: new colorizer option %s",CLSS,opt.name()));
		option = opt;
		display();
	}
}
