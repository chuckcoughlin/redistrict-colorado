/*
 * Copyright (c) 2009-2020, Peter Abeles. All Rights Reserved.
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

package org.ejml.data;

import java.io.Serializable;

/**
 * Base interface for all rectangular 64F real matrices
 *
 * @author Peter Abeles
 */
public interface EJMLMatrix extends Serializable {

    /**
     * Returns the number of rows in this matrix.
     *
     * @return Number of rows.
     */
    public int getNumRows();

    /**
     * Returns the number of columns in this matrix.
     *
     * @return Number of columns.
     */
    public int getNumCols();

    /**
     * Sets all values inside the matrix to zero
     */
    public void zero();

    /**
     * Creates an exact copy of the matrix
     */
     public EJMLMatrix clone();

    /**
     * Creates a new matrix with the same shape as this matrix
     */
    //<T extends EJMLMatrix> T createLike();

    /**
     * Creates a new matrix of the same type with the specified shape
     */
    //<T extends Matrix> T create( int numRows , int numCols );

    /**
     * Sets this matrix to be identical to the 'original' matrix passed in.
     */
    public void set( EJMLMatrix original );

    /**
     * Returns the type of matrix
     */
    //MatrixType getType();
    
    /**
     * Returns the value of value of the specified matrix element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    public double getElement(int row, int col);

    /**
     * Same as {@link #get} but does not perform bounds check on input parameters.  This results in about a 25%
     * speed increase but potentially sacrifices stability and makes it more difficult to track down simple errors.
     * It is not recommended that this function be used, except in highly optimized code where the bounds are
     * implicitly being checked.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    //double unsafe_get(int row, int col);

    /**
     * Sets the value of the specified matrix element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val The element's new value.
     */
    public void setElement(int row, int col, double val);

    /**
     * Same as {@link #set} but does not perform bounds check on input parameters.  This results in about a 25%
     * speed increase but potentially sacrifices stability and makes it more difficult to track down simple errors.
     * It is not recommended that this function be used, except in highly optimized code where the bounds are
     * implicitly being checked.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val The element's new value.
     */
    //void unsafe_set(int row, int col, double val);

    /**
     * Returns the number of elements in this matrix, which is the number of rows
     * times the number of columns.
     *
     * @return Number of elements in this matrix.
     */
    //int getNumElements();
}
