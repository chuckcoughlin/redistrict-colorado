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
 * Provides a Java representation of the PointSymbolizer. This defines how points are to be
 * rendered.
 *
 * @author Ian Turton, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class PointSymbolizer extends BasicSymbolizer implements Cloneable {

    private Graphic graphic = new Graphic();
    
    /** Creates a new instance of DefaultPointSymbolizer */
    public PointSymbolizer() {
        this(new Graphic(),null,null,null,"title:abstract");
    }

    protected PointSymbolizer(Graphic graphic, Unit<Length> uom, Geometry geom, String name, String desc) {
        super(name, desc, geom, uom);
        this.graphic = graphic;
    }

    /**
     * Provides the graphical-symbolization parameter to use for the point geometry.
     *
     * @return The Graphic to be used when drawing a point
     */
    public Graphic getGraphic() {
        return graphic;
    }

    /**
     * Setter for property graphic.
     *
     * @param graphic New value of property graphic.
     */
    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    /**
     * Accept a StyleVisitor to perform an operation on this symbolizer.
     *
     * @param visitor The StyleVisitor to accept.
     */
    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone.
     *
     * @return The deep copy clone.
     */
    public Object clone() {
        PointSymbolizer clone = new PointSymbolizer();
        if (graphic != null) clone.graphic = (Graphic) (graphic.clone());
        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((graphic == null) ? 0 : graphic.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        PointSymbolizer other = (PointSymbolizer) obj;
        if (graphic == null) {
            if (other.graphic != null) return false;
        } 
        else if (!graphic.equals(other.graphic)) return false;
        return true;
    }
}
