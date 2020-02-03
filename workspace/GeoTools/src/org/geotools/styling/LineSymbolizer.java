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
 * Provides a representation of a LineSymbolizer in an SLD document. A LineSymbolizer defines how a
 * line geometry should be rendered.
 *
 * @author James Macgill
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class LineSymbolizer extends BasicSymbolizer {

    private double offset = 0.;
    private Stroke stroke = null;

    public LineSymbolizer() {
    	super();
    }
    public LineSymbolizer(Stroke stroke,
            double offset,
            Unit<Length> uom,
            Geometry geom,
            String name,
            String desc) {
        super(name, desc, geom, uom);
    }

    public double getPerpendicularOffset() {
        return offset;
    }

    public void setPerpendicularOffset(double offset) {
        this.offset = offset;
    }

    /**
     * Provides the graphical-symbolization parameter to use for the linear geometry.
     *
     * @return The Stroke style to use when rendering lines.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Sets the graphical-symbolization parameter to use for the linear geometry.
     *
     * @param s The stroke style to use when rendering lines.
     */
    public void setStroke(Stroke s) {
        this.stroke = s;
    }

    /**
     * Accepts a StyleVisitor to perform some operation on this LineSymbolizer.
     *
     * @param visitor The visitor to accept.
     */
    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone.
     * @return The deep copy clone.
     */
    public Object clone() {
        LineSymbolizer clone = (LineSymbolizer) super.clone();
        clone.setPerpendicularOffset(getPerpendicularOffset());
        clone.setStroke(getStroke());
        return clone;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<LineSymbolizerImp property=");
        buf.append(getGeometry().toString());
        buf.append(" uom=");
        buf.append(unitOfMeasure);
        buf.append(" stroke=");
        buf.append(stroke);
        buf.append(">");
        return buf.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int)offset;
        result = prime * result + ((stroke == null) ? 0 : stroke.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        LineSymbolizer other = (LineSymbolizer) obj;
        if (other.offset != offset) return false;
        if (stroke == null) {
            if (other.stroke != null) return false;
        } else if (!stroke.equals(other.stroke)) return false;
        return true;
    }
}
