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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.Icon;
import org.geotools.util.Utilities;

/**
 * @author Ian Turton, CCG
 * @version $Id$
 */
public class ExternalGraphic implements Cloneable {
	private final static String CLSS = "ExternalGraphic";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
    private Icon inlineContent;
    private URL online;
    private URL location = null;
    private String format = null;
    private Map<String, Object> customProps = null;
    private final Set<Color> colorReplacements;

    public ExternalGraphic() {
    	colorReplacements = new TreeSet<Color>();
    }

    public ExternalGraphic(Icon icon, Collection<Color> replaces, URL source) {
        this.inlineContent = icon;
        if (replaces == null) {
            colorReplacements = new TreeSet<Color>();
        } else {
            colorReplacements = new TreeSet<Color>(replaces);
        }
        this.online = source;
    }

    /**
     * Provides the format of the external graphic.
     *
     * @return The format of the external graphic. Reported as its MIME type in a String object.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Provides the URL for where the external graphic resource can be located.
     *
     * @return The URL of the ExternalGraphic
     * @throws MalformedURLException If unable to represent external graphic as a URL
     */
    public URL getLocation()  {
        return location;
    }

    /**
     * Setter for property Format.
     *
     * @param format New value of property Format.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Setter for property location.
     *
     * @param location New value of property location.
     */
    public void setLocation(URL loc) {
        this.location = loc;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns a clone of the ExternalGraphic
     *
     * @see org.geotools.styling.ExternalGraphic#clone()
     */
    public Object clone() {
        ExternalGraphic clone = new ExternalGraphic();
        clone.colorReplacements.addAll(colorReplacements);
        return clone;
    }

    /**
     * Generates a hashcode for the ExternalGraphic
     *
     * @return The hash code.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (format != null) {
            result = (PRIME * result) + format.hashCode();
        }
        if (location != null) {
            result = (PRIME * result) + location.hashCode();
        }
        if (inlineContent != null) {
            result = (PRIME * result) + inlineContent.hashCode();
        }
        if (online != null) {
            result = (PRIME * result) + online.hashCode();
        }
        if (colorReplacements != null) {
            result = (PRIME * result) + colorReplacements.hashCode();
        }

        return result;
    }

    /**
     * Compares this ExternalGraphi with another.
     *
     * <p>Two external graphics are equal if they have the same uri and format.
     *
     * @param oth The other External graphic.
     * @return True if this and the other external graphic are equal.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth instanceof ExternalGraphic) {
            ExternalGraphic other = (ExternalGraphic) oth;
            return Utilities.equals(format, other.format);
        }

        return false;
    }

    public java.util.Map<String, Object> getCustomProperties() {
        return customProps;
    }

    public void setCustomProperties(java.util.Map<String, Object> list) {
        customProps = list;
    }

    public URL getURL() {
        return online;
    }

    public void setURL(URL online) {
        this.online = online;
    }

    public Icon getInlineContent() {
        return inlineContent;
    }

    public void setInlineContent(Icon inlineContent) {
        this.inlineContent = inlineContent;
    }

    public Collection<Color> getColors() {
        return Collections.unmodifiableCollection(colorReplacements);
    }

    public Set<Color> colorReplacements() {
        return this.colorReplacements;
    }


}
