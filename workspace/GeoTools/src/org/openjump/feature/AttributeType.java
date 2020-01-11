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
 */
package org.openjump.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

/**
 * Types of attributes. These include types specific to configuration of a feature.
 *
 * @since 1.0
 */
public enum AttributeType  {
	ATTRIBUTE_TYPE,
	BOOLEAN,
	COLOR,
	DATE,
	DOUBLE,
	GEOMETRY,
	INTEGER,
	LONG,
	OBJECT,
	STRING
	;

	/**
	 * @return attribute types in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (AttributeType type : AttributeType.values())
		{
			names.add(type.name());
		}
		return names;
	}
	/**
	 * @return AttributeTypes currently used through OpenJUMP user interface
	 */
	public static Collection<AttributeType> basicTypes() {
		List<AttributeType> basicTypes = new ArrayList<>();
		basicTypes.add(GEOMETRY);
		basicTypes.add(STRING);
		basicTypes.add(INTEGER);
		basicTypes.add(LONG);
		//basicTypes.add(FLOAT);
		basicTypes.add(DOUBLE);
		basicTypes.add(DATE);
		//basicTypes.add(TIMESTAMP);
		basicTypes.add(BOOLEAN);
		basicTypes.add(OBJECT);
		return basicTypes;
	}
	/**
	 * @return the java class used to store attributes of the specified type.
	 */
	public static Class<?> toJavaClass(AttributeType type) {
		switch(type) {
		case BOOLEAN: return Boolean.class;
		case DATE: return Date.class;
		case DOUBLE: return Double.class; 
		case GEOMETRY: return Geometry.class;
		case INTEGER: return Integer.class;
		case LONG: return Long.class; 
		case OBJECT: return Object.class; 
		case STRING: return String.class; 
		default: return Object.class;
		}
	}
}