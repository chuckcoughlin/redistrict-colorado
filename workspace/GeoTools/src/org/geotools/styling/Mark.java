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

/**
 * Default implementation of Mark.
 *
 * @author Ian Turton, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class Mark extends Graphic implements Cloneable {
	private final static String CLSS = "Mark";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private Fill fill;
    private Stroke stroke;

    private ExternalMark external;
    private String wellKnownName = null;

    /** Creates a new instance of DefaultMark */
    public Mark() {
    }

    public Mark(String name) {
        LOGGER.fine("creating " + name + " type mark");
        setWellKnownName(name);
    }

    public Mark(ExternalMark external) {
        LOGGER.fine("creating defaultMark");

        fill = new Fill();
        stroke = new Stroke();
        wellKnownName = "square";
        this.external = new ExternalMark();
    }

    /**
     * This parameter defines which fill style to use when rendering the Mark.
     *
     * @return the Fill definition to use when rendering the Mark.
     */
    public Fill getFill() {
        return fill;
    }

    /**
     * This paramterer defines which stroke style should be used when rendering the Mark.
     *
     * @return The Stroke definition to use when rendering the Mark.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * This parameter gives the well-known name of the shape of the mark.<br>
     * Allowed names include at least "square", "circle", "triangle", "star", "cross" and "x" though
     * renderers may draw a different symbol instead if they don't have a shape for all of these.
     * <br>
     *
     * @return The well-known name of a shape. The default value is "square".
     */
    public String getWellKnownName() {
        return wellKnownName;
    }

    /**
     * Setter for property fill.
     *
     * @param fill New value of property fill.
     */
    public void setFill(Fill fill) {
        this.fill = fill;
    }

    /**
     * Setter for property stroke.
     *
     * @param stroke New value of property stroke.
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Setter for property wellKnownName.
     *
     * @param wellKnownName New value of property wellKnownName.
     */
    public void setWellKnownName(String name) {
        this.wellKnownName = name;
    }

    public String toString() {
        return wellKnownName;
    }


    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy of the Mark.
     *
     * <p>Only the fill and stroke are cloned since Expressions should be immutable.
     *
     * @see org.geotools.styling.Mark#clone()
     */
    public Object clone() {
        Mark clone = new Mark();
       if (fill != null) {
           clone.fill = (Fill)(fill.clone());
       }
       if (stroke != null ) {
           clone.stroke = (Stroke) (stroke.clone());
       }
       return clone;
    }

    /**
     * The hashcode override for the MarkImpl.
     *
     * @return the Hashcode.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (fill != null) {
            result = (PRIME * result) + fill.hashCode();
        }

        if (stroke != null) {
            result = (PRIME * result) + stroke.hashCode();
        }

        if (wellKnownName != null) {
            result = (PRIME * result) + wellKnownName.hashCode();
        }

        return result;
    }

    /**
     * Compares this MarkImpl with another for equality.
     *
     * <p>Two MarkImpls are equal if they have the same well Known Name, the same size and rotation
     * and the same stroke and fill.
     *
     * @param oth The Other MarkImpl to compare with.
     * @return True if this and oth are equal.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        Mark other = (Mark) oth;

        // check expressions first - easiest
        if (this.wellKnownName == null) {
            if (other.wellKnownName != null) {
                return false;
            }
        } else {
            if (!this.wellKnownName.equals(other.wellKnownName)) {
                return false;
            }
        }

        if (this.fill == null) {
            if (other.fill != null) {
                return false;
            }
        } else {
            if (!this.fill.equals(other.fill)) {
                return false;
            }
        }

        if (this.stroke == null) {
            if (other.stroke != null) {
                return false;
            }
        } else {
            if (!this.stroke.equals(other.stroke)) {
                return false;
            }
        }

        return true;
    }

    public ExternalMark getExternalMark() {
        return external;
    }

    public void setExternalMark(ExternalMark external) {
        this.external = external;
    }
}
