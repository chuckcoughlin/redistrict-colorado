/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core.common;

/**
 * A Layer is on overlay on the map. It must be one of two types:
 * - Google map
 * - Shapefile
 */
public interface Layer {
    public String getName();
    public void setName(String nam);
}