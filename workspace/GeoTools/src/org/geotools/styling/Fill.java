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
import org.geotools.util.Utilities;

/**
 * @version $Id$
 * @author James Macgill, CCG
 */
public class Fill implements  Cloneable {
    private Color color = null;
    private double opacity = Double.NaN;
    private Graphic graphicFill = null;

    /** Creates a new instance of Fill */
    public Fill() {
    }

    /**
     * This parameter gives the solid color that will be used for a Fill.<br>
     * The color value is RGB-encoded using two hexidecimal digits per primary-color component, in
     * the order Red, Green, Blue, prefixed with the hash (#) sign. The hexidecimal digits between A
     * and F may be in either upper or lower case. For example, full red is encoded as "#ff0000"
     * (with no quotation marks). The default color is defined to be 50% gray ("#808080").
     *
     * <p>Note: in CSS this parameter is just called Fill and not Color.
     *
     * @return The color of the Fill encoded as a hexidecimal RGB value.
     */
    public Color getColor() {
        return color;
    }

    /**
     * This parameter gives the solid color that will be used for a Fill.<br>
     * The color value is RGB-encoded using two hexidecimal digits per primary-color component, in
     * the order Red, Green, Blue, prefixed with the hash (#) sign. The hexidecimal digits between A
     * and F may be in either upper or lower case. For example, full red is encoded as "#ff0000"
     * (with no quotation marks).
     *
     * <p>Note: in CSS this parameter is just called Fill and not Color.
     *
     * @param rgb The color of the Fill encoded as a hexidecimal RGB value.
     */
    public void setColor(Color rgb) {
        color = rgb;
    }

    public void setColor(String rgb) {
        if (color.toString().equals(rgb)) return;
        Color clr = Color.decode(rgb);
        this.color = clr;
    }

    /**
     * This specifies the level of translucency to use when rendering the fill. <br>
     * The value is encoded as a floating-point value between 0.0 and 1.0 with 0.0 representing
     * totally transparent and 1.0 representing totally opaque, with a linear scale of translucency
     * for intermediate values.<br>
     * For example, "0.65" would represent 65% opacity. The default value is 1.0 (opaque).
     *
     * @return The opacity of the fill, where 0.0 is completely transparent and 1.0 is completely
     *     opaque.
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Setter for property opacity.
     *
     * @param opacity New value of property opacity.
     */
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public void setOpacity(String op) {
    	try {
    		double val = Double.valueOf(op);
    		this.opacity = val;
    	}
    	catch(NumberFormatException nfe) {
    		
    	}
    }

    /**
     * This parameter indicates that a stipple-fill repeated graphic will be used and specifies the
     * fill graphic to use.
     *
     * @return graphic The graphic to use as a stipple fill. If null then no Stipple fill should be
     *     used.
     */
    public Graphic getGraphicFill() {
        return graphicFill;
    }

    /**
     * Setter for property graphic.
     *
     * @param graphicFill New value of property graphic.
     */
    public void setGraphicFill(Graphic graphic) {
        this.graphicFill = graphic;
    }


    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns a clone of the FillImpl.
     *
     * @see org.geotools.styling.Fill#clone()
     */
    public Object clone() {
        Fill clone = new Fill();
        clone.setColor(getColor());
        clone.setOpacity(getOpacity());
        clone.setGraphicFill(getGraphicFill());
        return clone;
    }

    /**
     * Generates a hashcode for the FillImpl.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (color != null) {
            result = PRIME * result + color.hashCode();
        }
        
        if (graphicFill != null) {
            result = PRIME * result + graphicFill.hashCode();
        }

        return result;
    }

    /**
     * Compares a FillImpl with another for equality.
     *
     * <p>Two FillImpls are equal if they contain the same, color, backgroundcolor, opacity and
     * graphicFill.
     *
     * @param oth The other FillImpl
     * @return True if this FillImpl is equal to oth.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth instanceof Fill) {
            Fill other = (Fill) oth;
            return Utilities.equals(this.color, other.color)
                    && Utilities.equals(this.opacity, other.opacity)
                    && Utilities.equals(this.graphicFill, other.graphicFill);
        }
        return false;
    }
}
