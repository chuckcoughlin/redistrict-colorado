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
package org.geotools.geometry;

import java.awt.geom.Point2D;
import java.util.Arrays;

import org.geotools.util.Utilities;
import org.opengis.geometry.MismatchedDimensionException;
import org.openjump.coordsys.CoordinateSystem;

/**
 * Holds the coordinates for a position within some coordinate reference system. Since {@code
 * DirectPosition}s, as data types, will often be included in larger objects (such as {@linkplain
 * org.geotools.geometry.Geometry geometries}) that have references to {@link
 * CoordinateSystem}, the {@link #getCoordinateSystem} method may returns {@code
 * null} if this particular {@code DirectPosition} is included in a larger object with such a
 * reference to a {@linkplain CoordinateSystem coordinate reference system}. In this case,
 * the coordinate reference system is implicitly assumed to take on the value of the containing
 * object's {@link CoordinateSystem}.
 *
 * <p>This particular implementation of {@code DirectPosition} is said "General" because it uses an
 * {@linkplain #ordinates array of ordinates} of an arbitrary length. If the direct position is know
 * to be always two-dimensional, then {@link DirectPosition2D} may provides a more efficient
 * implementation.
 *
 * <p>Most methods in this implementation are final for performance reason.
 *
 * @since 2.0
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @see DirectPosition1D
 * @see DirectPosition2D
 * @see java.awt.geom.Point2D
 */
public class DirectPosition extends Point2D.Double {
	private final static String CLSS = "DirectPosition";
    private static final long serialVersionUID = 9171833698385715524L;

    public final double[] ordinates;
    private CoordinateSystem crs;

    /**
     * Convenience method for checking coordinate reference system validity.
     *
     * @param crs The coordinate reference system to check.
     * @param expected the dimension expected.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    static void checkCoordinateSystemDimension(
            final CoordinateSystem crs, final int expected)
            throws MismatchedDimensionException {
        if (crs != null) {
            final int dimension = crs.getDimension();
            if (dimension != expected) {
            	throw new MismatchedDimensionException(String.format("%s.checkCoordinateSystemDimension: Mismatched dimension in %s (%d vs %d)",CLSS, crs.getName(), dimension, expected));
            }
        }
    }

    /**
     * Convenience method for checking object dimension validity. This method is usually invoked for
     * argument checking.
     *
     * @param name The name of the argument to check.
     * @param dimension The object dimension.
     * @param expectedDimension The Expected dimension for the object.
     * @throws MismatchedDimensionException if the object doesn't have the expected dimension.
     */
    static void ensureDimensionMatch(
            final String name, final int dimension, final int expectedDimension)
            throws MismatchedDimensionException {
        if (dimension != expectedDimension) {
            throw new MismatchedDimensionException(String.format("%s.ensureDimensionMatch: Mismatched dimension in %s (%d vs %d)",CLSS, name, dimension, expectedDimension));
        }
    }
    public DirectPosition() {
    	 ordinates = new double[2];
    }
    /**
     * Constructs a position using the specified coordinate reference system. The number of
     * dimensions is inferred from the coordinate reference system.
     *
     * @param crs The coordinate reference system to be given to this position.
     * @since 2.2
     */
    public DirectPosition(final CoordinateSystem crs) {
        this(crs.getDimension());
        this.crs = crs;
    }

    /**
     * Constructs a position with the specified number of dimensions.
     *
     * @param numDim Number of dimensions.
     * @throws NegativeArraySizeException if {@code numDim} is negative.
     */
    public DirectPosition(final int numDim) throws NegativeArraySizeException {
        ordinates = new double[numDim];
    }

    /**
     * Constructs a position with the specified ordinates. The {@code ordinates} array will be
     * copied.
     *
     * @param ordinates The ordinate values to copy.
     */
    public DirectPosition(final double[] ordinates) {
        this.ordinates = ordinates.clone();
    }

    /**
     * Constructs a 2D position from the specified ordinates. Despite their name, the
     * (<var>x</var>,<var>y</var>) coordinates don't need to be oriented toward ({@linkplain
     * org.openjump.coordsys.AxisDirection#EAST East}, {@linkplain
     * org.openjump.coordsys.AxisDirection#NORTH North}). See the {@link DirectPosition2D}
     * javadoc for details.
     *
     * @param x The first ordinate value.
     * @param y The second ordinate value.
     */
    public DirectPosition(final double x, final double y) {
        ordinates = new double[] {x, y};
    }
    
    /**
     * Constructs a 2D position from the specified ordinates. Despite their name, the
     * (<var>x</var>,<var>y</var>) coordinates don't need to be oriented toward ({@linkplain
     * org.openjump.coordsys.AxisDirection#EAST East}, {@linkplain
     * org.openjump.coordsys.AxisDirection#NORTH North}). See the {@link DirectPosition2D}
     * javadoc for details.
     *
     * @param x The first ordinate value.
     * @param y The second ordinate value.
     */
    public DirectPosition(final double x, final double y,CoordinateSystem coordsys) {
        ordinates = new double[] {x, y};
        setCoordinateSystem(coordsys);
    }

    /**
     * Constructs a 3D position from the specified ordinates. Despite their name, the
     * (<var>x</var>,<var>y</var>,<var>z</var>) coordinates don't need to be oriented toward
     * ({@linkplain org.openjump.coordsys.AxisDirection#EAST East}, {@linkplain
     * org.openjump.coordsys.AxisDirection#NORTH North}, {@linkplain
     * org.openjump.coordsys.AxisDirection#UP Up}).
     *
     * @param x The first ordinate value.
     * @param y The second ordinate value.
     * @param z The third ordinate value.
     */
    public DirectPosition(final double x, final double y, final double z) {
        ordinates = new double[] {x, y, z};
    }

    /**
     * Constructs a position from the specified {@link Point2D}.
     *
     * @param point The position to copy.
     */
    public DirectPosition(final Point2D point) {
        this(point.getX(), point.getY());
    }

    /**
     * Constructs a position initialized to the same values than the specified point.
     *
     * @param point The position to copy.
     * @since 2.2
     */
    public DirectPosition(final DirectPosition point) {
        ordinates = point.ordinates; // Should already be cloned.
        crs = point.getCoordinateSystem();
    }

    /**
     * Returns the coordinate reference system in which the coordinate is given. May be {@code null}
     * if this particular {@code DirectPosition} is included in a larger object with such a
     * reference to a {@linkplain CoordinateSystem coordinate reference system}.
     *
     * @return The coordinate reference system, or {@code null}.
     */
    public final CoordinateSystem getCoordinateSystem() {
        return crs;
    }

    /**
     * Set the coordinate reference system in which the coordinate is given.
     *
     * @param crs The new coordinate reference system, or {@code null}.
     * @throws MismatchedDimensionException if the specified CRS doesn't have the expected number of
     *     dimensions.
     */
    public void setCoordinateSystem(final CoordinateSystem crs)
            throws MismatchedDimensionException {
        checkCoordinateSystemDimension(crs, getDimension());
        this.crs = crs;
    }

    /**
     * The length of coordinate sequence (the number of entries). This may be less than or equal to
     * the dimensionality of the {@linkplain #getCoordinateSystem() coordinate reference
     * system}.
     *
     * @return The dimensionality of this position.
     */
    public final int getDimension() {
        return ordinates.length;
    }

    /**
     * Returns the ordinate at the specified dimension.
     *
     * @param dimension The dimension in the range 0 to 1 inclusive.
     * @return The coordinate at the specified dimension.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     * @todo Provides a more detailled error message.
     */
    public final double getOrdinate(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }
	public int getOffset() {
		// Assume no offset, for now
		return 0;
	}


    /**
     * Sets the ordinate value along the specified dimension.
     *
     * @param dimension the dimension for the ordinate of interest.
     * @param value the ordinate value of interest.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     */
    public final void setOrdinate(int dimension, double value) throws IndexOutOfBoundsException {
        ordinates[dimension] = value;
    }



    /**
     * Set this coordinate to the specified direct position. This method is identical to {@link
     * #setLocation(DirectPosition)}, but is slightly faster in the special case of an {@code
     * GeneralDirectPosition} implementation.
     *
     * @param position The new position for this point.
     * @throws MismatchedDimensionException if this point doesn't have the expected dimension.
     */
    public final void setLocation(final DirectPosition position)
            throws MismatchedDimensionException {
        ensureDimensionMatch("position", position.ordinates.length, ordinates.length);
        setCoordinateSystem(position.crs);
        System.arraycopy(position.ordinates, 0, ordinates, 0, ordinates.length);
    }

    /**
     * Set this coordinate to the specified {@link Point2D}. This coordinate must be
     * two-dimensional.
     *
     * @param point The new coordinate for this point.
     * @throws MismatchedDimensionException if this coordinate point is not two-dimensional.
     */
    public final void setLocation(final Point2D point) throws MismatchedDimensionException {
        if (ordinates.length != 2) {
        	throw new MismatchedDimensionException(String.format("%s.setLocation: Not 2-dimensional (%d)",CLSS, ordinates.length));
        }
        ordinates[0] = point.getX();
        ordinates[1] = point.getY();
    }

    /**
     * Returns a {@link Point2D} with the same coordinate as this direct position. This is a
     * convenience method for interoperability with Java2D.
     *
     * @return This position as a two-dimensional point.
     * @throws IllegalStateException if this coordinate point is not two-dimensional.
     */
    public Point2D toPoint2D() throws IllegalStateException {
        if (ordinates.length != 2) {
        	throw new IllegalStateException(String.format("%s.toPoint2D: Not 2-dimensional (%d)",CLSS, ordinates.length));
        }
        return new Point2D.Double(ordinates[0], ordinates[1]);
    }

    /** Returns a hash value for this coordinate. */
    @Override
    public int hashCode() {
        int code = Arrays.hashCode(ordinates);
        if (crs != null) {
            code += crs.hashCode();
        }
        assert code == super.hashCode();
        return code;
    }

    /** Returns a deep copy of this position. */
    @Override
    public DirectPosition clone() {
        super.clone();
        return new DirectPosition(ordinates);
    }
    
    /** Returns a hash value for the given coordinate. */
    static int hashCode(final DirectPosition position) {
        final int dimension = position.getDimension();
        int code = 1;
        for (int i = 0; i < dimension; i++) {
            final long bits = java.lang.Double.doubleToLongBits(position.getOrdinate(i));
            code = 31 * code + ((int) (bits) ^ (int) (bits >>> 32));
        }
        final CoordinateSystem crs = position.getCoordinateSystem();
        if (crs != null) {
            code += crs.hashCode();
        }
        return code;
    }
    
    /**
     * Returns {@code true} if the specified object is also a {@linkplain DirectPosition direct
     * position} with equals {@linkplain #getCoordinate coordinate} and {@linkplain
     * #getCoordinateSystem CRS}.
     *
     * @param object The object to compare with this position.
     * @return {@code true} if the given object is equals to this position.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof DirectPosition) {
            final DirectPosition that = (DirectPosition) object;
            final int dimension = getDimension();
            if (dimension == that.getDimension()) {
                for (int i = 0; i < dimension; i++) {
                    if (!Utilities.equals(this.getOrdinate(i), that.getOrdinate(i))) {
                        return false;
                    }
                }
                if (Utilities.equals(
                        this.getCoordinateSystem(), that.getCoordinateSystem())) {
                    assert hashCode() == that.hashCode() : this;
                    return true;
                }
            }
        }
        return false;
    }
}
