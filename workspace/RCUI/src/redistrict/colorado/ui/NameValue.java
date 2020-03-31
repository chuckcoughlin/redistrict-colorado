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
public class NameValue {
	private final String name;
	private double mean;
	private double stdDeviation;
	public static NameValue EMPTY = new NameValue("",0.);
	
	public NameValue(String name,double value) {
		this.name = name;
		this.mean = value;
		this.stdDeviation = 0.;
	}
	public NameValue(String name,double ave,double dev) {
		this.name = name;
		this.mean = ave;
		this.stdDeviation = dev;
	}
	public String getName() { return this.name; }
	public double getMean() { return this.mean; }
	public double getStandardDeviation() { return this.stdDeviation; }
	public double getValue() { return this.mean; }
	
}
