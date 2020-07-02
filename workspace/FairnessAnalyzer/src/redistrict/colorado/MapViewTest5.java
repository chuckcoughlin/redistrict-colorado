package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Envelope;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.pref.PreferenceKeys;

/**
 * NOTE: The test plan must exist and be populated with metrics.
 */
public class MapViewTest5 extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest5";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	private static final String PLAN_NAME = "Plan A";
	private GoogleMapView mapView;
	private PlanModel model = null;

	
	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		String api = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
		mapView = new GoogleMapView(api,GoogleMapView.PLAN_PATH);
		model = loadModel();
		mapView.addMapInitializedListener(this);
		mapView.start();
		
		Scene scene = new Scene(mapView);

		stage.setTitle("Google Maps Display of a Redistricting Plan");
		stage.setScene(scene);
		stage.show();
	}

	// Fill the dataset cache, then choose a plan for our test.
	// The plan must have been previously populated with metrics.
	private PlanModel loadModel() {
		PlanModel result = null;
		Database.getInstance().getDatasetTable().getDatasets();
		List<PlanModel> models = Database.getInstance().getPlanTable().getPlans();
		for( PlanModel m:models) {
			if( PLAN_NAME.equalsIgnoreCase(m.getName())   ) {
				result = m;
				Database.getInstance().getPlanTable().getMetrics(m);
				break;
			}   
		}
		return result;
	}
	
	// ---------------------- MapComponentInitializedListener -----------------------------
	// Once the map is initialized, set the boundaries to fit the desired area.
	@Override
	public void mapInitialized() {
		LOGGER.info("MapViewTest: map initialized ...");
		//Set the bounds of the map.
		if( model != null ) {
			List<PlanFeature> metrics = model.getMetrics();
			if( metrics!=null ) {
				setLabel(model.getName());
				Envelope boundary = model.getBoundary().getFeatures().getEnvelope();
				double north = boundary.getMaxY();
				double east = boundary.getMaxX();
				double south = boundary.getMinY();
				double west = boundary.getMinX();
				// Set the bounds to enclose the area of interest
				mapView.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));
			}
			else {
				LOGGER.info(String.format("MapViewTest5: model %s has no metrics.",model.getName()));
			}
		}
		else {
			LOGGER.info("MapViewTest5: model is NULL.");
		}
	}
	private void setLabel(String label) {
		String script = "setLabel(\'"+label+"\')";
		mapView.getEngine().executeScript(script);
	}
	public static void main(String[] args) {
		String arg = args[0];
    	Path path = Paths.get(arg);
    	PathConstants.setHome(path);
    	// Logging setup routes to console and file within "log" directory
    	LoggerUtility.getInstance().configureRootLogger(LOG_ROOT);
    	Database.getInstance().startup(PathConstants.DB_PATH);
        launch(args);
	}
}
