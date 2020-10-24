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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.operation.matrix.Matrix;



/**
 * Defines the location and precise orientation in 3-dimensional space of a defined ellipsoid (or
 * sphere) that approximates the shape of the earth. Used also for Cartesian coordinate system
 * centered in this ellipsoid (or sphere).
 *
 */
public class GeodeticDatum extends Datum  {
    private static final long serialVersionUID = 8832100095648302943L;

    /** The default WGS 1984 datum. */
    public static final GeodeticDatum WGS84;

    static {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(NAME_KEY, "WGS84");
        properties.put(ALIAS_KEY, "WGS 84");
        WGS84 = new GeodeticDatum(
                        properties, Ellipsoid.WGS84, PrimeMeridian.GREENWICH);
    }

    /**
     * The <code>{@value #BURSA_WOLF_KEY}</code> property for {@linkplain #getAffineTransform datum
     * shifts}.
     */
    public static final String BURSA_WOLF_KEY = "bursaWolf";

    /** The ellipsoid. */
    private final Ellipsoid ellipsoid;

    /** The prime meridian. */
    private final PrimeMeridian primeMeridian;

    /** Bursa Wolf parameters for datum shifts, or {@code null} if none. */
    private final BursaWolfParameters[] bursaWolf;

    /**
     * Constructs a new datum with the same values than the specified one. This copy constructor
     * provides a way to wrap an arbitrary implementation into a Geotools one or a user-defined one
     * (as a subclass), usually in order to leverage some implementation-specific API. This
     * constructor performs a shallow copy, i.e. the properties are not cloned.
     *
     * @since 2.2
     */
    public GeodeticDatum(final GeodeticDatum datum) {
        super(datum.getProperties());
        ellipsoid = datum.getEllipsoid();
        primeMeridian = datum.getPrimeMeridian();
        bursaWolf = datum.bursaWolf;
    }

    /**
     * Constructs a geodetic datum from a name.
     *
     * @param name The datum name.
     * @param ellipsoid The ellipsoid.
     * @param primeMeridian The prime meridian.
     */
    public GeodeticDatum(
            final String name, final Ellipsoid ellipsoid, final PrimeMeridian primeMeridian) {
        this(Collections.singletonMap(NAME_KEY, name), ellipsoid, primeMeridian);
    }

    /**
     * Constructs a geodetic datum from a set of properties. The properties map is given unchanged
     * to the {@linkplain Datum#AbstractDatum(Map) super-class constructor}. Additionally,
     * the following properties are understood by this construtor:
     *
     * <p>
     *
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #BURSA_WOLF_KEY "bursaWolf"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link BursaWolfParameters} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getBursaWolfParameters}</td>
     *   </tr>
     * </table>
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param ellipsoid The ellipsoid.
     * @param primeMeridian The prime meridian.
     */
    public GeodeticDatum(
            final Map<String, Object> properties,
            final Ellipsoid ellipsoid,
            final PrimeMeridian primeMeridian) {
        super(properties);
        this.ellipsoid = ellipsoid;
        this.primeMeridian = primeMeridian;
        BursaWolfParameters[] bursaWolf;
        final Object object = properties.get(BURSA_WOLF_KEY);
        if (object instanceof BursaWolfParameters) {
            bursaWolf = new BursaWolfParameters[] {((BursaWolfParameters) object).clone()};
        } else {
            bursaWolf = (BursaWolfParameters[]) object;
            if (bursaWolf != null) {
                if (bursaWolf.length == 0) {
                    bursaWolf = null;
                } else {
                    final Set<BursaWolfParameters> s = new LinkedHashSet<BursaWolfParameters>();
                    for (int i = 0; i < bursaWolf.length; i++) {
                        s.add(bursaWolf[i].clone());
                    }
                    bursaWolf = s.toArray(new BursaWolfParameters[s.size()]);
                }
            }
        }
        this.bursaWolf = bursaWolf;
    }

    /** Returns the ellipsoid. */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /** Returns the prime meridian. */
    public PrimeMeridian getPrimeMeridian() {
        return primeMeridian;
    }

    /**
     * Returns all Bursa Wolf parameters specified in the {@code properties} map at construction
     * time.
     *
     * @since 2.4
     */
    public BursaWolfParameters[] getBursaWolfParameters() {
        if (bursaWolf != null) {
            return bursaWolf.clone();
        }
        return new BursaWolfParameters[0];
    }

    /**
     * Returns Bursa Wolf parameters for a datum shift toward the specified target, or {@code null}
     * if none. This method search only for Bursa-Wolf parameters explicitly specified in the {@code
     * properties} map at construction time. This method doesn't try to infer a set of parameters
     * from indirect informations. For example it doesn't try to inverse the parameters specified in
     * the {@code target} datum if none were found in this datum. If such an elaborated search is
     * wanted, use {@link #getAffineTransform} instead.
     */
    public BursaWolfParameters getBursaWolfParameters(final GeodeticDatum target) {
        if (bursaWolf != null) {
            for (int i = 0; i < bursaWolf.length; i++) {
                final BursaWolfParameters candidate = bursaWolf[i];
                if( target.equals(candidate.targetDatum) ) {
                    return candidate.clone();
                }
            }
        }
        return null;
    }

    /**
     * Returns a matrix that can be used to define a transformation to the specified datum. If no
     * transformation path is found, then this method returns {@code null}.
     *
     * @param source The source datum.
     * @param target The target datum.
     * @return An affine transform from {@code source} to {@code target}, or {@code null} if none.
     * @see BursaWolfParameters#getAffineTransform
     */
    public static Matrix getAffineTransform(
            final GeodeticDatum source, final GeodeticDatum target) {
        return getAffineTransform(source, target, null);
    }

    /**
     * Returns a matrix that can be used to define a transformation to the specified datum. If no
     * transformation path is found, then this method returns {@code null}.
     *
     * @param source The source datum, must be non-null.
     * @param target The target datum, must be non-null.
     * @param exclusion The set of datum to exclude from the search, or {@code null}. This is used
     *     in order to avoid never-ending recursivity.
     * @return An affine transform from {@code source} to {@code target}, or {@code null} if none.
     * @see BursaWolfParameters#getAffineTransform
     */
    private static Matrix getAffineTransform(
            final GeodeticDatum source, final GeodeticDatum target, Set<GeodeticDatum> exclusion) {
        if (source instanceof GeodeticDatum) {
            final BursaWolfParameters[] bursaWolf = ((GeodeticDatum) source).bursaWolf;
            if (bursaWolf != null) {
                for (int i = 0; i < bursaWolf.length; i++) {
                    final BursaWolfParameters transformation = bursaWolf[i];
                    if( target.equals(transformation.targetDatum)) {
                        return transformation.getAffineTransform();
                    }
                }
            }
        }
        /*
         * No transformation found to the specified target datum.
         * Search if a transform exists in the opposite direction.
         */
        if (target instanceof GeodeticDatum) {
            final BursaWolfParameters[] bursaWolf = ((GeodeticDatum) target).bursaWolf;
            if (bursaWolf != null) {
                for (int i = 0; i < bursaWolf.length; i++) {
                    final BursaWolfParameters transformation = bursaWolf[i];
                    if( source.equals(transformation.targetDatum) ) {
                        final Matrix matrix = transformation.getAffineTransform();
                        matrix.invert();
                        return matrix;
                    }
                }
            }
        }
        /*
         * No direct tranformation found. Search for a path through some intermediate datum.
         * First, search if there is some BursaWolfParameters for the same target in both
         * 'source' and 'target' datum. If such an intermediate is found, ask for a path
         * as below:
         *
         *    source   -->   [common datum]   -->   target
         */
        if (source instanceof GeodeticDatum && target instanceof GeodeticDatum) {
            final BursaWolfParameters[] sourceParam = ((GeodeticDatum) source).bursaWolf;
            final BursaWolfParameters[] targetParam = ((GeodeticDatum) target).bursaWolf;
            if (sourceParam != null && targetParam != null) {
                GeodeticDatum sourceStep;
                GeodeticDatum targetStep;
                for (int i = 0; i < sourceParam.length; i++) {
                    sourceStep = sourceParam[i].targetDatum;
                    for (int j = 0; j < targetParam.length; j++) {
                        targetStep = targetParam[j].targetDatum;
                        if( sourceStep.equals(targetStep) ) {
                            final Matrix step1, step2;
                            if (exclusion == null) {
                                exclusion = new HashSet<GeodeticDatum>();
                            }
                            if (exclusion.add(source)) {
                                if (exclusion.add(target)) {
                                    step1 = getAffineTransform(source, sourceStep, exclusion);
                                    if (step1 != null) {
                                        step2 = getAffineTransform(targetStep, target, exclusion);
                                        if (step2 != null) {
                                            /*
                                             * Note: XMatrix.multiply(XMatrix) is equivalent to
                                             *       AffineTransform.concatenate(...): First
                                             *       transform by the supplied transform and
                                             *       then transform the result by the original
                                             *       transform.
                                             */
                                            step2.multiply(step1);
                                            return step2;
                                        }
                                    }
                                    exclusion.remove(target);
                                }
                                exclusion.remove(source);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified object is equals (at least on computation purpose) to
     * the {@link #WGS84} datum. This method may conservatively returns {@code false} if the
     * specified datum is uncertain (for example because it come from an other implementation).
     */
    public static boolean isWGS84(final Datum datum) {
        return datum != null && datum.equals(WGS84);
    }

}
