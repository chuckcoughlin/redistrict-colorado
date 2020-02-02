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
import java.util.ArrayList;
import java.util.List;

import org.geotools.util.Utilities;

/**
 * A simple implementation of the color map interface.
 *
 * @author iant
 * @author aaime
 */
public class ColorMap  {
	public static final int TYPE_RAMP = 0;
	public static final int TYPE_INTERVALS = 1;
	public static final int TYPE_VALUES = 2;
	
    private List<Color> list = new ArrayList<>();
    private int type = ColorMap.TYPE_RAMP;
    private boolean extendedColors;

    public ColorMap() {
    }

    public void addColorMapEntry(Color entry) {
        list.add(entry);
    }

    public Color[] getColorMapEntries() {
        return (Color[]) list.toArray(new Color[0]);
    }

    public Color getColorMapEntry(int index) {
        return list.get(index);
    }

    /** @see org.geotools.styling.ColorMap#getType() */
    public int getType() {
        return type;
    }

    /** @see org.geotools.styling.ColorMap#setType(int) */
    public void setType(int type) {
        if ((type < TYPE_RAMP) || (type > TYPE_VALUES)) {
            throw new IllegalArgumentException();
        }
        this.type = type;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    public boolean getExtendedColors() {
        return extendedColors;
    }

    public void setExtendedColors(boolean extended) {
        extendedColors = extended;
    }

    @Override
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (list != null) {
            result = (PRIME * result) + list.hashCode();
        }

        result = (PRIME * result) + type;
        result = (PRIME * result) + (extendedColors ? 1 : 0);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ColorMap) {
            ColorMap other = (ColorMap) obj;

            return Utilities.equals(list, other.list)
                    && Utilities.equals(type, other.type)
                    && Utilities.equals(extendedColors, other.extendedColors);
        }

        return false;
    }
}
