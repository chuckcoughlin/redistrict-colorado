/*
 * Copyright (c) 2020, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml.ops;

import static org.ejml.UtilEjml.reshapeOrDeclare;
import static org.ejml.UtilEjml.stringShapes;

import java.util.Arrays;

import org.ejml.EjmlParameters;
import org.ejml.MatrixDimensionException;
import org.ejml.UtilEjml;
import org.ejml.data.DMatrixD1;
import org.ejml.data.DMatrixRMaj;

/**
 * <p>
 * Common matrix operations are contained here.  Which specific underlying algorithm is used
 * is not specified just the out come of the operation.  Nor should calls to these functions
 * reply on the underlying implementation.  Which algorithm is used can depend on the matrix
 * being passed in.
 * </p>
 * <p>
 * For more exotic and specialized generic operations see {@link SpecializedOps_DDRM}.
 * </p>
 *
 * @author Peter Abeles
 * @see MatrixMatrixMult_DDRM
 * @see MatrixVectorMult_DDRM
 * @see SpecializedOps_DDRM
 * @see MatrixFeatures_DDRM
 */
@SuppressWarnings({"ForLoopReplaceableByForEach"})
public class CommonOps_DDRM {




    /**
     * <p>Performs an "in-place" transpose.</p>
     *
     * <p>
     * For square matrices the transpose is truly in-place and does not require
     * additional memory.  For non-square matrices, internally a temporary matrix is declared and
     * {@link #transpose(DMatrixRMaj, DMatrixRMaj)} is invoked.
     * </p>
     *
     * @param mat The matrix that is to be transposed. Modified.
     */
    public static void transpose( DMatrixRMaj mat ) {
        if (mat.numCols == mat.numRows) {
            TransposeAlgs_DDRM.square(mat);
        } else {
            DMatrixRMaj b = new DMatrixRMaj(mat.numCols, mat.numRows);
            transpose(mat, b);
            mat.set(b);
        }
    }

    /**
     * <p>
     * Transposes matrix 'a' and stores the results in 'b':<br>
     * <br>
     * b<sub>ij</sub> = a<sub>ji</sub><br>
     * where 'b' is the transpose of 'a'.
     * </p>
     *
     * @param A The original matrix.  Not modified.
     * @param A_tran Where the transpose is stored. If null a new matrix is created. Modified.
     * @return The transposed matrix.
     */
    public static DMatrixRMaj transpose( DMatrixRMaj A,  DMatrixRMaj A_tran ) {
        A_tran = reshapeOrDeclare(A_tran, A.numCols, A.numRows);

        if (A.numRows > EjmlParameters.TRANSPOSE_SWITCH &&
                A.numCols > EjmlParameters.TRANSPOSE_SWITCH)
            TransposeAlgs_DDRM.block(A, A_tran, EjmlParameters.BLOCK_WIDTH);
        else
            TransposeAlgs_DDRM.standard(A, A_tran);

        return A_tran;
    }

    /**
     * <p>
     * This computes the trace of the matrix:<br>
     * <br>
     * trace = &sum;<sub>i=1:n</sub> { a<sub>ii</sub> }<br>
     * where n = min(numRows,numCols)
     * </p>
     *
     * @param a A square matrix.  Not modified.
     */
    public static double trace( DMatrixD1 a ) {
        int N = Math.min(a.numRows, a.numCols);
        double sum = 0;
        int index = 0;
        for (int i = 0; i < N; i++) {
            sum += a.get(index);
            index += 1 + a.numCols;
        }

        return sum;
    }

 
 
 
    /**
     * Converts the columns in a matrix into a set of vectors.
     *
     * @param A Matrix.  Not modified.
     * @return An array of vectors.
     */
    public static DMatrixRMaj[] columnsToVector( DMatrixRMaj A,  DMatrixRMaj[] v ) {
        DMatrixRMaj[] ret;
        if (v == null || v.length < A.numCols) {
            ret = new DMatrixRMaj[A.numCols];
        } else {
            ret = v;
        }

        for (int i = 0; i < ret.length; i++) {
            if (ret[i] == null) {
                ret[i] = new DMatrixRMaj(A.numRows, 1);
            } else {
                ret[i].reshape(A.numRows, 1, false);
            }

            DMatrixRMaj u = ret[i];

            for (int j = 0; j < A.numRows; j++) {
                u.setElement(j, 0, A.getElement(j, i));
            }
        }

        return ret;
    }

    /**
     * Converts the rows in a matrix into a set of vectors.
     *
     * @param A Matrix.  Not modified.
     * @return An array of vectors.
     */
    public static DMatrixRMaj[] rowsToVector( DMatrixRMaj A,  DMatrixRMaj[] v ) {
        DMatrixRMaj[] ret;
        if (v == null || v.length < A.numRows) {
            ret = new DMatrixRMaj[A.numRows];
        } else {
            ret = v;
        }


        for (int i = 0; i < ret.length; i++) {
            if (ret[i] == null) {
                ret[i] = new DMatrixRMaj(A.numCols, 1);
            } else {
                ret[i].reshape(A.numCols, 1, false);
            }

            DMatrixRMaj u = ret[i];

            for (int j = 0; j < A.numCols; j++) {
                u.setElement(j, 0, A.getElement(i, j));
            }
        }

        return ret;
    }

    /**
     * Sets all the diagonal elements equal to one and everything else equal to zero.
     * If this is a square matrix then it will be an identity matrix.
     *
     * @param mat A square matrix.
     * @see #identity(int)
     */
    public static void setIdentity( DMatrixRMaj mat ) {
        int width = mat.numRows < mat.numCols ? mat.numRows : mat.numCols;

        Arrays.fill(mat.data, 0, mat.getNumElements(), 0);

        int index = 0;
        for (int i = 0; i < width; i++, index += mat.numCols + 1) {
            mat.data[index] = 1;
        }
    }

    /**
     * <p>
     * Creates an identity matrix of the specified size.<br>
     * <br>
     * a<sub>ij</sub> = 0   if i &ne; j<br>
     * a<sub>ij</sub> = 1   if i = j<br>
     * </p>
     *
     * @param width The width and height of the identity matrix.
     * @return A new instance of an identity matrix.
     */
    public static DMatrixRMaj identity( int width ) {
        DMatrixRMaj ret = new DMatrixRMaj(width, width);

        for (int i = 0; i < width; i++) {
            ret.setElement(i, i, 1.0);
        }

        return ret;
    }

    /**
     * Creates a rectangular matrix which is zero except along the diagonals.
     *
     * @param numRows Number of rows in the matrix.
     * @param numCols NUmber of columns in the matrix.
     * @return A matrix with diagonal elements equal to one.
     */
    public static DMatrixRMaj identity( int numRows, int numCols ) {
        DMatrixRMaj ret = new DMatrixRMaj(numRows, numCols);

        int small = numRows < numCols ? numRows : numCols;

        for (int i = 0; i < small; i++) {
            ret.setElement(i, i, 1.0);
        }

        return ret;
    }

    /**
     * <p>
     * Creates a new square matrix whose diagonal elements are specified by diagEl and all
     * the other elements are zero.<br>
     * <br>
     * a<sub>ij</sub> = 0         if i &le; j<br>
     * a<sub>ij</sub> = diag[i]   if i = j<br>
     * </p>
     *
     * @param diagEl Contains the values of the diagonal elements of the resulting matrix.
     * @return A new matrix.
     * @see #diagR
     */
    public static DMatrixRMaj diag( double... diagEl ) {
        return diag(null, diagEl.length, diagEl);
    }

    /**
     * @see #diag(double...)
     */
    public static DMatrixRMaj diag(  DMatrixRMaj ret, int width, double... diagEl ) {
        if (ret == null) {
            ret = new DMatrixRMaj(width, width);
        } else {
            if (ret.numRows != width || ret.numCols != width)
                throw new IllegalArgumentException("Unexpected matrix size");

            CommonOps_DDRM.fill(ret, 0);
        }

        for (int i = 0; i < width; i++) {
            ret.unsafe_set(i, i, diagEl[i]);
        }

        return ret;
    }

    /**
     * <p>
     * Creates a new rectangular matrix whose diagonal elements are specified by diagEl and all
     * the other elements are zero.<br>
     * <br>
     * a<sub>ij</sub> = 0         if i &le; j<br>
     * a<sub>ij</sub> = diag[i]   if i = j<br>
     * </p>
     *
     * @param numRows Number of rows in the matrix.
     * @param numCols Number of columns in the matrix.
     * @param diagEl Contains the values of the diagonal elements of the resulting matrix.
     * @return A new matrix.
     * @see #diag
     */
    public static DMatrixRMaj diagR( int numRows, int numCols, double... diagEl ) {
        DMatrixRMaj ret = new DMatrixRMaj(numRows, numCols);

        int o = Math.min(numRows, numCols);

        for (int i = 0; i < o; i++) {
            ret.setElement(i, i, diagEl[i]);
        }

        return ret;
    }

    /**
     * <p>
     * The Kronecker product of two matrices is defined as:<br>
     * C<sub>ij</sub> = a<sub>ij</sub>B<br>
     * where C<sub>ij</sub> is a sub matrix inside of C &isin; &real; <sup>m*k &times; n*l</sup>,
     * A &isin; &real; <sup>m &times; n</sup>, and B &isin; &real; <sup>k &times; l</sup>.
     * </p>
     *
     * @param A The left matrix in the operation. Not modified.
     * @param B The right matrix in the operation. Not modified.
     * @param C Where the results of the operation are stored. Nullable. Modified.
     */
    public static DMatrixRMaj kron( DMatrixRMaj A, DMatrixRMaj B,  DMatrixRMaj C ) {
        int numColsC = A.numCols*B.numCols;
        int numRowsC = A.numRows*B.numRows;

        C = reshapeOrDeclare(C,numRowsC, numColsC);

        // TODO see comment below
        // this will work well for small matrices
        // but an alternative version should be made for large matrices
        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                double a = A.getElement(i, j);

                for (int rowB = 0; rowB < B.numRows; rowB++) {
                    for (int colB = 0; colB < B.numCols; colB++) {
                        double val = a*B.getElement(rowB, colB);
                        C.unsafe_set(i*B.numRows + rowB, j*B.numCols + colB, val);
                    }
                }
            }
        }

        return C;
    }

    /**
     * <p>
     * Extracts a submatrix from 'src' and inserts it in a submatrix in 'dst'.
     * </p>
     * <p>
     * s<sub>i-y0 , j-x0</sub> = o<sub>ij</sub> for all y0 &le; i &lt; y1 and x0 &le; j &lt; x1 <br>
     * <br>
     * where 's<sub>ij</sub>' is an element in the submatrix and 'o<sub>ij</sub>' is an element in the
     * original matrix.
     * </p>
     *
     * @param src The original matrix which is to be copied.  Not modified.
     * @param srcX0 Start column.
     * @param srcX1 Stop column+1.
     * @param srcY0 Start row.
     * @param srcY1 Stop row+1.
     * @param dst Where the submatrix are stored.  Modified.
     * @param dstY0 Start row in dst.
     * @param dstX0 start column in dst.
     */
    public static void extract( DMatrixRMaj src,
                                int srcY0, int srcY1,
                                int srcX0, int srcX1,
                                DMatrixRMaj dst,
                                int dstY0, int dstX0 ) {
        if (srcY1 < srcY0 || srcY0 < 0 || srcY1 > src.getNumRows())
            throw new MatrixDimensionException("srcY1 < srcY0 || srcY0 < 0 || srcY1 > src.numRows. " + stringShapes(src, dst));
        if (srcX1 < srcX0 || srcX0 < 0 || srcX1 > src.getNumCols())
            throw new MatrixDimensionException("srcX1 < srcX0 || srcX0 < 0 || srcX1 > src.numCols. " + stringShapes(src, dst));

        int w = srcX1 - srcX0;
        int h = srcY1 - srcY0;

        if (dstY0 + h > dst.getNumRows())
            throw new MatrixDimensionException("dst is too small in rows. " + dst.getNumRows() + " < " + (dstY0 + h));
        if (dstX0 + w > dst.getNumCols())
            throw new MatrixDimensionException("dst is too small in columns. " + dst.getNumCols() + " < " + (dstX0 + w));

        // interestingly, the performance is only different for small matrices but identical for larger ones
        extract((DMatrixRMaj)src, srcY0, srcX0, (DMatrixRMaj)dst, dstY0, dstX0, h, w);
    }

  

    /**
     * <p>
     * Extracts a submatrix from 'src' and inserts it in a submatrix in 'dst'. Uses the shape of dst
     * to determine the size of the matrix extracted.
     * </p>
     *
     * @param src The original matrix which is to be copied.  Not modified.
     * @param srcY0 Start row in src.
     * @param srcX0 Start column in src.
     * @param dst Where the matrix is extracted into.
     */
    public static void extract( DMatrixRMaj src,
                                int srcY0, int srcX0,
                                DMatrixRMaj dst ) {
        extract(src, srcY0, srcY0 + dst.getNumRows(), srcX0, srcX0 + dst.getNumCols(), dst, 0, 0);
    }

    /**
     * <p>
     * Creates a new matrix which is the specified submatrix of 'src'
     * </p>
     * <p>
     * s<sub>i-y0 , j-x0</sub> = o<sub>ij</sub> for all y0 &le; i &lt; y1 and x0 &le; j &lt; x1 <br>
     * <br>
     * where 's<sub>ij</sub>' is an element in the submatrix and 'o<sub>ij</sub>' is an element in the
     * original matrix.
     * </p>
     *
     * @param src The original matrix which is to be copied.  Not modified.
     * @param srcX0 Start column.
     * @param srcX1 Stop column+1.
     * @param srcY0 Start row.
     * @param srcY1 Stop row+1.
     * @return Extracted submatrix.
     */
    public static DMatrixRMaj extract( DMatrixRMaj src,
                                       int srcY0, int srcY1,
                                       int srcX0, int srcX1 ) {
        if (srcY1 <= srcY0 || srcY0 < 0 || srcY1 > src.numRows)
            throw new MatrixDimensionException("srcY1 <= srcY0 || srcY0 < 0 || srcY1 > src.numRows");
        if (srcX1 <= srcX0 || srcX0 < 0 || srcX1 > src.numCols)
            throw new MatrixDimensionException("srcX1 <= srcX0 || srcX0 < 0 || srcX1 > src.numCols");

        int w = srcX1 - srcX0;
        int h = srcY1 - srcY0;

        DMatrixRMaj dst = new DMatrixRMaj(h, w);

        extract(src, srcY0, srcX0, dst, 0, 0, h, w);

        return dst;
    }
    
    public static void extract( DMatrixRMaj src,
    		int srcY0, int srcX0,
    		DMatrixRMaj dst,
    		int dstY0, int dstX0,
    		int numRows, int numCols ) {
    	for (int y = 0; y < numRows; y++) {
    		int indexSrc = src.getIndex(y + srcY0, srcX0);
    		int indexDst = dst.getIndex(y + dstY0, dstX0);
    		System.arraycopy(src.data, indexSrc, dst.data, indexDst, numCols);
    	}
    }

    /**
     * Extracts out a matrix from source given a sub matrix with arbitrary rows and columns specified in
     * two array lists
     *
     * @param src Source matrix. Not modified.
     * @param rows array of row indexes
     * @param rowsSize maximum element in row array
     * @param cols array of column indexes
     * @param colsSize maximum element in column array
     * @param dst output matrix.  Must be correct shape.
     */
    public static DMatrixRMaj extract( DMatrixRMaj src,
                                       int[] rows, int rowsSize,
                                       int[] cols, int colsSize,  DMatrixRMaj dst ) {
        dst = reshapeOrDeclare(dst, rowsSize, colsSize);

        int indexDst = 0;
        for (int i = 0; i < rowsSize; i++) {
            int indexSrcRow = src.numCols*rows[i];
            for (int j = 0; j < colsSize; j++) {
                dst.data[indexDst++] = src.data[indexSrcRow + cols[j]];
            }
        }

        return dst;
    }

 

    /**
     * Inserts into the specified elements of dst the source matrix.
     * <pre>
     * for i in len(rows):
     *   for j in len(cols):
     *      dst(rows[i],cols[j]) = src(i,j)
     * </pre>
     *
     * @param src Source matrix. Not modified.
     * @param dst output matrix.  Must be correct shape.
     * @param rows array of row indexes.
     * @param rowsSize maximum element in row array
     * @param cols array of column indexes
     * @param colsSize maximum element in column array
     */
    public static void insert( DMatrixRMaj src,
                               DMatrixRMaj dst,
                               int[] rows, int rowsSize,
                               int[] cols, int colsSize ) {
        UtilEjml.assertEq(rowsSize, src.numRows, "src's rows don't match rowsSize");
        UtilEjml.assertEq(colsSize, src.numCols, "src's columns don't match colsSize");

        int indexSrc = 0;
        for (int i = 0; i < rowsSize; i++) {
            int indexDstRow = dst.numCols*rows[i];
            for (int j = 0; j < colsSize; j++) {
                dst.data[indexDstRow + cols[j]] = src.data[indexSrc++];
            }
        }
    }
    /**
     * Inserts matrix 'src' into matrix 'dest' with the (0,0) of src at (row,col) in dest.
     * This is equivalent to calling extract(src,0,src.numRows,0,src.numCols,dest,destY0,destX0).
     *
     * @param src matrix that is being copied into dest. Not modified.
     * @param dest Where src is being copied into. Modified.
     * @param destY0 Start row for the copy into dest.
     * @param destX0 Start column for the copy into dest.
     */
    public static void insert( DMatrixRMaj src, DMatrixRMaj dest, int destY0, int destX0 ) {
        extract(src, 0, src.getNumRows(), 0, src.getNumCols(), dest, destY0, destX0);
    }
    /**
     * <p>
     * Performs a matrix inversion operation on the specified matrix and stores the results
     * in the same matrix.<br>
     * <br>
     * a = a<sup>-1</sup>
     * </p>
     *
     * <p>
     * If the algorithm could not invert the matrix then false is returned.  If it returns true
     * that just means the algorithm finished.  The results could still be bad
     * because the matrix is singular or nearly singular.
     * </p>
     *
     * @param mat The matrix that is to be inverted.  Results are stored here.  Modified.
     * @return true if it could invert the matrix false if it could not.
     */
    public static boolean invert( DMatrixRMaj mat ) {
        if (mat.numCols <= UnrolledInverseFromMinor_DDRM.MAX) {
            if (mat.numCols != mat.numRows) {
                throw new MatrixDimensionException("CommonOps_DDRM: Must be a square matrix.");
            }

            if (mat.numCols >= 2) {
                UnrolledInverseFromMinor_DDRM.inv(mat, mat);
            } 
            else {
                mat.set(0, 1.0/mat.get(0));
            }
        } 
        else {
        	// Things are greatly simplified if we don't consider larger matrices.
        	// We don't expect them
        	throw new MatrixDimensionException("CommonOps_DDRM: Matrix must have less than 5 columns");
        }
        return true;
    }
    /**
     * Removes columns from the matrix.
     *
     * @param A Matrix. Modified
     * @param col0 First column
     * @param col1 Last column, inclusive.
     */
    public static void removeColumns( DMatrixRMaj A, int col0, int col1 ) {
        UtilEjml.assertTrue(col0 < col1, "col1 must be >= col0");
        UtilEjml.assertTrue(col0 >= 0 && col1 <= A.numCols,"Columns which are to be removed must be in bounds");

        int step = col1 - col0 + 1;
        int offset = 0;
        for (int row = 0, idx = 0; row < A.numRows; row++) {
            for (int i = 0; i < col0; i++, idx++) {
                A.data[idx] = A.data[idx + offset];
            }
            offset += step;
            for (int i = col1 + 1; i < A.numCols; i++, idx++) {
                A.data[idx] = A.data[idx + offset];
            }
        }
        A.numCols -= step;
    }

  
    /**
     * Multiplies every element in row i by value[i].
     *
     * @param values array. Not modified.
     * @param A Matrix. Modified.
     */
    public static void multRows( double[] values, DMatrixRMaj A ) {
        if (values.length < A.numRows) {
            throw new IllegalArgumentException("Not enough elements in values.");
        }

        int index = 0;
        for (int row = 0; row < A.numRows; row++) {
            double v = values[row];
            for (int col = 0; col < A.numCols; col++, index++) {
                A.data[index] *= v;
            }
        }
    }

    /**
     * Divides every element in row i by value[i].
     *
     * @param values array. Not modified.
     * @param A Matrix. Modified.
     */
    public static void divideRows( double[] values, DMatrixRMaj A ) {
        if (values.length < A.numRows) {
            throw new IllegalArgumentException("Not enough elements in values.");
        }

        int index = 0;
        for (int row = 0; row < A.numRows; row++) {
            double v = values[row];
            for (int col = 0; col < A.numCols; col++, index++) {
                A.data[index] /= v;
            }
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a * b <br>
     * <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param output Where the results of the operation are stored. Modified.
     */
    public static DMatrixRMaj mult( DMatrixRMaj a,DMatrixRMaj b, DMatrixRMaj output ) {
        output = reshapeOrDeclare(output, a, a.numRows, b.numCols);
        UtilEjml.checkSameInstance(a, output);
        UtilEjml.checkSameInstance(b, output);

        if (b.numCols == 1) {
            MatrixVectorMult_DDRM.mult(a, b, output);
        } 
        else if (b.numCols >= EjmlParameters.MULT_COLUMN_SWITCH) {
            MatrixMatrixMult_DDRM.mult_reorder(a, b, output);
        } 
        else {
            MatrixMatrixMult_DDRM.mult_small(a, b, output);
        }

        return output;
    }
    
    /**
     * Multiplies every element in column i by value[i].
     *
     * @param A Matrix. Modified.
     * @param values array. Not modified.
     */
    public static void multCols( DMatrixRMaj A, double[] values ) {
        if (values.length < A.numCols) {
            throw new IllegalArgumentException("Not enough elements in values.");
        }

        int index = 0;
        for (int row = 0; row < A.numRows; row++) {
            for (int col = 0; col < A.numCols; col++, index++) {
                A.data[index] *= values[col];
            }
        }
    }

    /**
     * Divides every element in column i by value[i].
     *
     * @param A Matrix. Modified.
     * @param values array. Not modified.
     */
    public static void divideCols( DMatrixRMaj A, double[] values ) {
        if (values.length < A.numCols) {
            throw new IllegalArgumentException("Not enough elements in values.");
        }

        int index = 0;
        for (int row = 0; row < A.numRows; row++) {
            for (int col = 0; col < A.numCols; col++, index++) {
                A.data[index] /= values[col];
            }
        }
    }

    /**
     * Equivalent to multiplying a matrix B by the inverse of two diagonal matrices.
     * B = inv(A)*B*inv(C), where A=diag(a) and C=diag(c).
     *
     * @param diagA Array of length offsteA + B.numRows
     * @param offsetA First index in A
     * @param B Rectangular matrix
     * @param diagC Array of length indexC + B.numCols
     * @param offsetC First index in C
     */
    public static void divideRowsCols( double[] diagA, int offsetA,
                                       DMatrixRMaj B,
                                       double[] diagC, int offsetC ) {
        if (diagA.length - offsetA < B.numRows) {
            throw new IllegalArgumentException("Not enough elements in diagA.");
        }
        if (diagC.length - offsetC < B.numCols) {
            throw new IllegalArgumentException("Not enough elements in diagC.");
        }

        final int rows = B.numRows;
        final int cols = B.numCols;

        int index = 0;
        for (int row = 0; row < rows; row++) {
            double va = diagA[offsetA + row];
            for (int col = 0; col < cols; col++, index++) {
                B.data[index] /= va*diagC[offsetC + col];
            }
        }
    }

    /**
     * <p>
     * Computes the sum of each row in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = sum(i=1:n ; a<sub>ji</sub>)
     * </p>
     *
     * @param input INput matrix whose rows are summed.
     * @param output Optional storage for output. Reshaped into a column. Modified.
     * @return Vector containing the sum of each row in the input.
     */
    public static DMatrixRMaj sumRows( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, input.numRows, 1);

        for (int row = 0; row < input.numRows; row++) {
            double total = 0;

            int end = (row + 1)*input.numCols;
            for (int index = row*input.numCols; index < end; index++) {
                total += input.data[index];
            }

            output.set(row, total);
        }
        return output;
    }

    /**
     * <p>
     * Finds the element with the minimum value along each row in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = min(i=1:n ; a<sub>ji</sub>)
     * </p>
     *
     * @param input Input matrix
     * @param output Optional storage for output.  Reshaped into a column. Modified.
     * @return Vector containing the sum of each row in the input.
     */
    public static DMatrixRMaj minRows( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, input.numRows, 1);

        for (int row = 0; row < input.numRows; row++) {
            double min = Double.MAX_VALUE;

            int end = (row + 1)*input.numCols;
            for (int index = row*input.numCols; index < end; index++) {
                double v = input.data[index];
                if (v < min)
                    min = v;
            }

            output.set(row, min);
        }
        return output;
    }

    /**
     * <p>
     * Finds the element with the maximum value along each row in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = max(i=1:n ; a<sub>ji</sub>)
     * </p>
     *
     * @param input Input matrix
     * @param output Optional storage for output.  Reshaped into a column. Modified.
     * @return Vector containing the sum of each row in the input.
     */
    public static DMatrixRMaj maxRows( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, input.numRows, 1);

        for (int row = 0; row < input.numRows; row++) {
            double max = -Double.MAX_VALUE;

            int end = (row + 1)*input.numCols;
            for (int index = row*input.numCols; index < end; index++) {
                double v = input.data[index];
                if (v > max)
                    max = v;
            }

            output.set(row, max);
        }
        return output;
    }

    /**
     * <p>
     * Computes the sum of each column in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = sum(i=1:m ; a<sub>ij</sub>)
     * </p>
     *
     * @param input Input matrix
     * @param output Optional storage for output. Reshaped into a row vector. Modified.
     * @return Vector containing the sum of each column
     */
    public static DMatrixRMaj sumCols( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, 1, input.numCols);

        for (int cols = 0; cols < input.numCols; cols++) {
            double total = 0;

            int index = cols;
            int end = index + input.numCols*input.numRows;
            for (; index < end; index += input.numCols) {
                total += input.data[index];
            }

            output.set(cols, total);
        }
        return output;
    }

    /**
     * <p>
     * Finds the element with the minimum value along column in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = min(i=1:m ; a<sub>ij</sub>)
     * </p>
     *
     * @param input Input matrix
     * @param output Optional storage for output. Reshaped into a row vector. Modified.
     * @return Vector containing the minimum of each column
     */
    public static DMatrixRMaj minCols( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, 1, input.numCols);

        for (int cols = 0; cols < input.numCols; cols++) {
            double minimum = Double.MAX_VALUE;

            int index = cols;
            int end = index + input.numCols*input.numRows;
            for (; index < end; index += input.numCols) {
                double v = input.data[index];
                if (v < minimum)
                    minimum = v;
            }

            output.set(cols, minimum);
        }
        return output;
    }

    /**
     * <p>
     * Finds the element with the minimum value along column in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = min(i=1:m ; a<sub>ij</sub>)
     * </p>
     *
     * @param input Input matrix
     * @param output Optional storage for output. Reshaped into a row vector. Modified.
     * @return Vector containing the maximum of each column
     */
    public static DMatrixRMaj maxCols( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, 1, input.numCols);

        for (int cols = 0; cols < input.numCols; cols++) {
            double maximum = -Double.MAX_VALUE;

            int index = cols;
            int end = index + input.numCols*input.numRows;
            for (; index < end; index += input.numCols) {
                double v = input.data[index];
                if (v > maximum)
                    maximum = v;
            }

            output.set(cols, maximum);
        }
        return output;
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * a = a + b <br>
     * a<sub>ij</sub> = a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * @param a (input/output) A Matrix. Modified.
     * @param b (input) A Matrix. Not modified.
     */
    public static void addEquals( DMatrixD1 a, DMatrixD1 b ) {
        UtilEjml.checkSameShape(a, b, true);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            a.plus(i, b.get(i));
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * a = a + &beta; * b  <br>
     * a<sub>ij</sub> = a<sub>ij</sub> + &beta; * b<sub>ij</sub>
     * </p>
     *
     * @param beta The number that matrix 'b' is multiplied by.
     * @param a (input/output) A Matrix. Modified.
     * @param b (input) A Matrix. Not modified.
     */
    public static void addEquals( DMatrixD1 a, double beta, DMatrixD1 b ) {
        UtilEjml.checkSameShape(a, b, true);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            a.plus(i, beta*b.get(i));
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a + b <br>
     * c<sub>ij</sub> = a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param a A Matrix. Not modified.
     * @param b A Matrix. Not modified.
     * @param output (output) A Matrix where the results are stored. Can be null. Modified.
     * @return The results.
     */
    public static DMatrixRMaj add( final DMatrixRMaj a, final DMatrixRMaj b,  DMatrixRMaj output ) {
        UtilEjml.checkSameShape(a, b, true);
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.set(i, a.get(i) + b.get(i));
        }

        return output;
    }

  
    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a + &beta; * b <br>
     * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + &beta; * b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param alpha A scaling factor for matrix a.
     * @param a A Matrix. Not modified.
     * @param beta A scaling factor for matrix b.
     * @param b A Matrix. Not modified.
     * @param output (output) A Matrix where the results are stored. Can be null. Modified.
     * @return The results.
     */
    public static DMatrixRMaj add( double alpha, DMatrixRMaj a, double beta, DMatrixRMaj b,  DMatrixRMaj output ) {
        UtilEjml.checkSameShape(a, b, true);
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.set(i, alpha*a.get(i) + beta*b.get(i));
        }

        return output;
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a + b <br>
     * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param alpha A scaling factor for matrix a.
     * @param a A Matrix. Not modified.
     * @param b A Matrix. Not modified.
     * @param output (output) A Matrix where the results are stored. Can be null. Modified.
     * @return The results.
     */
    public static DMatrixRMaj add( double alpha, DMatrixRMaj a, DMatrixRMaj b, DMatrixRMaj output ) {
        UtilEjml.checkSameShape(a, b, true);
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.set(i, alpha*a.get(i) + b.get(i));
        }

        return output;
    }

    /**
     * <p>Performs an in-place scalar addition:<br>
     * <br>
     * a = a + val<br>
     * a<sub>ij</sub> = a<sub>ij</sub> + val<br>
     * </p>
     *
     * @param a A matrix.  Modified.
     * @param val The value that's added to each element.
     */
    public static void add( DMatrixD1 a, double val ) {
        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            a.plus(i, val);
        }
    }

    /**
     * <p>Performs scalar addition:<br>
     * <br>
     * c = a + val<br>
     * c<sub>ij</sub> = a<sub>ij</sub> + val<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @param val The value that's added to each element.
     * @param output (output) Storage for results. Can be null. Modified.
     * @return The resulting matrix
     */
    public static DMatrixRMaj add( DMatrixRMaj a, double val, DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.data[i] = a.data[i] + val;
        }

        return output;
    }

    /**
     * <p>Performs matrix scalar subtraction:<br>
     * <br>
     * c = a - val<br>
     * c<sub>ij</sub> = a<sub>ij</sub> - val<br>
     * </p>
     *
     * @param a (input) A matrix. Not modified.
     * @param val (input) The value that's subtracted to each element.
     * @param output (output) Storage for results. Can be null. Modified.
     * @return The resulting matrix
     */
    public static DMatrixRMaj subtract( DMatrixRMaj a, double val,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.data[i] = a.data[i] - val;
        }

        return output;
    }

    /**
     * <p>Performs matrix scalar subtraction:<br>
     * <br>
     * c = val - a<br>
     * c<sub>ij</sub> = val - a<sub>ij</sub><br>
     * </p>
     *
     * @param val (input) The value that's subtracted to each element.
     * @param a (input) A matrix. Not modified.
     * @param output (output) Storage for results. Can be null. Modified.
     * @return The resulting matrix
     */
    public static DMatrixRMaj subtract( double val, DMatrixRMaj a,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.data[i] = val - a.data[i];
        }

        return output;
    }

    /**
     * <p>Performs the following subtraction operation:<br>
     * <br>
     * a = a - b  <br>
     * a<sub>ij</sub> = a<sub>ij</sub> - b<sub>ij</sub>
     * </p>
     *
     * @param a (input) A Matrix. Modified.
     * @param b (input) A Matrix. Not modified.
     */
    public static void subtractEquals( DMatrixD1 a, DMatrixD1 b ) {
        UtilEjml.checkSameShape(a, b, true);
        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            a.data[i] -= b.data[i];
        }
    }

    /**
     * <p>Performs the following subtraction operation:<br>
     * <br>
     * c = a - b  <br>
     * c<sub>ij</sub> = a<sub>ij</sub> - b<sub>ij</sub>
     * </p>
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param a (input) A Matrix. Not modified.
     * @param b (input) A Matrix. Not modified.
     * @param output (output) A Matrix. Can be null. Modified.
     * @return The resulting matrix
     */
    public static DMatrixRMaj subtract( DMatrixRMaj a, DMatrixRMaj b,  DMatrixRMaj output ) {
        UtilEjml.checkSameShape(a, b, true);
        output = UtilEjml.reshapeOrDeclare(output, a);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            output.data[i] = a.data[i] - b.data[i];
        }

        return output;
    }

    /**
     * <p>
     * Performs an in-place element by element scalar multiplication.<br>
     * <br>
     * a<sub>ij</sub> = &alpha;*a<sub>ij</sub>
     * </p>
     *
     * @param a The matrix that is to be scaled.  Modified.
     * @param alpha the amount each element is multiplied by.
     */
    public static void scale( double alpha, DMatrixD1 a ) {
        // on very small matrices (2 by 2) the call to getNumElements() can slow it down
        // slightly compared to other libraries since it involves an extra multiplication.
        final int size = a.getNumElements();

        for (int i = 0; i < size; i++) {
            a.data[i] *= alpha;
        }
    }

    /**
     * <p>
     * Performs an element by element scalar multiplication.<br>
     * <br>
     * b<sub>ij</sub> = &alpha;*a<sub>ij</sub>
     * </p>
     *
     * @param alpha the amount each element is multiplied by.
     * @param a The matrix that is to be scaled.  Not modified.
     * @param b Where the scaled matrix is stored. Modified.
     */
    public static void scale( double alpha, DMatrixD1 a, DMatrixD1 b ) {
        b.reshape(a.numRows, a.numCols);

        final int size = a.getNumElements();

        for (int i = 0; i < size; i++) {
            b.data[i] = a.data[i]*alpha;
        }
    }

    /**
     * In-place scaling of a row in A
     *
     * @param alpha scale factor
     * @param A matrix
     * @param row which row in A
     */
    public static void scaleRow( double alpha, DMatrixRMaj A, int row ) {
        int idx = row*A.numCols;
        for (int col = 0; col < A.numCols; col++) {
            A.data[idx++] *= alpha;
        }
    }

    /**
     * In-place scaling of a column in A
     *
     * @param alpha scale factor
     * @param A matrix
     * @param col which row in A
     */
    public static void scaleCol( double alpha, DMatrixRMaj A, int col ) {
        int idx = col;
        for (int row = 0; row < A.numRows; row++, idx += A.numCols) {
            A.data[idx] *= alpha;
        }
    }

    /**
     * <p>
     * Performs an in-place element by element scalar division with the scalar on top.<br>
     * <br>
     * a<sub>ij</sub> = &alpha;/a<sub>ij</sub>
     * </p>
     *
     * @param a (input/output) The matrix whose elements are divide the scalar.  Modified.
     * @param alpha top value in division
     */
    public static void divide( double alpha, DMatrixD1 a ) {
        final int size = a.getNumElements();

        for (int i = 0; i < size; i++) {
            a.data[i] = alpha/a.data[i];
        }
    }

    /**
     * <p>
     * Performs an in-place element by element scalar division with the scalar on bottom.<br>
     * <br>
     * a<sub>ij</sub> = a<sub>ij</sub>/&alpha;
     * </p>
     *
     * @param a (input/output) The matrix whose elements are to be divided.  Modified.
     * @param alpha the amount each element is divided by.
     */
    public static void divide( DMatrixD1 a, double alpha ) {
        final int size = a.getNumElements();

        for (int i = 0; i < size; i++) {
            a.data[i] /= alpha;
        }
    }

   

    /**
     * <p>
     * Changes the sign of every element in the matrix.<br>
     * <br>
     * a<sub>ij</sub> = -a<sub>ij</sub>
     * </p>
     *
     * @param a A matrix. Modified.
     */
    public static void changeSign( DMatrixD1 a ) {
        final int size = a.getNumElements();

        for (int i = 0; i < size; i++) {
            a.data[i] = -a.data[i];
        }
    }

    /**
     * <p>
     * Changes the sign of every element in the matrix.<br>
     * <br>
     * output<sub>ij</sub> = -input<sub>ij</sub>
     * </p>
     *
     * @param input A matrix. Modified.
     */
    public static DMatrixRMaj changeSign( DMatrixRMaj input,  DMatrixRMaj output ) {
        output = UtilEjml.reshapeOrDeclare(output, input);

        final int size = input.getNumElements();

        for (int i = 0; i < size; i++) {
            output.data[i] = -input.data[i];
        }

        return output;
    }

    /**
     * <p>
     * Sets every element in the matrix to the specified value.<br>
     * <br>
     * a<sub>ij</sub> = value
     * <p>
     *
     * @param a A matrix whose elements are about to be set. Modified.
     * @param value The value each element will have.
     */
    public static void fill( DMatrixD1 a, double value ) {
        Arrays.fill(a.data, 0, a.getNumElements(), value);
    }
  

    /**
     * output = [a , b]
     */
    public static DMatrixRMaj concatColumns( DMatrixRMaj a, DMatrixRMaj b,  DMatrixRMaj output ) {
        int rows = Math.max(a.numRows, b.numRows);
        int cols = a.numCols + b.numCols;

        output = reshapeOrDeclare(output,rows,cols);
        output.zero();

        insert(a, output, 0, 0);
        insert(b, output, 0, a.numCols);

        return output;
    }

    /**
     * <p>Concatinates all the matrices together along their columns.  If the rows do not match the upper elements
     * are set to zero.</p>
     *
     * A = [ m[0] , ... , m[n-1] ]
     *
     * @param m Set of matrices
     * @return Resulting matrix
     */
    public static DMatrixRMaj concatColumnsMulti( DMatrixRMaj... m ) {
        int rows = 0;
        int cols = 0;

        for (int i = 0; i < m.length; i++) {
            rows = Math.max(rows, m[i].numRows);
            cols += m[i].numCols;
        }
        DMatrixRMaj R = new DMatrixRMaj(rows, cols);

        int col = 0;
        for (int i = 0; i < m.length; i++) {
            insert(m[i], R, 0, col);
            col += m[i].numCols;
        }

        return R;
    }

    /**
     * output = [a ; b]
     */
    public static void concatRows( DMatrixRMaj a, DMatrixRMaj b, DMatrixRMaj output ) {
        int rows = a.numRows + b.numRows;
        int cols = Math.max(a.numCols, b.numCols);

        output.reshape(rows, cols);
        output.zero();

        insert(a, output, 0, 0);
        insert(b, output, a.numRows, 0);
    }

    /**
     * <p>Concatinates all the matrices together along their columns.  If the rows do not match the upper elements
     * are set to zero.</p>
     *
     * A = [ m[0] ; ... ; m[n-1] ]
     *
     * @param m Set of matrices
     * @return Resulting matrix
     */
    public static DMatrixRMaj concatRowsMulti( DMatrixRMaj... m ) {
        int rows = 0;
        int cols = 0;

        for (int i = 0; i < m.length; i++) {
            rows += m[i].numRows;
            cols = Math.max(cols, m[i].numCols);
        }
        DMatrixRMaj R = new DMatrixRMaj(rows, cols);

        int row = 0;
        for (int i = 0; i < m.length; i++) {
            insert(m[i], R, row, 0);
            row += m[i].numRows;
        }

        return R;
    }
    /**
     * Returns the determinant of the matrix.  If the inverse of the matrix is also
     * needed, then using {@link org.ejml.interfaces.decomposition.LUDecomposition_F64} directly (or any
     * similar algorithm) can be more efficient.
     *
     * @param mat The matrix whose determinant is to be computed.  Not modified.
     * @return The determinant.
     */
    public static double det( DMatrixRMaj mat ) {
        int numCol = mat.getNumCols();
        int numRow = mat.getNumRows();

        if (numCol != numRow) {
            throw new MatrixDimensionException("Must be a square matrix.");
        } 
        else if (numCol <= UnrolledDeterminantFromMinor_DDRM.MAX) {
            // slight performance boost overall by doing it this way
            // when it was the case statement the VM did some strange optimization
            // and made case 2 about 1/2 the speed
            if (numCol >= 2) {
                return UnrolledDeterminantFromMinor_DDRM.det(mat);
            } 
            else {
                return mat.get(0);
            }
        } 
        else {
        	throw new MatrixDimensionException("CommonOps_DDRM.det: Do not handle matrices with over 5 cols");
        }
    }
    /**
     * Applies the row permutation specified by the vector to the input matrix and save the results
     * in the output matrix.  output[perm[j],:] = input[j,:]
     *
     * @param pinv (Input) Inverse permutation vector.  Specifies new order of the rows.
     * @param input (Input) Matrix which is to be permuted
     * @param output (Output) Matrix which has the permutation stored in it.  Is reshaped.
     */
    public static DMatrixRMaj permuteRowInv( int[] pinv, DMatrixRMaj input, DMatrixRMaj output ) {
        if (input.numRows > pinv.length)
            throw new MatrixDimensionException("permutation vector must have at least as many elements as input has rows");

        output = UtilEjml.reshapeOrDeclare(output, input.numRows, input.numCols);

        int m = input.numCols;
        for (int row = 0; row < input.numRows; row++) {
            System.arraycopy(input.data, row*m, output.data, pinv[row]*m, m);
        }
        return output;
    }

    /**
     * <p>Performs absolute value of a matrix:<br>
     * <br>
     * c = abs(a)<br>
     * c<sub>ij</sub> = abs(a<sub>ij</sub>)
     * </p>
     *
     * @param a A matrix. Not modified.
     * @param c A matrix. Modified.
     */
    public static void abs( DMatrixD1 a, DMatrixD1 c ) {
        c.reshape(a.numRows, a.numCols);

        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            c.data[i] = Math.abs(a.data[i]);
        }
    }

    /**
     * <p>Performs absolute value of a matrix:<br>
     * <br>
     * a = abs(a)<br>
     * a<sub>ij</sub> = abs(a<sub>ij</sub>)
     * </p>
     *
     * @param a A matrix. Modified.
     */
    public static void abs( DMatrixD1 a ) {
        final int length = a.getNumElements();

        for (int i = 0; i < length; i++) {
            a.data[i] = Math.abs(a.data[i]);
        }
    }

    /**
     * Given a symmetric matrix which is represented by a lower triangular matrix convert it back into
     * a full symmetric matrix.
     *
     * @param A (Input) Lower triangular matrix (Output) symmetric matrix
     */
    public static void symmLowerToFull( DMatrixRMaj A ) {
        if (A.numRows != A.numCols)
            throw new MatrixDimensionException("Must be a square matrix");

        final int cols = A.numCols;

        for (int row = 0; row < A.numRows; row++) {
            for (int col = row + 1; col < cols; col++) {
                A.data[row*cols + col] = A.data[col*cols + row];
            }
        }
    }

    /**
     * Given a symmetric matrix which is represented by a lower triangular matrix convert it back into
     * a full symmetric matrix.
     *
     * @param A (Input) Lower triangular matrix (Output) symmetric matrix
     */
    public static void symmUpperToFull( DMatrixRMaj A ) {
        if (A.numRows != A.numCols)
            throw new MatrixDimensionException("Must be a square matrix");

        final int cols = A.numCols;

        for (int row = 0; row < A.numRows; row++) {
            for (int col = 0; col <= row; col++) {
                A.data[row*cols + col] = A.data[col*cols + row];
            }
        }
    }
}
