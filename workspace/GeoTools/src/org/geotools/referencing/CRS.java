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
 */
package org.geotools.referencing;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.xml.crypto.dsig.TransformException;

import org.geotools.geometry.GeneralEnvelope;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;
import org.geotools.referencing.util.CRSUtilities;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.SingleCRS;

/**
 * Simple utility class for making use of the {@linkplain CoordinateReferenceSystem coordinate
 * reference system} and associated {@linkplain org.opengis.referencing.Factory} implementations.
 * This utility class is made up of static final functions. This class is not a factory or a
 * builder. It makes use of the GeoAPI factory interfaces provided by {@link
 * ReferencingFactoryFinder}.
 *
 * <p>The following methods may be added in a future version:
 *
 * <ul>
 *   <li>{@code CoordinateReferenceSystem parseXML(String)}
 * </ul>
 *
 * <p>When using {@link CoordinateReferenceSystem} matching methods of this class ({@link
 * #equalsIgnoreMetadata(Object, Object)},{@link #lookupIdentifier(IdentifiedObject, boolean)},
 * {@link #lookupEpsgCode(CoordinateReferenceSystem, boolean)}, {@link
 * #lookupIdentifier(IdentifiedObject, boolean)}, {@link #lookupIdentifier(Citation,
 * CoordinateReferenceSystem, boolean)}) against objects derived from a database other than the
 * official EPSG one it may be advisable to set a non zero comparison tolerance with {@link
 * Hints#putSystemDefault(java.awt.RenderingHints.Key, Object)} using the {@link
 * Hints#COMPARISON_TOLERANCE} key. A value of 10e-9 has proved to give satisfactory results with
 * definitions commonly found in .prj files accompaining shapefiles and georeferenced images.<br>
 * <b>Warning</b>: the tolerance value is used in all internal comparison, this will also change the
 * way math transforms are setup. Use with care.
 *
 * @since 2.1
 * @author Jody Garnett (Refractions Research)
 * @author Martin Desruisseaux
 * @author Andrea Aime
 */
public final class CRS {
	private final static String CLSS = "CRS";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    static volatile AtomicBoolean FORCED_LON_LAT = null;

    /** Enumeration describing axis order for geographic coordinate reference systems. */
    public static enum AxisOrder {
        /**
         * Ordering in which longitude comes before latitude, commonly referred to as x/y ordering.
         */
        EAST_NORTH,
        NORTH_EAST,
        /** Indicates axis ordering is not applicable to the coordinate reference system. */
        INAPPLICABLE;
    };

    /** A map with {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER} set to {@link Boolean#TRUE}. */
    private static final Hints FORCE_LONGITUDE_FIRST_AXIS_ORDER =
            new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);



    /** Do not allow instantiation of this class. */
    private CRS() {}

    //////////////////////////////////////////////////////////////
    ////                                                      ////
    ////                  CRS CREATION                        ////
    ////                                                      ////
    //////////////////////////////////////////////////////////////

  

    /**
     * Returns the domain of validity for the specified coordinate reference system, or {@code null}
     * if unknown.
     *
     * <p>This method fetchs the {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain of
     * validity} associated with the given CRS. Only {@linkplain GeographicExtent geographic
     * extents} of kind {@linkplain BoundingPolygon bounding polygon} are taken in account. If none
     * are found, then the {@linkplain #getGeographicBoundingBox geographic bounding boxes} are used
     * as a fallback.
     *
     * <p>The returned envelope is expressed in terms of the specified CRS.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @return The envelope in terms of the specified CRS, or {@code null} if none.
     * @see #getGeographicBoundingBox
     * @see org.geotools.geometry.GeneralEnvelope#normalize
     * @since 2.2
     */
    public static Envelope getEnvelope(final CoordinateReferenceSystem crs) {
        Envelope envelope = null;
        GeneralEnvelope merged = null;
        if (crs != null) {
            final Extent domainOfValidity = crs.getDomainOfValidity();
            if (domainOfValidity != null) {
                for (final GeographicExtent extent : domainOfValidity.getGeographicElements()) {
                    if (Boolean.FALSE.equals(extent.getInclusion())) {
                        continue;
                    }
                    if (extent instanceof BoundingPolygon) {
                        for (final Geometry geometry : ((BoundingPolygon) extent).getPolygons()) {
                            final Envelope candidate = geometry.getEnvelope();
                            if (candidate != null) {
                                final CoordinateReferenceSystem sourceCRS =
                                        candidate.getCoordinateReferenceSystem();
                                if (sourceCRS == null || equalsIgnoreMetadata(sourceCRS, crs)) {
                                    if (envelope == null) {
                                        envelope = candidate;
                                    } else {
                                        if (merged == null) {
                                            envelope = merged = new GeneralEnvelope(envelope);
                                        }
                                        merged.add(envelope);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
         * If no envelope was found, uses the geographic bounding box as a fallback. We will
         * need to transform it from WGS84 to the supplied CRS. This step was not required in
         * the previous block because the later selected only envelopes in the right CRS.
         */
        if (envelope == null) {
            final GeographicBoundingBox bounds = getGeographicBoundingBox(crs);
            if (bounds != null && !Boolean.FALSE.equals(bounds.getInclusion())) {
                envelope =
                        merged =
                                new GeneralEnvelope(
                                        new double[] {
                                            bounds.getWestBoundLongitude(),
                                            bounds.getSouthBoundLatitude()
                                        },
                                        new double[] {
                                            bounds.getEastBoundLongitude(),
                                            bounds.getNorthBoundLatitude()
                                        });
                /*
                 * We do not assign WGS84 inconditionnaly to the geographic bounding box, because
                 * it is not defined to be on a particular datum; it is only approximative bounds.
                 * We try to get the GeographicCRS from the user-supplied CRS and fallback on WGS
                 * 84 only if we found none.
                 */
                final SingleCRS targetCRS = getHorizontalCRS(crs);
                final GeographicCRS sourceCRS = CRSUtilities.getStandardGeographicCRS2D(targetCRS);
                merged.setCoordinateReferenceSystem(sourceCRS);
                try {
                    envelope = transform(envelope, targetCRS);
                } catch (TransformException exception) {
                    /*
                     * The envelope is probably outside the range of validity for this CRS.
                     * It should not occurs, since the envelope is supposed to describe the
                     * CRS area of validity. Logs a warning and returns null, since it is a
                     * legal return value according this method contract.
                     */
                    envelope = null;
                    unexpectedException("getEnvelope", exception);
                }
                /*
                 * If transform(...) created a new envelope, its CRS is already targetCRS so it
                 * doesn't matter if 'merged' is not anymore the right instance. If 'transform'
                 * returned the envelope unchanged, the 'merged' reference still valid and we
                 * want to ensure that it have the user-supplied CRS.
                 */
                merged.setCoordinateReferenceSystem(targetCRS);
            }
        }
        return envelope;
    }

    /**
     * Returns the valid geographic area for the specified coordinate reference system, or {@code
     * null} if unknown.
     *
     * <p>This method fetchs the {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain of
     * validity} associated with the given CRS. Only {@linkplain GeographicExtent geographic
     * extents} of kind {@linkplain GeographicBoundingBox geographic bounding box} are taken in
     * account.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @return The geographic area, or {@code null} if none.
     * @see #getEnvelope
     * @since 2.3
     */
    public static GeographicBoundingBox getGeographicBoundingBox(
            final CoordinateReferenceSystem crs) {
        GeographicBoundingBox bounds = null;
        GeographicBoundingBoxImpl merged = null;
        if (crs != null) {
            final Extent domainOfValidity = crs.getDomainOfValidity();
            if (domainOfValidity != null) {
                for (final GeographicExtent extent : domainOfValidity.getGeographicElements()) {
                    if (extent instanceof GeographicBoundingBox) {
                        final GeographicBoundingBox candidate = (GeographicBoundingBox) extent;
                        if (bounds == null) {
                            bounds = candidate;
                        } else {
                            if (merged == null) {
                                bounds = merged = new GeographicBoundingBoxImpl(bounds);
                            }
                            merged.add(candidate);
                        }
                    }
                }
            }
        }
        return bounds;
    }

 
    /**
    
    /**
     * Compares the specified objects for equality. If both objects are Geotools implementations of
     * class {@link AbstractIdentifiedObject}, then this method will ignore the metadata during the
     * comparaison.
     *
     * @param object1 The first object to compare (may be null).
     * @param object2 The second object to compare (may be null).
     * @return {@code true} if both objects are equals.
     * @since 2.2
     */
    public static boolean equalsIgnoreMetadata(final Object object1, final Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 instanceof AbstractIdentifiedObject
                && object2 instanceof AbstractIdentifiedObject) {
            return ((AbstractIdentifiedObject) object1)
                    .equals(((AbstractIdentifiedObject) object2), false);
        }
        return object1 != null && object1.equals(object2);
    }

}
