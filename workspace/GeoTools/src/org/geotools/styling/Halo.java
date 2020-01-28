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

import java.awt.Color;
import java.util.logging.Logger;

// OpenGIS dependencies

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.Utilities;
import org.geotools.util.factory.GeoTools;
import org.opengis.filter.FilterFactory;
import org.opengis.style.StyleVisitor;

/**
 * Direct implementation of Halo.
 *
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class Halo implements Cloneable {
	private final static String CLSS = "Halo";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private Fill fill;
    private double radius = Double.NaN;

    /**
     * Cast to HaloImpl (creating a copy if needed).
     *
     * @param halo
     * @return HaloImpl equal to the provided halo
     */
    static Halo cast(org.opengis.style.Halo halo) {
        if (halo == null) {
            return null;
        } else if (halo instanceof Halo) {
            return (Halo) halo;
        } else {
            Halo copy = new Halo();
            copy.setFill(halo.getFill());
            copy.setRadius(halo.getRadius());

            return copy;
        }
    }

    public Halo() {
    	init();
    }

    private void init() {
       fill = new Fill();
       radius = 1.0;
       fill.setColor(Color.WHITE);
    }

    /**
     * Getter for property fill.
     *
     * @return Value of property fill.
     */
    public Fill getFill() { return fill;}

    /**
     * Setter for property fill.
     *
     * @param fill New value of property fill.
     */
    public void setFill(org.opengis.style.Fill fill) {
        this.fill = FillImpl.cast(fill);
    }

    /**
     * Getter for property radius.
     *
     * @return Value of property radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Setter for property radius.
     *
     * @param radius New value of property radius.
     */
    public void setRadius(double r) {
        this.radius = r;
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone of the Halo.
     *
     * @return The clone.
     */
    public Object clone() {
        try {
            Halo clone = (Halo) super.clone();
            clone.fill = (FillImpl) ((Cloneable) fill).clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This will never happen");
        }
    }

    /**
     * Compares this HaloImpl with another for equality.
     *
     * @param obj THe other HaloImpl.
     * @return True if they are equal. They are equal if their fill and radius is equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Halo) {
            Halo other = (Halo) obj;

            return Utilities.equals(radius, other.radius) && Utilities.equals(fill, other.fill);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 37;
        int result = 17;

        if (radius != null) {
            result = (result * PRIME) + radius.hashCode();
        }

        if (fill != null) {
            result = (result * PRIME) + fill.hashCode();
        }

        return result;
    }
}
