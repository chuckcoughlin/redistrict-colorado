package redistrict.colorado.chart;

import java.util.List;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import redistrict.colorado.core.Declination;
import redistrict.colorado.core.SeatVote;
import redistrict.colorado.core.VoteSeatCurve;

/**
 * A chart for comparing multiple declinations.
 */
public class VoteSeatChart {
	private final static String CLSS = "VoteSeatChart";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final List<VoteSeatCurve> curves;
	private final LineChart<Number,Number> chart;
	/**
	 * @param vcsc a list of vote-seat curves already evaluated
	 */
	public VoteSeatChart(List<VoteSeatCurve> vscs) {
		this.curves = vscs;
		// Axes in both directions are 0-100 percent
		NumberAxis xAxis = new NumberAxis(0.,100.,25.);
		xAxis.setLabel("Percentage of Votes");
		xAxis.setTickLabelsVisible(true);
		NumberAxis yAxis = new NumberAxis(0.,100.,25.);
		yAxis.setLabel("Percentage of Seats");
		yAxis.setTickLabelsVisible(true);
		chart = new LineChart<>(xAxis,yAxis);
		chart.setTitle("Seats-Vote Curve");
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		
		XYChart.Series<Number,Number> series;
		// Draw the raw number series
		for(VoteSeatCurve vsc: curves) {
			series = new XYChart.Series<>();
			List<SeatVote> seatsVotesRep = vsc.getSeatVotesRepublican();
			for( SeatVote sv:seatsVotesRep ) {
				series.getData().add(new XYChart.Data<Number,Number>(100.*sv.getVotes(),100.*sv.getSeats()));
			}
			chart.getData().add(series);
			
			series = new XYChart.Series<>();
			List<SeatVote> seatsVotesDem = vsc.getSeatVotesDemocratic();
			for( SeatVote sv:seatsVotesDem ) {
				series.getData().add(new XYChart.Data<Number,Number>(100.*sv.getVotes(),100.*sv.getSeats()));
			}
			chart.getData().add(series);
		}
		
        // Style all the series
        int seriesNumber = 0;
        for(VoteSeatCurve vsc: curves) {
        	for(Node n:chart.lookupAll(".series"+String.valueOf(seriesNumber))) {   // Horizontal median
        		n.setStyle("-fx-stroke: red;");
        	}
        	seriesNumber++;
        	for(Node n:chart.lookupAll(".series"+String.valueOf(seriesNumber))) {   // Horizontal median
        		n.setStyle("-fx-stroke: blue;");
        	}
        	seriesNumber++;
        }
	}
	
	public LineChart<Number,Number> getChart() { return this.chart; }

}
