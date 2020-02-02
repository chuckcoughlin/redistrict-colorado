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

import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Icon;

/**
 * Default implementation of ExternalMark.
 *
 * @version $Id$
 */
public class ExternalMark {
	private final static String CLSS = "ExternalMark";
	private static Logger LOGGER = Logger.getLogger(CLSS);
    private URL url;
    private Icon inlineContent;
    private int index;
    private String format;

    public ExternalMark() {}

    public ExternalMark(Icon icon) {
        this.inlineContent = icon;
        this.index = -1;
        this.url = null;
        this.format = null;
    }

    public ExternalMark(URL resource, String format, int markIndex) {
        this.inlineContent = null;
        this.index = markIndex;
        this.url = resource;
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public Icon getInlineContent() {
        return inlineContent;
    }

    public int getMarkIndex() {
        return index;
    }

    public URL getURL() {
        return url;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    public void setInlineContent(Icon inline) {
        this.inlineContent = inline;
    }

    public void setFormat(String mimeType) {
        this.format = mimeType;
    }

    public void setMarkIndex(int markIndex) {
        this.index = markIndex;
    }

    public void setURL(URL resource) {
        this.url = resource;
    }

}
