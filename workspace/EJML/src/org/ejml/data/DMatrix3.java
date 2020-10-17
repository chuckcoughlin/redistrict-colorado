/*
 * Copyright (c) 2009-2019, Peter Abeles. All Rights Reserved.
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

/**
 * Fixed sized vector with 3 elements.  Can represent a 3 x 1 or 1 x 3 matrix, context dependent.
 * <p>DO NOT MODIFY.  Automatically generated code created by GenerateMatrixFixedN</p>
 *
 * @author Peter Abeles
 */
public class DMatrix3 implements EJMLMatrix {
	private static final long serialVersionUID = -640839944086691604L;
	public double a1,a2,a3;

    public DMatrix3() {
    }

    public DMatrix3(double a1, double a2, double a3)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }

    public DMatrix3(DMatrix3 o) {
        this.a1 = o.a1;
        this.a2 = o.a2;
        this.a3 = o.a3;
    }

    @Override
    public void zero() {
        a1 = 0.0;
        a2 = 0.0;
        a3 = 0.0;
    }

    public void set(double a1, double a2, double a3)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }

    public void set( int offset , double array[] ) {
        this.a1 = array[offset+0];
        this.a2 = array[offset+1];
        this.a3 = array[offset+2];
    }

    @Override
    public double getElement(int row, int col) {
        return unsafe_get(row,col);
    }

    public double unsafe_get(int row, int col) {
        if( row != 0 && col != 0 )
            throw new IllegalArgumentException("Row or column must be zero since this is a vector");

        int w = Math.max(row,col);

        if( w == 0 ) {
            return a1;
        } else if( w == 1 ) {
            return a2;
        } else if( w == 2 ) {
            return a3;
        } else {
            throw new IllegalArgumentException("Out of range.  "+w);
        }
    }

    @Override
    public void setElement(int row, int col, double val) {
        unsafe_set(row,col,val);
    }

    public void unsafe_set(int row, int col, double val) {
        if( row != 0 && col != 0 )
            throw new IllegalArgumentException("Row or column must be zero since this is a vector");

        int w = Math.max(row,col);

        if( w == 0 ) {
            a1 = val;
        } else if( w == 1 ) {
            a2 = val;
        } else if( w == 2 ) {
            a3 = val;
        } else {
            throw new IllegalArgumentException("Out of range.  "+w);
        }
    }

    @Override
    public void set(EJMLMatrix original) {
        DMatrix3 m = (DMatrix3)original;

        if( m.getNumCols() == 1 && m.getNumRows() == 3 ) {
            a1 = m.getElement(0,0);
            a2 = m.getElement(1,0);
            a3 = m.getElement(2,0);
        } else if( m.getNumRows() == 1 && m.getNumCols() == 3 ){
            a1 = m.getElement(0,0);
            a2 = m.getElement(0,1);
            a3 = m.getElement(0,2);
        } else {
            throw new IllegalArgumentException("Incompatible shape");
        }
    }

    @Override
    public int getNumRows() {
        return 3;
    }

    @Override
    public int getNumCols() {
        return 1;
    }

    public int getNumElements() {
        return 3;
    }

    @Override
    public DMatrix3 clone() {
        return new DMatrix3(this);
    }

    public <T extends EJMLMatrix> T createLike() {
        return (T)new DMatrix3();
    }

    public MatrixType getType() {
        return MatrixType.UNSPECIFIED;
    }}

