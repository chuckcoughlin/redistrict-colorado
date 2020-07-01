package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.pref.PreferenceKeys;

// This test sets the bounds of the map to include the state of colorado.
public class MapViewTest3 extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest3";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	double north = 41.;
	double south = 37.;
	double east = -103.05;
	double west = -109.05;
	private GoogleMapView mapView;

	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		String api = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);			
		mapView = new GoogleMapView(api,GoogleMapView.BOUNDS_PATH);
		mapView.addMapInitializedListener(this);
		mapView.start();
		
		Scene scene = new Scene(mapView);

		stage.setTitle("Show Google Map with Initial Bounds");
		stage.setScene(scene);
		stage.show();
	}

	
	// ---------------------- MapComponentInitializedListener -----------------------------
	// Once the map is initialized, set the boundaries to fit the desired area.
	@Override
	public void mapInitialized() {
		LOGGER.info("MapViewTest3: map initialized ...");
		//Set the bounds of the map.
		//Document doc = mapView.getEngine().getDocument();
		//mapView.dumpDocument(doc);
		mapView.getEngine().executeScript(String.format("initBounds(%8.2f,%8.2f,%8.2f,%8.2f)",north,east,south,west));
		
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
