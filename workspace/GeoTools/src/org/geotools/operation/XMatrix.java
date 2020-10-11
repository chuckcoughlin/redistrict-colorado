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
package org.geotools.operation;

import java.io.Serializable;


/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size.
 *
 * @since 2.2
 * @version 13.0
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class XMatrix implements Matrix,Serializable {
	private static final String CLSS = "XMatrix";
	
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = 7116561372481474290L;

    /** The matrix size, which is {@value}. */
    public static final int SIZE = 2;

    /** The first matrix element in the first row. */
    public double m00;

    /** The second matrix element in the first row. */
    public double m01;

    /** The first matrix element in the second row. */
    public double m10;

    /** The second matrix element in the second row. */
    public double m11;

    /** Creates a new identity matrix. */
    public XMatrix() {
        m00 = m11 = 1;
    }

    /** Creates a new matrix initialized to the specified values. */
    public XMatrix(final double m00, final double m01, final double m10, final double m11) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one. The specified
     * matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     */
    public XMatrix(final Matrix matrix) {
        if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
            throw new IllegalArgumentException(String.format("%s.copy: Illegal dimension (%d, args)",CLSS,matrix.getNumRow()));
        }
        m00 = matrix.getElement(0, 0);
        m01 = matrix.getElement(0, 1);
        m10 = matrix.getElement(1, 0);
        m11 = matrix.getElement(1, 1);
    }

    /** Used to cast/copy matrix to Matrix2 */
    XMatrix internal(Matrix matrix) {
        if (matrix instanceof XMatrix) {
            return (XMatrix) matrix;
        } 
        else {
            if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
                throw new IllegalArgumentException(String.format("%s.copy: Illegal dimension (%d, args)",CLSS,matrix.getNumRow()));
            }
            return new XMatrix(matrix);
        }
    }

    /**
     * Returns the number of rows in this matrix, which is always {@value #SIZE} in this
     * implementation.
     */
    public final int getNumRow() {
        return SIZE;
    }

    /**
     * Returns the number of colmuns in this matrix, which is always {@value #SIZE} in this
     * implementation.
     */
    public final int getNumCol() {
        return SIZE;
    }

    /** {@inheritDoc} */
    public final double getElement(final int row, final int col) {
    	switch (row) {
    	case 0:
    	{
    		switch (col) {
    		case 0:
    			return m00;
    		case 1:
    			return m01;
    		}
    		break;
    	}
    	case 1:
    	{
    		switch (col) {
    		case 0:
    			return m10;
    		case 1:
    			return m11;
    		}
    		break;
    	}
    	}
    	throw new IndexOutOfBoundsException();
    }

    /** {@inheritDoc} */
    public final void setElement(final int row, final int col, final double value) {
    	switch (row) {
    	case 0:
    	{
    		switch (col) {
    		case 0:
    			m00 = value;
    			return;
    		case 1:
    			m01 = value;
    			return;
    		}
    		break;
    	}
    	case 1:
    	{
    		switch (col) {
    		case 0:
    			m10 = value;
    			return;
    		case 1:
    			m11 = value;
    			return;
    		}
    		break;
    	}
    	}
    	throw new IndexOutOfBoundsException();
    }

    /** {@inheritDoc} */
    public final void setZero() {
        m00 = m01 = m10 = m11 = 0;
    }

    /** {@inheritDoc} */
    public final void setIdentity() {
        m01 = m10 = 0;
        m00 = m11 = 1;
        assert isIdentity();
    }

    /** {@inheritDoc} */
    public final boolean isIdentity() {
        return m01 == 0 && m10 == 0 && m00 == 1 && m11 == 1;
    }

    /** {@inheritDoc} */
    public final boolean isIdentity(double tolerance) {
    	tolerance = Math.abs(tolerance);
    	final int numRow = getNumRow();
    	final int numCol = getNumCol();
    	if (numRow != numCol) {
    		return false;
    	}
    	for (int j = 0; j < numRow; j++) {
    		for (int i = 0; i < numCol; i++) {
    			double e = getElement(j, i);
    			if (i == j) {
    				e--;
    			}
    			if (!(Math.abs(e) <= tolerance)) { // Uses '!' in order to catch NaN values.
    				return false;
    			}
    		}
    	}
    	// Note: we can't assert matrix.isAffine().
    	return true;
    }

    /** {@inheritDoc} */
    public final boolean isAffine() {
        return m10 == 0 && m11 == 1;
    }

    /** {@inheritDoc} */
    public final void negate() {
        m00 = -m00;
        m01 = -m01;
        m10 = -m10;
        m11 = -m11;
    }

    public void negate(Matrix matrix) {
        XMatrix k = internal(matrix);
        m00 = -k.m00;
        m01 = -k.m01;
        m10 = -k.m10;
        m11 = -k.m11;
    }

    /** {@inheritDoc} */
    public final void transpose() {
        final double swap = m10;
        m10 = m01;
        m01 = swap;
    }

    public void transpose(Matrix matrix) {
    	XMatrix k = internal(matrix);
        m00 = k.m00;
        m01 = k.m10;
        m10 = k.m01;
        m11 = k.m11;
    }

    /** Inverts this matrix in place. */
    public final void invert() {
        final double det = m00 * m11 - m01 * m10;
        if (det == 0) {
            throw new SingularMatrixException("Determinate is zero, cannot invert matrix");
        }
        final double swap = m00;
        m00 = m11 / det;
        m11 = swap / det;
        m10 = -m10 / det;
        m01 = -m01 / det;
    }

    public void invert(Matrix matrix) throws SingularMatrixException {
    	XMatrix k = internal(matrix);
        final double det = k.m00 * k.m11 - k.m01 * k.m10;
        if (det == 0) {
            throw new SingularMatrixException("Determinate is zero, cannot invert matrix");
        }
        m00 = k.m11 / det;
        m11 = k.m00 / det;
        m10 = -k.m10 / det;
        m01 = -k.m01 / det;
    }

    public final void multiply(final Matrix matrix) {
        mul(matrix);
    }

    /**
     * Returns {@code true} if the specified object is of type {@code Matrix2} and all of the data
     * members are equal to the corresponding data members in this matrix.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass().equals(getClass())) {
            final XMatrix that = (XMatrix) object;
            return Double.doubleToLongBits(this.m00) == Double.doubleToLongBits(that.m00)
                    && Double.doubleToLongBits(this.m01) == Double.doubleToLongBits(that.m01)
                    && Double.doubleToLongBits(this.m10) == Double.doubleToLongBits(that.m10)
                    && Double.doubleToLongBits(this.m11) == Double.doubleToLongBits(that.m11);
        }
        return false;
    }

    /** Returns a hash code value based on the data values in this object. */
    @Override
    public int hashCode() {
    	return (int)
    			((((Double.doubleToLongBits(m00) + 37 * Double.doubleToLongBits(m01))
    					+ 37 * Double.doubleToLongBits(m10))
    					+ 37 * Double.doubleToLongBits(m11))
    					^ serialVersionUID);
    }

    /**
     * Returns a string representation of this matrix. The returned string is implementation
     * dependent. It is usually provided for debugging purposes only.
     */
    @Override
    public String toString() {
        final int numRow = getNumRow();
        final int numCol = getNumCol();
        StringBuffer buffer = new StringBuffer();
        final String lineSeparator = System.getProperty("line.separator", "\n");
        for (int j = 0; j < numRow; j++) {
            for (int i = 0; i < numCol; i++) {
                buffer.append(String.format("%2.2f",getElement(j, i)));
                buffer.append(" ");
            }
            buffer.append(lineSeparator);
        }
        return buffer.toString();
    }

    /** Returns a clone of this matrix. */
    @Override
    public XMatrix clone() {
        try {
            return (XMatrix) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen, since we are cloneable.
            throw new AssertionError(e);
        }
    }

    public void getRow(int row, double[] array) {
    	if (array.length != SIZE) {
    		throw new IllegalArgumentException(
    				"Call getRow received an array of length "
    						+ array.length
    						+ ".  "
    						+ "The dimensions of the matrix is 2 by 2.");
    	}
    	if (row == 0) {
    		array[0] = m00;
    		array[1] = m01;
    	} else if (row == 1) {
    		array[0] = m10;
    		array[1] = m11;
    	} else {
    		throw new IllegalArgumentException(
    				"Specified element is out of bounds: (" + row + ", 0)");
    	}
    }

    public void setRow(int row, double... values) {
    	if (values.length != SIZE) {
    		throw new IllegalArgumentException(
    				"Call setRow received an array of length "
    						+ values.length
    						+ ".  "
    						+ "The dimensions of the matrix is 2 by 2.");
    	}
    	if (row == 0) {
    		m00 = values[0];
    		m01 = values[1];
    	} else if (row == 1) {
    		m10 = values[0];
    		m11 = values[1];
    	} else {
    		throw new IllegalArgumentException(
    				"Specified element is out of bounds: (" + row + " , 0)");
    	}
    }

    public void getColumn(int column, double[] array) {
    	if (array.length != SIZE) {
    		throw new IllegalArgumentException(
    				"Call getColumn received an array of length "
    						+ array.length
    						+ ".  "
    						+ "The dimensions of the matrix is 2 by 2.");
    	}
    	if (column == 0) {
    		array[0] = m00;
    		array[1] = m10;
    	} else if (column == 1) {
    		array[0] = m01;
    		array[1] = m11;
    	} else {
    		throw new IllegalArgumentException(
    				"Specified element is out of bounds: (0 , " + column + ")");
    	}
    }

    public void setColumn(int column, double... values) {
    	if (values.length != SIZE) {
    		throw new IllegalArgumentException(
    				"Call setColumn received an array of length "
    						+ values.length
    						+ ".  "
    						+ "The dimensions of the matrix is 2 by 2.");
    	}
    	if (column == 0) {
    		m00 = values[0];
    		m10 = values[1];
    	} else if (column == 1) {
    		m01 = values[0];
    		m11 = values[1];
    	} else {
    		throw new IllegalArgumentException(
    				"Specified element is out of bounds: (0 , " + column + ")");
    	}
    }

    public void add(double scalar) {
        m00 += scalar;
        m01 += scalar;
        m10 += scalar;
        m11 += scalar;
    }

    public void add(double scalar, XMatrix matrix) {
        final XMatrix k = internal(matrix);
        m00 = scalar + k.m00;
        m01 = scalar + k.m01;
        m10 = scalar + k.m10;
        m11 = scalar + k.m11;
    }

    public void add(XMatrix matrix) {
        final XMatrix k = internal(matrix);
        m00 += k.m00;
        m01 += k.m01;
        m10 += k.m10;
        m11 += k.m11;
    }

    public void add(XMatrix matrix1, XMatrix matrix2) {
        final XMatrix a = internal(matrix1);
        final XMatrix b = internal(matrix2);
        m00 = a.m00 + b.m00;
        m01 = a.m01 + b.m01;
        m10 = a.m10 + b.m10;
        m11 = a.m11 + b.m11;
    }

    public double determinate() {
        return (m00 * m11) - (m01 * m10);
    }

    public void mul(double scalar) {
        m00 *= scalar;
        m01 *= scalar;
        m10 *= scalar;
        m11 *= scalar;
    }

    public void mul(double scalar, Matrix matrix) {
        final XMatrix k = internal(matrix);
        m00 = scalar * k.m00;
        m01 = scalar * k.m01;
        m10 = scalar * k.m10;
        m11 = scalar * k.m11;
    }

    public void mul(Matrix matrix) {
        final XMatrix k = internal(matrix);
        double m0, m1;
        m0 = m00;
        m1 = m01;
        m00 = m0 * k.m00 + m1 * k.m10;
        m01 = m0 * k.m01 + m1 * k.m11;
        m0 = m10;
        m1 = m11;
        m10 = m0 * k.m00 + m1 * k.m10;
        m11 = m0 * k.m01 + m1 * k.m11;
    }

    public void mul(Matrix matrix1, Matrix matrix2) {
        final XMatrix a = internal(matrix1);
        final XMatrix b = internal(matrix2);
        m00 = a.m00 * b.m00 + a.m10 * b.m01;
        m01 = a.m00 * b.m10 + a.m10 * b.m11;
        m10 = a.m01 * b.m00 + a.m11 * b.m01;
        m11 = a.m01 * b.m10 + a.m11 * b.m11;
    }

    public void sub(double scalar) {
        m00 -= scalar;
        m01 -= scalar;
        m10 -= scalar;
        m11 -= scalar;
    }

    public void sub(double scalar, Matrix matrix) {
        final XMatrix k = internal(matrix);
        m00 = scalar - k.m00;
        m01 = scalar - k.m01;
        m10 = scalar - k.m10;
        m11 = scalar - k.m11;
    }

    public void sub(Matrix matrix) {
        final XMatrix k = internal(matrix);
        m00 -= k.m00;
        m01 -= k.m01;
        m10 -= k.m10;
        m11 -= k.m11;
    }

    public void sub(Matrix matrix1, Matrix matrix2) {
        final XMatrix a = internal(matrix1);
        final XMatrix b = internal(matrix2);
        m00 = a.m00 - b.m00;
        m01 = a.m01 - b.m01;
        m10 = a.m10 - b.m10;
        m11 = a.m11 - b.m11;
    }
}
