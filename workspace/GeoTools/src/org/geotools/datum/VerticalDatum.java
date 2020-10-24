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

import java.util.Collections;
import java.util.Map;

import org.geotools.data.wkt.IdentifiedObject;

/**
 * A textual description and/or a set of parameters identifying a particular reference level surface
 * used as a zero-height surface. The description includes its position with respect to the Earth
 * for any of the height types recognized by this standard. There are several types of vertical
 * datums, and each may place constraints on the {@linkplain
 * org.opengis.referencing.cs.CoordinateSystemAxis coordinate system axis} with which it is combined
 */
public class VerticalDatum extends Datum  {
    private static final long serialVersionUID = 380347456670516572L;
    
    // Constants taken from org.geotools.metadata.i18n.VocabularyKeys.

    /** The type of this vertical datum. Default is "geoidal". */
    private int verticalDatumType = GEOIDAL;

    /**
     * Constructs a vertical datum from a set of properties. The properties map is given unchanged
     * to the {@linkplain Datum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param type The type of this vertical datum.
     */
    public VerticalDatum(final Map<String, Object> properties, final int type) {
        super(properties);
        this.verticalDatumType = type;
    }
    
    /**
     * Constructs a vertical datum from a name.
     *
     * @param name The datum name.
     * @param type The type of this vertical datum.
     */
    public VerticalDatum(final String name, final int type) {
        this(Collections.singletonMap(NAME_KEY, name), type);
    }

    /** Default vertical datum for {@linkplain VerticalDatumType#GEOIDAL geoidal heights}. */
    public static final VerticalDatum GEOIDAL_DAATUM = new VerticalDatum(GEODIAL_NAME, GEOIDAL);

    /**
     * Default vertical datum for ellipsoidal heights. Ellipsoidal heights are measured along the
     * normal to the ellipsoid used in the definition of horizontal datum.
     */
    public static final VerticalDatum ELLIPSOIDAL_DATUM = new VerticalDatum(ELLIPSOIDAL_NAME, ELLIPSOIDAL);



    public int getVerticalDatumType() { return verticalDatumType;}
}
