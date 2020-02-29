
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package redistrict.colorado.core;

/**
 *  Hold statistics for a plan-district. 
 */
public class Metrics  {
    private static final long serialVersionUID = -871137208054228529L;
    private final String featureId; // feature ID
    private final long planId; // Layer ID
    private double 	area = 0.;
    private double 	perimeter = 0.;
    private double 	population = 0.;
    private double 	democrat = 0.;
    private double 	republican = 0.;
    private double 	black = 0.;
    private double 	hispanic = 0.;
    private double 	white	 = 0.;

    /**
     * Define a layer within a plan.
     * @param pid plan Id
     * @param fid feature Id
     */
    public Metrics(long pid,String fid) {
    	this.planId = pid;
        this.featureId = fid;
    }
    

    public String getFeatureId() { return this.featureId; }
    public long getPlanId() { return this.planId; }
    public double getArea() { return this.area; }
    public double getPerimeter() { return this.perimeter; }
    public double getPopulation() { return this.	population; }
    public double getDemocrat() { return this.democrat; }
    public double getRepublican() { return this.republican; }
    public double getBlack() { return this.black; }
    public double getHispanic() { return this.hispanic ; }
    public double getWhite() { return this.white; }

    public void incrementPopulation(double val) { this.population += val; }
    public void incrementDemocrat(double val) { this.democrat += val; }
    public void incrementRepublican(double val) { this.republican += val; }
    public void incrementtBlack(double val) { this.black += val; }
    public void incrementHispanic(double val) { this.hispanic += val; }
    public void incrementWhite(double val) { this.white += val; }
    
    public void setArea(double val) { this.area = val; }
    public void setPerimeter(double val) { this.perimeter = val; }
    public void setPopulation(double val) { this.population = val; }
    public void setDemocrat(double val) { this.democrat = val; }
    public void setRepublican(double val) { this.republican = val; }
    public void setBlack(double val) { this.black = val; }
    public void setHispanic(double val) { this.hispanic = val; }
    public void setWhite(double val) { this.white = val; }
 }
