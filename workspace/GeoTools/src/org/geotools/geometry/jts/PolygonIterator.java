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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 * A path iterator for the FeatureShape class, specialized to iterate over Polygon objects.
 *
 * @author Andrea Aime
 * @author simone giannecchini
 * @version $Id$
 */
public final class PolygonIterator extends AbstractPathIterator {
    /** Transform applied on the coordinates during iteration */
    private AffineTransform at;

    /** The rings describing the polygon geometry */
    private LineString[] rings;

    /** The current ring during iteration */
    private int currentRing = 0;

    /** Current line coordinate */
    private int currentCoord = 0;

    /** The array of coordinates that represents the line geometry */
    private CoordinateSequence coords = null;

    /** The previous coordinate (during iteration) */
    private Coordinate oldCoord = null;

    /** True when the iteration is terminated */
    private boolean done = false;

    /** Horizontal scale, got from the affine transform and cached */
    private double xScale;

    /** Vertical scale, got from the affine transform and cached */
    private double yScale;

    /**
     * Creates a new PolygonIterator object.
     *
     * @param p The polygon
     * @param at The affine transform applied to coordinates during iteration
     */
    public PolygonIterator(Polygon p, AffineTransform at) {
        int numInteriorRings = p.getNumInteriorRing();
        rings = new LineString[numInteriorRings + 1];
        rings[0] = p.getExteriorRing();

        for (int i = 0; i < numInteriorRings; i++) {
            rings[i + 1] = p.getInteriorRingN(i);
        }

        if (at == null) {
            at = new AffineTransform();
        }

        this.at = at;
        xScale = Math.sqrt((at.getScaleX() * at.getScaleX()) + (at.getShearX() * at.getShearX()));
        yScale = Math.sqrt((at.getScaleY() * at.getScaleY()) + (at.getShearY() * at.getShearY()));

        coords = rings[0].getCoordinateSequence();
    }

    /**
     * Returns the coordinates and type of the current path segment in the iteration. The return
     * value is the path-segment type: SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or
     * SEG_CLOSE. A double array of length 6 must be passed in and can be used to store the
     * coordinates of the point(s). Each point is stored as a pair of double x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types returns one point, SEG_QUADTO returns two points, SEG_CUBICTO
     * returns 3 points and SEG_CLOSE does not return any points.
     *
     * @param coords an array that holds the data returned from this method
     * @return the path-segment type of the current path segment.
     * @see #SEG_MOVETO
     * @see #SEG_LINETO
     * @see #SEG_QUADTO
     * @see #SEG_CUBICTO
     * @see #SEG_CLOSE
     */
    public int currentSegment(double[] coords) {
        // first make sure we're not at the last element, this prevents us from exceptions
        // in the case where coords.size() == 0
        if (currentCoord == this.coords.size()) {
            return SEG_CLOSE;
        } else if (currentCoord == 0) {
            coords[0] = this.coords.getX(0);
            coords[1] = this.coords.getY(0);
            transform(coords, 0, coords, 0, 1);

            return SEG_MOVETO;
        } else {
            coords[0] = this.coords.getX(currentCoord);
            coords[1] = this.coords.getY(currentCoord);
            transform(coords, 0, coords, 0, 1);

            return SEG_LINETO;
        }
    }

    protected void transform(double[] src, int index, double[] dest, int destIndex, int numPoints) {
        at.transform(src, index, dest, destIndex, numPoints);
    }

    /**
     * Return the winding rule for determining the interior of the path.
     *
     * @return <code>WIND_EVEN_ODD</code> by default.
     */
    public int getWindingRule() {
        return WIND_EVEN_ODD;
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
        if (currentCoord == coords.size()) {
            if (currentRing < (rings.length - 1)) {
                currentCoord = 0;
                currentRing++;
                coords = rings[currentRing].getCoordinateSequence();
            } else {
                done = true;
            }
        } 
        else {
           currentCoord++;
        }
    }
}
