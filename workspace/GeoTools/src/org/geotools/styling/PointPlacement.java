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

// OpenGIS dependencies

import org.geotools.util.Utilities;

/**
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class PointPlacement implements Cloneable {
	private final static String CLSS = "PointPlacement";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    // TODO: make container ready
    private AnchorPoint anchorPoint = new AnchorPoint();
    private Displacement displacement = new Displacement();
    private double rotation = 0.;

    public PointPlacement() {
    }


    /**
     * Returns the AnchorPoint which identifies the location inside a text label to use as an
     * "anchor" for positioning it relative to a point geometry.
     *
     * @return Label's AnchorPoint.
     */
    public AnchorPoint getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * Setter for property anchorPoint.
     *
     * @param anchorPoint New value of property anchorPoint.
     */
    public void setAnchorPoint(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    /**
     * Returns the Displacement which gives X and Y offset displacements to use for rendering a text
     * label near a point.
     *
     * @return The label displacement.
     */
    public Displacement getDisplacement() {
        return displacement;
    }

    /**
     * Setter for property displacement.
     *
     * @param displacement New value of property displacement.
     */
    public void setDisplacement(Displacement displacement) {
        this.displacement = displacement;
    }

    /**
     * Returns the rotation of the label.
     *
     * @return The rotation of the label.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Setter for property rotation.
     *
     * @param rotation New value of property rotation.
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see Cloneable#clone()
     */
    public Object clone() {
        PointPlacement clone = new PointPlacement();
        clone.anchorPoint = (AnchorPoint)(anchorPoint.clone());
        clone.displacement = (Displacement)(displacement.clone());
        return clone;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof PointPlacement) {
            PointPlacement other = (PointPlacement) obj;

            return Utilities.equals(anchorPoint, other.anchorPoint)
                    && Utilities.equals(displacement, other.displacement)
                    && Utilities.equals(rotation, other.rotation);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 37;
        int result = 17;

        if (anchorPoint != null) {
            result = (result * PRIME) + anchorPoint.hashCode();
        }

        if (displacement != null) {
            result = (result * PRIME) + displacement.hashCode();
        }
        result = (result * PRIME) + (int)rotation;
        return result;
    }
}
