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

// This is a basic Google Map with only a map-type control.
public class MapViewTest2 extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest2";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	GoogleMapView mapView;

	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest2: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		String api = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
		mapView = new GoogleMapView(api,GoogleMapView.PAGE_PATH);
		mapView.addMapInitializedListener(this);
		mapView.start();
		
		Scene scene = new Scene(mapView);

		stage.setTitle("Basic Google Map");
		stage.setScene(scene);
		stage.show();
	}

	
	// ---------------------- MapComponentInitializedListener -----------------------------
	// There is no action taken when the map is initialized.
	@Override
	public void mapInitialized() {
		LOGGER.info("MapViewTest2: map initialized ...");	
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
