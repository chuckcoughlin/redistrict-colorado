/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;

import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;

/**
 * Render a district from the shapefile as polygons on a Google Map.
 */
public class DistrictMapRenderer  implements MapComponentInitializedListener {
	private final static String CLSS = "DistrictMapRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private DatasetModel model = null;
	private String region;
	private final GoogleMapView overlay;
	private Feature feature = null;

	public DistrictMapRenderer(GoogleMapView mapView) {
		this.overlay = mapView;
		overlay.addMapInitializedListener(this);
		overlay.setDisableDoubleClick(true);
	}

	/**
	 * When a new model is defined or old model modified, make sure that its features are populated on screen.
	 * If the model has not been refreshed from the file yet this session, then do so now.
	 * @param m the model
	 */
	public void updateModel(DatasetModel m,String regionName) {
		this.model = m;
		this.region = regionName;
		String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.ID.name());
		for(Feature feat:model.getFeatures().getFeatures()) {
			if(feat.getAttribute(nameAttribute).equals(regionName)) {
				this.feature = feat;
				break;  // There should only be one
			}
		}
		overlay.start();
	}

	// ------------------------- MapComponentInitializedListener -----------------------
	@Override
	public void mapInitialized() {
		LOGGER.info(String.format("%s.mapInitialized: GoogleMap is ready",CLSS));
		//Set the bounds of the map.
		if( feature!=null ) {
			Envelope envelope = feature.getBounds();
			double north = envelope.getMaxY();
			double east = envelope.getMaxX();
			double south = envelope.getMinY();
			double west = envelope.getMinX();
			// Set the bounds to enclose the area of interest
			overlay.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));

			// Add the polygon
			String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.ID.name());
			String name = feature.getAttribute(nameAttribute).toString();
			setLabel(name);
			if( feature.getGeometry().getGeometryType().equals(Geometries.POLYGON.toString()) )  {
				addPolygon(feature.getAttribute(nameAttribute).toString(),(Polygon)feature.getGeometry());
			}
			// Add the polygons
			else if( feature.getGeometry().getGeometryType().equals(Geometries.MULTIPOLYGON.toString()))	 {
				GeometryCollection collection = (GeometryCollection)feature.getGeometry();
				for(int index=0;index<collection.getNumGeometries();index++) {
					addPolygon(name+String.valueOf(index),(Polygon)collection.getGeometryN(index));
				}
			}
			else {
				LOGGER.info(String.format("MapViewTest4: feature %s is not %s.",feature.getGeometry().getGeometryType(),
						Geometries.MULTIPOLYGON));
			}
		}	
		else {
			LOGGER.info("MapViewTest4: feature is NULL.");
		}
	}
	// Add a polygon to the map
	private void addPolygon(String name,Polygon poly) {
		overlay.getEngine().executeScript("clearCoordinates()");
		//String format = "MapViewTest4: addPolygon (%f,%f)";
		for(Coordinate c:poly.getCoordinates()) {
			overlay.getEngine().executeScript(String.format("addCoordinate(%s,%s)",String.valueOf(c.x),String.valueOf(c.y)));
			//LOGGER.info(String.format(format, c.x,c.y));
		}
		overlay.getEngine().executeScript("addPolygon()");
	}
	private void setLabel(String label) {
		String script = "setLabel(\'"+label+"\')";
		overlay.getEngine().executeScript(script);
	}
}
