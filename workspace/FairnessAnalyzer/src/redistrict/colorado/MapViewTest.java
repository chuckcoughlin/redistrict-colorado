package redistrict.colorado;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.gmaps.javascript.object.GoogleMap;
import redistrict.colorado.gmaps.javascript.object.LatLong;
import redistrict.colorado.gmaps.javascript.object.MapOptions;
import redistrict.colorado.gmaps.javascript.object.MapType;

public class MapViewTest extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	GoogleMapView mapView;
	GoogleMap map;

	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		
		//mapView = new GoogleMapView();
		mapView = new GoogleMapView("AIzaSyCAP3nDrVJ4i7MjtjOzP6AfRaz_Kmbwb7A");
		mapView.addMapInitializedListener(this);

		Scene scene = new Scene(mapView);

		stage.setTitle("JavaFX and Google Maps");
		stage.setScene(scene);
		stage.show();
	}

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
		launch(args);
	}
}
