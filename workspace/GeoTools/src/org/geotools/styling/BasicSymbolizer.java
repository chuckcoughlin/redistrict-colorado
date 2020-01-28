/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.locationtech.jts.geom.Geometry;

public class BasicSymbolizer implements Symbolizer {
    protected String name = "";
    protected String description = "";
    protected Geometry geometry = null;
    protected Unit<Length> unitOfMeasure = null;
    protected Map<String, String> options = null;

    protected BasicSymbolizer() {}

    public BasicSymbolizer(String name, String description, Geometry geometry, Unit<Length> unitOfMeasure) {
        this.name = name;
        this.description = description;
        this.geometry = geometry;
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setUnitOfMeasure(Unit<Length> uom) {
        this.unitOfMeasure = uom;
    }

    public Unit<Length> getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public boolean hasOption(String key) {
        return options != null && options.containsKey(key);
    }

    public Map<String, String> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<String, String>();
        }
        return options;
    }
    /**
     * Creates a deep copy clone.
     * @return The deep copy clone.
     */
    public Object clone() {
    	Symbolizer clone = new BasicSymbolizer();
        clone.setName(getName());
        clone.setDescription(getDescription());
        clone.setGeometry(getGeometry().clone());
        clone.setOptions(getOptions());
        clone.setUnitOfMeasure(getUnitOfMeasure());
        return clone;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
        result = prime * result + ((options == null) ? 0 : options.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BasicSymbolizer other = (BasicSymbolizer) obj;
        if (description == null) {
            if (other.description != null) return false;
        } else if (!description.equals(other.description)) return false;
        if (geometry == null) {
            if (other.geometry != null) return false;
        } else if (!geometry.equals(other.geometry)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (unitOfMeasure == null) {
            if (other.unitOfMeasure != null) return false;
        } else if (!unitOfMeasure.equals(other.unitOfMeasure)) return false;
        if (options == null) {
            if (other.options != null && !other.options.isEmpty()) return false;
        }
        if (options == null || options.isEmpty()) {
            // this options are NULL or empty
            if (other.options != null && !other.options.isEmpty()) {
                // the other options are neither NULL or empty
                return false;
            }
        } 
        else if (!options.equals(other.options)) {
            // options are not considered the same
            return false;
        }
        return true;
    }

	@Override
	public void accept(StyleVisitor visitor) {
		visitor.visit(this);
		
	}
}
