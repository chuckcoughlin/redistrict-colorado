/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.GateType;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.InfoDialog;
import redistrict.colorado.ui.NameValue;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private final static String CLSS = "Gate";
	protected static Logger LOGGER = Logger.getLogger(CLSS);
	protected static final GuiUtil guiu = new GuiUtil();
	private static final double HEIGHT = 190.;
	private static final double CHART_HEIGHT = 160.;
	private static final double WIDTH = 180.;
	private static final double CHART_WIDTH = 150.;
	public static final double AGGREGATE_TABLE_WIDTH  = 180; 
	private final Label header;
	private final Button info;
	private final InfoDialog infoDialog;
	protected final StackPane body;
	protected final NumberAxis xAxis;
    protected final CategoryAxis yAxis;
    private BarChart<Number,String> chart;
	private final Rectangle rectangle;
	protected final EventBindingHub hub; 
	protected final List<PlanModel>  sortedPlans; // sorted by score
	protected final Map<Long,Double> scoreMap;    // score by planId
	
	public Gate() {
		super(0.);     // No spacing
		this.hub = EventBindingHub.getInstance();
		this.scoreMap = new HashMap<>();
		this.sortedPlans = new ArrayList<>();
		this.header = new Label(getTitle());
		this.infoDialog = new InfoDialog(this);
		
		this.xAxis = new NumberAxis();
        this.yAxis = new CategoryAxis();
        yAxis.setVisible(false);
        this.chart = new BarChart<Number,String>(xAxis,yAxis);
        chart.setPrefWidth(CHART_WIDTH);
        chart.setPrefHeight(CHART_HEIGHT);
        chart.setLegendVisible(false);
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
		info.setId(ComponentIds.BUTTON_INFO);
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
	protected Node getResultsContents() { 
		StackPane node =  new StackPane(); 
		Text text = new Text("Unimplemented");
		TextFlow flow = new TextFlow();
		flow.getChildren().addAll(text);
		node.getChildren().add(flow);
		return node;
	}
	
	public abstract TextFlow getInfo();  // Display in "info" box.
	public abstract String getTitle();
	public abstract double getWeight();
	public abstract GateType getType();
	public abstract void setWeight(double weight);
	public abstract boolean useMaximum();
	
	public void evaluate(List<PlanModel> models) {	
	}
	
	public void showDialog() {
		try {
			// Trying to do this twice throws the exception
			infoDialog.initOwner(getScene().getWindow());
		}
		catch(IllegalStateException ignore) {}
        infoDialog.showAndWait();
    }
	
	public class ChartClickedHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent arg0) {
			showResultsDialog();
		}	
	}
	public class BellActioHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			showResultsDialog();
		}	
	}
	
	// Compare plans based on the composite score for this gate.
	protected Comparator<PlanModel> compareByScore = new Comparator<PlanModel>() {
	    @Override
	    public int compare(PlanModel m1, PlanModel m2) {
	    	double r1 = scoreMap.get(m1.getId());
	    	double r2 = scoreMap.get(m2.getId());
	        return (r1>r2?1:0);
	    }
	};
	// Compare name-value pairs based on the name.
	protected Comparator<NameValue> compareByName = new Comparator<NameValue>() {
	    @Override
	    public int compare(NameValue nv1, NameValue nv2) {
	    	String name1 = nv1.getName();
	    	String name2 = nv2.getName();
	        return (name1.compareTo(name2));
	    }
	};
	
	// Set the bar size to be around 20
	// This was developed purely by cut and try
	// The chart is made up of series, one bar each
	private void setBarWidth(int nbars) {
		double barGap  = 5.;
		chart.setBarGap(barGap);
		chart.setCategoryGap(120.-nbars*20.);
	}
	
	protected void showResultsDialog() {
		ComparisonResultsDialog resultsDialog = new ComparisonResultsDialog(Gate.this);
		resultsDialog.initOwner(Gate.this.getScene().getWindow());
		resultsDialog.setResizable(true);
		resultsDialog.showAndWait();
	}
	
	// Update the bars based on computations
	protected void updateChart() {
		chart.getData().clear();  // Remove existing series.
		setBarWidth(sortedPlans.size());

		int index = 1;
		for(PlanModel model:sortedPlans) {
			XYChart.Series<Number,String> series = new XYChart.Series<Number,String>();
			series.setName(String.valueOf(index));
		    XYChart.Data<Number,String> data = new XYChart.Data<Number,String> (scoreMap.get(model.getId()),"");
		    Color c = model.getFill();
		    String style = String.format("-fx-bar-fill: rgb(%d,%d,%d);", (int)(c.getRed()*255),(int)(c.getGreen()*255),(int)(c.getBlue()*255));
		    data.nodeProperty().addListener(new ChangeListener<Node>() {
		    	  @Override public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
		    		 //LOGGER.info(String.format("%s.updateChart: bar style = %s",CLSS,style));
		    	    if (newNode != null) {
		    	      newNode.setStyle(style); 
		    	    }
		    	  }
		    	});
		    series.getData().add(data);
		    index++;
		    chart.getData().add(series);
		}
		
		LOGGER.info("Gate.updateChart: complete.");
	}
}
