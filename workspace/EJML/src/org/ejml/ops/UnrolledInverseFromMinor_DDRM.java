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

package org.ejml.ops;

import org.ejml.data.DMatrixRMaj;

/**
 * Unrolled inverse from minor for DDRM type matrices.
 * The input matrix is scaled make it much less prone to overflow and underflow issues.
 *
 * <p>DO NOT MODIFY.  Automatically generated code created by GenerateUnrolledInverseFromMinor_DDRM</p>
 *
 * @author Peter Abeles
 */
public class UnrolledInverseFromMinor_DDRM {

    public static final int MAX = 5;

    public static void inv(DMatrixRMaj mat, DMatrixRMaj inv) {
        double max = Math.abs(mat.data[0]);
        int N = mat.getNumElements();
        
        for( int i = 1; i < N; i++ ) {
            double a = Math.abs(mat.data[i]);
            if( a > max ) max = a;
        }

        switch( mat.numRows ) {
            case 2: inv2(mat,inv,1.0/max); break;
            case 3: inv3(mat,inv,1.0/max); break;
            case 4: inv4(mat,inv,1.0/max); break;
            case 5: inv5(mat,inv,1.0/max); break;
            default: throw new IllegalArgumentException("Not supported");
        }
    }

    public static void inv2(DMatrixRMaj mat,  DMatrixRMaj inv, double scale)
    {
        double []data = mat.data;

        double a11 = data[ 0 ]*scale;
        double a12 = data[ 1 ]*scale;
        double a21 = data[ 2 ]*scale;
        double a22 = data[ 3 ]*scale;

        double m11 = a22;
        double m12 = -( a21);
        double m21 = -( a12);
        double m22 = a11;

        double det = (a11*m11 + a12*m12)/scale;

        data = inv.data;
        data[0] = m11 / det;
        data[1] = m21 / det;
        data[2] = m12 / det;
        data[3] = m22 / det;
    }

    public static void inv3(DMatrixRMaj mat,  DMatrixRMaj inv, double scale)
    {
        double []data = mat.data;

        double a11 = data[ 0 ]*scale;
        double a12 = data[ 1 ]*scale;
        double a13 = data[ 2 ]*scale;
        double a21 = data[ 3 ]*scale;
        double a22 = data[ 4 ]*scale;
        double a23 = data[ 5 ]*scale;
        double a31 = data[ 6 ]*scale;
        double a32 = data[ 7 ]*scale;
        double a33 = data[ 8 ]*scale;

        double m11 = a22*a33 - a23*a32;
        double m12 = -( a21*a33 - a23*a31);
        double m13 = a21*a32 - a22*a31;
        double m21 = -( a12*a33 - a13*a32);
        double m22 = a11*a33 - a13*a31;
        double m23 = -( a11*a32 - a12*a31);
        double m31 = a12*a23 - a13*a22;
        double m32 = -( a11*a23 - a13*a21);
        double m33 = a11*a22 - a12*a21;

        double det = (a11*m11 + a12*m12 + a13*m13)/scale;

        data = inv.data;
        data[0] = m11 / det;
        data[1] = m21 / det;
        data[2] = m31 / det;
        data[3] = m12 / det;
        data[4] = m22 / det;
        data[5] = m32 / det;
        data[6] = m13 / det;
        data[7] = m23 / det;
        data[8] = m33 / det;
    }

    public static void inv4(DMatrixRMaj mat,  DMatrixRMaj inv, double scale)
    {
        double []data = mat.data;

        double a11 = data[ 0 ]*scale;
        double a12 = data[ 1 ]*scale;
        double a13 = data[ 2 ]*scale;
        double a14 = data[ 3 ]*scale;
        double a21 = data[ 4 ]*scale;
        double a22 = data[ 5 ]*scale;
        double a23 = data[ 6 ]*scale;
        double a24 = data[ 7 ]*scale;
        double a31 = data[ 8 ]*scale;
        double a32 = data[ 9 ]*scale;
        double a33 = data[ 10 ]*scale;
        double a34 = data[ 11 ]*scale;
        double a41 = data[ 12 ]*scale;
        double a42 = data[ 13 ]*scale;
        double a43 = data[ 14 ]*scale;
        double a44 = data[ 15 ]*scale;

        double m11 =  + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42);
        double m12 = -(  + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41));
        double m13 =  + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41);
        double m14 = -(  + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41));
        double m21 = -(  + a12*(a33*a44 - a34*a43) - a13*(a32*a44 - a34*a42) + a14*(a32*a43 - a33*a42));
        double m22 =  + a11*(a33*a44 - a34*a43) - a13*(a31*a44 - a34*a41) + a14*(a31*a43 - a33*a41);
        double m23 = -(  + a11*(a32*a44 - a34*a42) - a12*(a31*a44 - a34*a41) + a14*(a31*a42 - a32*a41));
        double m24 =  + a11*(a32*a43 - a33*a42) - a12*(a31*a43 - a33*a41) + a13*(a31*a42 - a32*a41);
        double m31 =  + a12*(a23*a44 - a24*a43) - a13*(a22*a44 - a24*a42) + a14*(a22*a43 - a23*a42);
        double m32 = -(  + a11*(a23*a44 - a24*a43) - a13*(a21*a44 - a24*a41) + a14*(a21*a43 - a23*a41));
        double m33 =  + a11*(a22*a44 - a24*a42) - a12*(a21*a44 - a24*a41) + a14*(a21*a42 - a22*a41);
        double m34 = -(  + a11*(a22*a43 - a23*a42) - a12*(a21*a43 - a23*a41) + a13*(a21*a42 - a22*a41));
        double m41 = -(  + a12*(a23*a34 - a24*a33) - a13*(a22*a34 - a24*a32) + a14*(a22*a33 - a23*a32));
        double m42 =  + a11*(a23*a34 - a24*a33) - a13*(a21*a34 - a24*a31) + a14*(a21*a33 - a23*a31);
        double m43 = -(  + a11*(a22*a34 - a24*a32) - a12*(a21*a34 - a24*a31) + a14*(a21*a32 - a22*a31));
        double m44 =  + a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31);

        double det = (a11*m11 + a12*m12 + a13*m13 + a14*m14)/scale;

        data = inv.data;
        data[0] = m11 / det;
        data[1] = m21 / det;
        data[2] = m31 / det;
        data[3] = m41 / det;
        data[4] = m12 / det;
        data[5] = m22 / det;
        data[6] = m32 / det;
        data[7] = m42 / det;
        data[8] = m13 / det;
        data[9] = m23 / det;
        data[10] = m33 / det;
        data[11] = m43 / det;
        data[12] = m14 / det;
        data[13] = m24 / det;
        data[14] = m34 / det;
        data[15] = m44 / det;
    }

    public static void inv5(DMatrixRMaj mat,  DMatrixRMaj inv, double scale)
    {
        double []data = mat.data;

        double a11 = data[ 0 ]*scale;
        double a12 = data[ 1 ]*scale;
        double a13 = data[ 2 ]*scale;
        double a14 = data[ 3 ]*scale;
        double a15 = data[ 4 ]*scale;
        double a21 = data[ 5 ]*scale;
        double a22 = data[ 6 ]*scale;
        double a23 = data[ 7 ]*scale;
        double a24 = data[ 8 ]*scale;
        double a25 = data[ 9 ]*scale;
        double a31 = data[ 10 ]*scale;
        double a32 = data[ 11 ]*scale;
        double a33 = data[ 12 ]*scale;
        double a34 = data[ 13 ]*scale;
        double a35 = data[ 14 ]*scale;
        double a41 = data[ 15 ]*scale;
        double a42 = data[ 16 ]*scale;
        double a43 = data[ 17 ]*scale;
        double a44 = data[ 18 ]*scale;
        double a45 = data[ 19 ]*scale;
        double a51 = data[ 20 ]*scale;
        double a52 = data[ 21 ]*scale;
        double a53 = data[ 22 ]*scale;
        double a54 = data[ 23 ]*scale;
        double a55 = data[ 24 ]*scale;

        double m11 =  + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52));
        double m12 = -(  + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)));
        double m13 =  + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51));
        double m14 = -(  + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51)));
        double m15 =  + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51));
        double m21 = -(  + a12*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a13*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a14*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a15*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)));
        double m22 =  + a11*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a13*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a14*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a15*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51));
        double m23 = -(  + a11*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a12*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a14*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a15*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)));
        double m24 =  + a11*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a12*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a13*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a15*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51));
        double m25 = -(  + a11*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a12*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a13*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a14*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51)));
        double m31 =  + a12*( + a23*(a44*a55 - a45*a54) - a24*(a43*a55 - a45*a53) + a25*(a43*a54 - a44*a53)) - a13*( + a22*(a44*a55 - a45*a54) - a24*(a42*a55 - a45*a52) + a25*(a42*a54 - a44*a52)) + a14*( + a22*(a43*a55 - a45*a53) - a23*(a42*a55 - a45*a52) + a25*(a42*a53 - a43*a52)) - a15*( + a22*(a43*a54 - a44*a53) - a23*(a42*a54 - a44*a52) + a24*(a42*a53 - a43*a52));
        double m32 = -(  + a11*( + a23*(a44*a55 - a45*a54) - a24*(a43*a55 - a45*a53) + a25*(a43*a54 - a44*a53)) - a13*( + a21*(a44*a55 - a45*a54) - a24*(a41*a55 - a45*a51) + a25*(a41*a54 - a44*a51)) + a14*( + a21*(a43*a55 - a45*a53) - a23*(a41*a55 - a45*a51) + a25*(a41*a53 - a43*a51)) - a15*( + a21*(a43*a54 - a44*a53) - a23*(a41*a54 - a44*a51) + a24*(a41*a53 - a43*a51)));
        double m33 =  + a11*( + a22*(a44*a55 - a45*a54) - a24*(a42*a55 - a45*a52) + a25*(a42*a54 - a44*a52)) - a12*( + a21*(a44*a55 - a45*a54) - a24*(a41*a55 - a45*a51) + a25*(a41*a54 - a44*a51)) + a14*( + a21*(a42*a55 - a45*a52) - a22*(a41*a55 - a45*a51) + a25*(a41*a52 - a42*a51)) - a15*( + a21*(a42*a54 - a44*a52) - a22*(a41*a54 - a44*a51) + a24*(a41*a52 - a42*a51));
        double m34 = -(  + a11*( + a22*(a43*a55 - a45*a53) - a23*(a42*a55 - a45*a52) + a25*(a42*a53 - a43*a52)) - a12*( + a21*(a43*a55 - a45*a53) - a23*(a41*a55 - a45*a51) + a25*(a41*a53 - a43*a51)) + a13*( + a21*(a42*a55 - a45*a52) - a22*(a41*a55 - a45*a51) + a25*(a41*a52 - a42*a51)) - a15*( + a21*(a42*a53 - a43*a52) - a22*(a41*a53 - a43*a51) + a23*(a41*a52 - a42*a51)));
        double m35 =  + a11*( + a22*(a43*a54 - a44*a53) - a23*(a42*a54 - a44*a52) + a24*(a42*a53 - a43*a52)) - a12*( + a21*(a43*a54 - a44*a53) - a23*(a41*a54 - a44*a51) + a24*(a41*a53 - a43*a51)) + a13*( + a21*(a42*a54 - a44*a52) - a22*(a41*a54 - a44*a51) + a24*(a41*a52 - a42*a51)) - a14*( + a21*(a42*a53 - a43*a52) - a22*(a41*a53 - a43*a51) + a23*(a41*a52 - a42*a51));
        double m41 = -(  + a12*( + a23*(a34*a55 - a35*a54) - a24*(a33*a55 - a35*a53) + a25*(a33*a54 - a34*a53)) - a13*( + a22*(a34*a55 - a35*a54) - a24*(a32*a55 - a35*a52) + a25*(a32*a54 - a34*a52)) + a14*( + a22*(a33*a55 - a35*a53) - a23*(a32*a55 - a35*a52) + a25*(a32*a53 - a33*a52)) - a15*( + a22*(a33*a54 - a34*a53) - a23*(a32*a54 - a34*a52) + a24*(a32*a53 - a33*a52)));
        double m42 =  + a11*( + a23*(a34*a55 - a35*a54) - a24*(a33*a55 - a35*a53) + a25*(a33*a54 - a34*a53)) - a13*( + a21*(a34*a55 - a35*a54) - a24*(a31*a55 - a35*a51) + a25*(a31*a54 - a34*a51)) + a14*( + a21*(a33*a55 - a35*a53) - a23*(a31*a55 - a35*a51) + a25*(a31*a53 - a33*a51)) - a15*( + a21*(a33*a54 - a34*a53) - a23*(a31*a54 - a34*a51) + a24*(a31*a53 - a33*a51));
        double m43 = -(  + a11*( + a22*(a34*a55 - a35*a54) - a24*(a32*a55 - a35*a52) + a25*(a32*a54 - a34*a52)) - a12*( + a21*(a34*a55 - a35*a54) - a24*(a31*a55 - a35*a51) + a25*(a31*a54 - a34*a51)) + a14*( + a21*(a32*a55 - a35*a52) - a22*(a31*a55 - a35*a51) + a25*(a31*a52 - a32*a51)) - a15*( + a21*(a32*a54 - a34*a52) - a22*(a31*a54 - a34*a51) + a24*(a31*a52 - a32*a51)));
        double m44 =  + a11*( + a22*(a33*a55 - a35*a53) - a23*(a32*a55 - a35*a52) + a25*(a32*a53 - a33*a52)) - a12*( + a21*(a33*a55 - a35*a53) - a23*(a31*a55 - a35*a51) + a25*(a31*a53 - a33*a51)) + a13*( + a21*(a32*a55 - a35*a52) - a22*(a31*a55 - a35*a51) + a25*(a31*a52 - a32*a51)) - a15*( + a21*(a32*a53 - a33*a52) - a22*(a31*a53 - a33*a51) + a23*(a31*a52 - a32*a51));
        double m45 = -(  + a11*( + a22*(a33*a54 - a34*a53) - a23*(a32*a54 - a34*a52) + a24*(a32*a53 - a33*a52)) - a12*( + a21*(a33*a54 - a34*a53) - a23*(a31*a54 - a34*a51) + a24*(a31*a53 - a33*a51)) + a13*( + a21*(a32*a54 - a34*a52) - a22*(a31*a54 - a34*a51) + a24*(a31*a52 - a32*a51)) - a14*( + a21*(a32*a53 - a33*a52) - a22*(a31*a53 - a33*a51) + a23*(a31*a52 - a32*a51)));
        double m51 =  + a12*( + a23*(a34*a45 - a35*a44) - a24*(a33*a45 - a35*a43) + a25*(a33*a44 - a34*a43)) - a13*( + a22*(a34*a45 - a35*a44) - a24*(a32*a45 - a35*a42) + a25*(a32*a44 - a34*a42)) + a14*( + a22*(a33*a45 - a35*a43) - a23*(a32*a45 - a35*a42) + a25*(a32*a43 - a33*a42)) - a15*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42));
        double m52 = -(  + a11*( + a23*(a34*a45 - a35*a44) - a24*(a33*a45 - a35*a43) + a25*(a33*a44 - a34*a43)) - a13*( + a21*(a34*a45 - a35*a44) - a24*(a31*a45 - a35*a41) + a25*(a31*a44 - a34*a41)) + a14*( + a21*(a33*a45 - a35*a43) - a23*(a31*a45 - a35*a41) + a25*(a31*a43 - a33*a41)) - a15*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)));
        double m53 =  + a11*( + a22*(a34*a45 - a35*a44) - a24*(a32*a45 - a35*a42) + a25*(a32*a44 - a34*a42)) - a12*( + a21*(a34*a45 - a35*a44) - a24*(a31*a45 - a35*a41) + a25*(a31*a44 - a34*a41)) + a14*( + a21*(a32*a45 - a35*a42) - a22*(a31*a45 - a35*a41) + a25*(a31*a42 - a32*a41)) - a15*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41));
        double m54 = -(  + a11*( + a22*(a33*a45 - a35*a43) - a23*(a32*a45 - a35*a42) + a25*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a45 - a35*a43) - a23*(a31*a45 - a35*a41) + a25*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a45 - a35*a42) - a22*(a31*a45 - a35*a41) + a25*(a31*a42 - a32*a41)) - a15*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        double m55 =  + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41));

        double det = (a11*m11 + a12*m12 + a13*m13 + a14*m14 + a15*m15)/scale;

        data = inv.data;
        data[0] = m11 / det;
        data[1] = m21 / det;
        data[2] = m31 / det;
        data[3] = m41 / det;
        data[4] = m51 / det;
        data[5] = m12 / det;
        data[6] = m22 / det;
        data[7] = m32 / det;
        data[8] = m42 / det;
        data[9] = m52 / det;
        data[10] = m13 / det;
        data[11] = m23 / det;
        data[12] = m33 / det;
        data[13] = m43 / det;
        data[14] = m53 / det;
        data[15] = m14 / det;
        data[16] = m24 / det;
        data[17] = m34 / det;
        data[18] = m44 / det;
        data[19] = m54 / det;
        data[20] = m15 / det;
        data[21] = m25 / det;
        data[22] = m35 / det;
        data[23] = m45 / det;
        data[24] = m55 / det;
    }

}
