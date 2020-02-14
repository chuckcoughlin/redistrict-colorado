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
 */
package org.geotools.geometry.jts;

import java.awt.geom.AffineTransform;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;

/**
 * A path iterator for the FeatureShape class, specialized to iterate over LineString object.
 *
 * @author Andrea Aime
 * @author simone giannecchini
 * @version $Id$
 */
public final class LineIterator extends AbstractPathIterator {
    /** Transform applied on the coordinates during iteration */
    private AffineTransform at;

    /** The array of coordinates that represents the line geometry */
    private CoordinateSequence coordinates = null;

    /** Current line coordinate */
    private int currentCoord = 0;

    /** The previous coordinate (during iteration) */
    private float oldX = Float.NaN;

    private float oldY = Float.NaN;

    /** True when the iteration is terminated */
    private boolean done = false;

    /** True if the line is a ring */
    private boolean isClosed;
    /** Horizontal scale, got from the affine transform and cached */
    private float xScale;

    /** Vertical scale, got from the affine transform and cached */
    private float yScale;

    private int coordinateCount;

    private static final AffineTransform NO_TRANSFORM = new AffineTransform();

    /** */
    public LineIterator() {}

    /**
     * Creates a new instance of LineIterator
     *
     * @param ls The line string the iterator will use
     * @param at The affine transform applied to coordinates during iteration
     */
    public LineIterator(LineString ls, AffineTransform at) {
        init(ls, at);
    }

    /**
     * @param ls
     * @param at
     * @param generalize
     * @param maxDistance
     */
    private void init(LineString ls, AffineTransform at) {
        if (at == null) {
            at = NO_TRANSFORM;
        }

        this.at = at;
        coordinates = ls.getCoordinateSequence();
        coordinateCount = coordinates.size();
        isClosed = ls instanceof LinearRing;

        done = false;
        currentCoord = 0;

        oldX = Float.NaN;
        oldY = Float.NaN;
        
        xScale =
                (float) Math.sqrt(
                                (at.getScaleX() * at.getScaleX())
                                        + (at.getShearX() * at.getShearX()));
        yScale =
                (float) Math.sqrt(
                                (at.getScaleY() * at.getScaleY())
                                        + (at.getShearY() * at.getShearY()));
    }


    //    /**
    //     * Returns the coordinates and type of the current path segment in the
    //     * iteration. The return value is the path-segment type: SEG_MOVETO,
    //     * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A double array of
    //     * length 6 must be passed in and can be used to store the coordinates of
    //     * the point(s). Each point is stored as a pair of double x,y coordinates.
    //     * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns
    //     * two points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return
    //     * any points.
    //     *
    //     * @param coords an array that holds the data returned from this method
    //     *
    //     * @return the path-segment type of the current path segment.
    //     *
    //     * @see #SEG_MOVETO
    //     * @see #SEG_LINETO
    //     * @see #SEG_QUADTO
    //     * @see #SEG_CUBICTO
    //     * @see #SEG_CLOSE
    //     */
    //    public int currentSegment(float[] coords) {
    //        if (currentCoord == 0) {
    //            coords[0] = (float) coordinates.getX(0);
    //            coords[1] = (float) coordinates.getY(0);
    //            at.transform(coords, 0, coords, 0, 1);
    //
    //            return SEG_MOVETO;
    //        } else if ((currentCoord == coordinateCount) && isClosed) {
    //            return SEG_CLOSE;
    //        } else {
    //            coords[0] = oldX; // (float) coordinates.getX(currentCoord);
    //            coords[1] = oldY; // (float) coordinates.getY(currentCoord);
    //            at.transform(coords, 0, coords, 0, 1);
    //
    //            return SEG_LINETO;
    //        }
    //    }

    //    /**
    //     * Returns the coordinates and type of the current path segment in the
    //     * iteration. The return value is the path-segment type: SEG_MOVETO,
    //     * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A float array of
    //     * length 6 must be passed in and can be used to store the coordinates of
    //     * the point(s). Each point is stored as a pair of float x,y coordinates.
    //     * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns
    //     * two points, SEG_CUBICTO returns 3 points and SEG_CLOSE does not return
    //     * any points.
    //     *
    //     * @param coords an array that holds the data returned from this method
    //     *
    //     * @return the path-segment type of the current path segment.
    //     *
    //     * @see #SEG_MOVETO
    //     * @see #SEG_LINETO
    //     * @see #SEG_QUADTO
    //     * @see #SEG_CUBICTO
    //     * @see #SEG_CLOSE
    //     */
    //    public int currentSegment(float[] coords) {
    //        double[] dcoords = new double[2];
    //        int result = currentSegment(dcoords);
    //        coords[0] = (float) dcoords[0];
    //        coords[1] = (float) dcoords[1];
    //
    //        return result;
    //    }

    /**
     * Returns the winding rule for determining the interior of the path.
     *
     * @return the winding rule.
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

    /**
     * Tests if the iteration is complete.
     *
     * @return <code>true</code> if all the segments have been read; <code>false</code> otherwise.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Moves the iterator to the next segment of the path forwards along the primary direction of
     * traversal as long as there are more points in that direction.
     */
    public void next() {
        if (((currentCoord == (coordinateCount - 1)) && !isClosed)
                || ((currentCoord == coordinateCount) && isClosed)) {
            done = true;
        } 
        else {
            currentCoord++;
        }
    }

    /** @see java.awt.geom.PathIterator#currentSegment(double[]) */
    public int currentSegment(double[] coords) {
        if (currentCoord == 0) {
            coords[0] = (double) coordinates.getX(0);
            coords[1] = (double) coordinates.getY(0);
            at.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if ((currentCoord == coordinateCount) && isClosed) {
            return SEG_CLOSE;
        } else {
            coords[0] = coordinates.getX(currentCoord);
            coords[1] = coordinates.getY(currentCoord);
            at.transform(coords, 0, coords, 0, 1);

            return SEG_LINETO;
        }
    }
}
