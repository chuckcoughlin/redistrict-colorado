/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;

/**
 * The analysis model holds the current parameters for running the comparison.
 * Parameter values are stored in the Preferences table.
 */
public class AnalysisModel {
	private final long id;
	private long affiliationId;
	private long demographicId;
	private String affGeoName = null;
	private String blackName = null;
	private String demoGeoName = null;
	private String democratName = null;
	private String hispanicName = null;
	private String femaleName = null;
	private String maleName = null;
	private String populationName= null;
	private String republicanName= null;
	private String whiteName = null;
	
	public AnalysisModel(long id) {
		this.id = id;
		this.demographicId = -1;
		this.affiliationId = -1;
	}
	
	public long getAffiliationId() { return this.affiliationId; }
	public String getAffiliationGeometryName() { return this.affGeoName; }
	public long getDemographicId() { return this.demographicId; }
	public String getDemographicGeometryName() { return this.demoGeoName; }
	public String getAttributeForBlack() { return blackName; }
	public String getAttributeForDemocrat() { return democratName; }
	public String getAttributeForHispanic() { return hispanicName; }
	public String getAttributeForFemale() { return femaleName; }
	public String getAttributeForMale() { return maleName; }
	public String getAttributeForPopulation() { return populationName; }
	public String getAttributeForRepublican() { return republicanName; }
	public String getAttributeForWhite() { return whiteName; }
	
	// When we change the affiliations dataset, re-query for the alias names
	public void setAffiliationId(long aid) { this.affiliationId = aid; }
	public void setDemographicId(long did) { this.demographicId = did; }
	public void updateAffiliationFeatures() { 
		DatasetModel dm = DatasetCache.getInstance().getDataset(affiliationId);
		if(dm==null) return;
		this.affGeoName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.GEOMETRY.name());
		this.democratName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.DEMOCRAT.name());
		this.republicanName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.REPUBLICAN.name());
	}
	public void updateDemographicFeatures() {  
		DatasetModel dm = DatasetCache.getInstance().getDataset(demographicId);
		if(dm==null) return;
		this.demoGeoName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.GEOMETRY.name());
		this.blackName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.BLACK.name());
		this.hispanicName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.HISPANIC.name());
		this.femaleName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.FEMALE.name());
		this.maleName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.MALE.name());
		this.whiteName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.WHITE.name());
		this.populationName = Database.getInstance().getAttributeAliasTable().nameForAlias(dm.getId(), StandardAttributes.POPULATION.name());
	}
}
