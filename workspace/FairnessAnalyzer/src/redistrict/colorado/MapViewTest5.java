package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.StandardAttributes;
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
				Envelope boundary = model.getBoundary().getFeatures().getEnvelope();
				double north = boundary.getMaxY();
				double east = boundary.getMaxX();
				double south = boundary.getMinY();
				double west = boundary.getMinX();
				// Set the bounds to enclose the area of interest
				mapView.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));
				String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.ID.name());

				// Add the polygons
				for(Feature feat:model.getBoundary().getFeatures().getFeatures()) {
					String name = feat.getAttribute(nameAttribute).toString();
					name = "'"+name+"'";
					if( feat.getGeometry().getGeometryType().equals(Geometries.POLYGON.toString()) )  {
						addPolygon(name,(Polygon)feat.getGeometry());
					}
					// Add the polygons
					else if( feat.getGeometry().getGeometryType().equals(Geometries.MULTIPOLYGON.toString()))	 {
						GeometryCollection collection = (GeometryCollection)feat.getGeometry();
						for(int index=0;index<collection.getNumGeometries();index++) {
							addPolygon(name,(Polygon)collection.getGeometryN(index));
						}
					}
					else {
						LOGGER.info(String.format("MapViewTest4: feature %s is not %s.",feat.getGeometry().getGeometryType(),
								Geometries.MULTIPOLYGON));
					}
				}
			}
			else {
				LOGGER.info(String.format("MapViewTest5: model %s has no metrics.",model.getName()));
			}
		}
		else {
			LOGGER.info("MapViewTest5: model is NULL.");
		}
	}
	// Add a polygon to the map. The name is already single-quoted.
	private void addPolygon(String name,Polygon poly) {
		mapView.getEngine().executeScript("clearCoordinates()");
		//String format = "MapViewTest5: addPolygon (%f,%f)";
		for(Coordinate c:poly.getCoordinates()) {
			mapView.getEngine().executeScript(String.format("addCoordinate(%s,%s)",String.valueOf(c.x),String.valueOf(c.y)));
			//LOGGER.info(String.format(format, c.x,c.y));
		}
		String color = "'#33DD33'";
		mapView.getEngine().executeScript(String.format("addPolygon(%s,%s)",name,color));
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
