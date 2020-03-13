/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.List;

/**
 * A Plan is a comprehensive re-districting strategy. It is based on a BOUNDARIES
 * dataset. Its feature attributes depend on the current analyzer configuration.
 */
public class PlanModel {
	private final long id;
	private String name;
	private boolean active;
	private DatasetModel boundary;
	private List<PlanFeature> metrics;
	
	public PlanModel(long id,String nam) {
		this.id = id;
		this.name = nam;
		this.active = true;
		this.boundary = null;
		this.metrics = null;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public boolean isActive() { return this.active; }
	public DatasetModel getBoundary() { return boundary; }
	public List<PlanFeature> getMetrics() { return metrics; }
	
	public void setActive(boolean flag) { this.active = flag; }
	public void setName(String nam) { this.name = nam; }
	public void setBoundary(DatasetModel bound) { this.boundary = bound; }
	public void setMetrics(List<PlanFeature> list) { this.metrics = list; }
}
