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
public interface IdentifiedObject {
    /**
     * Keys for well-known properties. These are always lower-case.
     */
    String NAME_KEY = "name";
    String ALIAS_KEY = "alias";
    String IDENTIFIERS_KEY = "identifiers";
    String REMARKS_KEY = "remarks";
}
