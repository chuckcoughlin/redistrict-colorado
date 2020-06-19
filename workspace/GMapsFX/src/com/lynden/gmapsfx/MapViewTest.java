package com.lynden.gmapsfx;

import java.util.Locale;
import java.util.logging.Logger;

// Taken directly from:
// https://rterp.wordpress.com/2014/04/25/gmapsfx-add-google-maps-to-your-javafx-application/
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
		
		mapView = new GoogleMapView();
		//mapView = new GoogleMapView("en-US","AIzaSyCAP3nDrVJ4i7MjtjOzP6AfRaz_Kmbwb7A");
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
		.mapType(MapTypeIdEnum.ROADMAP)
		.overviewMapControl(false)
		.panControl(false)
		.rotateControl(false)
		.scaleControl(false)
		.streetViewControl(false)
		.zoomControl(false)
		.zoom(12);

		map = mapView.createMap(mapOptions);

		//Add a marker to the map
		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.position( new LatLong(47.6, -122.3) )
		.visible(Boolean.TRUE)
		.title("My Marker");
		Marker marker = new Marker( markerOptions );

		map.addMarker(marker);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
