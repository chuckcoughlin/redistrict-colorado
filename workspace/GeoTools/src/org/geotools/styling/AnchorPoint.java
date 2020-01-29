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

/**
 * Direct implementation of AnchorPoint.
 *
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class AnchorPoint implements Cloneable {
	private final static String CLSS = "AnchorPoint";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private double anchorPointX = 0.;
    private double anchorPointY = 0.;

    public AnchorPoint() {
    }

    public AnchorPoint(double x, double y) {
        anchorPointX = x;
        anchorPointY = y;
    }
    /**
     * Getter for property anchorPointX.
     *
     * @return Value of property anchorPointX.
     */
    public double getAnchorPointX() {
        return anchorPointX;
    }

    /**
     * Setter for property anchorPointX.
     *
     * @param anchorPointX New value of property anchorPointX.
     */
    public void setAnchorPointX(double anchorPointX) {
        this.anchorPointX = anchorPointX;
    }

    /**
     * Getter for property anchorPointY.
     *
     * @return Value of property anchorPointY.
     */
    public double getAnchorPointY() {
        return anchorPointY;
    }

    /**
     * Setter for property anchorPointY.
     *
     * @param anchorPointY New value of property anchorPointY.
     */
    public void setAnchorPointY(double anchorPointY) {
        this.anchorPointY = anchorPointY;
    }

    /**
     * Define the anchor point.
     *
     * @param x Literal value of property anchorPointX
     */
    public double getAnchorPointY(double x) {
        return this.anchorPointY;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see Cloneable#clone()
     */
    public Object clone() {
    	AnchorPoint clone = new AnchorPoint();
    	clone.anchorPointX = getAnchorPointX();
    	clone.anchorPointY = getAnchorPointY();
    	return clone;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AnchorPoint) {
            AnchorPoint other = (AnchorPoint) obj;

            return Utilities.equals(this.anchorPointX, other.anchorPointX)
                    && Utilities.equals(this.anchorPointY, other.anchorPointY);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 37;
        int result = 17;

        result = (result * PRIME) + (int)anchorPointX;
        result = (result * PRIME) + (int)anchorPointY;
        return result;
    }
}
