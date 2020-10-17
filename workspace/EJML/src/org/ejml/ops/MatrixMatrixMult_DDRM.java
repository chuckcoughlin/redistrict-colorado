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

import org.ejml.UtilEjml;
import org.ejml.data.DMatrixRMaj;

/**
 * <p>
 * This class contains various types of matrix matrix multiplication operations for {@link DMatrix1Row}.
 * </p>
 * <p>
 * Two algorithms that are equivalent can often have very different runtime performance.
 * This is because of how modern computers uses fast memory caches to speed up reading/writing to data.
 * Depending on the order in which variables are processed different algorithms can run much faster than others,
 * even if the number of operations is the same.
 * </p>
 *
 * <p>
 * Algorithms that are labeled as 'reorder' are designed to avoid caching jumping issues, some times at the cost
 * of increasing the number of operations. This is important for large matrices. The straight forward
 * implementation seems to be faster for small matrices.
 * </p>
 *
 * <p>
 * Algorithms that are labeled as 'aux' use an auxiliary array of length n. This array is used to create
 * a copy of an out of sequence column vector that is referenced several times. This reduces the number
 * of cache misses. If the 'aux' parameter passed in is null then the array is declared internally.
 * </p>
 *
 * <p>
 * Typically the straight forward implementation runs about 30% faster on smaller matrices and
 * about 5 times slower on larger matrices. This is all computer architecture and matrix shape/size specific.
 * </p>
 *
 * <p>DO NOT MODIFY. Automatically generated code created by GenerateMatrixMatrixMult_DDRM</p>
 *
 * @author Peter Abeles
 */
public class MatrixMatrixMult_DDRM {
    /**
     * @see CommonOps_DDRM#mult(org.ejml.data.DMatrix1Row, org.ejml.data.DMatrix1Row, org.ejml.data.DMatrix1Row)
     */
    public static void mult_reorder( DMatrixRMaj A, DMatrixRMaj B, DMatrixRMaj C ) {
        UtilEjml.assertTrue(A != C && B != C, "Neither 'A' or 'B' can be the same matrix as 'C'");
        UtilEjml.assertShape(A.numCols, B.numRows, "The 'A' and 'B' matrices do not have compatible dimensions");
        C.reshape(A.numRows, B.numCols);

        if (A.numCols == 0 || A.numRows == 0) {
            CommonOps_DDRM.fill(C, 0);
            return;
        }
        final int endOfKLoop = B.numRows*B.numCols;

        //CONCURRENT_BELOW EjmlConcurrency.loopFor(0, A.numRows, i -> {
        for (int i = 0; i < A.numRows; i++) {
            int indexCbase = i*C.numCols;
            int indexA = i*A.numCols;

            // need to assign C.data to a value initially
            int indexB = 0;
            int indexC = indexCbase;
            int end = indexB + B.numCols;

            double valA = A.data[indexA++];

            while (indexB < end) {
                C.set(indexC++, valA*B.data[indexB++]);
            }

            // now add to it
            while (indexB != endOfKLoop) { // k loop
                indexC = indexCbase;
                end = indexB + B.numCols;

                valA = A.data[indexA++];

                while (indexB < end) { // j loop
                    C.data[indexC++] += valA*B.data[indexB++];
                }
            }
        }
    }

    /**
     * @see CommonOps_DDRM#mult(org.ejml.data.DMatrix1Row, org.ejml.data.DMatrix1Row, org.ejml.data.DMatrix1Row)
     */
    public static void mult_small( DMatrixRMaj A, DMatrixRMaj B, DMatrixRMaj C ) {
        UtilEjml.assertTrue(A != C && B != C, "Neither 'A' or 'B' can be the same matrix as 'C'");
        UtilEjml.assertShape(A.numCols, B.numRows, "The 'A' and 'B' matrices do not have compatible dimensions");
        C.reshape(A.numRows, B.numCols);

        //CONCURRENT_BELOW EjmlConcurrency.loopFor(0, A.numRows, i -> {
        for (int i = 0; i < A.numRows; i++) {
            int cIndex = i*B.numCols;
            int aIndexStart = i*A.numCols;
            for (int j = 0; j < B.numCols; j++) {
                double total = 0;

                int indexA = aIndexStart;
                int indexB = j;
                int end = indexA + B.numRows;
                while (indexA < end) {
                    total += A.data[indexA++]*B.data[indexB];
                    indexB += B.numCols;
                }

                C.set(cIndex++, total);
            }
        }
    }
}