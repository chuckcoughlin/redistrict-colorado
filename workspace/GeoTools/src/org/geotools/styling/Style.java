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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.util.Utilities;
import org.opengis.style.StyleVisitor;

/**
 * Implementation of style.
 *
 * @author James Macgill, CCG
 * @version $Id$
 */
public class Style implements Cloneable {
	private final static String CLSS = "Style";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    private List<FeatureTypeStyle> featureTypeStyles = new ArrayList<FeatureTypeStyle>();
    private String description = "";
    private String name = "Default Styler";
    private boolean defaultB = false;
    private Symbolizer defaultSymbolizer;

    /** Creates a new instance of Style */
    protected Style() {}

    public String getDescription() { return description;}
    public void setDescription(String desc) { this.description=desc; }
    

    public FeatureTypeStyle[] getFeatureTypeStyles() {
        FeatureTypeStyle[] ret = new FeatureTypeStyle[] {new FeatureTypeStyle()};

        if ((featureTypeStyles != null) && (featureTypeStyles.size() != 0)) {
            if (LOGGER.isLoggable(Level.FINE))
                LOGGER.fine("number of fts set " + featureTypeStyles.size());

            ret = (FeatureTypeStyle[]) featureTypeStyles.toArray(new FeatureTypeStyle[] {});
        }
        return ret;
    }

    public List<FeatureTypeStyle> featureTypeStyles() {
        return featureTypeStyles;
    }

    public Symbolizer getDefaultSpecification() {
        return defaultSymbolizer;
    }

    public void setDefaultSpecification(org.geotools.styling.Symbolizer defaultSymbolizer) {
        this.defaultSymbolizer = defaultSymbolizer;
    }

    public void setFeatureTypeStyles(FeatureTypeStyle[] styles) {
        List<FeatureTypeStyle> newStyles = Arrays.asList(styles);

        this.featureTypeStyles.clear();
        this.featureTypeStyles.addAll(newStyles);

        LOGGER.fine("StyleImpl added " + featureTypeStyles.size() + " feature types");
    }

    public void addFeatureTypeStyle(FeatureTypeStyle type) {
        featureTypeStyles.add(type);
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return defaultB;
    }

    public void setDefault(boolean isDefault) {
        defaultB = isDefault;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Clones the Style. Creates deep copy clone of the style.
     *
     * @return the Clone of the style.
     * @see org.geotools.styling.Style#clone()
     */
    public Object clone() {
        Style clone;

        try {
            clone = (Style) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // this should never happen since we implement Cloneable
        }

        List<FeatureTypeStyle> ftsCopies = new ArrayList<>();

        for (FeatureTypeStyle featureTypeStyle : featureTypeStyles) {
            ftsCopies.add((FeatureTypeStyle) (featureTypeStyle.clone()));
        }

        clone.featureTypeStyles().clear();
        clone.featureTypeStyles().addAll(ftsCopies);

        return clone;
    }

    /**
     * Overrides hashcode.
     *
     * @return The hash code.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (featureTypeStyles != null) {
            result = (PRIME * result) + featureTypeStyles.hashCode();
        }

        if (description != null) {
            result = (PRIME * result) + description.hashCode();
        }

        if (name != null) {
            result = (PRIME * result) + name.hashCode();
        }

        result = (PRIME * result) + (defaultB ? 1 : 0);

        return result;
    }

    /**
     * Compares this Style with another.
     *
     * <p>Two StyleImpl are equal if they have the same properties and the same list of
     * FeatureTypeStyles.
     *
     * @param oth The object to compare with this for equality.
     * @return True if this and oth are equal.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth instanceof Style) {
            Style other = (Style) oth;

            return Utilities.equals(name, other.name)
                    && Utilities.equals(description, other.description)
                    && Utilities.equals(featureTypeStyles, other.featureTypeStyles);
        }

        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("StyleImpl");
        buf.append("[");
        if (name != null) {
            buf.append(" name=");
            buf.append(name);
        } else {
            buf.append(" UNNAMED");
        }
        if (defaultB) {
            buf.append(", DEFAULT");
        }
        //      if( title != null && title.length() != 0 ){
        //              buf.append(", title=");
        //              buf.append( title );
        //      }
        buf.append("]");
        return buf.toString();
    }

    public Object accept(StyleVisitor visitor,Object extraData) {
        return visitor.visit(this);
    }
}
