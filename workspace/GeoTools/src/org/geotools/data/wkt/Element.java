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
package org.geotools.data.wkt;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in a <cite>Well Known Text</cite> (WKT). A {@code Element} is made of {@link String},
 * {@link Number} and other {@link Element}. For example:
 *
 * <blockquote>
 * <pre>
 * PRIMEM["Greenwich", 0.0, AUTHORITY["some authority", "Greenwich"]]
 * </pre>
 * </blockquote>
 *
 * Each {@code Element} object can contains an arbitrary number of other elements. 
 */
public final class Element {
	private final static String CLSS = "Element";

    public String keyword;  // Keyword of this entity. E.g.: "PRIMEM"
    private boolean isRoot = false;
    /**
     * An ordered list of {@link String}s, {@link Number}s and other {@link Element}s. May be {@code
     * null} if the keyword was not followed by a pair of brackets (e.g. "NORTH").
     */
    private final List<Element> children;
    private final List<String> parameters;  // Arguments - String, but may be numeric

    /**
     * Constructs a new element.
     *
     * @param singleton The only children for this root.
     */
    public Element() {
        keyword = null;
        children = new ArrayList<>();
        parameters = new ArrayList<>();
    }

    public boolean getIsRoot()           { return this.isRoot;  }
    public String getKeyword()           { return this.keyword; }
    public void setIsRoot(boolean flag)  { this.isRoot = flag;  }
    public void setKeyword(String key)   { this.keyword = key; }
    public void addChild(Element child)  { this.children.add(child); }
    public void addParameter(String obj) { this.parameters.add(obj); }
    
    public List<Element> getChildren()   { return this.children; }
    public List<String>  getParameters() { return this.parameters; }
    public Element findChild(String key) {
    	Element child = null;
    	for( Element c:children ) {
    		if( c.keyword.equalsIgnoreCase(key) ) {
    			child = c;
    			break;
    		}
    	}
    	return child;
    }

   
    /**
     * Returns the keyword. This overriding is needed for correct formatting of the error message in
     * {@link #close}.
     */
    @Override
    public String toString() {
        return keyword;
    }
}
