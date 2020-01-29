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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.geotools.util.Utilities;

/**
 * Direct implementation of Graphic.
 *
 * @author Ian Turton, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class Graphic implements Cloneable {
	private final static String CLSS = "Graphic";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private final List<Graphic> graphics = new ArrayList<>();

    private AnchorPoint anchor = null;
    private double gap = 0.;
    private double initialGap = 0.;

    private double rotation = 0.;
    private double size = 0.;
    private Displacement displacement = null;
    private double opacity = 0.;

    /** Creates a new instance of DefaultGraphic */
    protected Graphic() {
    }

    public Graphic( AnchorPoint anchor, double gap, double initialGap) {
        this.anchor = anchor;
        this.gap = gap;
        this.initialGap = initialGap;
    }

    public List<Graphic> graphicalSymbols() {
        return graphics;
    }

    public AnchorPoint getAnchorPoint() {
        return anchor;
    }

    public void setAnchorPoint(AnchorPoint anchorPoint) {
        this.anchor = anchorPoint;
    }

    /**
     * This specifies the level of translucency to use when rendering the graphic.<br>
     * The value is encoded as a floating-point value between 0.0 and 1.0 with 0.0 representing
     * totally transparent and 1.0 representing totally opaque, with a linear scale of translucency
     * for intermediate values.<br>
     * For example, "0.65" would represent 65% opacity. The default value is 1.0 (opaque).
     *
     * @return The opacity of the Graphic, where 0.0 is completely transparent and 1.0 is completely
     *     opaque.
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * This parameter defines the rotation of a graphic in the clockwise direction about its centre
     * point in decimal degrees. The value encoded as a floating point number.
     *
     * @return The angle of rotation in decimal degrees. Negative values represent counter-clockwise
     *     rotation. The default is 0.0 (no rotation).
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * This paramteter gives the absolute size of the graphic in pixels encoded as a floating point
     * number.
     *
     * <p>The default size of an image format (such as GIFD) is the inherent size of the image. The
     * default size of a format without an inherent size (such as SVG) is defined to be 16 pixels in
     * height and the corresponding aspect in width. If a size is specified, the height of the
     * graphic will be scaled to that size and the corresponding aspect will be used for the width.
     *
     * @return The size of the graphic, the default is context specific. Negative values are not
     *     possible.
     */
    public double getSize() {
        return size;
    }

    public Displacement getDisplacement() {
        return displacement;
    }

    public double getInitialGap() {
        return initialGap;
    }

    public void setInitialGap(double initialGap) {
        this.initialGap = initialGap;
    }

    public double getGap() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public void setDisplacement(Displacement disp) {
        this.displacement = disp;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    /**
     * Setter for property rotation.
     *
     * @param rotation New value of property rotation.
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * Setter for property size.
     *
     * @param size New value of property size.
     */
    public void setSize(double size) {
        this.size = size;
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone.
     *
     * @return The deep copy clone.
     */
    public Object clone() {
        Graphic clone = new Graphic();
        clone.graphics.clear();
        clone.graphics.addAll(graphics);
        return clone;
    }

    /**
     * Override of hashcode
     *
     * @return The hashcode.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (graphics != null) {
            result = (PRIME * result) + graphics.hashCode();
        }

        result = (PRIME * result) + (int)rotation;
        result = (PRIME * result) + (int)size;
        result = (PRIME * result) + (int)opacity;
        result = (PRIME * result) + (int)gap;
        result = (PRIME * result) + (int)initialGap;
        return result;
    }

    /**
     * Compares this GraphicImpl with another for equality.
     *
     * <p>Two graphics are equal if and only if they both have the same geometry property name and
     * the same list of symbols and the same rotation, size and opacity.
     *
     * @param oth The other GraphicsImpl to compare with.
     * @return True if this is equal to oth according to the above conditions.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth instanceof Graphic) {
            Graphic other = (Graphic) oth;

            return Utilities.equals(this.size, other.size)
                    && Utilities.equals(this.rotation, other.rotation)
                    && Utilities.equals(this.opacity, other.opacity)
                    && Objects.equals(this.graphicalSymbols(), other.graphicalSymbols());
        }

        return false;
    }
}
