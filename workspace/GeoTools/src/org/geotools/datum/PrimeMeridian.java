/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
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
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotools.datum;

import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Angle;
import javax.measure.unit.USCustomarySystem;
import javax.measure.unit.Unit;

import org.geotools.data.wkt.IdentifiedObject;

/**
 * A prime meridian defines the origin from which longitude values are determined. The {@link
 * #getName name} initial value is "Greenwich", and that value shall be used when the {@linkplain
 * #getGreenwichLongitude greenwich longitude} value is zero.
 *
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @since 2.1
 */
public class PrimeMeridian  {
	private static final String CLSS = "PrimeMeridian";
    private static final long serialVersionUID = 541978454643213305L;

    /** The Greenwich meridian, with angular measurements in decimal degrees. */
    public static final PrimeMeridian GREENWICH = new PrimeMeridian("Greenwich", 0, USCustomarySystem.DEGREE_ANGLE);

    /** Longitude of the prime meridian measured from the Greenwich meridian, positive eastward. */
    private final double greenwichLongitude;

    /** The angular unit of the {@linkplain #getGreenwichLongitude Greenwich longitude}. */
    private final Unit<Angle> angularUnit;
    private final Map<String,Object> properties;

    /**
     * Constructs a prime meridian from a name. The {@code greenwichLongitude} value is assumed in
     * {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param name The datum name.
     * @param greenwichLongitude The longitude value relative to the Greenwich Meridian.
     */
    public PrimeMeridian(final String name, final double greenwichLongitude) {
        this(name, greenwichLongitude,USCustomarySystem.DEGREE_ANGLE);
    }

    /**
     * Constructs a prime meridian from a name.
     *
     * @param name The datum name.
     * @param greenwichLongitude The longitude value relative to the Greenwich Meridian.
     * @param angularUnit The angular unit of the longitude.
     */
    public PrimeMeridian(
            final String name, final double greenwichLongitude, final Unit<Angle> angularUnit) { 
    	 this(new HashMap<String,Object>(), greenwichLongitude, angularUnit);
    	 properties.put(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Constructs a prime meridian from a set of properties. The properties map is given unchanged
     * to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class
     * constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param greenwichLongitude The longitude value relative to the Greenwich Meridian.
     * @param angularUnit The angular unit of the longitude.
     */
    public PrimeMeridian(
            final Map<String, Object> props,
            final double greenwichLongitude,
            final Unit<Angle> angularUnit) {
        this.properties = props;
        this.greenwichLongitude = greenwichLongitude;
        this.angularUnit = angularUnit;
    }
    
    public String getName() { return (String)this.properties.get(IdentifiedObject.NAME_KEY); }

    /**
     * Longitude of the prime meridian measured from the Greenwich meridian, positive eastward. The
     * {@code greenwichLongitude} initial value is zero, and that value shall be used when the
     * {@linkplain #getName meridian name} value is "Greenwich".
     *
     * @return The prime meridian Greenwich longitude, in {@linkplain #getAngularUnit angular unit}.
     */
    public double getGreenwichLongitude() {
        return greenwichLongitude;
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian, expressed in the specified
     * units. This convenience method makes it easier to obtain longitude in decimal degrees ({@code
     * getGreenwichLongitude(NonSI.DEGREE_ANGLE)}), regardless of the underlying angular units of
     * this prime meridian.
     *
     * @param targetUnit The unit in which to express longitude.
     * @return The Greenwich longitude in the given units.
     */
    public double getGreenwichLongitude(final Unit<Angle> targetUnit) {
        return getAngularUnit().getConverterTo(targetUnit).convert(getGreenwichLongitude());
    }

    /** Returns the angular unit of the {@linkplain #getGreenwichLongitude Greenwich longitude}. */
    public Unit<Angle> getAngularUnit() {
        return angularUnit;
    }
}
