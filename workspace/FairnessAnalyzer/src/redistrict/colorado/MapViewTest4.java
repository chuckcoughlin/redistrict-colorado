package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.geotools.util.GeometryUtilities;
import org.locationtech.jts.geom.Envelope;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.pref.PreferenceKeys;

public class MapViewTest4 extends Application implements MapComponentInitializedListener {
	private static final String CLSS = "MapViewTest";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	private static final String DATASET_NAME = "Colorado Counties";
	private static final String COUNTY_NAME  = "BOULDER";
	GoogleMapView mapView;
	private Feature feat = null;
	

	@Override
	public void start(Stage stage) throws Exception {
		LOGGER.info("MapViewTest: startup ...");
		//Create the JavaFX component and set this as a listener so we know when 
		//the map has been initialized, at which point we can then begin manipulating it.
		String api = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
		DatasetModel model = loadModel();
		if( model!=null ) {
			feat = loadFeature(model);
			feat.getGeometry();
		}
		mapView = new GoogleMapView(api,GoogleMapView.DISTRICT_PATH);
		mapView.addMapInitializedListener(this);
		mapView.start();
		
		Scene scene = new Scene(mapView);

		stage.setTitle("Test Display of a District via Google Maps");
		stage.setScene(scene);
		stage.show();
	}

	// Fill the model cache, then choose one for our test.
	// These are not populated until contents are accessed.
	private DatasetModel loadModel() {
		DatasetModel result = null;
		List<DatasetModel> models = Database.getInstance().getDatasetTable().getDatasets();
		for( DatasetModel m:models) {
			if( m.getRole().equals(DatasetRole.BOUNDARIES) &&
				DATASET_NAME.equalsIgnoreCase(m.getName())   ) {
				result = m;
				break;
			}   
		}
		return result;
	}
	
	// For the selected model, choose the feature.
	private Feature loadFeature(DatasetModel m) {
		FeatureCollection fc = m.getFeatures();
		Feature result = null;
		String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(m.getId(), StandardAttributes.ID.name());
		for( Feature feat: fc.getFeatures()) {
			if( COUNTY_NAME.equalsIgnoreCase(feat.getAttribute(nameAttribute).toString())   ) {
				result = feat;
				break;
			}   
		}
		return result;
	}
	
	// ---------------------- MapComponentInitializedListener-----------------------------
	// Once the map is initialized, set the boundaries to fit the desired area.
	@Override
	public void mapInitialized() {
		LOGGER.info("MapViewTest4: map initialized ...");
		//Set the bounds of the map.
		if( feat!=null ) {
			if( feat.getGeometry().getGeometryType().equals(Geometries.POLYGON.toString()) ||
				feat.getGeometry().getGeometryType().equals(Geometries.MULTIPOLYGON.toString())	) {
				Envelope envelope = feat.getBounds();
				double north = envelope.getMaxY();
				double east = envelope.getMaxX();
				double south = envelope.getMinY();
				double west = envelope.getMinX();
				mapView.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));
			}
			else {
				LOGGER.info(String.format("MapViewTest4: feature %s is not %s.",feat.getGeometry().getGeometryType(),
						Geometries.MULTIPOLYGON));
				LOGGER.info(String.format("MapViewTest4: feature is a %s.",GeometryUtilities.toText(feat.getGeometry())));
			}
			
		}
		else {
			LOGGER.info("MapViewTest4: feature is NULL.");
		}
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
