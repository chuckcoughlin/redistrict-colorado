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
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;


/**
 * Provides a Java representation of an SLD TextSymbolizer that defines how text symbols should be
 * rendered.
 *
 * @author Ian Turton, CCG
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class TextSymbolizer extends BasicSymbolizer implements Cloneable {
    private List<Font> fonts = new ArrayList<Font>(1);
    private Fill fill;
    private Halo halo;
    private PointPlacement placement;
    private String label = null;
    private Graphic graphic = null;
    private String description = "";
    private String otherText = null;

    /** Creates a new instance of DefaultTextSymbolizer */
    protected TextSymbolizer() {
    }

    protected TextSymbolizer( String desc, String name, Unit<Length> uom) {
    	this.description = desc;
    	this.name = name;
    	this.unitOfMeasure = uom;
        fill = new Fill();
        fill.setColor(Color.BLACK); // default text fill is black
        halo = null;
        placement = new PointPlacement();
    }

    /**
     * Returns the fill to be used to fill the text when rendered.
     *
     * @return The fill to be used.
     */
    public Fill getFill() {
        return fill;
    }

    /**
     * Setter for property fill.
     *
     * @param fill New value of property fill.
     */
    public void setFill(Fill fill) {
        this.fill = fill;
    }

    public List<Font> fonts() {
        return fonts;
    }

    public Font getFont() {
        return fonts.isEmpty() ? null : fonts.get(0);
    }

    public void setFont(Font font) {
        if (this.fonts.size() == 1 && this.fonts.get(0) == font) {
            return; // no change
        }
        if (font != null) {
            if (this.fonts.isEmpty()) {
                this.fonts.add(font);
            }
            else {
                this.fonts.set(0, font);
            }
        }
    }

    /**
     * Setter for property font.
     *
     * @param font New value of property font.
     */
    public void addFont(Font font) {
        fonts.add(font);
    }

    /**
     * A halo fills an extended area outside the glyphs of a rendered text label to make the label
     * easier to read over a background.
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Setter for property halo.
     *
     * @param halo New value of property halo.
     */
    public void setHalo(Halo halo) {
        this.halo = halo;
    }

    /**
     * Returns the label expression.
     *
     * @return Label expression.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for property label.
     *
     * @param label New value of property label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * A pointPlacement specifies how a text element should be rendered relative to its geometric
     * point.
     *
     * @return Value of property labelPlacement.
     */
    public PointPlacement getLabelPlacement() {
        return placement;
    }

    /**
     * Setter for property labelPlacement.
     *
     * @param labelPlacement New value of property labelPlacement.
     */
    public void setLabelPlacement(PointPlacement labelPlacement) {
        this.placement = labelPlacement;
    }

    /**
     * Accept a StyleVisitor to perform an operation on this symbolizer.
     *
     * @param visitor The StyleVisitor to accept.
     */
    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone. TODO: Need to complete the deep copy, currently only shallow copy.
     *
     * @return The deep copy clone.
     */
    public Object clone() {
    	TextSymbolizer clone = new TextSymbolizer();
    	return clone;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public double getPriority() {
        return priority;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        if (this.graphic == graphic) {
            return;
        }
        this.graphic = graphic;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<TextSymbolizer property=");
        buf.append(" label=");
        buf.append(label);
        buf.append(">");
        buf.append(this.fonts);
        return buf.toString();
    }

    public String getFeatureDescription() {
        return description;
    }

    public void setFeatureDescription(String description) {
        this.description = description;
    }

    public String getString() {
        return otherText;
    }

    public void setString(String otherText) {
        this.otherText = otherText;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((fill == null) ? 0 : fill.hashCode());
        result = prime * result + ((fonts == null) ? 0 : fonts.hashCode());
        result = prime * result + ((graphic == null) ? 0 : graphic.hashCode());
        result = prime * result + ((halo == null) ? 0 : halo.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((otherText == null) ? 0 : otherText.hashCode());
        result = prime * result + ((placement == null) ? 0 : placement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        TextSymbolizer other = (TextSymbolizer) obj;
        if (description == null) {
            if (other.description != null) return false;
        } 
        else if (!description.equals(other.description)) return false;
        if (fill == null) {
            if (other.fill != null) return false;
        } 
        else if (!fill.equals(other.fill)) return false;
        if (fonts == null) {
            if (other.fonts != null) return false;
        } else if (!fonts.equals(other.fonts)) return false;
        if (graphic == null) {
            if (other.graphic != null) return false;
        } else if (!graphic.equals(other.graphic)) return false;
        if (halo == null) {
            if (other.halo != null) return false;
        } else if (!halo.equals(other.halo)) return false;
        if (label == null) {
            if (other.label != null) return false;
        } else if (!label.equals(other.label)) return false;
        if (otherText == null) {
            if (other.otherText != null) return false;
        } else if (!otherText.equals(other.otherText)) return false;
        if (placement == null) {
            if (other.placement != null) return false;
        } else if (!placement.equals(other.placement)) return false;
        return true;
    }
}
