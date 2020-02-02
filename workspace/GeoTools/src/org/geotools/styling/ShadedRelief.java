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
 *
 * Created on 13 November 2002, 13:59
 */
package org.geotools.styling;

import org.geotools.util.Utilities;

/**
 * Default implementation of ShadedRelief.
 *
 * @author iant
 */
public class ShadedRelief {
    private int reliefFactor = 55;
    private boolean brightnessOnly = false;

    public ShadedRelief() {
    }

    /**
     * The ReliefFactor gives the amount of exaggeration to use for the height of the ?hills.? A
     * value of around 55 (times) gives reasonable results for Earth-based DEMs. The default value
     * is system-dependent.
     *
     * @return an expression which evaluates to a double.
     */
    public int getReliefFactor() {
        return reliefFactor;
    }

    /**
     * indicates if brightnessOnly is true or false. Default is false.
     *
     * @return boolean brightnessOn.
     */
    public boolean isBrightnessOnly() {
        return brightnessOnly;
    }

    /**
     * turns brightnessOnly on or off depending on value of flag.
     *
     * @param flag boolean
     */
    public void setBrightnessOnly(boolean flag) {
        brightnessOnly = flag;
    }

    /**
     * The ReliefFactor gives the amount of exaggeration to use for the height of the ?hills.? A
     * value of around 55 (times) gives reasonable results for Earth-based DEMs. The default value
     * is system-dependent.
     *
     * @param reliefFactor an expression which evaluates to a double.
     */
    public void setReliefFactor(int reliefFactor) {
        this.reliefFactor = reliefFactor;
    }


    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        result = (PRIME * result) + reliefFactor;
        result = (PRIME * result) + (brightnessOnly ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ShadedRelief) {
            ShadedRelief other = (ShadedRelief) obj;

            return Utilities.equals(reliefFactor, other.reliefFactor)
                    && Utilities.equals(brightnessOnly, other.brightnessOnly);
        }

        return false;
    }

    static ShadedRelief cast(ShadedRelief shadedRelief) {
        if (shadedRelief == null) {
            return null;
        } else if (shadedRelief instanceof ShadedRelief) {
            return (ShadedRelief) shadedRelief;
        } else {
            ShadedRelief copy = new ShadedRelief();
            copy.setBrightnessOnly(shadedRelief.isBrightnessOnly());
            copy.setReliefFactor(shadedRelief.getReliefFactor());

            return copy;
        }
    }
}
