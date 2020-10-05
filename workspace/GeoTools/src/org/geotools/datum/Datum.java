/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.datum;

import java.util.Date;
import org.geotools.data.wkt.IdentifiedObject;

/**
 * Specifies the relationship of a coordinate system to the earth, thus creating a {@linkplain
 * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}. A datum uses
 * a parameter or set of parameters that determine the location of the origin of the coordinate
 * reference system. Each datum subtype can be associated with only specific types of {@linkplain
 * org.opengis.referencing.cs.CoordinateSystem coordinate systems}.
 *
 * @version <A HREF="http://portal.opengeospatial.org/files/?artifact_id=6716">Abstract
 *     specification 2.0</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see org.opengis.referencing.cs.CoordinateSystem
 * @see org.opengis.referencing.crs.CoordinateReferenceSystem
 */
public interface Datum extends IdentifiedObject {
    /**
     * Key for the <code>{@value}</code> property to be given to the {@linkplain DatumFactory datum
     * factory} <code>createFoo(&hellip;)</code> methods. This is used for setting the value to be
     * returned by {@link #getAnchorPoint}.
     *
     * @see #getAnchorPoint
     */
    String ANCHOR_POINT_KEY = "anchorPoint";

    /**
     * Key for the <code>{@value}</code> property to be given to the {@linkplain DatumFactory datum
     * factory} <code>createFoo(&hellip;)</code> methods. This is used for setting the value to be
     * returned by {@link #getRealizationEpoch}.
     *
     * @see #getRealizationEpoch
     */
    String REALIZATION_EPOCH_KEY = "realizationEpoch";

    /**
     * Key for the <code>{@value}</code> property to be given to the {@linkplain DatumFactory datum
     * factory} <code>createFoo(&hellip;)</code> methods. This is used for setting the value to be
     * returned by {@link #getDomainOfValidity}.
     *
     * @see #getDomainOfValidity
     * @since GeoAPI 2.1
     */
    String DOMAIN_OF_VALIDITY_KEY = "domainOfValidity";

    /**
     * Key for the <code>{@value}</code> property to be given to the {@linkplain DatumFactory datum
     * factory} <code>createFoo(&hellip;)</code> methods. This is used for setting the value to be
     * returned by {@link #getScope}.
     *
     * @see #getScope
     */
    String SCOPE_KEY = "scope";

    /**
     * Description, possibly including coordinates, of the point or points used to anchor the datum
     * to the Earth. Also known as the "origin", especially for Engineering and Image Datums.
     *
     * <p>
     *
     * <ul>
     *   <li>For a geodetic datum, this point is also known as the fundamental point, which is
     *       traditionally the point where the relationship between geoid and ellipsoid is defined.
     *       In some cases, the "fundamental point" may consist of a number of points. In those
     *       cases, the parameters defining the geoid/ellipsoid relationship have then been averaged
     *       for these points, and the averages adopted as the datum definition.
     *   <li>For an engineering datum, the anchor point may be a physical point, or it may be a
     *       point with defined coordinates in another CRS.
     *   <li>For an image datum, the anchor point is usually either the centre of the image or the
     *       corner of the image.
     *   <li>For a temporal datum, this attribute is not defined. Instead of the anchor point, a
     *       temporal datum carries a separate time origin of type {@link Date}.
     * </ul>
     *
     * @return A description of the anchor point, or {@code null} if none.
     */
    String getAnchorPoint();

    /**
     * Description of domain of usage, or limitations of usage, for which this datum object is
     * valid.
     *
     * @return A description of domain of usage, or {@code null} if none.
     */
    String getScope();
}
