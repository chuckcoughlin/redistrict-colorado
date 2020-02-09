/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2015, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import org.geotools.geometry.DirectPosition;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.openjump.coordsys.CoordinateSystem;


/**
 * A JTS envelope associated with a {@linkplain CoordinateSystem coordinate reference
 * system}. In addition, this JTS envelope also implements the GeoAPI {@linkplain
 * org.opengis.geometry.coordinate.Envelope envelope} interface for interoperability with GeoAPI.
 *
 * @since 2.2
 * @version $Id$
 * @author Jody Garnett
 * @author Martin Desruisseaux
 * @author Simone Giannecchini
 * @see org.geotools.geometry.Envelope2D
 * @see org.geotools.geometry.GeneralEnvelope
 * @see org.opengis.metadata.extent.GeographicBoundingBox
 */
public class ReferencedEnvelope extends Envelope  {
	private final static String CLSS = "ReferencedEnvelope";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	
	
    /** A ReferencedEnvelope containing "everything" */
    public static ReferencedEnvelope EVERYTHING =
            new ReferencedEnvelope(
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    null) {
                private static final long serialVersionUID = -3188702602373537164L;

                public boolean contains(Coordinate p) {
                    return true;
                }

                public boolean contains(DirectPosition pos) {
                    return true;
                }

                public boolean contains(double x, double y) {
                    return true;
                }

                public boolean contains(Envelope box) {
                    return true;
                }

                public boolean isEmpty() {
                    return false;
                }

                public boolean isNull() {
                    return true;
                }

                public double getArea() {
                    // return super.getArea();
                    return Double.POSITIVE_INFINITY;
                }

                public void setBounds(Envelope arg0) {
                    throw new IllegalStateException("Cannot modify ReferencedEnvelope.EVERYTHING");
                }

                public Coordinate centre() {
                    return new Coordinate();
                }

                public void setToNull() {
                    // um ignore this as we are already "null"
                }

                public boolean equals(Object obj) {
                    if (obj == EVERYTHING) {
                        return true;
                    }
                    if (obj instanceof ReferencedEnvelope) {
                        ReferencedEnvelope other = (ReferencedEnvelope) obj;
                        if (other.coordsys != EVERYTHING.coordsys) return false;
                        if (other.getMinX() != EVERYTHING.getMinX()) return false;
                        if (other.getMinY() != EVERYTHING.getMinY()) return false;
                        if (other.getMaxX() != EVERYTHING.getMaxX()) return false;
                        if (other.getMaxY() != EVERYTHING.getMaxY()) return false;

                        return true;
                    }
                    return super.equals(obj);
                }

                public String toString() {
                    return "ReferencedEnvelope.EVERYTHING";
                }
            };
    /** Serial number for compatibility with different versions. */
    private static final long serialVersionUID = -3188702602373537163L;

    /** The coordinate reference system, or {@code null}. */
    protected final CoordinateSystem coordsys;

    /** Creates a null envelope with a null coordinate reference system. */
    public ReferencedEnvelope() {
        this.coordsys = null;
    }

    /**
     * Creates a null envelope with the specified coordinate reference system.
     *
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    public ReferencedEnvelope(CoordinateSystem cs) throws MismatchedDimensionException {
        this.coordsys = cs;
        checkCoordinateSystemDimension();
    }

    /**
     * Creates an envelope for a region defined by maximum and minimum values.
     *
     * @param x1 The first x-value.
     * @param x2 The second x-value.
     * @param y1 The first y-value.
     * @param y2 The second y-value.
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    public ReferencedEnvelope(
            final double x1,
            final double x2,
            final double y1,
            final double y2,
            final CoordinateSystem cs)
            throws MismatchedDimensionException {
        super(x1, x2, y1, y2);
        this.coordsys = cs;
        checkCoordinateSystemDimension();
    }

    /**
     * Creates an envelope for a Java2D rectangle.
     *
     * <p>NOTE: if the rectangle is empty, the resulting ReferencedEnvelope will not be. In case
     * this is needed use {@link #create(Rectangle2D, CoordinateSystem)
     * ReferencedEnvelope.create(rectangle, crs)}
     *
     * @param rectangle The rectangle.
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     * @since 2.4
     */
    public ReferencedEnvelope(final Rectangle rectangle, final CoordinateSystem crs) throws MismatchedDimensionException {
        this(   rectangle.getMinX(),
                rectangle.getMaxX(),
                rectangle.getMinY(),
                rectangle.getMaxY(),
                crs);
    }
    public ReferencedEnvelope(final Rectangle rectangle ) {
        this(   rectangle.getMinX(),
                rectangle.getMaxX(),
                rectangle.getMinY(),
                rectangle.getMaxY(),
                null);
    }
    public ReferencedEnvelope(final Rectangle2D rectangle,final CoordinateSystem crs ) {
        this(   rectangle.getMinX(),
                rectangle.getMaxX(),
                rectangle.getMinY(),
                rectangle.getMaxY(),
                crs);
    }

    /**
     * Creates a new envelope from an existing envelope.
     *
     * @param envelope The envelope to initialize from
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     * @since 2.3
     */
    public ReferencedEnvelope(final ReferencedEnvelope envelope)
            throws MismatchedDimensionException {
        super(envelope);
        coordsys = envelope.getCoordinateSystem();
        checkCoordinateSystemDimension();
    }

    /**
     * Creates a new envelope from an existing JTS envelope.
     *
     * @param envelope The envelope to initialize from.
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionExceptionif the CRS dimension is not valid.
     */
    public ReferencedEnvelope(final Envelope envelope, final CoordinateSystem crs)
            throws MismatchedDimensionException {
        super(envelope);
        this.coordsys = crs;
        checkCoordinateSystemDimension();
    }

    /**
     * Convenience method for checking coordinate reference system validity.
     *
     * @throws IllegalArgumentException if the CRS dimension is not valid.
     */
    protected void checkCoordinateSystemDimension() throws MismatchedDimensionException {
        if (coordsys != null) {
            final int expected = getDimension();
            final int dimension = coordsys.getDimension();
            if (dimension > expected) {
                // check dimensions and choose ReferencedEnvelope or ReferencedEnvelope3D
                // or the factory method ReferencedEnvelope.reference( CoordinateSystem )
                throw new MismatchedDimensionException(
                        String.format("%s.checkCoordinateSystemDimension: %s has mismatched dimension (%d vs %d)",CLSS,coordsys.getName(),
                                Integer.valueOf(dimension),Integer.valueOf(expected)));
            }
        }
    }


    /** Returns the coordinate reference system associated with this envelope. */
    public CoordinateSystem getCoordinateSystem() {
        return coordsys;
    }

    /** Returns the number of dimensions. */
    public int getDimension() {return 2;}

    /** Returns the minimal ordinate along the specified dimension. */
    public double getMinimum(final int dimension) {
        switch (dimension) {
            case 0:
                return getMinX();

            case 1:
                return getMinY();

            default:
                throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /** Returns the maximal ordinate along the specified dimension. */
    public double getMaximum(final int dimension) {
        switch (dimension) {
            case 0:
                return getMaxX();

            case 1:
                return getMaxY();

            default:
                throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /** Returns the center ordinate along the specified dimension. */
    public double getMedian(final int dimension) {
        switch (dimension) {
            case 0:
                return 0.5 * (getMinX() + getMaxX());

            case 1:
                return 0.5 * (getMinY() + getMaxY());

            default:
                throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * Returns the envelope length along the specified dimension. This length is equals to the
     * maximum ordinate minus the minimal ordinate.
     */
    public double getSpan(final int dimension) {
        switch (dimension) {
            case 0:
                return getWidth();

            case 1:
                return getHeight();

            default:
                throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * A coordinate position consisting of all the minimal ordinates for each dimension for all
     * points within the {@code Envelope}.
     */
    public DirectPosition getLowerCorner() {
        return new DirectPosition(getMinX(), getMinY(),coordsys);
    }

    /**
     * A coordinate position consisting of all the maximal ordinates for each dimension for all
     * points within the {@code Envelope}.
     */
    public DirectPosition getUpperCorner() {
        return new DirectPosition(getMaxX(), getMaxY(),coordsys );
    }

    /**
     * Returns {@code true} if lengths along all dimension are zero.
     *
     * @since 2.4
     */
    public boolean isEmpty() {
        return super.isNull();
    }

    /**
     * Returns {@code true} if the provided location is contained by this bounding box.
     *
     * @since 2.4
     */
    public boolean contains(DirectPosition pos) {
        return super.contains(pos.getOrdinate(0), pos.getOrdinate(1));
    }

    /**
     * Returns {@code true} if the provided bounds are contained by this bounding box.
     *
     * @since 2.4
     */
    public boolean contains(final Envelope bbox) {
        return super.contains(bbox);
    }

    /**
     * Check if this bounding box intersects the provided bounds.
     *
     * @since 2.4
     */
    public boolean intersects(final Envelope bbox) {
        return super.intersects(bbox);
    }
    /** Check if this bounding box intersects the provided bounds. */
    @Override
    public ReferencedEnvelope intersection(Envelope env) {
        return new ReferencedEnvelope(super.intersection(env), this.getCoordinateSystem());
    }

    /**
     * Include the provided bounding box, expanding as necessary.
     *
     * @since 2.4
     */
    public void include(final Envelope bbox) {
        expandToInclude(ReferencedEnvelope.reference(bbox));
    }
    /**
     * Expand to include the provided DirectPosition
     *
     * @param pt
     */
    public void expandToInclude(DirectPosition pt) {
        Coordinate coordinate = new Coordinate(pt.getOrdinate(0), pt.getOrdinate(1));
        expandToInclude(coordinate);
    }
    /** Include the provided envelope, expanding as necessary. */
    @Override
    public void expandToInclude(Envelope other) {
       if(other == null ) return;
       super.expandToInclude(other);
    }

    /**
     * Include the provided coordinates, expanding as necessary.
     *
     * @since 2.4
     */
    public void include(double x, double y) {
        super.expandToInclude(x, y);
    }

    /**
     * Initialize the bounding envelope with another bounding box.
     *
     * @since 2.4
     */
    public void setBounds(final Envelope bbox) {
        super.init(bbox);
    }


    /**
     * Transforms the referenced envelope to the specified coordinate reference system.
     *
     * <p>This method can handle the case where the envelope contains the North or South pole, or
     * when it cross the &plusmn;180ï¿½ longitude.
     *
     * @param targetCRS The target coordinate reference system.
     * @param lenient {@code true} if datum shift should be applied even if there is insuffisient
     *     information. Otherwise (if {@code false}), an exception is thrown in such case.
     * @return The transformed envelope.
     * @throws FactoryException if the math transform can't be determined.
     * @throws TransformException if at least one coordinate can't be transformed.
     * @see CRS#transform(CoordinateOperation, org.opengis.geometry.Envelope)
 	 
    public ReferencedEnvelope transform(CoordinateSystem targetCRS, boolean lenient) throws TransformException {
        return transform(targetCRS, lenient, 5);
    }
*/
    /**
     * Transforms the referenced envelope to the specified coordinate reference system using the
     * specified amount of points.
     *
     * <p>This method can handle the case where the envelope contains the North or South pole, or
     * when it crosses the &plusmn;180ï¿½ longitude.
     *
     * @param targetCRS The target coordinate reference system.
     * @param lenient {@code true} if datum shift should be applied even if there is insuffisient
     *     information. Otherwise (if {@code false}), an exception is thrown in such case.
     * @param numPointsForTransformation The number of points to use for sampling the envelope.
     * @return The transformed envelope.
     * @throws FactoryException if the math transform can't be determined.
     * @throws TransformException if at least one coordinate can't be transformed.
     * @see CRS#transform(CoordinateOperation, org.opengis.geometry.Envelope)
     * @since 2.3
   
    public ReferencedEnvelope transform(final CoordinateSystem targetCRS,final boolean lenient,final int numPointsForTransformation) throws TransformException {
        if (coordsys == null) {
            if (isEmpty()) {
                // We don't have a CRS yet because we are still empty, being empty is
                // something we can represent in the targetCRS
                return new ReferencedEnvelope(targetCRS);
            } 
            else {
                // really this is a the code that created this ReferencedEnvelope
                throw new NullPointerException("Unable to transform referenced envelope, crs has not yet been provided.");
            }
        }

        /*
         * Gets a first estimation using an algorithm capable to take singularity in account
         * (North pole, South pole, 180deg longitude). We will expand this initial box later.
   
        CoordinateOperation op =
                createFromAffineTransform(
                        IDENTITY, sourceCRS, targetCRS, MatrixFactory.create(dim + 1));
        final GeneralEnvelope transformed = CRS.transform(operation, this);
        transformed.setCoordinateReferenceSystem(targetCRS);

        /*
         * Now expands the box using the usual utility methods.
  
        final ReferencedEnvelope target = new ReferencedEnvelope(transformed);
        final MathTransform transform = operation.getMathTransform();
        JTS.transform(this, target, transform, numPointsForTransformation);

        return target;
    }
*/
    /**
     * Returns a hash value for this envelope. This value need not remain consistent between
     * different implementations of the same class.
     */
    @Override
    public int hashCode() {
        int code = super.hashCode() ^ (int) serialVersionUID;
        if (coordsys != null) {
            code ^= coordsys.hashCode();
        }
        return code;
    }

    /** Compares the specified object with this envelope for equality.
     * We would like to ignore any metadata. */
    @Override
    public boolean equals(final Object object) {
    	return super.equals(object);
    }

    /**
     * Returns a string representation of this envelope. The default implementation is okay for
     * occasional formatting (for example for debugging purpose).
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(this.getClass().getCanonicalName()).append('[');
        final int dimension = getDimension();

        for (int i = 0; i < dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            buffer.append(getMinimum(i)).append(" : ").append(getMaximum(i));
        }
        return buffer.append(']').toString();
    }


    /**
     * Utility method to create a ReferencedEnvelope from an JTS Envelope class, supporting 2d as
     * well as 3d envelopes (returning the right class).
     *
     * @param env The JTS Envelope object
     * @return ReferencedEnvelope, ReferencedEnvelope3D if it is 3d,<br>
     *     results in a null/an empty envelope, if input envelope was a null/an empty envelope
     */
    public static ReferencedEnvelope create(Envelope env, CoordinateSystem crs) {
        if (env == null) {
            return null;
        }

        if (env.isNull()) {
            return new ReferencedEnvelope(crs);
        } 
        else {
            return new ReferencedEnvelope(env, crs);
        }
    }

    /**
     * Cast to a ReferencedEnvelope (used to ensure that an Envelope is a ReferencedEnvelope).
     *
     * <p>This method first checks if <tt>e</tt> is an instanceof {@link ReferencedEnvelope}, if it
     * is, itself is returned. If not <code>new ReferencedEnvelpe(e,null)</code> is returned.
     *
     * <p>If e is null, null is returned.
     *
     * @param e The envelope. Can be null.
     * @return A ReferencedEnvelope using the specified envelope, or null if the envelope was null.
     */
    public static ReferencedEnvelope reference(Envelope e) {
        if (e == null) {
            return null;
        } 
        else {

            if (e instanceof ReferencedEnvelope) {
                return (ReferencedEnvelope) e;
            }
            return new ReferencedEnvelope(e, null);
        }
    }

    /**
     * Utility method to create a ReferencedEnvelope from a Java2D Rectangle class, supporting empty
     * rectangles.
     *
     * @param rectangle The Java2D Rectangle object
     * @return ReferencedEnvelope,<br>
     *     results in a null/an empty envelope, if input rectangle was empty
     */
    public static ReferencedEnvelope create(Rectangle2D rectangle, CoordinateSystem crs) {
        if (rectangle.isEmpty()) {
            return new ReferencedEnvelope(crs);
        } 
        else {
            return new ReferencedEnvelope(rectangle, crs);
        }
    }
}
