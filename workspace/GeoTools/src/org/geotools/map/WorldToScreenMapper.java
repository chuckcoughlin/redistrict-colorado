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
package org.geotools.map;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.geotools.referencing.ReferencedEnvelope;
import org.geotools.util.RendererUtilities;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateSystem;

/**
 * A helper class for building <var>n</var>-dimensional {@linkplain AffineTransform affine
 * transform} mapping {@linkplain WorldEnvelope coordinates} to {@linkplain screen coordinates}.
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class WorldToScreenMapper {
	private final static String CLSS = "WorldToScreenMapper";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	public static final int ANCHOR_CELL_CENTER = 1;
	public static final int ANCHOR_CELL_CORNER = 2;

    private final ReferencedEnvelope worldEnvelope;
    private final ReferencedEnvelope screenEnvelope;

    /**
     * Whatever the {@code gridToCRS} transform will maps pixel center or corner. The default value
     * is {@link PixelInCell#CELL_CENTER}.
     */
    private int anchor = ANCHOR_CELL_CENTER;

    /** The math transform, or {@code null} if not yet computed. */
    private AffineTransform transform;

    /**
     * Creates a new instance for the specified world and screen envelopes.
     *
     * @param world an envelope surrounding the physical features
     * @param screen an envelope that describes screen coordinate. This envelope must
     *     contains entirely all pixels, i.e. the envelope's upper left corner must coincide with
     *     the upper left corner of the first pixel and the envelope's lower right corner must
     *     coincide with the lower right corner of the last pixel.
     */
    public WorldToScreenMapper(final ReferencedEnvelope world, final ReferencedEnvelope screen) {
        this.worldEnvelope = world;
        this.screenEnvelope = screen;
        this.transform = null;
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
    public void setPixelAnchor(final int anchor) {this.anchor = anchor;}

    /**
     * @return the world coordinates.
     */
    public ReferencedEnvelope getWorldEnvelope()  { return worldEnvelope; }

    /**
     * @return The envelope.
     */
    public ReferencedEnvelope getScreenEnvelope() { return screenEnvelope; }

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
    	AxisDirection world = worldEnvelope.getCoordinateSystem().getAxis(i).getDirection();
    	AxisDirection user = screenEnvelope.getCoordinateSystem().getAxis(i).getDirection();
    	boolean result = !world.equals(user);
        if( swapXY(screenEnvelope.getCoordinateSystem()) ) result = !result;
       
        return result;
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
     * Creates an affine transform from physical to screen coordinates where the source maps
     * completely to the destination.
     *
     * @return the transform.
     */
    public AffineTransform createTransform()  {
        if (transform == null) {
            /*
             * Setup the multi-dimensional affine transform for use with OpenGIS.
             * According OpenGIS specification, transforms must map pixel center.
             * This is done by adding 0.5 to grid coordinates.
             */
            double translate = 0.0;   // ANCHOR_CELL_CORNER
            if(getPixelAnchor()==ANCHOR_CELL_CENTER) {
                translate = 0.5;
            } 

            if( worldEnvelope.getDimension()>1 ){
                double scalex = RendererUtilities.calculateXScale(worldEnvelope, screenEnvelope);
                double scaley = RendererUtilities.calculateYScale(worldEnvelope, screenEnvelope);
                double offsetx = screenEnvelope.getMaximum(0);
                double offsety = screenEnvelope.getMaximum(1);
                if (reverseAxis(0)) {
                    offsetx = screenEnvelope.getMinimum(0);
                } 
                else {
                    scalex = -scalex;
                }
                if (reverseAxis(1)) {
                    offsety = screenEnvelope.getMinimum(1);
                } 
                else {
                    scaley = -scaley;
                }
                offsetx -= scalex * (worldEnvelope.getMinX() - translate);
                offsety -= scaley * (worldEnvelope.getMinY() - translate);
                
                boolean swapXY = swapXY(screenEnvelope.getCoordinateSystem());
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
                transform = new AffineTransform(m00,m10,m01,m11,m02,m12); 
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
    public AffineTransform createAffineTransform() throws IllegalStateException {
        final AffineTransform transform = createTransform();
        if (transform != null) {
            return transform;
        }
        throw new IllegalStateException(String.format("%s.createAffineTransform: not an affine transform", CLSS));
    }
}
