/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011-2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.operation.matrix;


/**
 * A two dimensional array of numbers. Row and column numbering begins with zero.
 *
 * @version <A HREF="http://www.opengis.org/docs/01-009.pdf">Implementation specification 1.0</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see java.awt.geom.AffineTransform
 * @see <A HREF="http://ejml.org/"EJML</A>
 */

public interface Matrix extends Cloneable {
	/**
	 * @return The number of rows in this matrix.
	 */
	public int getNumRows();
	// Same signature than GMatrix, for straightforward implementation.

	/**
	 * @return The number of columns in this matrix.
	 */
	public int getNumCols();

	/**
	 * Retrieves the value at the specified row and column of this matrix.
	 *
	 * @param row The row number to be retrieved (zero indexed).
	 * @param column The column number to be retrieved (zero indexed).
	 * @return The value at the indexed element.
	 */
	public double getElement(int row, int column);

	/**
	 * Modifies the value at the specified row and column of this matrix.
	 *
	 * @param row The row number to be retrieved (zero indexed).
	 * @param column The column number to be retrieved (zero indexed).
	 * @param value The new matrix element value.
	 */
	public void setElement(int row, int column, double value);

	/**
	 * Returns {@code true} if this matrix is an identity matrix.
	 *
	 * @return {@code true} if this matrix is an identity matrix.
	 */
	public boolean isIdentity();

	/**
	 * Returns a clone of this matrix.
	 *
	 * @return A clone of this matrix.
	 */
	public Matrix clone();

	/** Negates the value of this matrix: {@code this = -this}. */
	public void negate();

	/**
	 * Negates the value of this matrix: {@code this = -matrix}.
	 *
	 * @param matrix Matrix to negated
	 */
	public void negate(Matrix matrix);

	/** Sets the value of this matrix to its transpose. */
	public void transpose();

	/**
	 * Set to the transpose of the provided matrix.
	 *
	 * @param matrix The original matrix. Not modified.
	 */
	public void transpose(Matrix matrix);

	/**
	 * Inverts this matrix in place.
	 *
	 * @throws SingularMatrixException if this matrix is not invertible.
	 */
	public void invert() throws SingularMatrixException;

	/**
	 * Set to the inverse of the provided matrix.
	 *
	 * @param matrix The matrix that is to be inverted. Not modified.
	 * @throws SingularMatrixException if this matrix is not invertible.
	 */
	public void invert(Matrix matrix) throws SingularMatrixException;

	/**
	 * Performs an in-place scalar addition.
	 *
	 * @param scalar The value that's added to each element.
	 */
	public void add(double scalar);

	/**
	 * Set to the scalar addition of <code>scalar+matrix<code>
	 *
	 * @param scalar The value that's added to each element.
	 * @param matrix The matrix that is to be added. Not modified.
	 */
	public void add(double scalar, Matrix matrix);
	/** Set to the matrix addition of <code>this+matrix</code>.
	 *
	 * @param matrix The matrix that is to be added. Not modified.
	 */
	public void add(Matrix matrix);

	/**
	 * Set to the matrix addition of <code>matrix1+matrix2</code>.
	 *
	 * @param matrix1 First matrix to be added. Not modified.
	 * @param matrix2 Second matrix to be added. Not modified.
	 */
	public void add(Matrix matrix1, Matrix matrix2);

	/** Computes the determinant */
	public double determinate();

	/**
	 * Sets the value of this matrix to the result of multiplying itself with the specified matrix.
	 * In other words, performs {@code this} = {@code this} &times; {@code matrix}. In the context
	 * of coordinate transformations, this is equivalent to <code>
	 * {@linkplain java.awt.geom.AffineTransform#concatenate AffineTransform.concatenate}</code>:
	 * first transforms by the supplied transform and then transform the result by the original
	 * transform.
	 *
	 * @param matrix The matrix to multiply to this matrix.
	 */
	public void multiply(Matrix matrix);
	/**
	 * Sets this matrix to the result of multiplying itself with the provided scalar.
	 *
	 * @param scalar
	 */
	public void mul(double scalar);

	/**
	 * Sets the value of this matrix to the result of multiplying the provided scalar and matrix.
	 *
	 * @param scalar
	 * @param matrix
	 */
	public void mul(double scalar, Matrix matrix);

	/**
	 * Sets the value of this matrix to the result of multiplying itself with the specified matrix.
	 * In other words, performs {@code this} = {@code this} &times; {@code matrix}. In the context
	 * of coordinate transformations, this is equivalent to <code>
	 * {@linkplain java.awt.geom.AffineTransform#concatenate AffineTransform.concatenate}</code>:
	 * first transforms by the supplied transform and then transform the result by the original
	 * transform.
	 *
	 * @param matrix The matrix to multiply to this matrix.
	 */
	public void mul(Matrix matrix);
	/**
	 * Sets the value of this matrix to the result of multiplying matrix1 and matrix2.
	 *
	 * @param matrix1
	 * @param matrix2
	 */
	public void mul(Matrix matrix1, Matrix matrix2);

    public void setIdentity();
	/**
	 * In-place matrix subtraction: <code>this - scalar</code>.
	 *
	 * @param scalar
	 */
	public void sub(double scalar);
	/**
	 * Set to the difference of <code>scalar - matrix2</code>.
	 *
	 * @param scalar
	 * @param matrix matrix, not modified
	 */
	public void sub(double scalar, Matrix matrix);

	/**
	 * In-place matrix subtraction: <code>this - matrix</code>.
	 *
	 * @param matrix m by n matrix. Not modified.
	 */
	public void sub(Matrix matrix);

	/**
	 * Set to the difference of <code>matrix1 - matrix2</code>.
	 *
	 * @param matrix1 matrix, not modified
	 * @param matrix2 matrix, not modified
	 */
	public void sub(Matrix matrix1, Matrix matrix2);
    public void zero();

}
