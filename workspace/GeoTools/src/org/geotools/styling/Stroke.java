/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2015, Open Source Geospatial Foundation (OSGeo)
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
import java.util.ArrayList;
import java.util.List;
import org.geotools.util.Utilities;

/**
 * Provides a Java representation of the Stroke object in an SLD document. A stroke defines how a
 * line is rendered.
 *
 * @author James Macgill, CCG
 * @version $Id$
 */
public class Stroke implements Cloneable {
    private Color color;
    private List<Float> dashArray;
    private double dashOffset;
    private Graphic fillGraphic;
    private Graphic strokeGraphic;
    private String lineCap;
    private String lineJoin;
    private double opacity;
    private double width;

    /** Creates a new instance of Stroke */
    public Stroke() {
    	dashArray = new ArrayList<>();
    }
    
    public Stroke(Color fill, double width) {
    	this.color = fill;
    	this.width = width;
    }

    /**
     * This parameter gives the solid color that will be used for a stroke.<br>
     * The color value is RGB-encoded using two hexidecimal digits per primary-color component in
     * the order Red, Green, Blue, prefixed with the hash (#) sign. The hexidecimal digits between A
     * and F may be in either upper or lower case. For example, full red is encoded as "#ff0000"
     * (with no quotation marks). The default color is defined to be black ("#000000"). Note: in CSS
     * this parameter is just called Stroke and not Color.
     *
     * @return The color of the stroke encoded as a hexidecimal RGB value.
     */
    public Color getColor() {
        return color;
    }

    /**
     * This parameter sets the solid color that will be used for a stroke.<br>
     * The color value is RGB-encoded using two hexidecimal digits per primary-color component in
     * the order Red, Green, Blue, prefixed with the hash (#) sign. The hexidecimal digits between A
     * and F may be in either upper or lower case. For example, full red is encoded as "#ff0000"
     * (with no quotation marks). The default color is defined to be black ("#000000"). Note: in CSS
     * this parameter is just called Stroke and not Color.
     *
     * @param color The color of the stroke encoded as a hexidecimal RGB value. This must not be
     *     null.
     */
    public void setColor(Color clr) {
        this.color = clr;
    }

    /**
     * Shortcut to retrieve dash array in the case where all expressions are literal numbers. Return
     * the default value if one of the expressions is not a literal.
     */
    public float[] getDashArray() {
        float[] values = new float[dashArray.size()];
        int index = 0;
        for( Float val : dashArray ) {
            values[index] = dashArray.get(index);
            index++;
        }
        return values;
    }

    /** Shortcut to define dash array using literal numbers. */
    public void setDashArray(float[] literalDashArray) {
        if (literalDashArray != null) {
            dashArray = new ArrayList<>(literalDashArray.length);
            for (float value : literalDashArray) {
                dashArray.add(value);
            }
        }
    }

    /**
     * This parameter encodes the dash pattern as a list of expressions.<br>
     * The first expression gives the length in pixels of the dash to draw, the second gives the
     * amount of space to leave, and this pattern repeats.<br>
     * If an odd number of values is given, then the pattern is expanded by repeating it twice to
     * give an even number of values.
     */
    public List<Float> dashArray() {
        return dashArray;
    }

    /**
     * This parameter encodes the dash pattern as a list of expressions.<br>
     * The first expression gives the length in pixels of the dash to draw, the second gives the
     * amount of space to leave, and this pattern repeats.<br>
     * If an odd number of values is given, then the pattern is expanded by repeating it twice to
     * give an even number of values.
     */
    public void setDashArray(List<Float> dashArray) {
        this.dashArray = dashArray;
    }

    /**
     * This param determines where the dash pattern should start from.
     *
     * @return where the dash should start from.
     */
    public double getDashOffset() {
        return dashOffset;
    }

    /**
     * This param determines where the dash pattern should start from.
     *
     * @param dashOffset The distance into the dash pattern that should act as the start.
     */
    public void setDashOffset(double dashOffset) {
        this.dashOffset = dashOffset;
    }

    /**
     * This parameter indicates that a stipple-fill repeated graphic will be used and specifies the
     * fill graphic to use.
     *
     * @return The graphic to use as a stipple fill. If null, then no Stipple fill should be used.
     */
    public Graphic getGraphicFill() {
        return fillGraphic;
    }

    /**
     * This parameter indicates that a stipple-fill repeated graphic will be used and specifies the
     * fill graphic to use.
     *
     * @param fillGraphic The graphic to use as a stipple fill. If null, then no Stipple fill should
     *     be used.
     */
    public void setGraphicFill(Graphic fillGraphic) {
        this.fillGraphic = fillGraphic;
    }

    /**
     * This parameter indicates that a repeated-linear-graphic graphic stroke type will be used and
     * specifies the graphic to use. Proper stroking with a linear graphic requires two "hot-spot"
     * points within the space of the graphic to indicate where the rendering line starts and stops.
     * In the case of raster images with no special mark-up, this line will be assumed to be the
     * middle pixel row of the image, starting from the first pixel column and ending at the last
     * pixel column.
     *
     * @return The graphic to use as a linear graphic. If null, then no graphic stroke should be
     *     used.
     */
    public Graphic getGraphicStroke() {
        return strokeGraphic;
    }

    /**
     * This parameter indicates that a repeated-linear-graphic graphic stroke type will be used and
     * specifies the graphic to use. Proper stroking with a linear graphic requires two "hot-spot"
     * points within the space of the graphic to indicate where the rendering line starts and stops.
     * In the case of raster images with no special mark-up, this line will be assumed to be the
     * middle pixel row of the image, starting from the first pixel column and ending at the last
     * pixel column.
     *
     * @param strokeGraphic The graphic to use as a linear graphic. If null, then no graphic stroke
     *     should be used.
     */
    public void setGraphicStroke(Graphic strokeGraphic) {
        this.strokeGraphic = strokeGraphic;
    }

    /**
     * This parameter controls how line strings should be capped.
     *
     * @return The cap style. This will be one of "butt", "round" and "square" There is no defined
     *     default.
     */
    public String getLineCap() {
        return lineCap;
    }

    /**
     * This parameter controls how line strings should be capped.
     *
     * @param lineCap The cap style. This can be one of "butt", "round" and "square" There is no
     *     defined default.
     */
    public void setLineCap(String lineCap) {
        this.lineCap = lineCap;
    }

    /**
     * This parameter controls how line strings should be joined together.
     *
     * @return The join style. This will be one of "mitre", "round" and "bevel". There is no defined
     *     default.
     */
    public String getLineJoin() {
        return lineJoin;
    }

    /**
     * This parameter controls how line strings should be joined together.
     *
     * @param lineJoin The join style. This will be one of "mitre", "round" and "bevel". There is no
     *     defined default.
     */
    public void setLineJoin(String lineJoin) {
        this.lineJoin = lineJoin;
    }

    /**
     * This specifies the level of translucency to use when rendering the stroke.<br>
     * The value is encoded as a floating-point value between 0.0 and 1.0 with 0.0 representing
     * totally transparent and 1.0 representing totally opaque. A linear scale of translucency is
     * used for intermediate values.<br>
     * For example, "0.65" would represent 65% opacity. The default value is 1.0 (opaque).
     *
     * @return The opacity of the stroke, where 0.0 is completely transparent and 1.0 is completely
     *     opaque.
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * This specifies the level of translucency to use when rendering the stroke.<br>
     * The value is encoded as a floating-point value between 0.0 and 1.0 with 0.0 representing
     * totally transparent and 1.0 representing totally opaque. A linear scale of translucency is
     * used for intermediate values.<br>
     * For example, "0.65" would represent 65% opacity. The default value is 1.0 (opaque).
     *
     * @param opacity The opacity of the stroke, where 0.0 is completely transparent and 1.0 is
     *     completely opaque.
     */
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    /**
     * This parameter gives the absolute width (thickness) of a stroke in pixels encoded as a float.
     * The default is 1.0. Fractional numbers are allowed but negative numbers are not.
     *
     * @return The width of the stroke in pixels. This may be fractional but not negative.
     */
    public double getWidth() {
        return width;
    }

    /**
     * This parameter sets the absolute width (thickness) of a stroke in pixels encoded as a float.
     * The default is 1.0. Fractional numbers are allowed but negative numbers are not.
     *
     * @param width The width of the stroke in pixels. This may be fractional but not negative.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    public String toString() {
        StringBuffer out = new StringBuffer("org.geotools.styling.StrokeImpl:\n");
        out.append("\tColor " + this.color + "\n");
        out.append("\tWidth " + this.width + "\n");
        out.append("\tOpacity " + this.opacity + "\n");
        out.append("\tLineCap " + this.lineCap + "\n");
        out.append("\tLineJoin " + this.lineJoin + "\n");
        out.append("\tDash Array " + this.dashArray + "\n");
        out.append("\tDash Offset " + this.dashOffset + "\n");
        out.append("\tFill Graphic " + this.fillGraphic + "\n");
        out.append("\tStroke Graphic " + this.strokeGraphic);

        return out.toString();
    }
/*
    public java.awt.Color getColor(SimpleFeature feature) {
        return java.awt.Color.decode((String) this.getColor().evaluate(feature));
    }
*/
    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Clone the StrokeImpl object.
     *
     * <p>The clone is a deep copy of the original, except for the expression values which are
     * immutable.
     *
     * @see org.geotools.styling.Stroke#clone()
     */
    public Object clone() {
    	Stroke clone = new Stroke();
    	if (dashArray != null) {
    		clone.setDashArray((new ArrayList<Float>(dashArray)));
    	}

    	if (fillGraphic != null ) {
    		clone.fillGraphic = (Graphic)(fillGraphic.clone());
    	}

    	if (strokeGraphic != null) {
    		clone.strokeGraphic = (Graphic)(strokeGraphic.clone());
    	}
    	return clone;
    }

    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (color != null) {
            result = (PRIME * result) + color.hashCode();
        }
        result = (PRIME * result) + (int)dashOffset;
        if (fillGraphic != null) {
            result = (PRIME * result) + fillGraphic.hashCode();
        }

        if (strokeGraphic != null) {
            result = (PRIME * result) + strokeGraphic.hashCode();
        }

        result = (PRIME * result) + lineCap.hashCode();
        result = (PRIME * result) + lineJoin.hashCode();

        result = (PRIME * result) + (int)opacity;
        result = (PRIME * result) + (int)width;

        if (dashArray != null) {
            result = (PRIME * result) + dashArray.hashCode();
        }

        return result;
    }

    /**
     * Compares this stroke with another stroke for equality.
     *
     * @param oth The other StrokeImpl to compare
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

        Stroke other = (Stroke) oth;

        // check the color first - most likely to change
        if (!Utilities.equals(getColor(), other.getColor())) {
            return false;
        }
        // check the width
        if (!Utilities.equals(getWidth(), other.getWidth())) {
            return false;
        }
        if (!Utilities.equals(getLineCap(), other.getLineCap())) {
            return false;
        }
        if (!Utilities.equals(getLineJoin(), other.getLineJoin())) {
            return false;
        }
        if (!Utilities.equals(getOpacity(), other.getOpacity())) {
            return false;
        }
        if (!Utilities.equals(getGraphicFill(), other.getGraphicFill())) {
            return false;
        }
        if (!Utilities.equals(getGraphicStroke(), other.getGraphicStroke())) {
            return false;
        }
        if (!Utilities.equals(dashArray(), other.dashArray())) {
            return false;
        }
        return true;
    }
}
