/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;

import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;

/**
 * Render shape as referenced by a plan.
 */
public class PlanMapRenderer implements MapComponentInitializedListener {
	private final static String CLSS = "PlanMapRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private PlanModel model = null;
	private GoogleMapView overlay;

	public PlanMapRenderer(GoogleMapView mapView) {

		this.overlay = mapView;
		overlay.addMapInitializedListener(this);
		overlay.setDisableDoubleClick(true);
	}

	/**
	 * When a new model is defined or old model modified, make sure that its features are populated on screen.
	 * If the model has not been refreshed from the file yet this session, then do so now.
	 * @param m the model
	 */
	public void updateModel(PlanModel m) {
		this.model = m;
		overlay.start();
	}

	// ------------------------- MapComponentInitializedListener -----------------------
	@Override
	public void mapInitialized() {
		LOGGER.info(String.format("%s.mapInitialized: GoogleMap is ready",CLSS));
		//Set the bounds of the map.

		List<PlanFeature> metrics = model.getMetrics();
		if( metrics!=null ) {
			setLabel(model.getName());
			Envelope boundary = model.getBoundary().getFeatures().getEnvelope();
			double north = boundary.getMaxY();
			double east = boundary.getMaxX();
			double south = boundary.getMinY();
			double west = boundary.getMinX();
			// Set the bounds to enclose the area of interest
			overlay.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));
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
					LOGGER.info(String.format("%s: feature %s is not %s.",CLSS,feat.getGeometry().getGeometryType(),
							Geometries.MULTIPOLYGON));
				}
			}
		}
		else {
			LOGGER.info(String.format("%s: model %s has no metrics.",CLSS,model.getName()));
		}
	}
	// Add a polygon to the map. The name is already single-quoted.
	private void addPolygon(String name,Polygon poly) {
		overlay.getEngine().executeScript("clearCoordinates()");
		//String format = "MapViewTest5: addPolygon (%f,%f)";
		for(Coordinate c:poly.getCoordinates()) {
			overlay.getEngine().executeScript(String.format("addCoordinate(%s,%s)",String.valueOf(c.x),String.valueOf(c.y)));
			//LOGGER.info(String.format(format, c.x,c.y));
		}
		overlay.getEngine().executeScript(String.format("addPolygon(%s)",name));
	}
	private void setLabel(String label) {
		String script = "setLabel(\'"+label+"\')";
		overlay.getEngine().executeScript(script);
	}
}
