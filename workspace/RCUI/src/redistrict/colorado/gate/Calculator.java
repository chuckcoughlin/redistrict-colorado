/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.logging.Logger;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.db.DatasetCache;

/**
 * The Calculator is a collection of static methods that produce results for the various
 * "gates".
 */
public class Calculator {
	private final static String CLSS = "Calculator";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	/**
	 * Augment a single polygon with values from an affiliation dataset.
	 */
	public static void aggregateAffiliations(PlanFeature planFeat, Polygon polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getAffiliationId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			for(Feature feat:fc.getFeatures()) {
				Polygon geometry = (Polygon)(feat.getAttribute(am.getAffiliationGeometryName()));
				Geometry intersect = polygon.intersection(geometry);
				if( intersect!=null) {
					double areaRatio = intersect.getArea() / geometry.getArea();
					double increment = (Double)feat.getAttribute(am.getAttributeForBlack());
					planFeat.incrementBlack(areaRatio*increment);
					increment = (Double)feat.getAttribute(am.getAttributeForHispanic());
					planFeat.incrementHispanic(areaRatio*increment);
					increment = (Double)feat.getAttribute(am.getAttributeForWhite());
					planFeat.incrementWhite(areaRatio*increment);
					increment = (Double)feat.getAttribute(am.getAttributeForPopulation());
					planFeat.incrementPopulation(areaRatio*increment);
				}
			}
		}
	}


	/**
	 * Augment a single polygon with values from an demographic dataset.
	 */
	public static void aggregateDemographics(PlanFeature planFeat, Polygon polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getDemographicId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			for(Feature feat:fc.getFeatures()) {
				Polygon geometry = (Polygon)(feat.getAttribute(am.getDemographicGeometryName()));
				Geometry intersect = polygon.intersection(geometry);
				if( intersect!=null) {
					double areaRatio = intersect.getArea() / geometry.getArea();
					double increment = (Double)feat.getAttribute(am.getAttributeForDemocrat());
					planFeat.incrementDemocrat(areaRatio*increment);
					increment = (Double)feat.getAttribute(am.getAttributeForRepublican());
					planFeat.incrementRepublican(areaRatio*increment);
				}
			}
		}
	}

}
