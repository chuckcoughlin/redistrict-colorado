/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.InfoDialog;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private final static String CLSS = "Gate";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final GuiUtil guiu = new GuiUtil();
	private static final double HEIGHT = 190.;
	private static final double CHART_HEIGHT = 160.;
	private static final double WIDTH = 180.;
	private static final double CHART_WIDTH = 150.;
	private final Label header;
	private final Button info;
	private final ComparisonResultsDialog resultsDialog;
	private final InfoDialog infoDialog;
	private final StackPane body;
	private final NumberAxis xAxis;
    private final CategoryAxis yAxis;
    private final BarChart<Number,String> chart;
	private final Rectangle rectangle;
	protected final EventBindingHub hub; 
	
	public Gate() {
		super(0.);     // No spacing
		this.hub = EventBindingHub.getInstance();
		this.header = new Label(getTitle());
		this.infoDialog = new InfoDialog(getInfo());
		this.resultsDialog = new ComparisonResultsDialog(this);
		this.xAxis = new NumberAxis();
        this.yAxis = new CategoryAxis();
        this.chart = new BarChart<Number,String>(xAxis,yAxis);
        chart.setPrefWidth(CHART_WIDTH);
        chart.setPrefHeight(CHART_HEIGHT);
        chart.setOnMouseClicked(new ChartClickedHandler ());
       
        
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(WIDTH+1);
		header.getStyleClass().add("graph-header");
		info = new Button("",guiu.loadImage("images/information.png"));
		info.setOnAction( new EventHandler<ActionEvent>() {
	        @Override public void handle( ActionEvent e ) {
	        	showDialog(); 
	        }
	    } );
		this.body = new StackPane();
		body.setAlignment(Pos.CENTER);
		rectangle = new Rectangle(WIDTH,HEIGHT);
		rectangle.getStyleClass().add("graph-rectangle");
		StackPane.setAlignment(header, Pos.TOP_CENTER);
		StackPane.setAlignment(rectangle, Pos.CENTER);
		StackPane.setAlignment(chart, Pos.CENTER);
		StackPane.setAlignment(info, Pos.BOTTOM_RIGHT);
		body.getChildren().addAll(rectangle,chart,header,info);
		getChildren().addAll(body);	
	}
	protected BarChart<Number,String> getChart() { return this.chart; }
	
	public abstract TextFlow getInfo();  // Display in "info" box.
	public abstract String getTitle();
	public abstract double getWeight();
	public abstract GateType getType();
	public abstract void setWeight(double weight);
	
	public void evaluate(List<PlanModel> models) {	
	}
	
	public void showDialog() {
        infoDialog.showAndWait();
    }
	
	public class ChartClickedHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent arg0) {
			LOGGER.info("ChartclickedHandler: CLICKED");
			resultsDialog.showAndWait();
		}	
	}
	
}
