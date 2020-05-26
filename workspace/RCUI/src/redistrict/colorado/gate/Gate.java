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
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import redistrict.colorado.db.Database;
import redistrict.colorado.db.PreferencesTable;
import redistrict.colorado.table.NameValue;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.InfoDialog;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private final static String CLSS = "Gate";
	protected static Logger LOGGER = Logger.getLogger(CLSS);
	protected static final GuiUtil guiu = new GuiUtil();
	protected static final double HEIGHT = 190.;
	private static final double CHART_HEIGHT = 160.;
	public static final double WIDTH = 180.;
	private static final double CHART_WIDTH = 150.;
	public static final double AGGREGATE_TABLE_WIDTH  = 180;
	protected final Label header;
	protected final Button info;
	protected InfoDialog infoDialog;
	protected final StackPane body;
	protected NumberAxis xAxis = null;
    protected CategoryAxis yAxis = null;
    private BarChart<Number,String> barChart = null;
	protected Rectangle rectangle = null;
	protected final EventBindingHub hub; 
	protected final List<PlanModel>  sortedPlans; // sorted by score
	protected final Map<Long,NameValue> scoreMap; // score by planId
	
	public Gate() {
		super(0.);     // No spacing
		this.hub = EventBindingHub.getInstance();
		this.scoreMap = new HashMap<>();
		this.sortedPlans = new ArrayList<>();
		this.header = new Label(getTitle());
		this.infoDialog = new InfoDialog(this);
		info = new Button("",guiu.loadImage("images/information.png"));
		info.setOnAction( new EventHandler<ActionEvent>() {
	        @Override public void handle( ActionEvent e ) {
	        	showDialog(); 
	        }
	    } );
		info.setId(ComponentIds.BUTTON_INFO);
		this.body = new StackPane();
		init();
	}
	
	/**
	 * Initialize the main UI. This works for all except the composite.
	 */
	protected void init() {
		this.xAxis = new NumberAxis();
        this.yAxis = new CategoryAxis();
        yAxis.setVisible(false);
        this.barChart = new BarChart<Number,String>(xAxis,yAxis);
        barChart.setPrefWidth(CHART_WIDTH);
        barChart.setMaxWidth(CHART_WIDTH);
        barChart.setPrefHeight(CHART_HEIGHT);
        barChart.setLegendVisible(false);
        barChart.setOnMouseClicked(new ChartClickedHandler ());
       
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(WIDTH+1);
		header.getStyleClass().add("graph-header");

		body.setAlignment(Pos.CENTER);
		rectangle = new Rectangle(WIDTH,HEIGHT);
		rectangle.getStyleClass().add("graph-rectangle");
		StackPane.setAlignment(header, Pos.TOP_CENTER);
		StackPane.setAlignment(rectangle, Pos.CENTER);
		StackPane.setAlignment(barChart, Pos.CENTER);
		StackPane.setMargin(barChart, new Insets(0, 10, 5, 0));  // top right bottom left
		StackPane.setAlignment(info, Pos.BOTTOM_RIGHT);
		body.getChildren().addAll(rectangle,barChart,header,info);
		getChildren().addAll(body);	
	}
	protected BarChart<Number,String> getChart() { return this.barChart; }
	protected Node getResultsContents() { 
		StackPane node =  new StackPane(); 
		Text text = new Text("Unimplemented");
		TextFlow flow = new TextFlow();
		flow.getChildren().addAll(text);
		node.getChildren().add(flow);
		return node;
	}
	/*
	 * @return a label to display on top of the bar.
	 * 			Otherwise return null
	 */
	protected Label getBarOverlayLabel(PlanModel model) {
		return null;
	}
	public abstract TextFlow getInfo();  // Display in "info" box.
	public abstract GateType getType();
	public abstract String getScoreAttribute();
	public abstract String getTitle();
	public double getScore(long planId) {
		NameValue nv = scoreMap.get(planId);
		Object val = nv.getValue(getScoreAttribute());
		return GuiUtil.toDouble(val);
	}

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


	// Compare name-value pairs based on the name.
	protected Comparator<NameValue> compareByName = new Comparator<NameValue>() {
	    @Override
	    public int compare(NameValue nv1, NameValue nv2) {
	    	String name1 = nv1.getName();
	    	String name2 = nv2.getName();
	        return (name1.compareTo(name2));
	    }
	};
	// Compare plans based on the scoring attribute for this gate. The attribute
	// must be numeric. The scoreMap must be pre-populated with name-value objects
	// containing that attribute.
	protected Comparator<PlanModel> compareByScore = new Comparator<PlanModel>() {
		@Override
		public int compare(PlanModel m1, PlanModel m2) {
			String att = getScoreAttribute();
			if( scoreMap.get(m1.getId()).getValue(att) != null &&
				scoreMap.get(m2.getId()).getValue(att) != null   ) {
				double r1 = Double.parseDouble(scoreMap.get(m1.getId()).getValue(att).toString());
				double r2 = Double.parseDouble(scoreMap.get(m2.getId()).getValue(att).toString());
				return (r1>r2?1:0);
			}
			return 0;
		}
	};
	
	// Compare double values in a list
	protected Comparator<Double> compareByValue = new Comparator<Double>() {
	    @Override
	    public int compare(Double dbl1, Double dbl2) {
	        return(dbl1>dbl2?1:0);
	    }
	};
	
	// Read a preferences string and convert to a double. The default is there just in case of error.
	protected double getThreshold(String key,double defaultThreshold) {
		double threshold = defaultThreshold;
		try {
			String val = Database.getInstance().getPreferencesTable().getParameter(key);
			if( !val.isEmpty()) threshold = Double.parseDouble(val);
		}
		catch(NumberFormatException nfe) {
			LOGGER.warning(String.format("%s.getThreshold: Error converting %s to double. Using %d. (%s)",CLSS,key,defaultThreshold,nfe.getLocalizedMessage()));
		}
		return threshold;
	}
	
	// Set the bar size to be around 20
	// This was developed purely by cut and try
	// The chart is made up of series, one bar each
	private void setBarWidth(int nbars) {
		double barGap  = 5.;
		barChart.setBarGap(barGap);
		barChart.setCategoryGap(120.-nbars*20.);
	}
	
	protected void showResultsDialog() {
		ComparisonResultsDialog resultsDialog = new ComparisonResultsDialog(Gate.this);
		resultsDialog.initOwner(Gate.this.getScene().getWindow());
		resultsDialog.setResizable(true);
		resultsDialog.showAndWait();
	}
	
	// Update the bars based on computations
	protected void updateChart() {
		barChart.getData().clear();  // Remove existing series.
		setBarWidth(sortedPlans.size());

		int index = 1;
		String att = getScoreAttribute();
		for(PlanModel model:sortedPlans) {
			XYChart.Series<Number,String> series = new XYChart.Series<Number,String>();
			series.setName(String.valueOf(index));
		    XYChart.Data<Number,String> data = new XYChart.Data<Number,String> (guiu.toDouble(scoreMap.get(model.getId()).getValue(att)),"");

		    Color c = model.getFill();
		    String style = String.format("-fx-bar-fill: rgb(%d,%d,%d);", (int)(c.getRed()*255),(int)(c.getGreen()*255),(int)(c.getBlue()*255));
		    series.getData().add(data);
		    data.nodeProperty().addListener(new ChangeListener<Node>() {
		    	@Override public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) { 
		    		// Node is the stack pane.
		    		if (newNode != null) {
		    			//LOGGER.info(String.format("%s.updateChart: bar style = %s, node is %s",CLSS,style,newNode.getClass().getCanonicalName()));
		    			newNode.setStyle(style);
		    		}
		    	}
		    });
		    
		    Label overlay = getBarOverlayLabel(model);
		    if( overlay!=null ) {
		    	StackPane node = new StackPane();
		    	Group group = new Group(overlay);
		    	StackPane.setAlignment(group, Pos.BOTTOM_CENTER);
		    	StackPane.setMargin(group, new Insets(0, 0, 5, 0));
		    	node.getChildren().add(group);
		    	data.setNode(node);
		    }
	        
		    barChart.getData().add(series);
		    index++;
		}
		
		LOGGER.info("Gate.updateChart: complete.");
	}
}
