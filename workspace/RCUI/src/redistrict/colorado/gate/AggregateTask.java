package redistrict.colorado.gate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.util.Geometries;
import org.locationtech.jts.geom.Geometry;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureUtil;

import javafx.concurrent.Task;
import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;

public class AggregateTask  extends Task<List<PlanFeature>> {
	private final static String CLSS = "AggregateTask";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final PlanModel model;
	private final AnalysisModel am;
	
	public AggregateTask(PlanModel mdl,AnalysisModel analysisModel) {
		this.model = mdl;
		this.am = analysisModel;
	}
	
	@Override
	protected List<PlanFeature> call()  {
		List<PlanFeature> attributes = new ArrayList<>();
		DatasetModel boundaryDataset = model.getBoundary();
		// Populate attributes for each feature
		String idName = Database.getInstance().getAttributeAliasTable().nameForAlias(boundaryDataset.getId(), StandardAttributes.ID.name());
		String geoName = Database.getInstance().getAttributeAliasTable().nameForAlias(boundaryDataset.getId(), StandardAttributes.GEOMETRY.name());

		int count = boundaryDataset.getFeatures().getFeatures().size();
		int index = 1;
		for(Feature feat:boundaryDataset.getFeatures().getFeatures()) {
			this.updateProgress(index, count);
			PlanFeature attribute = new PlanFeature(model.getId(),feat.getID());
			if(idName!=null) attribute.setName(feat.getString(idName).toString());
			this.updateMessage("Aggregating district "+attribute.getName());
			if(geoName!=null) {
				Geometry geometry = (Geometry)(feat.getAttribute(geoName));
				attribute.setArea(geometry.getArea());
				attribute.setPerimeter(geometry.getLength());
				aggregateAffiliations(attribute, geometry,am);
				aggregateDemographics(attribute, geometry,am);
				aggregateCountyBoundaries(attribute,geometry,am);
			}
			attributes.add(attribute);
			index++;
		}
		LOGGER.info(String.format("%s.call: Complete, returned %d attributes",CLSS,attributes.size()));
		return attributes;
	}
	/**
	 * Augment a single polygon with values from an affiliation dataset.
	 */
	public void aggregateAffiliations(PlanFeature planFeat, Geometry polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getAffiliationId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			int count = 0;
			for(Feature feat:fc.getFeatures()) {
				Geometry geometry = (Geometry)(feat.getAttribute(am.getAffiliationGeometryName()));
				Geometries type = Geometries.get(geometry);
				// If the shapes don't intersect, ignore.
				if( geometry.disjoint(polygon)) {
					continue; 
				}
				if( !type.equals(Geometries.POLYGON) && !type.equals(Geometries.MULTIPOLYGON))  {
					LOGGER.warning(String.format("%s.aggregateAffiliations: Geometry not a polygon (%s)", CLSS,type.getName()));
					continue;
				}
				double areaRatio = 1.0;
				try {
					Geometry intersect = polygon.intersection(geometry);
					if( intersect!=null && intersect.getEnvelope()!=null && !intersect.isEmpty() ) {
						areaRatio = intersect.getArea() / geometry.getArea();
					}
					else {
						areaRatio = 0.;
					}
				}
				catch(Exception ex) {
					LOGGER.warning(String.format("%s.aggregateAffiliations: Intersect exception (%s)", CLSS,ex.getLocalizedMessage()));
				}
				long increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForDemocrat()));
				planFeat.incrementDemocrat(areaRatio*increment);
				increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForRepublican()));
				planFeat.incrementRepublican(areaRatio*increment);
				count++;
			}
			LOGGER.info(String.format("%s.aggregateAffiliations: %d features of %d intersect", CLSS,count,fc.getFeatures().size()));
		}
	}

	/**
	 * Augment a single polygon with values from an demographic dataset.
	 * NOTE: We have seen datasets with no apparent total population. In this case
	 * 		 simply add male and female.
	 */
	public void aggregateDemographics(PlanFeature planFeat, Geometry polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getDemographicId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			int count = 0;
			for(Feature feat:fc.getFeatures()) {
				Geometry geometry = (Geometry)(feat.getAttribute(am.getDemographicGeometryName()));
				Geometries type = Geometries.get(geometry);
				if( geometry.disjoint(polygon)) {
					continue;
				}
				if( !type.equals(Geometries.POLYGON) && !type.equals(Geometries.MULTIPOLYGON)) {
					LOGGER.warning(String.format("%s.aggregateDemographics: Geometry not a polygon (%s)", CLSS,type.getName()));
					continue;
				}
				double areaRatio = 1.0;
				try {
					Geometry intersect = polygon.intersection(geometry);
					if( intersect!=null && intersect.getEnvelope()!=null && !intersect.isEmpty() ) {
						areaRatio = intersect.getArea() / geometry.getArea();
					}
					else {
						areaRatio = 0.;
					}
				}
				catch(Exception ex) {
					LOGGER.warning(String.format("%s.aggregateDemographics: Intersect exception (%s)", CLSS,ex.getLocalizedMessage()));
				}

				long increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForBlack()));
				planFeat.incrementBlack(areaRatio*increment);
				increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForHispanic()));
				planFeat.incrementHispanic(areaRatio*increment);
				increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForWhite()));
				planFeat.incrementWhite(areaRatio*increment);
				if(am.getAttributeForPopulation()==null) { // Use male+female
					increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForFemale()));
					planFeat.incrementPopulation(areaRatio*increment);
					increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForMale()));
					planFeat.incrementPopulation(areaRatio*increment);
				}
				else {
					increment = FeatureUtil.castToLong(feat.getAttribute(am.getAttributeForPopulation()));
					planFeat.incrementPopulation(areaRatio*increment);
				}
				count++;
			}
			LOGGER.info(String.format("%s.aggregateDemographics: %d features of %d intersect", CLSS,count,fc.getFeatures().size()));
		}
	}	
	/**
	 * Augment a single polygon (district) with values from a county (or municipal) boundaries dataset.
	 * Simply sum the number of times there is a total or partial mapping.
	 * @param planFeature a feature representing a district
	 * @param polygon an area representing a county
	 * @param am the model holding the metrics
	 */
	public void aggregateCountyBoundaries(PlanFeature planFeat, Geometry polygon,AnalysisModel am) {
		if( am == null ) return;
		DatasetModel dm = DatasetCache.getInstance().getDataset(am.getCountyBoundariesId());
		if( dm!=null ) {
			FeatureCollection fc = dm.getFeatures();
			if( fc==null ) return;
			int count = 0;
			for(Feature feat:fc.getFeatures()) {
				Geometry geometry = (Geometry)(feat.getAttribute(am.getCountyGeometryName()));
				Geometries type = Geometries.get(geometry);
				// Disjoint means there is no overlap.
				if( geometry.disjoint(polygon)) {
					continue;
				}
				if( !type.equals(Geometries.POLYGON) && !type.equals(Geometries.MULTIPOLYGON)) {
					LOGGER.warning(String.format("%s.aggregateCountyBoundaries: Geometry not a polygon (%s)", CLSS,type.getName()));
					continue;
				}
				// It's not disjoint so it shares area.
				planFeat.incrementCrossings(1.);
				count++;
			}
			LOGGER.info(String.format("%s.aggregateCountyBoundaries: %d features of %d intersect", CLSS,count,fc.getFeatures().size()));
		}
	}	
}

