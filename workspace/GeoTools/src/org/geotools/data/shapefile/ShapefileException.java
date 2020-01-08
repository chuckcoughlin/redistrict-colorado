/*
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.data.shapefile;

/**
  * Thrown when an error relating to the shapefile occurs.
  * The class requires a String explanation.
  */
public class ShapefileException extends Exception{ 
	private static final long serialVersionUID = -3172843097485065993L;
	private final String msg;
    public ShapefileException(String s){
        super(s);
        this.msg = "ShapefileException: "+s;;
    }
    @Override
    public String getLocalizedMessage() {
    	return msg;
    }
}




