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
 *    
 *    Derived from class DefaultEllipsoid.
 */
package org.geotools.referencing;

import java.awt.geom.Point2D;
import java.util.Map;

import javax.measure.quantity.Length;
import javax.measure.unit.USCustomarySystem;
import javax.measure.unit.Unit;

import org.geotools.util.Utilities;

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
	private final static String CLSS = "Ellipsoid";
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -1149451543954764081L;
    private final String name;

    /**
     * WGS 1984 ellipsoid with axis in {@linkplain SI#METER metres}. This ellipsoid is used in GPS
     * systems and is the default for most {@code org.geotools} packages.
     */
    public static final Ellipsoid DEFAULT = createFlattenedSphere("WGS84", 6378137.0, 298.257223563,USCustomarySystem.METER);

 
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
    protected Ellipsoid(
            final String name,
            final double semiMajorAxis,
            final double semiMinorAxis,
            final double inverseFlattening,
            final boolean ivfDefinitive,
            final Unit<Length> unit) {
    	this.name = name;
        this.unit = unit;
        this.semiMajorAxis = check("semiMajorAxis", semiMajorAxis);
        this.semiMinorAxis = check("semiMinorAxis", semiMinorAxis);
        this.inverseFlattening = check("inverseFlattening", inverseFlattening);
        this.ivfDefinitive = ivfDefinitive;
        Utilities.ensureNonNull("unit", unit);
    }

 

    /**
     * Constructs a new ellipsoid using the specified axis length and inverse flattening value. The
     * properties map is given unchanged to the {@linkplain
     * AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param name the name.
     * @param semiMajorAxis The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static Ellipsoid createFlattenedSphere(
            final String name,
            final double semiMajorAxis,
            final double inverseFlattening,
            final Unit<Length> unit) {
        return new Ellipsoid(
                    name,
                    semiMajorAxis,
                    semiMajorAxis * (1 - 1 / inverseFlattening),
                    inverseFlattening,
                    true,
                    unit);
    }


    /**
     * Checks the argument validity. Argument {@code value} should be greater than zero.
     *
     * @param name Argument name.
     * @param value Argument value.
     * @return {@code value}.
     * @throws IllegalArgumentException if {@code value} is not greater than 0.
     */
    static double check(final String name, double value) throws IllegalArgumentException {
        if (value > 0) {
            return value;
        }
        throw new IllegalArgumentException(String.format("%s.check %s: Argument must be positive (%2.1f)",CLSS,name,value));

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
