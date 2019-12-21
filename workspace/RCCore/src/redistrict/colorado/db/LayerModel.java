/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import redistrict.colorado.core.LayerRole;

/**
 * Encapsulate values from one row of the Layer table.
 */
public class LayerModel {
	private final long id;
	private String name;
	private String description;
	private int displayOrder;
	private String shapefilePath;
	private LayerRole role;
	
	LayerModel(long id,String nam) {
		this.id = id;
		this.name = nam;
		this.description = "";
		this.displayOrder = 0;
		this.shapefilePath = "";
		this.role = LayerRole.BOUNDARIES;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getShapefilePath() { return this.shapefilePath; }
	public int getDisplayOrder() { return this.displayOrder; }
	public LayerRole getRole() { return this.role; }
	public void setName(String nam) { this.name = nam; }
	public void setDescription(String desc) { this.description = desc; }
	public void setShapefilePath(String path) { this.shapefilePath = path; }
	public void setDisplayOrder(int order) { this.displayOrder = order; }
	public void setRole(LayerRole r) { this.role = r; }
}
