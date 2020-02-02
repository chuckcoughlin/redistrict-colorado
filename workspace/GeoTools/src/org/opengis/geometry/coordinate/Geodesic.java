/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.opengis.geometry.coordinate;

import java.util.List;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.opengis.geometry.CurveInterpolation;

/**
 * This interface has been merged from: GeodesicString, CurveSegment, GenericSegment, Curve.
 * Enhance as needed to add methods required by the app.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as">ISO 19107</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see GeometryFactory#createGeodesicString
 */
public interface Geodesic {
    /**
     * Returns a sequence of positions between which this {@code GeodesicString} is interpolated
     * using geodesics from the geoid or {@linkplain org.opengis.referencing.datum.Ellipsoid
     * ellipsoid} of the {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem
     * coordinate reference system} being used. The organization of these points is identical to
     * that in {@link LineString}.
     *
     * @return The control points.
     */
    PointArray getControlPoints();

    /**
     * The interpolation for a {@code GeodesicString} is "{@linkplain CurveInterpolation#GEODESIC
     * geodesic}".
     *
     * @return Always {@link CurveInterpolation#GEODESIC}.
     */
    CurveInterpolation getInterpolation();

    /**
     * Decomposes a geodesic string into an equivalent sequence of geodesic segments.
     *
     * @return The equivalent sequence of geodesic segments.
     */
    List<Geodesic> asGeodesics();
}
