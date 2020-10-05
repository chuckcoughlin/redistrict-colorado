/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collection;
import java.util.Map;

/**
 * A set of pre-defined constants and static methods working on {@linkplain Citation citations}.
 * Pre-defined metadata constants are usually declared in implementation classes like {@link
 * ResponsiblePartyImpl}. But citations are an exception since they are extensively referenced in
 * the Geotools library, and handling citations requires some convenience methods. They are factored
 * out in this {@code Citations} class for clarity.
 *
 * <p>Citations may be about an <cite>organisation</cite> (e.g. {@linkplain #OPEN_GIS OpenGIS}), a
 * <cite>specification</cite> (e.g. {@linkplain #WMS}) or an <cite>authority</cite> that maintains
 * definitions of codes (e.g. {@linkplain #EPSG}). In the later case, the citation contains an
 * {@linkplain Citation#getIdentifiers identifier} which is the namespace of the codes maintained by
 * the authority. For example the identifier for the {@link #EPSG} citation is {@code "EPSG"}, and
 * EPSG codes look like {@code "EPSG:4326"}.
 *
 * @since 2.2
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett
 * @todo Classify the pre-defined constants using the javadoc {@code @category} tag once it will be
 *     available (targeted for J2SE 1.6).
 */
public final class Citations {
    /** Do not allows instantiation of this class. */
    private Citations() {}

    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////               O R G A N I S A T I O N S               ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> organisation.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium". An {@linkplain
     * Citation#getAlternateTitles alternate title} for this citation is "OGC" (according ISO 19115,
     * alternate titles often contain abreviations).
     *
     * @see ResponsiblePartyImpl#OGC
     * @see #OPEN_GIS
     */
    public static final Citation OGC;

    static {
        Citation c = new Citation("Open Geospatial Consortium");
        c.getAlternateTitles().add(new String("OGC"));
        OGC = c;
    }

    /**
     * The <A HREF="http://www.opengis.org">OpenGIS consortium</A> organisation. "OpenGIS
     * consortium" is the old name for "Open Geospatial consortium". {@linkplain
     * Citation#getAlternateTitles Alternate titles} for this citation are "OpenGIS" and "OGC"
     * (according ISO 19115, alternate titles often contain abreviations).
     *
     * @see ResponsiblePartyImpl#OPEN_GIS
     * @see #OGC
     */
    public static final Citation OPEN_GIS;

    static {
        Citation c = new Citation("OpenGIS Consortium");
        final Collection<String> alt = c.getAlternateTitles();
        alt.add("OpenGIS");
        alt.addAll(OGC.getAlternateTitles());
        OPEN_GIS = c;
    }

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> organisation.
     *
     * @see ResponsiblePartyImpl#ESRI
     */
    public static final Citation ESRI;

    static {
        Citation c = new Citation("ESRI");
        c.addAuthority("ESRI", true);
        ESRI = c;
    }

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> organisation.
     *
     * @see ResponsiblePartyImpl#ORACLE
     */
    public static final Citation ORACLE;

    static {
        Citation c = new Citation("Oracle");
        ORACLE = c;
    }

    /**
     * The <A HREF="http://postgis.refractions.net">PostGIS</A> project.
     *
     * @see ResponsiblePartyImpl#POSTGIS
     * @since 2.4
     */
    public static final Citation POSTGIS;

    static {
        Citation c = new Citation("PostGIS");
        POSTGIS = c;
    }

    /**
     * The <A HREF="http://www.geotools.org">Geotools</A> project.
     *
     * @see ResponsiblePartyImpl#GEOTOOLS
     */
    public static final Citation GEOTOOLS;

    static {
        
    	Citation c = new Citation("GeoTools");
        GEOTOOLS = c;
    }

    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////              S P E C I F I C A T I O N S              ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The Web Map Service specification. {@linkplain Citation#getAlternateTitles Alternate titles}
     * for this citation are "WMS", "WMS 1.3.0", "OGC 04-024" and "ISO 19128". Note that the version
     * numbers may be upgrated in future Geotools versions.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1 specification</A>
     * @see <A HREF="http://portal.opengis.org/files/?artifact_id=5316">WMS 1.3.0 specification</A>
     * @see ResponsiblePartyImpl#OGC
     * @see OnLineResourceImpl#WMS
     */
    public static final Citation WMS;

    static {
        Citation c = new Citation("Web Map Service");
        final Collection<String> titles = c.getAlternateTitles();
        titles.add("WMS");
        titles.add("WMS 1.3.0");
        titles.add("OGC 04-024");
        titles.add("ISO 19128");

        titles.add("OGC");
        c.getIdentifiers().put("Publisher", "WMS");
        WMS = c;
    }

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> specification.
     *
     * @see ResponsiblePartyImpl#GEOTIFF
     */
    public static final Citation GEOTIFF;

    static {
        Citation c = new Citation("GEOTIFF");
        GEOTIFF = c;
    }

    /**
     * The <A HREF="http://java.sun.com/products/java-media/jai">Java Advanced Imaging</A> library.
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is "JAI"
     * (according ISO 19115, alternate titles often contain abreviations).
     *
     * @see ResponsiblePartyImpl#SUN_MICROSYSTEMS
     */
    public static final Citation JAI;

    static {
        Citation c = new Citation("Java Advanced Imaging");
        c.getAlternateTitles().add("JAI");
        c.getAlternateTitles().add("Sun Microsystems");
        c.getAlternateTitles().add("Oracle");
        JAI = c;
    }

    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////             C R S   A U T H O R I T I E S             ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> authority. An
     * {@linkplain Citation#getAlternateTitles alternate title} for this citation is "EPSG"
     * (according ISO 19115, alternate titles often contain abreviations). In addition, this
     * citation contains the "EPSG" {@linkplain Citation#getIdentifiers identifier} for the
     * "Authority name" {@linkplain Citation#getIdentifierTypes identifier type}.
     *
     * <p>This citation is used as an authority for {@linkplain
     * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory
     * CRS authority factory} on EPSG data, Geotools compares the {@code "EPSG"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain
     * Citation#getTitle title} and {@linkplain Citation#getAlternateTitles alternate titles} if
     * there is no identifier) using the {@link #identifierMatches(Citation,String)
     * identifierMatches} method.
     *
     * @see ResponsiblePartyImpl#EPSG
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     */
    public static final Citation EPSG;

    static {
        Citation c = new Citation("EPSG");
        c.addAuthority("EPSG", true);
        EPSG = c;
    }

    /**
     * The <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1</A> "Automatic Projections"
     * authority. An {@linkplain Citation#getAlternateTitles alternate title} for this citation is
     * "AUTO" (according ISO 19115, alternate titles often contain abreviations). In addition, this
     * citation contains the "AUTO" {@linkplain Citation#getIdentifiers identifier} for the
     * "Authority name" {@linkplain Citation#getIdentifierTypes identifier type}.
     *
     * <p><strong>Warning:</strong> {@code AUTO} is different from {@link #AUTO2} used for WMS
     * 1.3.0.
     *
     * <p>This citation is used as an authority for {@linkplain
     * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory
     * CRS authority factory} on AUTO data, Geotools compares the {@code "AUTO"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain
     * Citation#getTitle title} and {@linkplain Citation#getAlternateTitles alternate titles} if
     * there is no identifier) using the {@link #identifierMatches(Citation,String)
     * identifierMatches} method.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1 specification</A>
     * @see #WMS
     * @see #AUTO2
     * @see #CRS
     * @see #EPSG
     */
    public static final Citation AUTO;

    static { // Sanity check ensure that all @see tags are actually available in the metadata
        final Citation c = new Citation("Automatic Projections");
        c.addAuthority("AUTO", false);
        /*
         * Do not put "WMS 1.1.1" and "OGC 01-068r3" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO authority name.
         */
        Map<String,String> identifiers = c.getIdentifiers();
        identifiers.put("Publisher","OCG"); 
        AUTO = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "Automatic Projections" authority. An {@linkplain Citation#getAlternateTitles
     * alternate title} for this citation is "AUTO2" (according ISO 19115, alternate titles often
     * contain abreviations). In addition, this citation contains the "AUTO2" {@linkplain
     * Citation#getIdentifiers identifier} for the "Authority name" {@linkplain
     * Citation#getIdentifierTypes identifier type}.
     *
     * <p><strong>Warning:</strong> {@code AUTO2} is different from {@link #AUTO} used for WMS 1.1.1
     * and earlier.
     *
     * <p>This citation is used as an authority for {@linkplain
     * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory
     * CRS authority factory} on AUTO2 data, Geotools compares the {@code "AUTO2"} string against
     * the {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain
     * Citation#getTitle title} and {@linkplain Citation#getAlternateTitles alternate titles} if
     * there is no identifier) using the {@link #identifierMatches(Citation,String)
     * identifierMatches} method.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://portal.opengis.org/files/?artifact_id=5316">WMS 1.3.0 specification</A>
     * @see #WMS
     * @see #AUTO
     * @see #CRS
     * @see #EPSG
     */
    public static final Citation AUTO2;

    static {
        Citation c = new Citation("Automatic Projections2");
        c.addAuthority("AUTO2", false);
        /*
         * Do not put "WMS 1.3.0" and "OGC 04-024" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO2 authority name.
         */
        final Collection<String> alt = c.getAlternateTitles();
        alt.add("OGC");
        Map<String,String> identifiers = c.getIdentifiers();
        identifiers.put("Publisher", "WMS");
        AUTO2 = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "CRS" authority. This is defined in the same document than {@link #AUTO2}.
     *
     * @see #WMS
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     * @see #EPSG
     */
    public static final Citation CRS;

    static {
        final Citation c = new Citation("Web Map Service CRS");
        c.addAuthority("CRS", false);
        c.getAlternateTitles().addAll(AUTO2.getAlternateTitles());
        CRS = c;
    }

    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////             End of constants declarations             ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /** List of citations declared in this class. */
    private static final Citation[] AUTHORITIES = {
        OGC, OPEN_GIS, ESRI, ORACLE, GEOTOOLS, WMS, GEOTIFF, JAI, EPSG, AUTO, AUTO2, CRS
    };

    /**
     * Returns a citation of the given name. If the given name matches a {@linkplain
     * Citation#getTitle title} or an {@linkplain Citation#getAlternateTitles alternate titles} of
     * one of the pre-defined constants ({@link #EPSG}, {@link #GEOTIFF}, <cite>etc.</cite>), then
     * this constant is returned. Otherwise, a new citation is created with the specified name as
     * the title.
     *
     * @param title The citation title (or alternate title).
     * @return A citation using the specified name
     */
    public static Citation fromName(final String title) {
        for (int i = 0; i < AUTHORITIES.length; i++) {
            final Citation citation = AUTHORITIES[i];
            if( citation.getTitle().equalsIgnoreCase(title)) {
                return citation;
            }
        }
        return new Citation(title);
    }
}
