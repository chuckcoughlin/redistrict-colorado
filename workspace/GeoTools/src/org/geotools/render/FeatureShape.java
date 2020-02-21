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
package org.geotools.render;



import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.geotools.geometry.Geometries;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;


import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


/**
 * Convert a JTS geometry to the Shape for rendering on a canvas. A "decimator" is used to
 * cull points that are so close that they occupy the same pixel. Measurements are screen coordinates.
 */
public final class FeatureShape extends Shape {
	private final static double DECIMATOR_TOLERANCE = 0.8; // Minimum pixel distance tolerated.
    private Geometry geometry;
    private final Decimator decimator;

    /**
     * Creates a shape from a feature.
     *
     * @param feature - the feature to draw
     */
    public FeatureShape(Feature feature) {
    	this(feature.getGeometry());
    }
    
    /**
     * Creates a shape from a geometry. 
     *
     * @param geom - the wrapped geometry
     */
    public FeatureShape(Geometry geom) {
    	this.geometry = geom;
    	this.decimator = new Decimator(DECIMATOR_TOLERANCE);
    }

    private void transformGeometry(Geometry geometry)  {
    	GeometryCollection collection = null;
		switch (Geometries.get(geometry)) {
		case POINT:
			break;
		case MULTIPOINT:
			break;
		case LINESTRING:
			decimator.decimate(geometry);
			break;
		case MULTILINESTRING:
			break;
		case POLYGON:
			decimator.decimate(geometry);
			break;
		case MULTIPOLYGON:
			break;
		case GEOMETRYCOLLECTION:
		default:
			throw new UnsupportedOperationException("No style method for " + geometry.getGeometryType());
		}

        
    }



    /**
     * Sets the geometry contained in this lite shape. Convenient to reuse this object instead of
     * creating it again and again during rendering
     *
     * @param g
     * @throws TransformException
     * @throws FactoryException
     */
    public void setGeometry(Geometry g) {
        if (g != null) {
            this.geometry = g;
            transformGeometry(geometry);
        }
    }

    /**
     * Tests if the interior of the <code>Shape</code> entirely contains the specified <code>
     * Rectangle2D</code>. This method might conservatively return <code>false</code> when:
     *
     * <ul>
     *   <li>the <code>intersect</code> method returns <code>true</code> and
     *   <li>the calculations to determine whether or not the <code>Shape</code> entirely contains
     *       the <code>Rectangle2D</code> are prohibitively expensive.
     * </ul>
     *
     * This means that this method might return <code>false</code> even though the <code>Shape
     * </code> contains the <code>Rectangle2D</code>. The <code>Area</code> class can be used to
     * perform more accurate computations of geometric intersection for any <code>Shape</code>
     * object if a more precise answer is required.
     *
     * @param r The specified <code>Rectangle2D</code>
     * @return <code>true</code> if the interior of the <code>Shape</code> entirely contains the
     *     <code>Rectangle2D</code>; <code>false</code> otherwise or, if the <code>Shape</code>
     *     contains the <code>Rectangle2D</code> and the <code>intersects</code> method returns
     *     <code>true</code> and the containment calculations would be too expensive to perform.
     * @see #contains(double, double, double, double)
     */
    public boolean contains(Rectangle2D r) {
        Geometry rect = rectangleToGeometry(r);

        return geometry.contains(rect);
    }

    /**
     * Tests if a specified {@link Point2D}is inside the boundary of the <code>Shape</code>.
     *
     * @param p a specified <code>Point2D</code>
     * @return <code>true</code> if the specified <code>Point2D</code> is inside the boundary of the
     *     <code>Shape</code>; <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
        Coordinate coord = new Coordinate(p.getX(), p.getY());
        Geometry point = geometry.getFactory().createPoint(coord);

        return geometry.contains(point);
    }

    /**
     * Tests if the specified coordinates are inside the boundary of the <code>Shape</code>.
     *
     * @param x the specified coordinates, x value
     * @param y the specified coordinates, y value
     * @return <code>true</code> if the specified coordinates are inside the <code>Shape</code>
     *     boundary; <code>false</code> otherwise.
     */
    public boolean contains(double x, double y) {
        Coordinate coord = new Coordinate(x, y);
        Geometry point = geometry.getFactory().createPoint(coord);

        return geometry.contains(point);
    }

    /**
     * Tests if the interior of the <code>Shape</code> entirely contains the specified rectangular
     * area. All coordinates that lie inside the rectangular area must lie within the <code>Shape
     * </code> for the entire rectanglar area to be considered contained within the <code>Shape
     * </code>.
     *
     * <p>This method might conservatively return <code>false</code> when:
     *
     * <ul>
     *   <li>the <code>intersect</code> method returns <code>true</code> and
     *   <li>the calculations to determine whether or not the <code>Shape</code> entirely contains
     *       the rectangular area are prohibitively expensive.
     * </ul>
     *
     * This means that this method might return <code>false</code> even though the <code>Shape
     * </code> contains the rectangular area. The <code>Area</code> class can be used to perform
     * more accurate computations of geometric intersection for any <code>Shape</code> object if a
     * more precise answer is required.
     *
     * @param x the coordinates of the specified rectangular area, x value
     * @param y the coordinates of the specified rectangular area, y value
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return <code>true</code> if the interior of the <code>Shape</code> entirely contains the
     *     specified rectangular area; <code>false</code> otherwise or, if the <code>Shape</code>
     *     contains the rectangular area and the <code>intersects</code> method returns <code>true
     *     </code> and the containment calculations would be too expensive to perform.
     * @see java.awt.geom.Area
     * @see #intersects
     */
    public boolean contains(double x, double y, double w, double h) {
        Geometry rect = createRectangle(x, y, w, h);

        return geometry.contains(rect);
    }

    /**
     * Returns an integer {@link Rectangle}that completely encloses the <code>Shape</code>. Note
     * that there is no guarantee that the returned <code>Rectangle</code> is the smallest bounding
     * box that encloses the <code>Shape</code>, only that the <code>Shape</code> lies entirely
     * within the indicated <code>Rectangle</code>. The returned <code>Rectangle</code> might also
     * fail to completely enclose the <code>Shape</code> if the <code>Shape</code> overflows the
     * limited range of the integer data type. The <code>getBounds2D</code> method generally returns
     * a tighter bounding box due to its greater flexibility in representation.
     *
     * @return an integer <code>Rectangle</code> that completely encloses the <code>Shape</code>.
     * @see #getBounds2D
     */
    public Rectangle getBounds() {
        Rectangle2D env = getBounds2D();
        return new Rectangle(
                (int) Math.round(env.getMinX()),
                (int) Math.round(env.getMinY()),
                (int) Math.ceil(env.getWidth()),
                (int) Math.ceil(env.getHeight()));
    }

    /**
     * Returns a high precision and more accurate bounding box of the <code>Shape</code> than the
     * <code>getBounds</code> method. Note that there is no guarantee that the returned {@link
     * Rectangle2D}is the smallest bounding box that encloses the <code>Shape</code>, only that the
     * <code>Shape</code> lies entirely within the indicated <code>Rectangle2D</code>. The bounding
     * box returned by this method is usually tighter than that returned by the <code>getBounds
     * </code> method and never fails due to overflow problems since the return value can be an
     * instance of the <code>Rectangle2D</code> that uses double precision values to store the
     * dimensions.
     *
     * @return an instance of <code>Rectangle2D</code> that is a high-precision bounding box of the
     *     <code>Shape</code>.
     * @see #getBounds
     */
    public Rectangle2D getBounds2D() {
        Envelope env = geometry.getEnvelopeInternal();
        // note, we dont' use getWidth/getHeight since they are slower
        return new Rectangle2D.Double(
                env.getMinX(),
                env.getMinY(),
                env.getMaxX() - env.getMinX(),
                env.getMaxY() - env.getMinY());
    }



    /**
     * Tests if the interior of the <code>Shape</code> intersects the interior of a specified
     * rectangular area. The rectangular area is considered to intersect the <code>Shape</code> if
     * any point is contained in both the interior of the <code>Shape</code> and the specified
     * rectangular area.
     *
     * <p>This method might conservatively return <code>true</code> when:
     *
     * <ul>
     *   <li>there is a high probability that the rectangular area and the <code>Shape</code>
     *       intersect, but
     *   <li>the calculations to accurately determine this intersection are prohibitively expensive.
     * </ul>
     *
     * This means that this method might return <code>true</code> even though the rectangular area
     * does not intersect the <code>Shape</code>. The {@link java.awt.geom.Area Area}class can be
     * used to perform more accurate computations of geometric intersection for any <code>Shape
     * </code> object if a more precise answer is required.
     *
     * @param x the coordinates of the specified rectangular area, x value
     * @param y the coordinates of the specified rectangular area, y value
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return <code>true</code> if the interior of the <code>Shape</code> and the interior of the
     *     rectangular area intersect, or are both highly likely to intersect and intersection
     *     calculations would be too expensive to perform; <code>false</code> otherwise.
     * @see java.awt.geom.Area
     */
    public boolean intersects(double x, double y, double w, double h) {
        Geometry rect = createRectangle(x, y, w, h);

        return geometry.intersects(rect);
    }

    /**
     * Converts the Rectangle2D passed as parameter in a jts Geometry object
     *
     * @param r the rectangle to be converted
     * @return a geometry with the same vertices as the rectangle
     */
    private Geometry rectangleToGeometry(Rectangle2D r) {
        return createRectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    /**
     * Creates a jts Geometry object representing a rectangle with the given parameters
     *
     * @param x left coordinate
     * @param y bottom coordinate
     * @param w width
     * @param h height
     * @return a rectangle with the specified position and size
     */
    private Geometry createRectangle(double x, double y, double w, double h) {
        Coordinate[] coords = {
            new Coordinate(x, y),
            new Coordinate(x, y + h),
            new Coordinate(x + w, y + h),
            new Coordinate(x + w, y),
            new Coordinate(x, y)
        };
        LinearRing lr = geometry.getFactory().createLinearRing(coords);

        return geometry.getFactory().createPolygon(lr, null);
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
