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

	private double maxDemocrat;
	private double maxRepublican;
	private double minWhite;
	private double maxWhite;
	
	public PlanModel(long id) {
		this.id = id;
		this.active = true;
		this.boundary = null;
		this.fill = Color.BLACK;
		this.metrics = null;
		this.name = "";
		this.description="";
		// Initialze the bounds to an illegal value
		this.maxRepublican = -1.;
		this.maxDemocrat = -1.;
		this.minWhite = -1.;
		this.maxWhite = -1.;
	}
	
	public long getId() { return this.id; }
	public boolean isActive() { return this.active; }
	public DatasetModel getBoundary() { return boundary; }
	public List<PlanFeature> getMetrics() { return metrics; }
	public String getDescription() { return this.description; }
	public Color getFill() { return this.fill; }
	public String getName() { return this.name; }
	public double getMaxRepublican() {
		if(maxRepublican<0.) maxRepublican = computeMaxRepublican();
		return maxRepublican;
	}
	public double getMaxDemocrat() {
		if(maxDemocrat<0.) maxDemocrat = computeMaxDemocrat();
		return maxDemocrat;
	}
	public double getMinWhite() {
		if(minWhite<0.) minWhite = computeMinWhite();
		return minWhite;
	}
	public double getMaxWhite() {
		if(maxWhite<0.) maxWhite = computeMaxWhite();
		return maxWhite;
	}
	
	public void setActive(boolean flag) { this.active = flag; }
	public void setBoundary(DatasetModel bound) { this.boundary = bound; }
	public void setDescription(String desc) { this.description = desc; }
	public void setFill(Color color) { this.fill = color; }
	public void setMetrics(List<PlanFeature> list) { this.metrics = list; }
	public void setName(String nam) { this.name = nam; }
	
	// return the greatest fraction of republicans
	private double computeMaxRepublican() {
		double ans = -1;
		if(metrics!=null && metrics.size()>0) {
			PlanFeature first = metrics.get(0);
			ans = first.getRepublican()/(first.getDemocrat()+first.getRepublican());
			for(PlanFeature feat:metrics) {
				double total = feat.getDemocrat()+feat.getRepublican();
				if( feat.getRepublican()/total>ans) ans = feat.getRepublican()/total;
			}
		}
		return ans;
	}
	// return the largest fraction of democrats
	private double computeMaxDemocrat() {
		double ans = -1;
		if(metrics!=null && metrics.size()>0) {
			PlanFeature first = metrics.get(0);
			ans = first.getDemocrat()/(first.getDemocrat()+first.getRepublican());
			for(PlanFeature feat:metrics) {
				double total = feat.getDemocrat()+feat.getRepublican();
				if( feat.getDemocrat()/total>ans) ans = feat.getDemocrat()/total;
			}
		}
		return ans;
	}
	// return the smallest fraction of whites
	private double computeMinWhite() {
		double ans = -1;
		if(metrics!=null && metrics.size()>0) {
			PlanFeature first = metrics.get(0);
			ans = first.getWhite()/(first.getPopulation());
			for(PlanFeature feat:metrics) {
				double total = feat.getPopulation();
				if( feat.getWhite()/total<ans) ans = feat.getWhite()/total;
			}
		}
		return ans;
	}
	// return the largest fraction of non-whites
	private double computeMaxWhite() {
		double ans = -1;
		if(metrics!=null && metrics.size()>0) {
			PlanFeature first = metrics.get(0);
			ans = first.getWhite()/(first.getPopulation());
			for(PlanFeature feat:metrics) {
				double total = feat.getPopulation();
				if( feat.getWhite()/total>ans) ans = feat.getWhite()/total;
			}
		}
		return ans;
	}
}
