/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import org.openjump.feature.FeatureCollection;

/**
 * A Layer is on overlay on the map. It must be one of two types:
 * - Google map
 * - Shapefile
 */
public class LayerModel{
	private final long id;
	private String name;
	private String description;
	private String shapefilePath;
	private LayerRole role;
	private FeatureCollection features;
	
	public LayerModel(long id,String nam) {
		this.id = id;
		this.name = nam;
		this.description = "";
		this.shapefilePath = "";
		this.role = LayerRole.BOUNDARIES;
		this.features = null;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getShapefilePath() { return this.shapefilePath; }
	public LayerRole getRole() { return this.role; }
	public FeatureCollection getFeatures() { return this.features; }
	public void setName(String nam) { this.name = nam; }
	public void setDescription(String desc) { this.description = desc; }
	public void setShapefilePath(String path) { this.shapefilePath = path; }
	public void setRole(LayerRole r) { this.role = r; }
	public void setFeatures(FeatureCollection fc) { this.features = fc; }
}
