/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package org.openjump.coordsys;

import java.io.Serializable;
import java.util.Properties;


/** 
 * This class represents a coordinate system. We have also subsumed the functionality of a CoordinateReferenceSystem.
 * In doing so we're eliminated at jeast 5 layers of abstract classes and interfaces.
 */
public class CoordinateSystem implements Comparable<CoordinateSystem>, Serializable {
    private static final long serialVersionUID = -811718450919581831L;
    public static final String NAME_KEY = "NAME";
    public static final String ALIAS_KEY = "ALIAS";
    public static final String DOMAIN_OF_VALIDITY_KEY = "DOMAIN";
    private Projection projection;
    private Properties properties;
    private String name;
    private int epsgCode;
    
	public static final CoordinateSystem DEFAULT = new CoordinateSystem("coordsys.CoordinateSystem.default",0, null) {
	    public Projection getProjection() {
	        throw new UnsupportedOperationException();
	    }
	    public int getEPSGCode() {
	        throw new UnsupportedOperationException();
	    }

	};
	
	public static final CoordinateSystem UNSPECIFIED = new CoordinateSystem("coordsys.CoordinateSystem.unspecified",0, null) {
	    public Projection getProjection() {
	        throw new UnsupportedOperationException();
	    }
	    public int getEPSGCode() {
	        throw new UnsupportedOperationException();
	    }
	};
    
    /**
     * @see <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip122.html">www.javaworld.com</a>
     */
    private Object readResolve() {
        return name.equals(UNSPECIFIED.name) ? UNSPECIFIED : this; 
    }    

    public CoordinateSystem(String name, int epsgCode, Projection projection) {
        this.name = name;
        this.projection = projection;
        this.epsgCode = epsgCode;
        this.properties = new Properties();
	    properties.put(NAME_KEY, "WGS84(DD)"); // Name used in WCS 1.0.;
        properties.put(ALIAS_KEY, "WGS84");
        properties.put(DOMAIN_OF_VALIDITY_KEY, "WORLD");
    }
    
    public String toString() {
        return name;
    }
    public String getName() {
        return name;
    }

    public int getDimension() { return 2; }
    
    public int getEPSGCode() {
        return epsgCode;
    }
    public String getProperty(String key) { return properties.getProperty(key); }

	public int compareTo(CoordinateSystem o) {
        if (this == o) { return 0; }
        if (this == UNSPECIFIED) { return -1; }
        if (o == UNSPECIFIED) { return 1; }
		return toString().compareTo(o.toString());
	}
}
