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
package org.geotools.operation.matrix;

import java.io.Serializable;


/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. This trivial matrix is returned as a
 * result of {@linkplain org.opengis.referencing.operation.MathTransform1D} derivative computation.
 *
 * @since 2.2
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class Matrix1 implements Matrix, Serializable {
	private static final String CLSS = "Matrix1";
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -4829171016106097031L;

    /** The only element in this matrix. */
    public double m00;

    /** The matrix size, which is {@value}. */
    public static final int SIZE = 1;

    /** Creates a new identity matrix. */
    public Matrix1() {
        m00 = 1;
    }

    /** Creates a new matrix initialized to the specified value. */
    public Matrix1(final double m00) {
        this.m00 = m00;
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one. The specified
     * matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     */
    public Matrix1(final Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = matrix.getElement(0, 0);
    }

    /**
     * Returns the number of rows in this matrix, which is always {@value #SIZE} in this
     * implementation.
     */
    @Override
    public final int getNumRows() {
        return SIZE;
    }

    /**
     * Returns the number of colmuns in this matrix, which is always {@value #SIZE} in this
     * implementation.
     */
    @Override
    public final int getNumCols() {
        return SIZE;
    }

    @Override
    public final double getElement(final int row, final int col) {
        if (row == 0 && col == 0) {
            return m00;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public final void setElement(final int row, final int col, final double value) {
        if (row == 0 && col == 0) {
            m00 = value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public final void zero() {
        m00 = 0;
    }

    @Override
    public final void setIdentity() {
        m00 = 1;
    }

    /** {@inheritDoc} */
    public final boolean isIdentity() {
        return m00 == 1;
    }

    /** {@inheritDoc} */
    public final boolean isIdentity(double tolerance) {
        return Math.abs(m00 - 1) <= Math.abs(tolerance);
    }

    /** {@inheritDoc} */
    public final boolean isAffine() {
        return m00 == 1;
    }

    /** {@inheritDoc} */
    public final void negate() {
        m00 = -m00;
    }

    @Override
    public void negate(Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = -matrix.getElement(0, 0);
    }

    @Override
    public final void transpose() {
        // Nothing to do for a 1x1 matrix.
    }

    @Override
    public void transpose(Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = matrix.getElement(0, 0);
    }

    /** Inverts this matrix in place. */
    public final void invert() {
        if (m00 == 0) {
            throw new SingularMatrixException("1 dimensional m is singular");
        }
        m00 = 1.0 / m00;
    }

    @Override
    public void invert(Matrix matrix) throws SingularMatrixException {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s.invert: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        if (matrix.getElement(0, 0) == 0) {
            throw new SingularMatrixException("1 dimensional matrix is singular");
        }
        m00 = 1.0 / matrix.getElement(0, 0);
    }

    /** {@inheritDoc} */
    public final void multiply(final Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s.multiply: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 *= matrix.getElement(0, 0);
    }

    /**
     * Returns {@code true} if the specified object is of type {@code Matrix1} and all of the data
     * members are equal to the corresponding data members in this matrix.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass().equals(getClass())) {
            final Matrix1 that = (Matrix1) object;
            return Double.doubleToLongBits(this.m00) == Double.doubleToLongBits(that.m00);
        }
        return false;
    }

    /** Returns a hash code value based on the data values in this object. */
    @Override
    public int hashCode() {
        return (int) (Double.doubleToLongBits(m00) ^ serialVersionUID);
    }


    /** Returns a clone of this matrix. */
    @Override
    public Matrix1 clone() {
        try {
            return (Matrix1) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen, since we are cloneable.
            throw new AssertionError(e);
        }
    }

    public void getRow(int row, double[] array) {
        if (array.length != 1) {
            throw new IllegalArgumentException(
                    "Call getRow received an array of length "
                            + array.length
                            + ".  "
                            + "The dimensions of the matrix is 1 by 1.");
        }
        if (row != 0) {
            throw new IllegalArgumentException(
                    "Specified element is out of bounds: (" + row + " , 0)");
        }
        array[0] = m00;
    }

    public void setRow(int row, double... values) {
        if (values.length != 1) {
            throw new IllegalArgumentException(
                    "Call setRow received an array of length "
                            + values.length
                            + ".  "
                            + "The dimensions of the matrix is 1 by 1.");
        }
        m00 = values[0];
    }

    public void getColumn(int column, double[] array) {
        if (array.length != 1) {
            throw new IllegalArgumentException(
                    "Call getColumn received an array of length "
                            + array.length
                            + ".  "
                            + "The dimensions of the matrix is 1 by 1.");
        }
        if (column != 0) {
            throw new IllegalArgumentException(
                    "Specified element is out of bounds: (0 , " + column + ")");
        }
        array[0] = m00;
    }

    public void setColumn(int column, double... values) {
        if (values.length != 0) {
            throw new IllegalArgumentException(
                    "Call setColumn received an array of length "
                            + values.length
                            + ".  "
                            + "The dimensions of the matrix is 1 by 1.");
        }
        m00 = values[0];
    }

    @Override
    public void add(double scalar) {
        m00 += scalar;
    }

    @Override
    public void add(double scalar, Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = scalar + matrix.getElement(0, 0);
    }

    @Override
    public void add(Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 += matrix.getElement(0, 0);
    }

    @Override
    public void add(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getNumRows() != SIZE || matrix1.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s.add: Matrix size mismatch(%d vs %d)",
        			CLSS,matrix1.getNumRows(),matrix2.getNumRows()));
        }
        if (matrix2.getNumRows() != SIZE || matrix2.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix1.getNumRows()));
        }
        m00 = matrix1.getElement(0,0) + matrix2.getElement(0,0);
    }

    @Override
    public double determinate() {
        return m00; // trivial 1x1 matrix
    }

    @Override
    public void mul(double scalar) {
        m00 *= scalar;
    }

    @Override
    public void mul(double scalar, Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = scalar * matrix.getElement(0, 0);
    }

    @Override
    public void mul(Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 *= matrix.getElement(0, 0);
    }

    @Override
    public void mul(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getNumRows() != SIZE || matrix1.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix1.getNumRows()));
        }
        if (matrix2.getNumRows() != SIZE || matrix2.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix1.getNumRows()));
        }
        m00 = matrix1.getElement(0, 0) * matrix2.getElement(0, 0);
    }

    @Override
    public void sub(double scalar) {
        m00 -= scalar;
    }

    @Override
    public void sub(Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 -= matrix.getElement(0, 0);
    }

    @Override
    public void sub(double scalar, Matrix matrix) {
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        m00 = scalar - matrix.getElement(0, 0);
    }

    @Override
    public void sub(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getNumRows() != SIZE || matrix1.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix1.getNumRows()));
        }
        if (matrix2.getNumRows() != SIZE || matrix2.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix1.getNumRows()));
        }
        m00 = matrix1.getElement(0, 0) - matrix2.getElement(0, 0);
    }
}
