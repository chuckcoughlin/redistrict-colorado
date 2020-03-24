/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.List;

import javafx.scene.paint.Color;

/**
 * A Plan is a re-districting strategy. It is based on a BOUNDARIES dataset. 
 * Its feature attributes (metrics) depend on the current analyzer configuration.
 */
public class PlanModel {
	private final long id;
	private String description;
	private String name;
	private Color fill;
	private boolean active;
	private DatasetModel boundary;
	private List<PlanFeature> metrics;
	
	public PlanModel(long id) {
		this.id = id;
		this.active = true;
		this.boundary = null;
		this.fill = Color.BLACK;
		this.metrics = null;
		this.name = "";
		this.description="";
	}
	
	public long getId() { return this.id; }
	public boolean isActive() { return this.active; }
	public DatasetModel getBoundary() { return boundary; }
	public List<PlanFeature> getMetrics() { return metrics; }
	public String getDescription() { return this.description; }
	public Color getFill() { return this.fill; }
	public String getName() { return this.name; }
	
	public void setActive(boolean flag) { this.active = flag; }
	public void setBoundary(DatasetModel bound) { this.boundary = bound; }
	public void setDescription(String desc) { this.description = desc; }
	public void setFill(Color color) { this.fill = color; }
	public void setMetrics(List<PlanFeature> list) { this.metrics = list; }
	public void setName(String nam) { this.name = nam; }
}
