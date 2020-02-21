/**
 *  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.style;

import org.geotools.style.color.Colorizer;
import org.openjump.feature.Feature;

import javafx.scene.paint.Color;

/**
 * This is a holder for attributes used to decorate feature shapes
 * within a layer. It contains all possible attributes needed for the various
 * shapes. Not all attributes are used for all shapes.
 */
public class Style  {
    private Color lineColor = Color.BLACK;
    private Color fillColor = Color.ANTIQUEWHITE;
    private double lineWidth = 1.0;
    private Colorizer fillColorizer = null;
    
    /** 
     * Create a style instance appropriate for Polygons
     * @param lc boundary line color
     * @param lw boundary line width
     * @param fc fill color
     */
    public Style(Color lc, double lw, Color fc) {
    	this.lineColor = lc;
    	this.lineWidth=lw;
    	this.fillColor=fc;
    }
    public void  setFillColor(Color clr) { this.fillColor=clr; }
    public double getLineWidth() { return this.lineWidth;}
    public void setLineWidth(double dbl) { this.lineWidth=dbl; }    
    public Color getLineColor() { return this.lineColor;}
    public void setLineColor(Color clr) { this.lineColor=clr; }    
    public void setFillColorizer(Colorizer clrizer) { this.fillColorizer=clrizer; }
    
    public Color getFillColor(Feature feat) { 
    	Color fill = this.fillColor;
    	if(fillColorizer!=null) fill = fillColorizer.getFeatureFill(feat);
    	return fill;
    }
}
