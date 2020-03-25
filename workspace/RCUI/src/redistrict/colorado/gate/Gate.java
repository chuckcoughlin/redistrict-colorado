/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.core.GateType;
import redistrict.colorado.ui.GuiUtil;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private static final Color BASIC_FILL = Color.LIGHTGRAY;
	private static final GuiUtil guiu = new GuiUtil();
	private static final double HEIGHT = 200.;
	private static final double CHART_HEIGHT = 160.;
	private static final double WIDTH = 150.;
	private static final double CHART_WIDTH = 120.;
	private final Label header;
	private final Label info;
	private final StackPane body;
	private final NumberAxis xAxis;
    private final CategoryAxis yAxis;
    private final BarChart<Number,String> bc;
	private final Rectangle rectangle;
	
	public Gate() {
		super(0.);     // No spacing
		this.header = new Label(getTitle());
		this.xAxis = new NumberAxis();
        this.yAxis = new CategoryAxis();
        this.bc = new BarChart<Number,String>(xAxis,yAxis);
        bc.setPrefWidth(CHART_WIDTH);
        bc.setPrefHeight(CHART_HEIGHT);
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(WIDTH);
		header.getStyleClass().add("graph-header");
		info = new Label("",guiu.loadImage("images/information.png"));
		// KLUDGE: For an unknown reason header didn't align with body
		//VBox.setMargin(header,new Insets(0,-15,0,0));  // top right bottom left
		this.body = new StackPane();
		body.setAlignment(Pos.CENTER);
		this.rectangle = new Rectangle(WIDTH,HEIGHT-40.);
		rectangle.setFill(BASIC_FILL);
		rectangle.getStyleClass().add("graph-box");
		StackPane.setAlignment(rectangle, Pos.TOP_CENTER);
		StackPane.setAlignment(info, Pos.BOTTOM_RIGHT);
		body.getChildren().addAll(rectangle,bc,info);
		getChildren().addAll(header,body);	
	}
	public abstract TextFlow getInfo();  // Display in "info" box.
	public abstract String getTitle();
	public abstract double getWeight();
	public abstract GateType getType();
	public abstract void setWeight(double weight);
	
}
