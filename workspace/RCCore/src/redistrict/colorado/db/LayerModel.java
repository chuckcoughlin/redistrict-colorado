/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

/**
 * Encapsulate values from one row of the Layer table.
 */
public class LayerModel {
	private final long id;
	private String name;
	private String description;
	
	LayerModel(long id,String nam,String desc) {
		this.id = id;
		this.name = nam;
		this.description = desc;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public void setName(String nam) { this.name = nam; }
	public void setDescription(String desc) { this.description = desc; }
}
