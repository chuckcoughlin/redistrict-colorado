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
 */
package org.geotools.referencing.operation;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Utilities;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.opengis.geometry.MismatchedDimensionException;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateSystem;

/**
 * A helper class for building <var>n</var>-dimensional {@linkplain AffineTransform affine
 * transform} mapping {@linkplain GridEnvelope grid ranges} to {@linkplain Envelope envelopes}. The
 * affine transform will be computed automatically from the information specified by the {@link
 * #setGridRange setGridRange} and {@link #setEnvelope setEnvelope} methods, which are mandatory.
 * All other setter methods are optional hints about the affine transform to be created. This
 * builder is convenient when the following conditions are meet:
 *
 * <p>
 *
 * <ul>
 *   <li>
 *       <p>Pixels coordinates (usually (<var>x</var>,<var>y</var>) integer values inside the
 *       rectangle specified by the grid range) are expressed in some {@linkplain
 *       CoordinateReferenceSystem coordinate reference system} known at compile time. This is often
 *       the case. For example the CRS attached to {@link BufferedImage} has always ({@linkplain
 *       AxisDirection#COLUMN_POSITIVE column}, {@linkplain AxisDirection#ROW_POSITIVE row}) axis,
 *       with the origin (0,0) in the upper left corner, and row values increasing down.
 *   <li>
 *       <p>"Real world" coordinates (inside the envelope) are expressed in arbitrary
 *       <em>horizontal</em> coordinate reference system. Axis directions may be ({@linkplain
 *       AxisDirection#NORTH North}, {@linkplain AxisDirection#WEST West}), or ({@linkplain
 *       AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}), <cite>etc.</cite>.
 * </ul>
 *
 * <p>In such case (and assuming that the image's CRS has the same characteristics than the {@link
 * BufferedImage}'s CRS described above):
 *
 * <p>
 *
 * <ul>
 *   <li>
 *       <p>{@link #setSwapXY swapXY} shall be set to {@code true} if the "real world" axis order is
 *       ({@linkplain AxisDirection#NORTH North}, {@linkplain AxisDirection#EAST East}) instead of
 *       ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}). This axis
 *       swapping is necessary for mapping the ({@linkplain AxisDirection#COLUMN_POSITIVE column},
 *       {@linkplain AxisDirection#ROW_POSITIVE row}) axis order associated to the image CRS.
 *   <li>
 *       <p>In addition, the "real world" axis directions shall be reversed (by invoking <code>
 *       {@linkplain #reverseAxis reverseAxis}(dimension)</code>) if their direction is {@link
 *       AxisDirection#WEST WEST} (<var>x</var> axis) or {@link AxisDirection#NORTH NORTH}
 *       (<var>y</var> axis), in order to get them oriented toward the {@link AxisDirection#EAST
 *       EAST} or {@link AxisDirection#SOUTH SOUTH} direction respectively. The later may seems
 *       unatural, but it reflects the fact that row values are increasing down in an {@link
 *       BufferedImage}'s CRS.
 * </ul>
 *
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class GridToEnvelopeMapper {
	private final static String CLSS = "GridToEnvelopeMapper";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	public static final int ANCHOR_CELL_CENTER = 1;
	public static final int ANCHOR_CELL_CORNER = 2;

    /** The grid range, or {@code null} if not yet specified. */
    private ReferencedEnvelope gridRange;

    /** The envelope, or {@code null} if not yet specified. */
    private ReferencedEnvelope envelope;

    /**
     * Whatever the {@code gridToCRS} transform will maps pixel center or corner. The default value
     * is {@link PixelInCell#CELL_CENTER}.
     */
    private int anchor = ANCHOR_CELL_CENTER;

    /** The math transform, or {@code null} if not yet computed. */
    private AffineTransformation transform;

    /** Creates a new instance of {@code GridToEnvelopeMapper}. */
    public GridToEnvelopeMapper() {}

    /**
     * Creates a new instance for the specified grid range and envelope.
     *
     * @param gridRange The valid coordinate range of a grid coverage.
     * @param userRange The corresponding coordinate range in user coordinate. This envelope must
     *     contains entirely all pixels, i.e. the envelope's upper left corner must coincide with
     *     the upper left corner of the first pixel and the envelope's lower right corner must
     *     coincide with the lower right corner of the last pixel.
     * @throws MismatchedDimensionException if the grid range and the envelope doesn't have
     *     consistent dimensions.
     */
    public GridToEnvelopeMapper(final ReferencedEnvelope gridRange, final ReferencedEnvelope userRange) {
        this.gridRange = gridRange;
        this.envelope = userRange;
    }

    /**
     * Returns whatever the grid range maps {@linkplain PixelInCell#CELL_CENTER pixel center} or
     * {@linkplain PixelInCell#CELL_CORNER pixel corner}. The former is OGC convention while the
     * later is Java2D/JAI convention. The default is cell center (OGC convention).
     *
     * @return Whatever the grid range maps pixel center or corner.
     * @since 2.5
     */
    public int getPixelAnchor() {
        return anchor;
    }

    /**
     * Sets whatever the grid range maps {@linkplain PixelInCell#CELL_CENTER pixel center} or
     * {@linkplain PixelInCell#CELL_CORNER pixel corner}. The former is OGC convention while the
     * later is Java2D/JAI convention.
     *
     * @param anchor Whatever the grid range maps pixel center or corner.
     * @since 2.5
     */
    public void setPixelAnchor(final int anchor) {
        if(this.anchor!=anchor ) {
            this.anchor = anchor;
        }
    }

    /**
     * Returns the grid range.
     *
     * @return The grid range.
     * @throws IllegalStateException if the grid range has not yet been defined.
     */
    public ReferencedEnvelope getGridRange() throws IllegalStateException {
        if (gridRange == null) {
            throw new IllegalStateException(String.format("%s.getGridRange: Grid range never set", CLSS));
        }
        return gridRange;
    }

    /**
     * Sets the grid range.
     *
     * @param gridRange The new grid range.
     */
    public void setGridRange(final ReferencedEnvelope gridRange) {
        if (!Utilities.equals(this.gridRange, gridRange)) {
            this.gridRange = gridRange;
        }
    }

    /**
     * Returns the envelope. For performance reason, this method do not clone the envelope. So the
     * returned object should not be modified.
     *
     * @return The envelope.
     * @throws IllegalStateException if the envelope has not yet been defined.
     */
    public ReferencedEnvelope getEnvelope() throws IllegalStateException {
        if (envelope == null) {
        	throw new IllegalStateException(String.format("%s.getEnvelope: envelope never set", CLSS));
        }
        return envelope;
    }

    /**
     * Sets the envelope. This method do not clone the specified envelope, so it should not be
     * modified after this method has been invoked.
     *
     * @param envelope The new envelope.
     */
    public void setEnvelope(final ReferencedEnvelope envelope) {
        if (!Utilities.equals(this.envelope, envelope)) {
            this.envelope = envelope;
        }
    }

    /**
     * Returns true if the axis in <cite>user</cite> space (not grid space) should have their
     * direction reversed. 
     * <p>
     *
     * <ul>
     *   <li>Axis should be reverted if needed in order to point toward their "{@linkplain
     *       AxisDirection#absolute absolute}" direction.
     *   <li>An exception to the above rule is the second axis in grid space, which is assumed to be
     *       the <var>y</var> axis on output device (usually the screen). This axis is reversed
     *       again in order to match the bottom direction often used with such devices.
     * </ul>
     *
     * @return The reversal state of each axis, or {@code null} if unspecified.
     */
    public boolean reverseAxis(int i) {
    	AxisDirection grid = gridRange.getCoordinateSystem().getAxis(i).getDirection();
    	AxisDirection user = envelope.getCoordinateSystem().getAxis(i).getDirection();
    	boolean result = !grid.equals(user);
        if( swapXY(envelope.getCoordinateSystem()) ) result = !result;
       
        return result;
    }

    /** Returns the coordinate system in use with the envelope. */
    private CoordinateSystem getCoordinateSystem() {
        if (envelope != null) {
            final CoordinateSystem crs;
            crs = envelope.getCoordinateSystem();
            if (crs != null) {
                return crs;
            }
        }
        return null;
    }
    /**
     * Applies heuristic rules in order to determine if the two first axis should be interchanged.
     */
    private boolean swapXY(final CoordinateSystem crs) {
        if (crs != null && crs.getDimension() >= 2) {
            return AxisDirection.NORTH.equals(crs.getAxis(0).getDirection())
                    && AxisDirection.EAST.equals(crs.getAxis(1).getDirection());
        }
        return false;
    }
    /**
     * Creates a affine transform using the information provided by setter methods.
     * This assumes no reversing
     *
     * @return the transform.
     * @throws IllegalStateException if the grid range or the envelope were not set.
     */
    public AffineTransformation createTransform() throws IllegalStateException {
        if (transform == null) {
            final ReferencedEnvelope gridEnvelope = getGridRange();
            final ReferencedEnvelope userEnvelope = getEnvelope();
            final boolean swapXY = swapXY(userEnvelope.getCoordinateSystem());
            final int gridType = getPixelAnchor();
            final int dimension = gridEnvelope.getDimension();
            
            /*
             * Setup the multi-dimensional affine transform for use with OpenGIS.
             * According OpenGIS specification, transforms must map pixel center.
             * This is done by adding 0.5 to grid coordinates.
             */
            final double translate;
            if(gridType==ANCHOR_CELL_CENTER) {
                translate = 0.5;
            } 
            else if (gridType==ANCHOR_CELL_CORNER) {
                translate = 0.0;
            } 
            else {
            	throw new IllegalStateException(String.format("%s.createTransform: Illegal grid type (%d)", CLSS,gridType));
            }

            if( dimension>1 ){

                
                double scalex = userEnvelope.getSpan(0) / gridEnvelope.getSpan(0);
                double scaley = userEnvelope.getSpan(1) / gridEnvelope.getSpan(1);
                double offsetx = userEnvelope.getMaximum(0);
                double offsety = userEnvelope.getMaximum(1);
                if (reverseAxis(0)) {
                    offsetx = userEnvelope.getMinimum(0);
                } 
                else {
                    scalex = -scalex;
                }
                if (reverseAxis(1)) {
                    offsety = userEnvelope.getMinimum(1);
                } 
                else {
                    scaley = -scaley;
                }
                offsetx -= scalex * (gridEnvelope.getMinX() - translate);
                offsety -= scaley * (gridEnvelope.getMinY() - translate);
                
                if( swapXY ) {
                	double tmp = scalex;
                	scalex = scaley;
                	scaley = tmp;
                }
                
            	double m00 = scalex; // x scale
                double m01 = 0.; 	// x shear
                double m02 = offsetx; 	// dx
                double m10 = 0.; 	// y shear
                double m11 = scaley;  // y scale
                double m12 = offsety; // dy
                transform = new AffineTransformation(m00,m01,m02,m10,m11,m12); 
            }
            
        }
        return transform;
    }

    /**
     * Returns the math transform as a two-dimensional affine transform.
     *
     * @return The math transform as a two-dimensional affine transform.
     * @throws IllegalStateException if the math transform is not of the appropriate type.
     */
    public AffineTransformation createAffineTransform() throws IllegalStateException {
        final AffineTransformation transform = createTransform();
        if (transform != null) {
            return transform;
        }
        throw new IllegalStateException(String.format("%s.createAffineTransform: not an affine transform", CLSS));
    }
}
