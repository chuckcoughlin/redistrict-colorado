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
 * Default implementation of LinePlacement.
 *
 * @author Ian Turton, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class LinePlacement implements Cloneable {
	private final static String CLSS = "LinePlacement";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private double perpendicularOffset;
    private boolean generalized;
    private boolean aligned;
    private boolean repeated;
    private double gap;
    private double initialGap;

    public LinePlacement() {
    }

    public LinePlacement(LinePlacement placement) {
        this.gap = placement.getGap();
        this.initialGap = placement.getInitialGap();
        this.generalized = placement.isGeneralizeLine();
        this.perpendicularOffset = placement.getPerpendicularOffset();
        this.repeated = placement.isRepeated();
        this.aligned = placement.isAligned();
    }
    public LinePlacement(
            boolean aligned,
            boolean repeated,
            boolean generalized,
            double gap,
            double initialGap) {
        this.gap = gap;
        this.initialGap = initialGap;
        this.generalized = generalized;
        this.aligned = aligned;
        this.repeated = repeated;
        init();
    }

    /** Creates a new instance of DefaultLinePlacement */
    private void init() {
        perpendicularOffset = 0.;
    }

    /**
     * Getter for property perpendicularOffset.
     *
     * @return Value of property perpendicularOffset.
     */
    public double getPerpendicularOffset() {
        return perpendicularOffset;
    }

    /**
     * Setter for property perpendicularOffset.
     *
     * @param perpendicularOffset New value of property perpendicularOffset.
     */
    public void setPerpendicularOffset(double perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public double getInitialGap() {
        return initialGap;
    }

    public double getGap() {
        return gap;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public boolean isAligned() {
        return aligned;
    }

    public boolean isGeneralizeLine() {
        return generalized;
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see Cloneable#clone()
     */
    public Object clone() {
    	LinePlacement clone = new LinePlacement();
    	return clone;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof LinePlacement) {
            LinePlacement other = (LinePlacement) obj;

            return Utilities.equals(perpendicularOffset, other.perpendicularOffset)
                    && Utilities.equals(repeated, other.repeated)
                    && Utilities.equals(generalized, other.generalized)
                    && Utilities.equals(aligned, other.aligned)
                    && Utilities.equals(initialGap, other.initialGap)
                    && Utilities.equals(gap, other.gap);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 37;
        int result = 17;

        result = (result * PRIME) + (int)perpendicularOffset;
        result = (result * PRIME) + (int)gap;
        result = (result * PRIME) + (int)initialGap;
        result = (result * PRIME) + Boolean.valueOf(generalized).hashCode();
        result = (result * PRIME) + Boolean.valueOf(aligned).hashCode();
        result = (result * PRIME) + Boolean.valueOf(repeated).hashCode();

        return result;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public void setGeneralized(boolean generalized) {
        this.generalized = generalized;
    }

    public void setAligned(boolean aligned) {
        this.aligned = aligned;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public void setInitialGap(double initialGap) {
        this.initialGap = initialGap;
    }
}
