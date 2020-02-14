/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.renderer.style;

/**
 * Base class for resolved styles. Styles are resolved according a particular rendering context. The
 * base class make no assumption about the output device (AWT, SWT, <i>etc.</i>). However, a
 * particular output device may need to be choosen for concrete subclasses, for example {@link
 * Style2D} for targeting <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A>.
 *
 * @version $Id$
 */
public class Style implements Cloneable {
    protected double maxScale = Double.POSITIVE_INFINITY;
    protected double minScale = 0;
    private String description = "";
    private String name = "Default Styler";
    
    /** Creates a new instance of Style */
    protected Style() {}
    protected Style(String nam,String desc) {
    	this.name = nam;
    	this.description=desc;
    }
    public String getDescription() { return description;}
    public void setDescription(String desc) { this.description=desc; }
    public String getName() { return name;}
    public void setName(String nam) { this.name=nam; }
    
    /**
     * Gets the maximum scale at which the geometry has to be painted with this style (inclusive)
     *
     * @return - the maximum painting scale
     */
    public double getMaxScale() {
        return this.maxScale;
    }

    /**
     * Gets the minimum scale at which the geometry has to be painted with this style (inclusive)
     *
     * @return - the minimum painting scale
     */
    public double getMinScale() {
        return this.minScale;
    }

    /**
     * Sets minimum and maximum scale, and performs integrity checks on these value (will throw and
     * IllegalArgumentException in minScale > maxScale)
     *
     * @param minScale
     * @param maxScale
     */
    public void setMinMaxScale(double minScale, double maxScale) {
        if (minScale > maxScale) {
            throw new IllegalArgumentException("Max scale must be bigger than min scale");
        }

        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    /**
     * Checks whethere the style should be used for painting at scale <code>scale</scale>
     *
     * @param scale The scale queried
     *
     * @return True if <code>scale</code> is whithin the scale range of this style (false
     *         otherwise)
     */
    public boolean isScaleInRange(double scale) {
        return (scale >= minScale) && (scale <= maxScale);
    }
    /**
     * Overrides hashcode.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        result = (PRIME * result) + description.hashCode();
        result = (PRIME * result) + name.hashCode();
        return result;
    }
    @Override
    public Style clone() {
       Style clone =  new Style(name,description);
       clone.setMinMaxScale(getMinScale(), getMaxScale());
       return clone;
    }
}
