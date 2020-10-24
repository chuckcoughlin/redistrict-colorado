/*
 * Copyright (c) 2009-2017, Peter Abeles. All Rights Reserved.
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
 * This code was auto generated by GenerateDeterminantFromMinor and should not be modified
 * directly.  
 * 
 * @author Peter Abeles
 */
public class UnrolledDeterminantFromMinor_DDRM {
    
    public static final int MAX = 6;
    
    public static double det( DMatrixRMaj mat ) {
        switch( mat.numRows ) {
            case 2: return det2(mat);
            case 3: return det3(mat);
            case 4: return det4(mat);
            case 5: return det5(mat);
            case 6: return det6(mat);
            default: throw new IllegalArgumentException("Not supported");
        }
    }

    public static double det2( DMatrixRMaj mat )
    {
        double m[] = mat.data;

        return m[0]*m[3] - m[1]*m[2];
    }

    public static double det3( DMatrixRMaj mat )
    {
        double m[] = mat.data;

        double a11 = m[0];
        double a12 = m[1];
        double a13 = m[2];
        double a21 = m[3];
        double a22 = m[4];
        double a23 = m[5];
        double a31 = m[6];
        double a32 = m[7];
        double a33 = m[8];

        double a = a11*(a22*a33 - a23*a32);
        double b = a12*(a21*a33 - a23*a31);
        double c = a13*(a21*a32 - a31*a22);

        return a-b+c;
    }

    public static double det4( DMatrixRMaj mat )
    {
        double []data = mat.data;

        double  a11 = data[ 5 ]; double  a12 = data[ 6 ]; double  a13 = data[ 7 ];
        double  a21 = data[ 9 ]; double  a22 = data[ 10 ];double  a23 = data[ 11 ];
        double  a31 = data[ 13 ];double  a32 = data[ 14 ];double  a33 = data[ 15 ];

        double ret = 0;
        ret += data[ 0 ] * ( + a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31));
        a11 = data[ 4 ];
        a21 = data[ 8 ];
        a31 = data[ 12 ];
        ret -= data[ 1 ] * ( + a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31));
        a12 = data[ 5 ];
        a22 = data[ 9 ];
        a32 = data[ 13 ];
        ret += data[ 2 ] * ( + a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31));
        a13 = data[ 6 ];
        a23 = data[ 10 ];
        a33 = data[ 14 ];
        ret -= data[ 3 ] * ( + a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31));
        return ret;
    }

    public static double det5( DMatrixRMaj mat )
    {
        double []data = mat.data;

        double  a11 = data[ 6 ];
        double  a12 = data[ 7 ];
        double  a13 = data[ 8 ];
        double  a14 = data[ 9 ];
        double  a21 = data[ 11 ];
        double  a22 = data[ 12 ];
        double  a23 = data[ 13 ];
        double  a24 = data[ 14 ];
        double  a31 = data[ 16 ];
        double  a32 = data[ 17 ];
        double  a33 = data[ 18 ];
        double  a34 = data[ 19 ];
        double  a41 = data[ 21 ];
        double  a42 = data[ 22 ];
        double  a43 = data[ 23 ];
        double  a44 = data[ 24 ];

        double ret = 0;
        ret += data[ 0 ] * ( + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        a11 = data[ 5 ];
        a21 = data[ 10 ];
        a31 = data[ 15 ];
        a41 = data[ 20 ];
        ret -= data[ 1 ] * ( + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        a12 = data[ 6 ];
        a22 = data[ 11 ];
        a32 = data[ 16 ];
        a42 = data[ 21 ];
        ret += data[ 2 ] * ( + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        a13 = data[ 7 ];
        a23 = data[ 12 ];
        a33 = data[ 17 ];
        a43 = data[ 22 ];
        ret -= data[ 3 ] * ( + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        a14 = data[ 8 ];
        a24 = data[ 13 ];
        a34 = data[ 18 ];
        a44 = data[ 23 ];
        ret += data[ 4 ] * ( + a11*( + a22*(a33*a44 - a34*a43) - a23*(a32*a44 - a34*a42) + a24*(a32*a43 - a33*a42)) - a12*( + a21*(a33*a44 - a34*a43) - a23*(a31*a44 - a34*a41) + a24*(a31*a43 - a33*a41)) + a13*( + a21*(a32*a44 - a34*a42) - a22*(a31*a44 - a34*a41) + a24*(a31*a42 - a32*a41)) - a14*( + a21*(a32*a43 - a33*a42) - a22*(a31*a43 - a33*a41) + a23*(a31*a42 - a32*a41)));
        return ret;
    }

    public static double det6( DMatrixRMaj mat )
    {
        double []data = mat.data;

        double  a11 = data[ 7 ];
        double  a12 = data[ 8 ];
        double  a13 = data[ 9 ];
        double  a14 = data[ 10 ];
        double  a15 = data[ 11 ];
        double  a21 = data[ 13 ];
        double  a22 = data[ 14 ];
        double  a23 = data[ 15 ];
        double  a24 = data[ 16 ];
        double  a25 = data[ 17 ];
        double  a31 = data[ 19 ];
        double  a32 = data[ 20 ];
        double  a33 = data[ 21 ];
        double  a34 = data[ 22 ];
        double  a35 = data[ 23 ];
        double  a41 = data[ 25 ];
        double  a42 = data[ 26 ];
        double  a43 = data[ 27 ];
        double  a44 = data[ 28 ];
        double  a45 = data[ 29 ];
        double  a51 = data[ 31 ];
        double  a52 = data[ 32 ];
        double  a53 = data[ 33 ];
        double  a54 = data[ 34 ];
        double  a55 = data[ 35 ];

        double ret = 0;
        ret += data[ 0 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        a11 = data[ 6 ];
        a21 = data[ 12 ];
        a31 = data[ 18 ];
        a41 = data[ 24 ];
        a51 = data[ 30 ];
        ret -= data[ 1 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        a12 = data[ 7 ];
        a22 = data[ 13 ];
        a32 = data[ 19 ];
        a42 = data[ 25 ];
        a52 = data[ 31 ];
        ret += data[ 2 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        a13 = data[ 8 ];
        a23 = data[ 14 ];
        a33 = data[ 20 ];
        a43 = data[ 26 ];
        a53 = data[ 32 ];
        ret -= data[ 3 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        a14 = data[ 9 ];
        a24 = data[ 15 ];
        a34 = data[ 21 ];
        a44 = data[ 27 ];
        a54 = data[ 33 ];
        ret += data[ 4 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        a15 = data[ 10 ];
        a25 = data[ 16 ];
        a35 = data[ 22 ];
        a45 = data[ 28 ];
        a55 = data[ 34 ];
        ret -= data[ 5 ] * ( + a11*( + a22*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) + a24*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a25*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52))) - a12*( + a21*( + a33*(a44*a55 - a45*a54) - a34*(a43*a55 - a45*a53) + a35*(a43*a54 - a44*a53)) - a23*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) - a25*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51))) + a13*( + a21*( + a32*(a44*a55 - a45*a54) - a34*(a42*a55 - a45*a52) + a35*(a42*a54 - a44*a52)) - a22*( + a31*(a44*a55 - a45*a54) - a34*(a41*a55 - a45*a51) + a35*(a41*a54 - a44*a51)) + a24*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51))) - a14*( + a21*( + a32*(a43*a55 - a45*a53) - a33*(a42*a55 - a45*a52) + a35*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a55 - a45*a53) - a33*(a41*a55 - a45*a51) + a35*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a55 - a45*a52) - a32*(a41*a55 - a45*a51) + a35*(a41*a52 - a42*a51)) - a25*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))) + a15*( + a21*( + a32*(a43*a54 - a44*a53) - a33*(a42*a54 - a44*a52) + a34*(a42*a53 - a43*a52)) - a22*( + a31*(a43*a54 - a44*a53) - a33*(a41*a54 - a44*a51) + a34*(a41*a53 - a43*a51)) + a23*( + a31*(a42*a54 - a44*a52) - a32*(a41*a54 - a44*a51) + a34*(a41*a52 - a42*a51)) - a24*( + a31*(a42*a53 - a43*a52) - a32*(a41*a53 - a43*a51) + a33*(a41*a52 - a42*a51))));
        return ret;
    }

}