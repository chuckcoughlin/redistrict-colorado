/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.operation;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;


/**
 * Transforms two-dimensional coordinate points using an affine transform. This class both extends
 * {@link AffineTransform} and implements {@link MathTransform2D}, so it can be used as a bridge
 * between Java2D and the referencing module.
 *
 * @since 2.5
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class AffineTransform2D extends AffineTransform
        implements MathTransform, LinearTransform, Cloneable {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -5266837898367149069L;

    /** The inverse transform. This field will be computed only when needed. */
    private transient AffineTransform2D inverse;
    private final Map<String,Object> properties;

    /** Constructs a default affine transform which is an identity transform */
    public AffineTransform2D() {
        super();
        this.properties = new HashMap<>();
    }
    
    /** Constructs a new affine transform with the same coefficient than the specified transform. */
    public AffineTransform2D(final AffineTransform transform) {
        super(transform);
        this.properties = new HashMap<>();
    }

    /**
     * Constructs a new {@code AffineTransform2D} from 6 values representing the 6 specifiable
     * entries of the 3&times;3 transformation matrix. Those values are given unchanged to the
     * {@link AffineTransform#AffineTransform(double,double,double,double,double,double) super class
     * constructor}.
     *
     * @since 2.5
     */
    public AffineTransform2D(
            double m00, double m10, double m01, double m11, double m02, double m12) {
        super(m00, m10, m01, m11, m02, m12);
        this.properties = new HashMap<>();
    }

    @Override
    public Map<String,Object> getProperties() { return this.properties; }
    
    /** Gets the dimension of input points, which is fixed to 2. */
    public final int getSourceDimensions() {
        return 2;
    }

    /** Gets the dimension of output points, which is fixed to 2. */
    public final int getTargetDimensions() {
        return 2;
    }

    /** Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}. */
    public Coordinate transform(final Coordinate ptSrc, Coordinate ptDst) {
        if (ptDst == null) {
            ptDst = new Coordinate();
        } 
        final double[] array = new double[2];
        array[0] = ptSrc.getOrdinate(0);
        array[1] = ptSrc.getOrdinate(1);
        transform(array, 0, array, 0, 1);
        ptDst.setOrdinate(0, array[0]);
        ptDst.setOrdinate(1, array[1]);
        return ptDst;
    }
    /**
     * Transforms the given shape. This method is similar to {@link #createTransformedShape
     * createTransformedShape} except that:
     *
     * <p>
     *
     * <ul>
     *   <li>It tries to preserve the shape kind when possible. For example if the given shape is an
     *       instance of {@link RectangularShape} and the given transform do not involve rotation,
     *       then the returned shape may be some instance of the same class.
     *   <li>It tries to recycle the given object if {@code overwrite} is {@code true}.
     * </ul>
     *
     * @param transform Affine transform to use.
     * @param shape The shape to transform.
     * @param overwrite If {@code true}, this method is allowed to overwrite {@code shape} with the
     *     transform result. If {@code false}, then {@code shape} is never modified.
     * @return The direct transform of the given shape. May or may not be the same instance than the
     *     given shape.
     * @see #createTransformedShape
     * @since 2.5
     */
    public static Shape transform(final AffineTransform transform, Shape shape, boolean overwrite) {
        final int type = transform.getType();
        if (type == TYPE_IDENTITY) {
            return shape;
        }
        // If there is only scale, flip, quadrant rotation or translation,
        // then we can optimize the transformation of rectangular shapes.
        if ((type & (TYPE_GENERAL_ROTATION | TYPE_GENERAL_TRANSFORM)) == 0) {
            // For a Rectangle input, the output should be a rectangle as well.
            if (shape instanceof Rectangle2D) {
                final Rectangle2D rect = (Rectangle2D) shape;
                return transform(transform, rect, overwrite ? rect : null);
            }
            // For other rectangular shapes, we restrict to cases whithout
            // rotation or flip because we don't know if the shape is symetric.
            if ((type & (TYPE_FLIP | TYPE_MASK_ROTATION)) == 0) {
                if (shape instanceof RectangularShape) {
                    RectangularShape rect = (RectangularShape) shape;
                    if (!overwrite) {
                        rect = (RectangularShape) rect.clone();
                    }
                    final Rectangle2D frame = rect.getFrame();
                    rect.setFrame(transform(transform, frame, frame));
                    return rect;
                }
            }
        }
        // TODO: Check for Path2D instance instead of GeneralPath
        //       when we will be allowed to compile for Java 6.
        if (shape instanceof GeneralPath) {
            final GeneralPath path = (GeneralPath) shape;
            if (overwrite) {
                path.transform(transform);
            } else {
                shape = path.createTransformedShape(transform);
            }
        } else if (shape instanceof Area) {
            final Area area = (Area) shape;
            if (overwrite) {
                area.transform(transform);
            } else {
                shape = area.createTransformedArea(transform);
            }
        } else {
            final GeneralPath path = new GeneralPath(shape);
            path.transform(transform);
            shape = path;
            // TODO: use the line below instead of the above 3 lines when we will
            //       be allowed to compile for Java 6:
            //          shape = new Path2D.Double(shape, transform);
        }
        return shape;
    }

    /**
     * Transforms the specified shape.
     *
     * @param shape Shape to transform.
     * @return Transformed shape, or {@code shape} if this transform is the identity transform.
     */
    @Override
    public Shape createTransformedShape(final Shape shape) {
        return transform(this, shape, false);
    }

    /** Returns this transform as an affine transform matrix. */
    public Matrix getMatrix() {
        return new XMatrix(this.getMatrix());
    }

    /**
     * Gets the derivative of this transform at a point. For an affine transform, the derivative is
     * the same everywhere.
     */
    public Matrix derivative(final Point2D point) {
        return new XMatrix(
                getScaleX(), getShearX(),
                getShearY(), getScaleY());
    }

    /**
     * Gets the derivative of this transform at a point. For an affine transform, the derivative is
     * the same everywhere.
     */
    public Matrix derivative(final Coordinate point) {
        return derivative((Point2D) null);
    }

    /**
     * Creates the inverse transform of this object.
     *
     * @throws NoninvertibleTransformException if this transform can't be inverted.
     */
    public MathTransform inverse() throws TransformException {
        if (inverse == null) {
            if (isIdentity()) {
                inverse = this;
            } else
                try {
                    synchronized (this) {
                        inverse = new AffineTransform2D(createInverse());
                        inverse.inverse = this;
                    }
                } catch (java.awt.geom.NoninvertibleTransformException exception) {
                    throw new TransformException(
                            exception.getLocalizedMessage(), exception);
                }
        }
        return inverse;
    }

    /**
     * Returns a new affine transform which is a modifiable copy of this transform. We override this
     * method because it is {@linkplain AffineTransform#clone defined in the super-class}. However
     * this implementation do not returns a new {@code AffineTransform2D} instance because the later
     * is unmodifiable, which make exact cloning useless.
     */
    @Override
    public AffineTransform clone() {
        return new AffineTransform(this);
    }



    /**
     * Checks whether or not this {@code XAffineTransform} is the identity by using the provided
     * {@code tolerance}.
     *
     * @param tolerance The tolerance to use for this check.
     * @return {@code true} if the transform is identity, {@code false} otherwise.
     * @since 2.3.1
     */
    public boolean isIdentity(double tolerance) {
        return isIdentity(this, tolerance);
    }

    /**
     * Returns {@code true} if the specified affine transform is an identity transform up to the
     * specified tolerance. This method is equivalent to computing the difference between this
     * matrix and an identity matrix (as created by {@link AffineTransform#AffineTransform() new
     * AffineTransform()}) and returning {@code true} if and only if all differences are smaller
     * than or equal to {@code tolerance}.
     *
     * <p>This method is used for working around rounding error in affine transforms resulting from
     * a computation, as in the example below:
     *
     * <blockquote>
     *
     * <pre>
     * [ 1.0000000000000000001  0.0                      0.0 ]
     * [ 0.0                    0.999999999999999999999  0.0 ]
     * [ 0.0                    0.0                      1.0 ]
     * </pre>
     *
     * </blockquote>
     *
     * @param tr The affine transform to be checked for identity.
     * @param tolerance The tolerance value to use when checking for identity. return {@code true}
     *     if this tranformation is close enough to the identity, {@code false} otherwise.
     * @since 2.3.1
     */
    public static boolean isIdentity(final AffineTransform tr, double tolerance) {
        if (tr.isIdentity()) {
            return true;
        }
        tolerance = Math.abs(tolerance);
        return Math.abs(tr.getScaleX() - 1) <= tolerance
                && Math.abs(tr.getScaleY() - 1) <= tolerance
                && Math.abs(tr.getShearX()) <= tolerance
                && Math.abs(tr.getShearY()) <= tolerance
                && Math.abs(tr.getTranslateX()) <= tolerance
                && Math.abs(tr.getTranslateY()) <= tolerance;
    }

    @Override
    @SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
    public boolean equals(Object obj) {
        if (!(obj instanceof AffineTransform)) {
            return false;
        }

        AffineTransform a = (AffineTransform) obj;

        return     getScaleX() == a.getScaleX()
                && getScaleY() == a.getScaleY()
                && getShearX() == a.getShearX()
                && getShearY() == a.getShearY()
                && getTranslateX() == a.getTranslateX()
                && getTranslateY() == a.getTranslateY();
    }
    /**
     * Returns a rectangle which entirely contains the direct transform of {@code bounds}. This
     * operation is equivalent to:
     *
     * <blockquote>
     *
     * <code>
     * {@linkplain #createTransformedShape createTransformedShape}(bounds).{@linkplain
     * Rectangle2D#getBounds2D() getBounds2D()}
     * </code>
     *
     * </blockquote>
     *
     * @param transform Affine transform to use.
     * @param bounds Rectangle to transform. This rectangle will not be modified except if {@code
     *     dest} is the same reference.
     * @param dest Rectangle in which to place the result. If null, a new rectangle will be created.
     * @return The direct transform of the {@code bounds} rectangle.
     * @see org.geotools.referencing.CRS#transform(
     *     org.opengis.referencing.operation.MathTransform2D, Rectangle2D, Rectangle2D)
     */
    public static Rectangle2D transform(
            final AffineTransform transform, final Rectangle2D bounds, final Rectangle2D dest) {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        final Point2D.Double point = new Point2D.Double();
        for (int i = 0; i < 4; i++) {
            point.x = (i & 1) == 0 ? bounds.getMinX() : bounds.getMaxX();
            point.y = (i & 2) == 0 ? bounds.getMinY() : bounds.getMaxY();
            transform.transform(point, point);
            if (point.x < xmin) xmin = point.x;
            if (point.x > xmax) xmax = point.x;
            if (point.y < ymin) ymin = point.y;
            if (point.y > ymax) ymax = point.y;
        }
        if (dest != null) {
            dest.setRect(xmin, ymin, xmax - xmin, ymax - ymin);
            return dest;
        }
        return new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /**
     * Returns a rectangle which entirely contains the inverse transform of {@code bounds}. This
     * operation is equivalent to:
     *
     * <blockquote>
     *
     * <code>
     * {@linkplain #createInverse() createInverse()}.{@linkplain
     * #createTransformedShape createTransformedShape}(bounds).{@linkplain
     * Rectangle2D#getBounds2D() getBounds2D()}
     * </code>
     *
     * </blockquote>
     *
     * @param transform Affine transform to use.
     * @param bounds Rectangle to transform. This rectangle will not be modified.
     * @param dest Rectangle in which to place the result. If null, a new rectangle will be created.
     * @return The inverse transform of the {@code bounds} rectangle.
     * @throws NoninvertibleTransformException if the affine transform can't be inverted.
     */
    public static Rectangle2D inverseTransform(
            final AffineTransform transform, final Rectangle2D bounds, final Rectangle2D dest)
            throws NoninvertibleTransformException {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        final Point2D.Double point = new Point2D.Double();
        for (int i = 0; i < 4; i++) {
            point.x = (i & 1) == 0 ? bounds.getMinX() : bounds.getMaxX();
            point.y = (i & 2) == 0 ? bounds.getMinY() : bounds.getMaxY();
            transform.inverseTransform(point, point);
            if (point.x < xmin) xmin = point.x;
            if (point.x > xmax) xmax = point.x;
            if (point.y < ymin) ymin = point.y;
            if (point.y > ymax) ymax = point.y;
        }
        if (dest != null) {
            dest.setRect(xmin, ymin, xmax - xmin, ymax - ymin);
            return dest;
        }
        return new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /**
     * Calculates the inverse affine transform of a point without without applying the translation
     * components.
     *
     * @param transform Affine transform to use.
     * @param source Point to transform. This rectangle will not be modified.
     * @param dest Point in which to place the result. If {@code null}, a new point will be created.
     * @return The inverse transform of the {@code source} point.
     * @throws NoninvertibleTransformException if the affine transform can't be inverted.
     */
    public static Point2D inverseDeltaTransform(
            final AffineTransform transform, final Point2D source, final Point2D dest)
            throws NoninvertibleTransformException {
        final double m00 = transform.getScaleX();
        final double m11 = transform.getScaleY();
        final double m01 = transform.getShearX();
        final double m10 = transform.getShearY();
        final double det = m00 * m11 - m01 * m10;
        if (!(Math.abs(det) > Double.MIN_VALUE)) {
            return transform.createInverse().deltaTransform(source, dest);
        }
        final double x0 = source.getX();
        final double y0 = source.getY();
        final double x = (x0 * m11 - y0 * m01) / det;
        final double y = (y0 * m00 - x0 * m10) / det;
        if (dest != null) {
            dest.setLocation(x, y);
            return dest;
        }
        return new Point2D.Double(x, y);
    }

    /**
     * Returns the magnitude of scale factor <var>x</var> by cancelling the effect of eventual flip
     * and rotation. This factor is calculated by <IMG
     * src="{@docRoot}/org/geotools/display/canvas/doc-files/scaleX0.png">.
     */
    public static double getScaleX0(final AffineTransform tr) {
        final double scale = tr.getScaleX();
        final double shear = tr.getShearX();
        if (shear == 0) return Math.abs(scale); // Optimization for a very common case.
        if (scale == 0) return Math.abs(shear); // Not as common as above, but still common enough.
        return Math.hypot(scale, shear);
    }

    /**
     * Returns the magnitude of scale factor <var>y</var> by cancelling the effect of eventual flip
     * and rotation. This factor is calculated by <IMG
     * src="{@docRoot}/org/geotools/display/canvas/doc-files/scaleY0.png">.
     */
    public static double getScaleY0(final AffineTransform tr) {
        final double scale = tr.getScaleY();
        final double shear = tr.getShearY();
        if (shear == 0) return Math.abs(scale); // Optimization for a very common case.
        if (scale == 0) return Math.abs(shear); // Not as common as above, but still common enough.
        return Math.hypot(scale, shear);
    }

    /**
     * Returns a global scale factor for the specified affine transform. This scale factor will
     * combines {@link #getScaleX0} and {@link #getScaleY0}. The way to compute such a "global"
     * scale is somewhat arbitrary and may change in a future version.
     */
    public static double getScale(final AffineTransform tr) {
        return 0.5 * (getScaleX0(tr) + getScaleY0(tr));
    }

    /**
     * Returns an affine transform representing a zoom carried out around a central point
     * (<var>x</var>, <var>y</var>). The transforms will leave the specified (<var>x</var>,
     * <var>y</var>) coordinate unchanged.
     *
     * @param sx Scale along <var>x</var> axis.
     * @param sy Scale along <var>y</var> axis.
     * @param x <var>x</var> coordinates of the central point.
     * @param y <var>y</var> coordinates of the central point.
     * @return Affine transform of a zoom which leaves the (<var>x</var>,<var>y</var>) coordinate
     *     unchanged.
     */
    public static AffineTransform getScaleInstance(
            final double sx, final double sy, final double x, final double y) {
        return new AffineTransform(sx, 0, 0, sy, (1 - sx) * x, (1 - sy) * y);
    }

    /**
     * Checks whether the matrix coefficients are close to whole numbers. If this is the case, these
     * coefficients will be rounded up to the nearest whole numbers. This rounding up is useful, for
     * example, for speeding up image displays. Above all, it is efficient when we know that a
     * matrix has a chance of being close to the similarity matrix.
     *
     * @param tr The matrix to round. Rounding will be applied in place.
     * @param tolerance The maximal departure from integers in order to allow rounding. It is
     *     typically a small number like {@code 1E-6}.
     * @since 2.3.1
     */
    public static void round(final AffineTransform tr, final double tolerance) {
        double r;
        final double m00, m01, m10, m11;
        if (Math.abs((m00 = Math.rint(r = tr.getScaleX())) - r) <= tolerance
                && Math.abs((m01 = Math.rint(r = tr.getShearX())) - r) <= tolerance
                && Math.abs((m11 = Math.rint(r = tr.getScaleY())) - r) <= tolerance
                && Math.abs((m10 = Math.rint(r = tr.getShearY())) - r) <= tolerance) {
            if ((m00 != 0 || m01 != 0) && (m10 != 0 || m11 != 0)) {
                double m02 = Math.rint(r = tr.getTranslateX());
                if (!(Math.abs(m02 - r) <= tolerance)) m02 = r;
                double m12 = Math.rint(r = tr.getTranslateY());
                if (!(Math.abs(m12 - r) <= tolerance)) m12 = r;
                tr.setTransform(m00, m10, m01, m11, m02, m12);
            }
        }
    }
}
