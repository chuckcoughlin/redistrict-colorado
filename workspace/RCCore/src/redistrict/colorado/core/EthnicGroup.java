/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

/*
 * Population parameters for an ethnic group, 
 */
public class EthnicGroup {

	private final Ethnicity ethnicity;
	
	public EthnicGroup(Ethnicity eth)  {
		this.ethnicity = eth;
	}
	
	public Ethnicity getEthnicity() { return this.ethnicity; }
}
