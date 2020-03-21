/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Geometry;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureUtil;

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
	public static void aggregateAffiliations(PlanFeature planFeat, Geometry polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getAffiliationId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			int count = 0;
			for(Feature feat:fc.getFeatures()) {
				Geometry geometry = (Geometry)(feat.getAttribute(am.getAffiliationGeometryName()));
				Geometries type = Geometries.get(geometry);
				if( geometry.disjoint(polygon)) continue;
				if( !type.equals(Geometries.POLYGON)) continue;
				try {
					Geometry intersect = polygon.intersection(geometry);
					if( intersect!=null && intersect.getEnvelope()!=null && !intersect.isEmpty() ) {
						double areaRatio = intersect.getArea() / geometry.getArea();
						long increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForDemocrat()));
						planFeat.incrementDemocrat(areaRatio*increment);
						increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForRepublican()));
						planFeat.incrementRepublican(areaRatio*increment);
						count++;
					}
				}
				catch(Exception ex) {
					LOGGER.warning(String.format("%s.aggregateAffiliations: Intersect exception (%s)", CLSS,ex.getLocalizedMessage()));
				}
			}
			LOGGER.info(String.format("%s.aggregateAffiliations: %d features of %d intersect", CLSS,count,fc.getFeatures().size()));
		}
	}


	/**
	 * Augment a single polygon with values from an demographic dataset.
	 */
	public static void aggregateDemographics(PlanFeature planFeat, Geometry polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getDemographicId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			int count = 0;
			for(Feature feat:fc.getFeatures()) {
				Geometry geometry = (Geometry)(feat.getAttribute(am.getDemographicGeometryName()));
				Geometries type = Geometries.get(geometry);
				if( geometry.disjoint(polygon)) continue;
				if( !type.equals(Geometries.POLYGON)) continue;
				try {
					Geometry intersect = polygon.intersection(geometry);
					if( intersect!=null && intersect.getEnvelope()!=null && !intersect.isEmpty() ) {
						double areaRatio = intersect.getArea() / geometry.getArea();
						long increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForBlack()));
						planFeat.incrementBlack(areaRatio*increment);
						increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForHispanic()));
						planFeat.incrementHispanic(areaRatio*increment);
						increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForWhite()));
						planFeat.incrementWhite(areaRatio*increment);
						increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForPopulation()));
						planFeat.incrementPopulation(areaRatio*increment);
						count++;
					}
				}
				catch(Exception ex) {
					LOGGER.warning(String.format("%s.aggregateDemographics: Intersect exception (%s)", CLSS,ex.getLocalizedMessage()));
				}
			}
			LOGGER.info(String.format("%s.aggregateDemographics: %d features of %d intersect", CLSS,count,fc.getFeatures().size()));
		}
	}

}
