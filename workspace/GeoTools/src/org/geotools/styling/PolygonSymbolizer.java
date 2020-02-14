/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.styling;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.locationtech.jts.geom.Geometry;

/**
 * Provides a representation of a PolygonSymbolizer in an SLD Document. A PolygonSymbolizer defines
 * how a polygon geometry should be rendered.
 *
 * @author James Macgill, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class PolygonSymbolizer extends BasicSymbolizer implements Cloneable {

    private double offset;
    private Displacement disp;
    private Fill fill = new Fill();
    private Stroke stroke = new Stroke();

    /** Creates a new instance of DefaultPolygonStyler */
    public PolygonSymbolizer() {
    }

    protected PolygonSymbolizer(
            Stroke stroke,
            Fill fill,
            Displacement dsp,
            double offset,
            Unit<Length> uom,
            Geometry geom,
            String name,
            String desc) {
        super(name, desc, geom, uom);
        this.stroke = stroke;
        this.fill = fill;
        this.disp = dsp;
        this.offset = offset;
    }

    public double getPerpendicularOffset() {
        return offset;
    }

    public void setPerpendicularOffset(double offset) {
        this.offset = offset;
    }

    public Displacement getDisplacement() {
        return disp;
    }

    public void setDisplacement(Displacement displacement) {
        this.disp = displacement;
    }
    /**
     * Provides the graphical-symbolization parameter to use to fill the area of the geometry.
     *
     * @return The Fill style to use when rendering the area.
     */
    public Fill getFill() {
        return fill;
    }

    /**
     * Sets the graphical-symbolization parameter to use to fill the area of the geometry.
     *
     * @param fill The Fill style to use when rendering the area.
     */
    public void setFill(Fill fill) {
        this.fill = fill;
    }

    /**
     * Provides the graphical-symbolization parameter to use for the outline of the Polygon.
     *
     * @return The Stroke style to use when rendering lines.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Sets the graphical-symbolization parameter to use for the outline of the Polygon.
     *
     * @param stroke The Stroke style to use when rendering lines.
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Accepts a StyleVisitor to perform some operation on this PolygonSymbolizer.
     *
     * @param visitor The visitor to accept.
     */
    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone. TODO: Need to complete the deep copy, currently only shallow copy.
     *
     * @return The deep copy clone.
     */
    public Object clone() {
    	PolygonSymbolizer clone = new PolygonSymbolizer();
    	if (fill != null) {
    		clone.fill = (Fill) (fill.clone());
    	}
    	if (stroke != null) {
    		clone.stroke = (Stroke) (stroke.clone());
    	}
    	return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((disp == null) ? 0 : disp.hashCode());
        result = prime * result + ((fill == null) ? 0 : fill.hashCode());
        result = prime * result + (int)offset;
        result = prime * result + ((stroke == null) ? 0 : stroke.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        PolygonSymbolizer other = (PolygonSymbolizer) obj;
        if (disp == null) {
            if (other.disp != null) return false;
        } else if (!disp.equals(other.disp)) return false;
        if (fill == null) {
            if (other.fill != null) return false;
        } else if (!fill.equals(other.fill)) return false;
        if( offset!=other.offset ) return false;
        if (stroke == null) {
            if (other.stroke != null) return false;
        } else if (!stroke.equals(other.stroke)) return false;
        return true;
    }
}
