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
package org.geotools.operation.matrix;

import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

import org.ejml.UtilEjml;
import org.ejml.data.DMatrixRMaj;
import org.ejml.ops.CommonOps_DDRM;
import org.opengis.MismatchedDimensionException;
import org.openjump.coordsys.AxisDirection;


/**
 * A two dimensional array of numbers. Row and column numbering begins with zero.
 *
 * @since 2.2
 * @version 14.0
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Simone Giannecchini
 * @see java.awt.geom.AffineTransform
 */
public class GeneralMatrix implements Matrix, Serializable {
    private static final String CLSS = "General Matrix";
    private static final long serialVersionUID = 8447482612423035361L;

    DMatrixRMaj mat;

    /**
     * Constructs a square identity matrix of size {@code size}&nbsp;&times;&nbsp;{@code size}.
     *
     * @param size The number of rows and columns.
     */
    public GeneralMatrix(final int size) {
        mat = new DMatrixRMaj(size, size);
        setIdentity();
    }

    /**
     * Creates a matrix of size {@code numRow}&nbsp;&times;&nbsp;{@code numCol}. Elements on the
     * diagonal <var>j==i</var> are set to 1.
     *
     * @param numRow Number of rows.
     * @param numCol Number of columns.
     */
    public GeneralMatrix(final int numRow, final int numCol) {
        mat = new DMatrixRMaj(numRow, numCol);
        setIdentity();
    }

    /**
     * Constructs a {@code numRow}&nbsp;&times;&nbsp;{@code numCol} matrix initialized to the values
     * in the {@code matrix} array. The array values are copied in one row at a time in row major
     * fashion. The array should be exactly <code>numRow*numCol</code> in length. Note that because
     * row and column numbering begins with zero, {@code numRow} and {@code numCol} will be one
     * larger than the maximum possible matrix index values.
     *
     * @param numRow Number of rows.
     * @param numCol Number of columns.
     * @param matrix Initial values in row order
     */
    public GeneralMatrix(final int numRow, final int numCol, final double... matrix) {
        mat = new DMatrixRMaj(numRow, numCol, true, matrix);
        if (numRow * numCol != matrix.length) {
            throw new IllegalArgumentException(String.valueOf(matrix.length));
        }
    }

    /**
     * Constructs a {@code numRow}&nbsp;&times;&nbsp;{@code numCol} matrix initialized to the values
     * in the {@code matrix} array. The array values are copied in one row at a time in row major
     * fashion. The array should be exactly <code>numRow*numCol</code> in length. Note that because
     * row and column numbering begins with zero, {@code numRow} and {@code numCol} will be one
     * larger than the maximum possible matrix index values.
     *
     * @param numRow Number of rows.
     * @param numCol Number of columns.
     * @param matrix Initial values in row order
     */
    public GeneralMatrix(final int numRow, final int numCol, final Matrix matrix) {
        mat = new DMatrixRMaj(numRow, numCol);
        if (matrix.getNumRows() != numRow || matrix.getNumCols() != numCol) {
        	throw new IllegalArgumentException(String.format("%s: Illegal matrix size (%d)",CLSS,matrix.getNumRows()));
        }
        for (int j = 0; j < numRow; j++) {
            for (int i = 0; i < numCol; i++) {
                setElement(j, i, matrix.getElement(j, i));
            }
        }
    }

    /**
     * Constructs a new matrix from a two-dimensional array of doubles.
     *
     * @param matrix Array of rows. Each row must have the same length.
     * @throws IllegalArgumentException if the specified matrix is not regular (i.e. if all rows
     *     doesn't have the same length).
     */
    public GeneralMatrix(final double[][] matrix) throws IllegalArgumentException {
        mat = new DMatrixRMaj(matrix);
        final int numRow = getNumRows();
        final int numCol = getNumCols();
        for (int j = 0; j < numRow; j++) {
            if (matrix[j].length != numCol) {
            	throw new IllegalArgumentException(String.format("%s: Illegal matrix not regular (%d)",
            			CLSS,mat.getNumRows()));
            }
            for (int i = 0; i < numCol; i++) {
                mat.setElement(j, i, matrix[j][i]);
            }
        }
    }

    /**
     * Constructs a new matrix and copies the initial values from the parameter matrix.
     *
     * @param matrix The matrix to copy.
     */
    public GeneralMatrix(final Matrix matrix) {
        if (matrix instanceof GeneralMatrix) {
            mat = new DMatrixRMaj(((GeneralMatrix) matrix).mat);
        } 
        else {
            mat = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());

            final int height = getNumRows();
            final int width = getNumCols();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    mat.setElement(j, i, matrix.getElement(j, i));
                }
            }
        }
    }

    /**
     * Constructs a new matrix and copies the initial values from the parameter matrix.
     *
     * @param matrix The matrix to copy.
     */
    public GeneralMatrix(final GeneralMatrix matrix) {
        mat = new DMatrixRMaj(matrix.mat);
    }

    /**
     * Constructs a 3&times;3 matrix from the specified affine transform.
     *
     * @param transform The matrix to copy.
     */
    public GeneralMatrix(final AffineTransform transform) {
        mat =
                new DMatrixRMaj(
                        3,
                        3,
                        true,
                        new double[] {
                            transform.getScaleX(),
                            transform.getShearX(),
                            transform.getTranslateX(),
                            transform.getShearY(),
                            transform.getScaleY(),
                            transform.getTranslateY(),
                            0,
                            0,
                            1
                        });
        assert isAffine() : this;
    }
    //
    // In-place operations
    //
    /**
     * Cast (or convert) Matrix to internal DMatrixRMaj representation required for CommonOps_DDRM.
     *
     * @param matrix
     * @return
     */
    private DMatrixRMaj internal(Matrix matrix) {
        if (matrix instanceof GeneralMatrix) {
            return ((GeneralMatrix) matrix).mat;
        } else {
            DMatrixRMaj a = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());
            for (int j = 0; j < a.numRows; j++) {
                for (int i = 0; i < a.numCols; i++) {
                    a.setElement(j, i, matrix.getElement(j, i));
                }
            }
            return a;
        }
    }


    /**
     * Retrieves the specifiable values in the transformation matrix into a 2-dimensional array of
     * double precision values. The values are stored into the 2-dimensional array using the row
     * index as the first subscript and the column index as the second. Values are copied; changes
     * to the returned array will not change this matrix.
     *
     * @param matrix The matrix to extract elements from.
     * @return The matrix elements.
     */
    public static double[][] getElements(final Matrix matrix) {
        if (matrix instanceof GeneralMatrix) {
            return ((GeneralMatrix) matrix).getElements();
        }
        final int numCol = matrix.getNumCols();
        final double[][] rows = new double[matrix.getNumRows()][];
        for (int j = 0; j < rows.length; j++) {
            final double[] row;
            rows[j] = row = new double[numCol];
            for (int i = 0; i < row.length; i++) {
                row[i] = matrix.getElement(j, i);
            }
        }
        return rows;
    }

    /**
     * Retrieves the specifiable values in the transformation matrix into a 2-dimensional array of
     * double precision values. The values are stored into the 2-dimensional array using the row
     * index as the first subscript and the column index as the second. Values are copied; changes
     * to the returned array will not change this matrix.
     *
     * @return The matrix elements.
     */
    public final double[][] getElements() {
        final int numCol = getNumCols();
        final double[][] rows = new double[getNumRows()][];
        for (int j = 0; j < rows.length; j++) {
            getRow(j, rows[j] = new double[numCol]);
        }
        return rows;
    }

    /** {@inheritDoc} */
    public final boolean isAffine() {
        int dimension = getNumRows();
        if (dimension != getNumCols()) {
            return false;
        }
        dimension--;
        for (int i = 0; i <= dimension; i++) {
            if (getElement(dimension, i) != (i == dimension ? 1 : 0)) {
                return false;
            }
        }
        return true;
    }

    /** Changes the sign of each element in the matrix. */
    @Override
    public void negate() {
        // JNH: This seems the most aggressive approach.
        CommonOps_DDRM.changeSign(mat);
    }

    @Override
    public void negate(Matrix matrix) {
        DMatrixRMaj a = internal(matrix);
        CommonOps_DDRM.changeSign(a);
        this.mat = a;
    }

    /** Transposes the matrix. */
    @Override
    public void transpose() {
        CommonOps_DDRM.transpose(mat);
    }

    @Override
    public void transpose(Matrix matrix) {
        DMatrixRMaj a = internal(matrix);
        CommonOps_DDRM.transpose(a, mat);
    }

    @Override
    public void invert() {
        boolean success = CommonOps_DDRM.invert(mat);
        if (!success) {
            throw new SingularMatrixException("Could not invert, possible singular matrix?");
        }
    }

    @Override
    public void invert(Matrix matrix) throws SingularMatrixException {
        DMatrixRMaj a;
        if (matrix instanceof GeneralMatrix) {
            a = new DMatrixRMaj(((GeneralMatrix) matrix).mat);
        } else {
            a = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());
            for (int j = 0; j < mat.numRows; j++) {
                for (int i = 0; i < mat.numCols; i++) {
                    mat.setElement(j, i, matrix.getElement(j, i));
                }
            }
        }
        boolean success = CommonOps_DDRM.invert(a);
        if (!success) {
            throw new SingularMatrixException("Could not invert, possible singular matrix?");
        }
        this.mat = a;
    }

    /**
     * Gets the number of rows in the matrix.
     *
     * @return The number of rows in the matrix.
     */
    @Override
    public int getNumRows() {
        return mat.getNumRows();
    }

    /**
     * Gets the number of columns in the matrix.
     *
     * @return The number of columns in the matrix.
     */
    @Override
    public int getNumCols() {
        return mat.getNumCols();
    }

    /**
     * Returns the value at the row, column position in the matrix.
     *
     * @param row
     * @param column
     * @return Matrix value at the given row and column.
     */
    @Override
    public double getElement(int row, int column) {
        return mat.getElement(row, column);
    }

    public void setColumn(int column, double... values) {
        if (values.length != mat.getNumCols()) {
            throw new IllegalArgumentException(
                    "Call setRow received an array of length "
                            + values.length
                            + ".  "
                            + "The dimensions of the matrix is "
                            + mat.getNumRows()
                            + " by "
                            + mat.getNumCols()
                            + ".");
        }
        for (int i = 0; i < values.length; i++) {
            mat.setElement(i, column, values[i]);
        }
    }

    public void setRow(int row, double... values) {
        if (values.length != mat.getNumCols()) {
            throw new IllegalArgumentException(
                    "Call setRow received an array of length "
                            + values.length
                            + ".  "
                            + "The dimensions of the matrix is "
                            + mat.getNumRows()
                            + " by "
                            + mat.getNumCols()
                            + ".");
        }

        for (int i = 0; i < values.length; i++) {
            mat.setElement(row, i, values[i]);
        }
    }

    /**
     * Sets the value of the row, column position in the matrix.
     *
     * @param row
     * @param column
     * @param value
     */
    @Override
    public void setElement(int row, int column, double value) {
        mat.setElement(row, column, value);
    }

    /** Sets each value of the matrix to 0.0. */
    @Override
    public void zero() {
        mat.zero();
    }

    /** Sets the main diagonal of this matrix to be 1.0. */
    @Override
    public void setIdentity() {
        CommonOps_DDRM.setIdentity(mat);
    }

    /** Returns {@code true} if this matrix is an identity matrix. */
    public final boolean isIdentity() {
        final int numRow = getNumRows();
        final int numCol = getNumCols();
        if (numRow != numCol) {
            return false;
        }
        for (int j = 0; j < numRow; j++) {
            for (int i = 0; i < numCol; i++) {
                if (getElement(j, i) != (i == j ? 1.0 : 0.0)) {
                    return false;
                }
            }
        }
        assert isAffine() : this;
        assert isIdentity(0) : this;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.3.1
     */
    public final boolean isIdentity(double tolerance) {
        return isIdentity(this, tolerance);
    }

    /** Returns {@code true} if the matrix is an identity matrix using the provided tolerance. */
    static boolean isIdentity(final Matrix matrix, double tolerance) {
        tolerance = Math.abs(tolerance);
        final int numRow = matrix.getNumRows();
        final int numCol = matrix.getNumCols();
        if (numRow != numCol) {
            return false;
        }
        for (int j = 0; j < numRow; j++) {
            for (int i = 0; i < numCol; i++) {
                double e = matrix.getElement(j, i);
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
    public final void multiply(final Matrix matrix) {
        mul(matrix);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (mat == null) {
            return prime * result;
        }
        result = prime * result + mat.numRows;
        result = prime * result + mat.numCols;
        for (double d : mat.data) {
            long bits = Double.doubleToRawLongBits(d);
            result = prime * result + ((int) (bits ^ (bits >>> 32)));
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GeneralMatrix other = (GeneralMatrix) obj;
        return equals(other, 0);
    }

    public boolean equals(final Matrix matrix, final double tolerance) {
        return epsilonEquals(this, matrix, tolerance);
    }

    /** Compares the element values. */
    static boolean epsilonEquals(final Matrix m1, final Matrix m2, final double tolerance) {
        final int numRow = m1.getNumRows();
        if (numRow != m2.getNumRows()) {
            return false;
        }
        final int numCol = m1.getNumCols();
        if (numCol != m2.getNumCols()) {
            return false;
        }
        for (int j = 0; j < numRow; j++) {
            for (int i = 0; i < numCol; i++) {
                final double v1 = m1.getElement(j, i);
                final double v2 = m2.getElement(j, i);
                if (!(Math.abs(v1 - v2) <= tolerance)) {
                    if (Double.doubleToLongBits(v1) == Double.doubleToLongBits(v2)) {
                        // Special case for NaN and infinite values.
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns an affine transform for this matrix. This is a convenience method for
     * interoperability with Java2D.
     *
     * @return The affine transform for this matrix.
     * @throws IllegalStateException if this matrix is not 3&times;3, or if the last row is not
     *     {@code [0 0 1]}.
     */
    public final AffineTransform toAffineTransform2D() throws IllegalStateException {
        int check;
        if ((check = getNumRows()) != 3 || (check = getNumCols()) != 3) {
            throw new IllegalStateException("Not 2 dimensional");
        }
        if (isAffine()) {
            return new AffineTransform(
                    getElement(0, 0),
                    getElement(1, 0),
                    getElement(0, 1),
                    getElement(1, 1),
                    getElement(0, 2),
                    getElement(1, 2));
        }
        throw new IllegalStateException("Not an affine transform");
    }




    // Method Compatibility
    /** Returns a clone of this matrix. */
    @Override
    public GeneralMatrix clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            // should not happen
            throw new RuntimeException(e);
        }
        return new GeneralMatrix(this);
    }

    /** Extract a subMatrix to the provided target */
    public void copySubMatrix(
            int rowSource,
            int colSource,
            int numRows,
            int numCol,
            int rowDest,
            int colDest,
            GeneralMatrix target) {
        int rowLimit = rowSource + numRows;
        int colLimit = colSource + numCol;
        CommonOps_DDRM.extract(
                mat, rowSource, rowLimit, colSource, colLimit, target.mat, rowDest, colDest);
    }

    /**
     * Extract col to provided array.
     *
     * @param col
     * @param array
     */
    public void getColumn(int col, double[] array) {
        for (int j = 0; j < array.length; j++) {
            array[j] = mat.getElement(j, col);
        }
    }

    @Override
    public void mul(double scalar) {
        CommonOps_DDRM.scale(scalar, this.mat);
    }

    @Override
    public void mul(double scalar, Matrix matrix) {
        DMatrixRMaj a = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());
        CommonOps_DDRM.scale(scalar, internal(matrix), a);
        mat = a;
    }

    /**
     * Extract row to provided array
     *
     * @param row
     * @param array
     */
    public void getRow(int row, double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = mat.getElement(row, i);
        }
    }

    /**
     * In-place multiply with provided matrix.
     *
     * @param matrix
     */
    public final void mul(Matrix matrix) {
        DMatrixRMaj b = internal(matrix);
        DMatrixRMaj ret = new DMatrixRMaj(mat.numRows, b.numCols);
        CommonOps_DDRM.mult(mat, b, ret);
        mat = ret;
    }

    /**
     * In-place update from matrix1 * matrix2.
     *
     * @param matrix1
     * @param matrix2
     */
    public void mul(Matrix matrix1, Matrix matrix2) {
        DMatrixRMaj a = internal(matrix1);
        DMatrixRMaj b = internal(matrix2);
        if (a == mat || b == mat) {
            mat = new DMatrixRMaj(a.numRows, b.numCols);
        } else {
            mat.reshape(a.numRows, b.numCols, false);
        }
        CommonOps_DDRM.mult(a, b, mat);
    }

    @Override
    public void sub(double scalar) {
        CommonOps_DDRM.subtract(mat, scalar, mat);
    }

    @Override
    public void sub(double scalar, Matrix matrix) {
        DMatrixRMaj a = internal(matrix);
        mat.reshape(a.numRows, a.numCols, false);
        CommonOps_DDRM.subtract(scalar, a, mat);
    }

    public void sub(Matrix matrix) {
        CommonOps_DDRM.subtract(mat, internal(matrix), mat);
    }

    public void sub(Matrix matrix1, Matrix matrix2) {
        DMatrixRMaj a = internal(matrix1);
        DMatrixRMaj b = internal(matrix2);
        mat.reshape(a.numRows, a.numCols, false);
        CommonOps_DDRM.subtract(a, b, mat);
    }

    /**
     * Update in place to the provided matrix (row-order).
     *
     * @param matrix
     */
    public void set(double[] matrix) {
        mat.setData(matrix);
    }

    /**
     * Resize the matrix to the specified number of rows and columns (preserving remaining
     * elements).
     *
     * @param numRows The new number of rows in the matrix.
     * @param numCols The new number of columns in the matrix.
     */
    public void setSize(int numRows, int numCols) {
        if (numRows != mat.numCols || numCols != mat.numCols) {
            // grow or shrink
            DMatrixRMaj ret = new DMatrixRMaj(numRows, numCols);
            CommonOps_DDRM.extract(mat, 0, numRows, 0, numCols, ret, 0, 0);
            mat = ret;
        }
    }

    @Override
    public void add(double scalar) {
        CommonOps_DDRM.add(mat, scalar, mat);
    }

    @Override
    public void add(double scalar, Matrix matrix) {
        DMatrixRMaj a = internal(matrix);
        mat.reshape(a.numRows, a.numCols, false);
        CommonOps_DDRM.add(a, scalar, mat);
    }

    @Override
    public void add(Matrix matrix) {
        CommonOps_DDRM.add(mat, internal(matrix), mat);
    }

    @Override
    public void add(Matrix matrix1, Matrix matrix2) {
        DMatrixRMaj a = internal(matrix1);
        DMatrixRMaj b = internal(matrix2);
        mat.reshape(a.numRows, a.numCols, false);
        CommonOps_DDRM.add(a, b, mat);
    }

    @Override
    public double determinate() {
        double det = CommonOps_DDRM.det(mat);
        // if the decomposition silently failed then the matrix is most likely singular
        if (UtilEjml.isUncountable(det)) return 0;
        return det;
    }
}
