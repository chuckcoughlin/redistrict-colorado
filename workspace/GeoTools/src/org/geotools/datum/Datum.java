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

import java.util.Date;
import java.util.Map;

import org.geotools.data.wkt.IdentifiedObject;

/**
 * Specifies the relationship of a coordinate system to the earth, thus creating a {@linkplain
 * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}. A datum uses
 * a parameter or set of parameters that determine the location of the origin of the coordinate
 * reference system. Each datum subtype can be associated with only specific types of {@linkplain
 * org.opengis.referencing.cs.AbstractCS coordinate systems}.
 *
 * <p>A datum can be defined as a set of real points on the earth that have coordinates. The
 * definition of the datum may also include the temporal behavior (such as the rate of change of the
 * orientation of the coordinate axes).
 *
 * <p>This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @since 2.1
 * @see org.geotools.referencing.cs.AbstractCS
 * @see org.geotools.referencing.crs.AbstractCRS
 */
public class Datum extends IdentifiedObject {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -4894180465652474930L;
    private final Map<String,Object> properties;

    public static final String ANCHOR_POINT_KEY = "anchorPoint";
    public static final String REALIZATION_EPOCH_KEY = "realizationEpoch";
    public static final String DOMAIN_OF_VALIDITY_KEY = "domainOfValidity";
    public static final String SCOPE_KEY = "scope";


    /**
     * Constructs a datum from a set of properties. The properties given in argument follow the same
     * rules than for the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}. Additionally, the following properties are understood by this
     * constructor: <br>
     * <br>
     *
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #ANCHOR_POINT_KEY "anchorPoint"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link InternationalString} or {@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getAnchorPoint}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #REALIZATION_EPOCH_KEY "realizationEpoch"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Date}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRealizationEpoch}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #DOMAIN_OF_VALIDITY_KEY "domainOfValidity"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Extent}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getDomainOfValidity}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #SCOPE_KEY "scope"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link InternationalString} or {@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getScope}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to the identified object.
     */
    public Datum(final Map<String,Object> props) {
        this.properties = props;
    }
    
    public String getName() { return (String)properties.get(NAME_KEY); }
    public Map<String,Object> getProperties() { return this.properties; }
   

    /**
     * Description, possibly including coordinates, of the point or points used to anchor the datum
     * to the Earth. Also known as the "origin", especially for Engineering and Image Datums.
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
     */
    public String getAnchorPoint() {
        return (String)properties.get(ANCHOR_POINT_KEY);
    }


    /**
     * Description of domain of usage, or limitations of usage, for which this datum object is
     * valid.
     */
    public String getScope() {
    	return (String)properties.get(SCOPE_KEY);
    }
}
