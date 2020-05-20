package redistrict.colorado.chart;

import java.util.List;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import redistrict.colorado.core.Declination;

/**
 * A chart for comparing multiple declinations.
 */
public class DeclinationChart {
	private final static String CLSS = "DeclinationChart";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final List<Declination> declinations;
	private final LineChart<Number,Number> chart;
	private int ndistricts;
	/**
	 * @param decs a list of declinations already evaluated
	 */
	public DeclinationChart(List<Declination> decs) {
		this.declinations = decs;
		ndistricts = decs.get(0).getSize();
		NumberAxis xAxis = new NumberAxis(0,ndistricts,ndistricts/2);
		xAxis.setTickMarkVisible(false);
		xAxis.setTickLabelsVisible(false);
		NumberAxis yAxis = new NumberAxis(0,1.0,0.1);
		yAxis.setLabel("Fraction Dem Vote");
		yAxis.setTickLabelsVisible(false);
		chart = new LineChart<>(xAxis,yAxis);
		chart.setTitle("Declination Comparison");
		chart.setLegendVisible(false);
		
		XYChart.Series<Number,Number> series;
		// Draw the raw number series
		for(Declination dec: declinations) {
			series = new XYChart.Series<>();
			double[] raw = dec.getDemFractions();
			
			int index = 0;
			for( double val:raw ) {
				series.getData().add(new XYChart.Data<Number,Number>((double)index,val));
				index++;
			}
			chart.getData().add(series);
		}
		
		//Draw the broken line results
		for(Declination dec: declinations) {
			series = new XYChart.Series<>();
			double demSeats = dec.getDemSeats();
			double repSeats = dec.getRepSeats();
			series.getData().add(new XYChart.Data<Number,Number>(repSeats/2., dec.getRepMean()));
			series.getData().add(new XYChart.Data<Number,Number>((demSeats+repSeats)/2., 0.5));
			series.getData().add(new XYChart.Data<Number,Number>(repSeats+(demSeats/2.), dec.getDemMean()));
			chart.getData().add(series);
		}
		//Draw horizontal line at midpoint
		series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<Number,Number>(0., 0.5));
        series.getData().add(new XYChart.Data<Number,Number>((double)ndistricts, 0.5));
        chart.getData().add(series);
        // Style all the series
        int seriesNumber = 0;
        for(Declination dec: declinations) {
        	for(Node n:chart.lookupAll(".series"+String.valueOf(seriesNumber))) {   // Horizontal median
        		n.setStyle("-fx-stroke-width: 1px; -fx-background-radius: 0.0px; -fx-padding: 0.0px;");
        	}
        	seriesNumber++;
        }

        for(Declination dec: declinations) {
        	int r = (int) (255 * dec.getColor().getRed()) ;
            int g = (int) (255 * dec.getColor().getGreen()) ;
            int b = (int) (255 * dec.getColor().getBlue()) ;
        	for(Node n:chart.lookupAll(".series"+String.valueOf(seriesNumber))) {   // Horizontal median
        		n.setStyle(String.format("-fx-stroke: #%02x%02x%02x; -fx-background-color: #%02x%02x%02x;",r,g,b,r,g,b));
        	}
        	seriesNumber++;
        }

        for(Node n:chart.lookupAll(".series"+String.valueOf(seriesNumber))) {   // Horizontal median
        	n.setStyle("-fx-stroke: black; -fx-background-radius: 0.0px; -fx-padding: 0.0px;");
        }
	}
	
	public LineChart<Number,Number> getChart() { return this.chart; }

}
