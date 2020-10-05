/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Standardized resource reference (simplified)
 *
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett
 */
public class Citation {
    private static final long serialVersionUID = -4415559967618358778L;

    /** Name by which the cited resource is known. */
    private String title;

    /**
     * Short name or other language name by which the cited information is known. Example: "DCW" as
     * an alternative title for "Digital Chart of the World.
     */
    private final Collection<String> alternateTitles;

    /** Reference date for the cited resource. */
    private final Collection<Date> dates;

    /** Version of the cited resource. */
    private String edition;

    /**
     * Date of the edition in millisecondes ellapsed sine January 1st, 1970, or {@link
     * Long#MIN_VALUE} if none.
     */
    private long editionDate = Long.MIN_VALUE;

    /**
     * Unique identifier for the resource. Example: Universal Product Code (UPC), National Stock
     * Number (NSN).
     */
    private final Map<String,String> identifiers;


    /** Constructs an initially empty citation. */
    public Citation() {
    	this.alternateTitles = new ArrayList<>();
    	this.identifiers = new HashMap<>();
    	this.dates = new ArrayList<>();
    }


    /**
     * Constructs a citation with the specified title.
     *
     * @param title The title, as a {@link String} or an {@link InternationalString} object.
     */
    public Citation(final String title) {
    	this();
        setTitle(title);
    }



    /**
     * Adds the specified identifier as a CRS authority factory. This is used as a convenience
     * method for the creation of constants, and for making sure that all of them use the same
     * identifier type.
     */
    final void addAuthority(final String identifier, final boolean asTitle) {
        if (asTitle) {
            getAlternateTitles().add(identifier);
        }
        getIdentifiers().put(identifier,identifier);
    }

    /** Returns the name by which the cited resource is known. */
    public String getTitle() {return title; }
    public void setTitle(final String newValue) {title = newValue; }

    /**
     * Returns the short name or other language name by which the cited information is known.
     * Example: "DCW" as an alternative title for "Digital Chart of the World".
     */
    public Collection<String> getAlternateTitles() {
        return (alternateTitles);
    }

    /** Returns the reference date for the cited resource. */
    public Collection<Date> getDates() {
        return dates ;
    }


    /** Returns the version of the cited resource. */
    public String getEdition() {
        return edition;
    }

    /** Set the version of the cited resource. */
    public void setEdition(final String newValue) {
        edition = newValue;
    }

    /** Returns the date of the edition, or {@code null} if none. */
    public Date getEditionDate() {
        return (editionDate != Long.MIN_VALUE) ? new Date(editionDate) : null;
    }

    /**
     * Set the date of the edition, or {@code null} if none.
     *
     * @todo Use an unmodifiable {@link Date} here.
     */
    public void setEditionDate(final Date newValue) {
        editionDate = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the unique identifier for the resource. Example: Universal Product Code (UPC),
     * National Stock Number (NSN).
     */
    public Map<String,String> getIdentifiers() {
        return (identifiers);
    }
    
    public void setIdentifier(String key,String name) { identifiers.put(key,name); }
}
