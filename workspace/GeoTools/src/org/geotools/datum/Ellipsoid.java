/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.datum;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Length;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.MetricSystem;
import javax.measure.unit.Unit;

import org.geotools.data.wkt.IdentifiedObject;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

/**
 * Geometric figure that can be used to describe the approximate shape of the earth. In mathematical
 * terms, it is a surface formed by the rotation of an ellipse about its minor axis. An ellipsoid
 * requires two defining parameters:
 *
 * <ul>
 *   <li>{@linkplain #getSemiMajorAxis semi-major axis} and {@linkplain #getInverseFlattening
 *       inverse flattening}, or
 *   <li>{@linkplain #getSemiMajorAxis semi-major axis} and {@linkplain #getSemiMinorAxis semi-minor
 *       axis}.
 * </ul>
 *
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class Ellipsoid  {
    private static final String CLSS = "Ellipsoid";
    private static final long serialVersionUID = -1149451543954764081L;
    
  
    /**
     * WGS 1984 ellipsoid with axis in {@linkplain SI#METER metres}. This ellipsoid is used in GPS
     * systems and is the default for most {@code org.geotools} packages.
     */
    public static final Ellipsoid WGS84 =
            createFlattenedSphere("WGS84", 6378137.0, 298.257223563, MetricSystem.METRE);

    /**
     * GRS 80 ellipsoid with axis in {@linkplain SI#METER metres}.
     *
     * @since 2.2
     */
    public static final Ellipsoid GRS80 =
            createFlattenedSphere("GRS80", 6378137.0, 298.257222101, MetricSystem.METRE);

    /** International 1924 ellipsoid with axis in {@linkplain SI#METER metres}. */
    public static final Ellipsoid INTERNATIONAL_1924 =
            createFlattenedSphere("International 1924", 6378388.0, 297.0, MetricSystem.METRE);

    /**
     * Clarke 1866 ellipsoid with axis in {@linkplain SI#METER metres}.
     *
     * @since 2.2
     */
    public static final Ellipsoid CLARKE_1866 =
            createFlattenedSphere("Clarke 1866", 6378206.4, 294.9786982, MetricSystem.METRE);

    /**
     * A sphere with a radius of 6371000 {@linkplain SI#METER metres}. Spheres use a simplier
     * algorithm for {@linkplain #orthodromicDistance orthodromic distance computation}, which may
     * be faster and more robust.
     */
    public static final Ellipsoid SPHERE =
            createEllipsoid("SPHERE", 6371000, 6371000, MetricSystem.METRE);

    /**
     * The equatorial radius.
     *
     * @see #getSemiMajorAxis
     */
    private final double semiMajorAxis;

    /**
     * The polar radius.
     *
     * @see #getSemiMinorAxis
     */
    private final double semiMinorAxis;

    /**
     * The inverse of the flattening value, or {@link Double#POSITIVE_INFINITY} if the ellipsoid is
     * a sphere.
     *
     * @see #getInverseFlattening
     */
    private final double inverseFlattening;

    /**
     * Tells if the Inverse Flattening definitive for this ellipsoid.
     *
     * @see #isIvfDefinitive
     */
    private final boolean ivfDefinitive;

    /** The units of the semi-major and semi-minor axis values. */
    private final Unit<Length> unit;
    private final Map<String, Object> properties;

    /**
     * Constructs a new ellipsoid with the same values than the specified one. This copy constructor
     * provides a way to wrap an arbitrary implementation into a Geotools one or a user-defined one
     * (as a subclass), usually in order to leverage some implementation-specific API. This
     * constructor performs a shallow copy, i.e. the properties are not cloned.
     *
     * @param ellipsoid The ellipsoid to copy.
     * @since 2.2
     * @see #wrap
     */
    public Ellipsoid(final Ellipsoid ellipsoid) {
    	properties = new HashMap<>();
        semiMajorAxis = ellipsoid.getSemiMajorAxis();
        semiMinorAxis = ellipsoid.getSemiMinorAxis();
        inverseFlattening = ellipsoid.getInverseFlattening();
        ivfDefinitive = ellipsoid.isIvfDefinitive();
        unit = ellipsoid.getAxisUnit();
    }

    /**
     * Constructs a new ellipsoid using the specified axis length. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param inverseFlattening The inverse of the flattening value.
     * @param ivfDefinitive {@code true} if the inverse flattening is definitive.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @see #createEllipsoid
     * @see #createFlattenedSphere
     */
    public Ellipsoid(
            final Map<String, Object> props,
            final double semiMajor,
            final double semiMinor,
            final double invFlattening,
            final boolean ivfDefinitive,
            final Unit<Length> unit) {
        this.properties = props;
        this.unit = unit;
        this.semiMajorAxis = semiMajor;
        this.semiMinorAxis = semiMinor;
        this.inverseFlattening = invFlattening;
        this.ivfDefinitive = ivfDefinitive;
        if( semiMajorAxis<0 || semiMinorAxis <0 || inverseFlattening<0 || unit==null ) {
        	throw new IllegalArgumentException (String.format("%s: Illegal argument (%s)",CLSS));
        }
    }
    
    public Object getProperty(String key) { return properties.get(key); }
    public void setProperty(String key, Object value) { this.properties.put(key, value); }

    /**
     * Constructs a new ellipsoid using the specified axis length.
     *
     * @param name The ellipsoid name.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static Ellipsoid createEllipsoid(
            final String name,
            final double semiMajorAxis,
            final double semiMinorAxis,
            final BaseUnit<Length> unit) {
        Ellipsoid e = createEllipsoid( new HashMap<String,Object>(),semiMajorAxis, semiMinorAxis, unit);
        e.setProperty(IdentifiedObject.NAME_KEY, name);
        return e;
    }

    /**
     * Constructs a new ellipsoid using the specified axis length. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static Ellipsoid createEllipsoid(
            final Map<String, Object> properties,
            final double semiMajorAxis,
            final double semiMinorAxis,
            final BaseUnit<Length> unit) {
        if (semiMajorAxis == semiMinorAxis) {
            return new Spheroid(properties, semiMajorAxis, false, unit);
        } 
        else {
            return new Ellipsoid(
                    properties,
                    semiMajorAxis,
                    semiMinorAxis,
                    semiMajorAxis / (semiMajorAxis - semiMinorAxis),
                    false,
                    unit);
        }
    }

    /**
     * Constructs a new ellipsoid using the specified axis length and inverse flattening value.
     *
     * @param name The ellipsoid name.
     * @param semiMajorAxis The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static Ellipsoid createFlattenedSphere(
            final String name,
            final double semiMajorAxis,
            final double inverseFlattening,
            final BaseUnit<Length> unit) {
    	Ellipsoid e = createFlattenedSphere( new HashMap<String,Object>(),semiMajorAxis,inverseFlattening , unit);
        e.setProperty(IdentifiedObject.NAME_KEY, name);
        return e;
    }

    /**
     * Constructs a new ellipsoid using the specified axis length and inverse flattening value. The
     * properties map is given unchanged to the {@linkplain
     * AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static Ellipsoid createFlattenedSphere(
            final Map<String, Object> properties,
            final double semiMajorAxis,
            final double inverseFlattening,
            final BaseUnit<Length> unit) {
        if (Double.isInfinite(inverseFlattening)) {
            return new Spheroid(properties, semiMajorAxis, true, unit);
        } 
        else {
            return new Ellipsoid(
                    properties,
                    semiMajorAxis,
                    semiMajorAxis * (1 - 1 / inverseFlattening),
                    inverseFlattening,
                    true,
                    unit);
        }
    }


    /**
     * Returns the linear unit of the {@linkplain #getSemiMajorAxis semi-major} and {@linkplain
     * #getSemiMinorAxis semi-minor} axis values.
     *
     * @return The axis linear unit.
     */
    public Unit<Length> getAxisUnit() {
        return unit;
    }

    /**
     * Length of the semi-major axis of the ellipsoid. This is the equatorial radius in {@linkplain
     * #getAxisUnit axis linear unit}.
     *
     * @return Length of semi-major axis.
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Length of the semi-minor axis of the ellipsoid. This is the polar radius in {@linkplain
     * #getAxisUnit axis linear unit}.
     *
     * @return Length of semi-minor axis.
     */
    public double getSemiMinorAxis() {
        return semiMinorAxis;
    }

    /**
     * The ratio of the distance between the center and a focus of the ellipse to the length of its
     * semimajor axis. The eccentricity can alternately be computed from the equation: <code>
     * e=sqrt(2f-f²)</code>.
     *
     * @return The eccentricity of this ellipsoid.
     */
    public double getEccentricity() {
        final double f = 1 - getSemiMinorAxis() / getSemiMajorAxis();
        return Math.sqrt(2 * f - f * f);
    }

    /**
     * Returns the value of the inverse of the flattening constant. Flattening is a value used to
     * indicate how closely an ellipsoid approaches a spherical shape. The inverse flattening is
     * related to the equatorial/polar radius by the formula
     *
     * <p><var>ivf</var>&nbsp;=&nbsp;<var>r</var><sub>e</sub>/(<var>r</var><sub>e</sub>-<var>r</var><sub>p</sub>).
     *
     * <p>For perfect spheres (i.e. if {@link #isSphere} returns {@code true}), the {@link
     * Double#POSITIVE_INFINITY} value is used.
     *
     * @return The inverse flattening value.
     */
    public double getInverseFlattening() {
        return inverseFlattening;
    }

    /**
     * Indicates if the {@linkplain #getInverseFlattening inverse flattening} is definitive for this
     * ellipsoid. Some ellipsoids use the IVF as the defining value, and calculate the polar radius
     * whenever asked. Other ellipsoids use the polar radius to calculate the IVF whenever asked.
     * This distinction can be important to avoid floating-point rounding errors.
     *
     * @return {@code true} if the {@linkplain #getInverseFlattening inverse flattening} is
     *     definitive, or {@code false} if the {@linkplain #getSemiMinorAxis polar radius} is
     *     definitive.
     */
    public boolean isIvfDefinitive() {
        return ivfDefinitive;
    }

    /**
     * {@code true} if the ellipsoid is degenerate and is actually a sphere. The sphere is
     * completely defined by the {@linkplain #getSemiMajorAxis semi-major axis}, which is the radius
     * of the sphere.
     *
     * @return {@code true} if the ellipsoid is degenerate and is actually a sphere.
     */
    public boolean isSphere() {
        return semiMajorAxis == semiMinorAxis;
    }

    /**
     * Returns the orthodromic distance between two geographic coordinates. The orthodromic distance
     * is the shortest distance between two points on a sphere's surface. The default implementation
     * delegates the work to {@link #orthodromicDistance(double,double,double,double)}.
     *
     * @param P1 Longitude and latitude of first point (in decimal degrees).
     * @param P2 Longitude and latitude of second point (in decimal degrees).
     * @return The orthodromic distance (in the units of this ellipsoid).
     */
    public double orthodromicDistance(final Point2D P1, final Point2D P2) {
        return orthodromicDistance(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }

    /**
     * Returns the orthodromic distance between two geographic coordinates. The orthodromic distance
     * is the shortest distance between two points on a sphere's surface. The orthodromic path is
     * always on a great circle. This is different from the <cite>loxodromic distance</cite>, which
     * is a longer distance on a path with a constant direction on the compass.
     *
     * @param x1 Longitude of first point (in decimal degrees).
     * @param y1 Latitude of first point (in decimal degrees).
     * @param x2 Longitude of second point (in decimal degrees).
     * @param y2 Latitude of second point (in decimal degrees).
     * @return The orthodromic distance (in the units of this ellipsoid's axis).
     */
    public double orthodromicDistance(double x1, double y1, double x2, double y2) {
        Geodesic geod = new Geodesic(getSemiMajorAxis(), 1 / getInverseFlattening());
        GeodesicData g = geod.Inverse(y1, x1, y2, x2, GeodesicMask.DISTANCE);
        return g.s12;
    }


    /**
     * Returns a hash value for this ellipsoid. {@linkplain #getName Name}, {@linkplain #getRemarks
     * remarks} and the like are not taken in account. In other words, two ellipsoids will return
     * the same hash value if they are equal in the sense of <code>
     * {@link #equals equals}(AbstractIdentifiedObject, <strong>false</strong>)</code>.
     *
     * @return The hash code value. This value doesn't need to be the same in past or future
     *     versions of this class.
     */
    @Override
    public int hashCode() {
        long longCode = 37 * Double.doubleToLongBits(semiMajorAxis);
        if (ivfDefinitive) {
            longCode += inverseFlattening;
        } else {
            longCode += semiMinorAxis;
        }
        return (((int) (longCode >>> 32)) ^ (int) longCode);
    }
}
