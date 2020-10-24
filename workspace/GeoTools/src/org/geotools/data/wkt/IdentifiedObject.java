/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.data.wkt;

/**
 * Supplementary identification and remarks information for a CRS or CRS-related object. When {@link
 * org.opengis.referencing.crs.CRSAuthorityFactory} is used to create an object, the {@linkplain
 * ReferenceIdentifier#getAuthority authority} and {@linkplain ReferenceIdentifier#getCode authority
 * code} values should be set to the authority name of the factory object, and the authority code
 * supplied by the client, respectively. The other values may or may not be set. If the authority is
 * EPSG, the implementer may consider using the corresponding metadata values in the EPSG tables.
 *
 * @departure ISO 19111 defines also an {@code IdentifiedObjectBase} interface. The later is omitted
 *     in GeoAPI because the split between {@code IdentifiedObject} and {@code IdentifiedObjectBase}
 *     in OGC/ISO specification was mostly a workaround for introducing {@code IdentifiedObject} in
 *     ISO 19111 without changing the {@code ReferenceSystem} definition in ISO 19115.
 * @version <A HREF="http://portal.opengeospatial.org/files/?artifact_id=6716">Abstract
 *     specification 2.0</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 2.0
 */
public class IdentifiedObject {
    /**
     * Keys for well-known properties. These are always lower-case.
     */
    public static final String NAME_KEY = "name";
    public static final String ALIAS_KEY = "alias";
    public static final String IDENTIFIERS_KEY = "identifiers";
    public static final String REMARKS_KEY = "remarks";
    
    /**
     * Resource keys taken from org.geotools.metadata.i18n.VocabularyKeys.
     * We've added the accompanying names where needed.
     */
    public static final int CARTESIAN = 14;
    public static final int DEPTH = 44;
    public static final int ELLIPSOID = 56;
    public static final int ELLIPSOIDAL = 57;
    public static final int ERROR = 63;
    public static final int EXCEPTION = 67;
    public static final int GEOCENTRIC = 77;
    public static final int GEOIDAL = 88;
    public static final int IDENTITY = 102;
    public static final int LATITUDE = 120;
    public static final int LONGITUDE = 132;
    public static final int MATH_TRANSFORM = 135;
    
    public static final String GEODIAL_NAME = "GEODIAL";
    public static final String ELLIPSOIDAL_NAME = "ELLIPSOIDAL";
}
