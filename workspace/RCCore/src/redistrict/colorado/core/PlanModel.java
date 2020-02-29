/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A Plan is a comprehensive re-districting strategy. It consists of a list of
 * Layers in various roles.
 */
public class PlanModel {
	private final long id;
	private String name;
	private String description;
	private boolean active;
	private List<PlanLayer> layers;
	
	public PlanModel(long id,String nam) {
		this.id = id;
		this.name = nam;
		this.description = "";
		this.active = true;
		this.layers = null;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public boolean isActive() { return this.active; }
	public List<PlanLayer> getLayers() { return layers; }
	
	public void setActive(boolean flag) { this.active = flag; }
	public void setName(String nam) { this.name = nam; }
	public void setDescription(String desc) { this.description = desc; }
	public void setLayers(List<PlanLayer> list) { this.layers = list; }
}
