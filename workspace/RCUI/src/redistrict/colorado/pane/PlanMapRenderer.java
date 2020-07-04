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

import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.ui.ColorizingOption;

/**
 * Render shape as referenced by a plan.
 */
public class PlanMapRenderer implements MapComponentInitializedListener {
	private final static String CLSS = "PlanMapRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private PlanModel model = null;
	private GoogleMapView overlay;
	private ColorizingOption colorizingOption = ColorizingOption.AFFILIATION;

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
		colorizingOption = EventBindingHub.getInstance().getSelectedColorOption();
		
		List<PlanFeature> metrics = model.getMetrics();
		if( metrics!=null ) {
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
				PlanFeature pf = getPlanFeature(name);
				name = "'"+name+"'";
				if( feat.getGeometry().getGeometryType().equals(Geometries.POLYGON.toString()) )  {
					addPolygon(name,pf,(Polygon)feat.getGeometry());
				}
				// Add the polygons
				else if( feat.getGeometry().getGeometryType().equals(Geometries.MULTIPOLYGON.toString()))	 {
					GeometryCollection collection = (GeometryCollection)feat.getGeometry();
					for(int index=0;index<collection.getNumGeometries();index++) {
						addPolygon(name,pf,(Polygon)collection.getGeometryN(index));
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
	private void addPolygon(String name,PlanFeature feature,Polygon poly) {
		overlay.getEngine().executeScript("clearCoordinates()");
		//String format = "MapViewTest5: addPolygon (%f,%f)";
		for(Coordinate c:poly.getCoordinates()) {
			overlay.getEngine().executeScript(String.format("addCoordinate(%s,%s)",String.valueOf(c.x),String.valueOf(c.y)));
			//LOGGER.info(String.format(format, c.x,c.y));
		}
		String color = "'#AAAAAA'";
		if(colorizingOption.equals(ColorizingOption.AFFILIATION)) {
			color = getAffiliationColor(feature);
		}
		else {
			color = getDemographicsColor(feature);
		}
		overlay.getEngine().executeScript(String.format("addPolygon(%s,%s)",name,color));
	}
	
	private String getAffiliationColor(PlanFeature feature) {
		double total = feature.getDemocrat() + feature.getRepublican();
		double dem = feature.getDemocrat()/total;
		double rep = feature.getRepublican()/total;
		String color = String.format("'#%02X%02X%02X'",(int)(256.*rep),0,(int)(256.*dem));
		LOGGER.warning(String.format("%s.getAffiliationColor: %s %2.2f,%2.2f %s",CLSS,feature.getName(),dem,rep,color));
		return color;
	}
	// Return a gray color representing the fraction of minorities
	// This was not successful as there wasn't enough of a difference
	private String getDemographicsColor(PlanFeature feature) {
		double val = feature.getWhite()/feature.getPopulation();
		int c = (int)(256.*val);
		String color = String.format("'#%02X%02X%02X'",c,c,c);
		LOGGER.warning(String.format("%s.getDemographicsColor: %s %2.2f %s",CLSS,feature.getName(),val,color));
		return color;	
	}
	// Do a linear search for the plan feature by name.
	private PlanFeature getPlanFeature(String name) {
		List<PlanFeature> features = model.getMetrics();
		for(PlanFeature feature:features) {
			if(feature.getName().equalsIgnoreCase(name) ) return feature;
		}
		LOGGER.warning(String.format("%s.getPlanFeature: No feature named %s",CLSS,name));
		return null;
	}
}