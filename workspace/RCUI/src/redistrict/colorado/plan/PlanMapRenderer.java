/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
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
	private ColorizingOption colorizingOption = ColorizingOption.NONE;
	private final OverlayColorGenerator colorGenerator;

	public PlanMapRenderer(GoogleMapView mapView) {

		this.overlay = mapView;
		this.colorGenerator = new OverlayColorGenerator();
		overlay.addMapInitializedListener(this);
		overlay.setDisableDoubleClick(true);
	}

	// Set the colorizing option then repaint
	public void setColorizingOption(ColorizingOption opt) {
		colorizingOption = opt;
		overlay.start();
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
		if( model==null ) return;
		LOGGER.info(String.format("%s.mapInitialized: GoogleMap %s ready (%s)",CLSS, model.getName(),colorizingOption.name()));
		
		List<PlanFeature> metrics = model.getMetrics();
		if( metrics!=null ) {
			Envelope boundary = model.getBoundary().getFeatures().getEnvelope();
			double north = boundary.getMaxY();
			double east = boundary.getMaxX();
			double south = boundary.getMinY();
			double west = boundary.getMinX();
			// Set the bounds to enclose the area of interest
			overlay.getEngine().executeScript(String.format("initBounds(%8.6f,%8.6f,%8.6f,%8.6f)",north,east,south,west));
			String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getBoundary().getId(), StandardAttributes.ID.name());
			// Add the polygons
			for(Feature feat:model.getBoundary().getFeatures().getFeatures()) {
				String name = feat.getAttribute(nameAttribute).toString();
				PlanFeature pf = getPlanFeature(name);
				if( feat.getGeometry().getGeometryType().equals(Geometries.POLYGON.toString()) )  {
					addPolygon(name,pf,(Polygon)feat.getGeometry());
				}
				// Add the polygons
				else if( feat.getGeometry().getGeometryType().equals(Geometries.MULTIPOLYGON.toString()))	 {
					GeometryCollection collection = (GeometryCollection)feat.getGeometry();
					for(int index=0;index<collection.getNumGeometries();index++) {
						Geometry geo = collection.getGeometryN(index);
						if( geo.getGeometryType().equals(Geometries.POLYGON.toString())) {
							addPolygon(name,pf,(Polygon)geo);
						}
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
	// If the plan hasn't been initialized yet the plan feature will be null
	private void addPolygon(String name,PlanFeature feature,Polygon poly) {
		if( feature==null ) return;  
		
		overlay.getEngine().executeScript("clearCoordinates()");
		//String format = "PlanMapRenderer: addPolygon (%f,%f)";
		for(Coordinate c:poly.getCoordinates()) {
			overlay.getEngine().executeScript(String.format("addCoordinate(%s,%s)",String.valueOf(c.x),String.valueOf(c.y)));
			//LOGGER.info(String.format(format, c.x,c.y));
		}
		String color = "#FFFFFF00";  // Transparent
		if(colorizingOption.name().equals(ColorizingOption.AFFILIATION.name())) {
			color = colorGenerator.getAffiliationColor(model.getMaxRepublican(),model.getMaxDemocrat(),feature);
		}
		else if(colorizingOption.name().equals(ColorizingOption.DEMOGRAPHICS.name())) {
			color = colorGenerator.getDemographicsColor(model.getMinWhite(),model.getMaxWhite(),feature);
		}
		String content = makeContent(feature); 
		overlay.getEngine().executeScript(String.format("addPolygon('%s','%s','%s')",name,color,content));
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
	// Return the HTML for the info window
	private String makeContent(PlanFeature feature) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("    <center><h3>");
		sb.append(feature.getName());
		sb.append("</h3></center>");
		sb.append("<table style=\"width:100%,font-size:6px\">");
		double total = feature.getDemocrat()+feature.getRepublican();
		sb.append("<tr><td>Democrat</td><td>");
		sb.append(String.format("%2.1f",100.*feature.getDemocrat()/total));
		sb.append("%</td></tr> ");
		sb.append("<tr><td>Republican</td><td>");
		sb.append(String.format("%2.1f",100.*feature.getRepublican()/total));
		sb.append("%</td></tr> ");
		sb.append("<tr><td>Black</td><td>");
		sb.append(String.format("%2.1f",100.*feature.getBlack()/feature.getPopulation()));
		sb.append("%</td></tr> ");
		sb.append("<tr><td>Hispanic</td><td>");
		sb.append(String.format("%2.1f",100.*feature.getHispanic()/feature.getPopulation()));
		sb.append("%</td></tr> ");
		sb.append("<tr><td>White</td><td>");
		sb.append(String.format("%2.1f",100.*feature.getWhite()/feature.getPopulation()));
		sb.append("%</td></tr>");
		sb.append("</table>");
		sb.append("</html>");
		return sb.toString();
	}
}
