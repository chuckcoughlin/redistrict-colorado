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

/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. It is used primarily for supporting
 * datum shifts.
 *
 * @since 2.2
 * @version 13.0
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class Matrix4 extends GeneralMatrix implements Matrix {
    private static final String CLSS = "Matrix4";
    private static final long serialVersionUID = 5685762518066856311L;

    /** The matrix size, which is {@value}. */
    public static final int SIZE = 4;

    /** Creates a new identity matrix. */
    public Matrix4() {
        super(SIZE);
        setIdentity();
    }

    /** Creates a new matrix initialized to the specified values. */
    public Matrix4(
            double m00,
            double m01,
            double m02,
            double m03,
            double m10,
            double m11,
            double m12,
            double m13,
            double m20,
            double m21,
            double m22,
            double m23,
            double m30,
            double m31,
            double m32,
            double m33) {
        super(
                SIZE,
                SIZE,
                new double[] {
                    m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23,
                    m30, m31, m32, m33
                });
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one. The specified
     * matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     */
    public Matrix4(final Matrix matrix) {
        super(SIZE);
        if (matrix.getNumRows() != SIZE || matrix.getNumCols() != SIZE) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE; i++) {
                setElement(j, i, matrix.getElement(j, i));
            }
        }
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

    /** Returns a clone of this matrix. */
    @Override
    public Matrix4 clone() {
        return new Matrix4(this);
    }
}
