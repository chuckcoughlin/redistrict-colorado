/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/**
 * The analysis model holds the current parameters for running the comparison.
 * Parameter values are stored in the Preferences table.
 */
public class AnalysisModel {
	private DatasetModel affiliations;
	private DatasetModel demographics;
	
	public AnalysisModel(long id) {
		this.affiliations = null;
		this.demographics = null;
	}
	
	public DatasetModel getAffiliations() { return this.affiliations; }
	public DatasetModel getDemographics() { return this.demographics; }
	
	public void setAffiliation(DatasetModel model) { this.affiliations = model; }
	public void setDemographics(DatasetModel model) { this.demographics = model; }
}
