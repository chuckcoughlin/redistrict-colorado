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

/**
 * Specifies that type of data structure a matrix is encoded with.
 *
 * @author Peter Abeles
 */
public enum MatrixType {

    DDRM(true,true,64,DMatrixRMaj.class),
    ZSCC(false,false,64,Object.class),
    CSCC(false,false,32,Object.class),
    UNSPECIFIED(false,false,0,Object.class);

    final boolean fixed;
    final boolean dense;
    final boolean real;
    final int bits;
    final Class<?> classType;

    MatrixType(boolean real, boolean dense, int bits, Class<?> type) {
        this(false,real,dense,bits,type);
    }

    MatrixType(boolean fixed, boolean real, boolean dense, int bits, Class<?> type ) {
        this.real = real;
        this.fixed = fixed;
        this.dense = dense;
        this.bits = bits;
        this.classType = type;
    }

    public static MatrixType lookup( Class<?> type ) {
        if( type == DMatrixRMaj.class )
            return MatrixType.DDRM;
        else
            throw new IllegalArgumentException("Unknown class");
    }

    /**
     * Looks up the default matrix type for the specified features
     */
    public static MatrixType lookup( boolean dense, boolean real, int bits ) {
        if( dense ) {
            return DDRM;
        } 
        else {
           throw new IllegalArgumentException("MatrixType: Complex or sparse not yet supported");
        }
    }

    public boolean isReal() {
        return real;
    }

    public boolean isFixed() {
        return fixed;
    }

    public boolean isDense() {
        return dense;
    }

    public int getBits() {
        return bits;
    }

    public Class getClassType() {
        return classType;
    }

    public Matrix create(int rows , int cols ) {
        switch( this ) {
            case DDRM: return new DMatrixRMaj(rows,cols);
            default:
                throw new RuntimeException("Unknown Matrix Type "+this);
        }

    }
}
