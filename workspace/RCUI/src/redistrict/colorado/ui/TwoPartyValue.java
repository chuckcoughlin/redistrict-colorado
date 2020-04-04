/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

/*
 * Hold a name-value pair. It can be either a simple value, or a mean with
 * standard deviation, depending on the constructor. 
 */
public class TwoPartyValue {
	private final String name;
	private double democrat;
	private double republican;
	public static TwoPartyValue EMPTY = new TwoPartyValue("",0.,0.);
	
	public TwoPartyValue(String name,double dem,double rep) {
		this.name = name;
		this.democrat = dem;
		this.republican = rep;
	}

	public String getName() { return this.name; }
	public double getDemocrat() { return this.democrat; }
	public double getRepublican() { return this.republican; }

}
