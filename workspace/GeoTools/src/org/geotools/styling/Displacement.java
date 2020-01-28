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
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class Displacement implements Cloneable {
	private final static String CLSS = "Displacement";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private double displacementX = 0.;
    private double displacementY = 0.;

    public Displacement() {
    }

    public Displacement(double dx, double dy) {
        displacementX = dx;
        displacementY = dy;
    }


    /**
     * Setter for property displacementX.
     *
     * @param displacementX New value of property displacementX.
     */
    public void setDisplacementX(double displacementX) {
        this.displacementX = displacementX;
    }
    /**
     * Setter for property displacementY.
     *
     * @param displacementY New value of property displacementY.
     */
    public void setDisplacementY(double displacementY) {
        this.displacementY = displacementY;
    }

    /**
     * Getter for property displacementX.
     *
     * @return Value of property displacementX.
     */
    public double getDisplacementX() {
        return displacementX;
    }
    
    public void accept(StyleVisitor visitor) {
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
        result = (result * PRIME) + (int)displacementX;
        result = (result * PRIME) + (int)displacementY;

        return result;
    }

}
