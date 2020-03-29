/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

/*
 * Hold a name-value pair
 */
public class NameValue {
	private final String name;
	private double value;
	public static NameValue EMPTY = new NameValue("",0.);
	
	public NameValue(String name,double value) {
		this.name = name;
		this.value = value;
	}
	public String getName() { return this.name; }
	public double getValue() { return this.value; }
	
}
