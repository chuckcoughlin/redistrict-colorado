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

import java.util.logging.Logger;

import org.geotools.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.style.StyleVisitor;

/**
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class Displacement implements Cloneable {
	private final static String CLSS = "Displacement";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private Expression displacementX = null;
    private Expression displacementY = null;

    public Displacement() {
    }

    public Displacement(Expression dx, Expression dy) {
        displacementX = dx;
        displacementY = dy;
    }


    /**
     * Setter for property displacementX.
     *
     * @param displacementX New value of property displacementX.
     */
    public void setDisplacementX(Expression displacementX) {
        this.displacementX = displacementX;
    }
    /**
     * Set displacement x to the provided literal.
     *
     * @param displacementX New value of property displacementX.
     */
    public void setDisplacementX(double displacementX) {
        this.displacementX = filterFactory.literal(displacementX);
    }
    /**
     * Setter for property displacementY.
     *
     * @param displacementY New value of property displacementY.
     */
    public void setDisplacementY(Expression displacementY) {
        this.displacementY = displacementY;
    }
    /**
     * Set displacement y to the provided literal.
     *
     * @param displacementY New value of property displacementX.
     */
    public void setDisplacementY(double displacementY) {
        this.displacementY = filterFactory.literal(displacementY);
    }

    /**
     * Getter for property displacementX.
     *
     * @return Value of property displacementX.
     */
    public Expression getDisplacementX() {
        return displacementX;
    }

    /**
     * Getter for property displacementY.
     *
     * @return Value of property displacementY.
     */
    public Expression getDisplacementY() {
        return displacementY;
    }

    public Object accept(StyleVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see org.opengis.util.Cloneable#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Will not happen");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Displacement) {
            Displacement other = (Displacement) obj;

            return Utilities.equals(displacementX, other.displacementX)
                    && Utilities.equals(displacementY, other.displacementY);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 37;
        int result = 17;

        if (displacementX != null) {
            result = (result * PRIME) + displacementX.hashCode();
        }

        if (displacementY != null) {
            result = (result * PRIME) + displacementY.hashCode();
        }

        return result;
    }

    static Displacement cast(org.opengis.style.Displacement displacement) {
        if (displacement == null) {
            return null;
        } else if (displacement instanceof Displacement) {
            return (Displacement) displacement;
        } else {
            Displacement copy = new Displacement();
            copy.setDisplacementX(displacement.getDisplacementX());
            copy.setDisplacementY(displacement.getDisplacementY());

            return copy;
        }
    }
}
