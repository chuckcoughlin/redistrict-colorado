package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.gmaps.javascript.object.GoogleMap;
import redistrict.colorado.gmaps.javascript.object.LatLong;
import redistrict.colorado.gmaps.javascript.object.MapOptions;
import redistrict.colorado.gmaps.javascript.object.MapType;

public class MapViewTest extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	GoogleMapView mapView;
	GoogleMap map;

	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		
		mapView = new GoogleMapView("AIzaSyCAP3nDrVJ4i7MjtjOzP6AfRaz_Kmbwb7A");
		mapView.addMapInitializedListener(this);
		
		Scene scene = new Scene(mapView);

		stage.setTitle("JavaFX and Google Maps");
		stage.setScene(scene);
		stage.show();
	}

	
	// ---------------------- MapComponentInitializedListener -----------------------------
	@Override
	public void mapInitialized() {
		LOGGER.info("MapViewTest: map initialized ...");
		//Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(new LatLong(47.6097, -122.3331))
		.mapType(MapType.ROADMAP)
		.overviewMapControl(false)
		.panControl(false)
		.rotateControl(false)
		.scaleControl(false)
		.streetViewControl(false)
		.zoomControl(false)
		.zoom(12);

		map = mapView.createMap(mapOptions);
	}

	public static void main(String[] args) {
		String arg = args[0];
    	Path path = Paths.get(arg);
    	PathConstants.setHome(path);
    	// Logging setup routes to console and file within "log" directory
    	LoggerUtility.getInstance().configureRootLogger(LOG_ROOT);
        launch(args);
	}
}
