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

package org.ejml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.ejml.data.DGrowArray;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.IGrowArray;
import org.ejml.data.Matrix;

/**
 * Various functions that are useful but don't have a clear location that they belong in.
 *
 * @author Peter Abeles
 */
public class UtilEjml {
    public static double EPS = Math.pow(2, -52);
    public static float F_EPS = (float)Math.pow(2, -21);

    public static double PI = Math.PI;
    public static double PI2 = 2.0*Math.PI;
    public static double PId2 = Math.PI/2.0;

    public static float F_PI = (float)Math.PI;
    public static float F_PI2 = (float)(2.0*Math.PI);
    public static float F_PId2 = (float)(Math.PI/2.0);

    // tolerances for unit tests
    public static float TEST_F32 = 5e-4f;
    public static double TEST_F64 = 1e-8;
    public static float TESTP_F32 = 1e-6f;
    public static double TESTP_F64 = 1e-12;
    public static float TEST_F32_SQ = (float)Math.sqrt(TEST_F32);
    public static double TEST_F64_SQ = Math.sqrt(TEST_F64);

    // The maximize size it will do inverse on
    public static int maxInverseSize = 5;

    public static final int[] ZERO_LENGTH_I32 = new int[0];
    public static final float[] ZERO_LENGTH_F32 = new float[0];
    public static final double[] ZERO_LENGTH_F64 = new double[0];

    public static void checkSameInstance( Object a, Object b ) {
        if (a == b)
            throw new IllegalArgumentException("Can't pass in the same instance");
    }

    /**
     * If the input matrix is null a new matrix is created and returned. If it exists it will be reshaped and returned.
     *
     * @param a (Input/Output) matrix which is to be checked. Can be null.
     * @param rows Desired number of rows
     * @param cols Desired number of cols
     * @return modified matrix or new matrix
     */
    public static DMatrixRMaj reshapeOrDeclare( DMatrixRMaj a, int rows, int cols ) {
        if (a == null)
            return new DMatrixRMaj(rows, cols);
        else if (a.numRows != rows || a.numCols != cols)
            a.reshape(rows, cols);
        return a;
    }


    public static void checkSameShape( Matrix a, Matrix b, boolean allowedSameInstance ) {
        if (a.getNumRows() != b.getNumRows() || a.getNumCols() != b.getNumCols()) {
            throw new MatrixDimensionException("Must be same shape. " + a.getNumRows() + "x" + a.getNumCols() + " vs " + b.getNumRows() + "x" + b.getNumCols());
        }
        if (!allowedSameInstance && a == b)
            throw new IllegalArgumentException("Must not be the same instance");
    }

    public static void checkSameShape( Matrix a, Matrix b, Matrix c ) {
        if (a.getNumRows() != b.getNumRows() || a.getNumCols() != b.getNumCols()) {
            throw new MatrixDimensionException("Must be same shape. " + a.getNumRows() + "x" + a.getNumCols() + " vs " + b.getNumRows() + "x" + b.getNumCols());
        }
        if (a.getNumRows() != c.getNumRows() || a.getNumCols() != c.getNumCols()) {
            throw new IllegalArgumentException("Must be same shape. " + a.getNumRows() + "x" + a.getNumCols() + " vs " + c.getNumRows() + "x" + c.getNumCols());
        }
    }

    public static void checkTooLarge( int rows, int cols ) {
        if ((rows*cols) != ((long)rows*cols))
            throw new IllegalArgumentException("Matrix size exceeds the size of an integer");
    }

    public static void checkTooLargeComplex( int rows, int cols ) {
        if ((2*rows*cols) != ((long)rows*cols*2))
            throw new IllegalArgumentException("Matrix size exceeds the size of an integer");
    }

    public static boolean isUncountable( double val ) {
        return Double.isNaN(val) || Double.isInfinite(val);
    }

    public static boolean isUncountable( float val ) {
        return Float.isNaN(val) || Float.isInfinite(val);
    }

    public static boolean isIdentical( double a, double b, double tol ) {
        // if either is negative or positive infinity the result will be positive infinity
        // if either is NaN the result will be NaN
        double diff = Math.abs(a - b);

        // diff = NaN == false
        // diff = infinity == false
        if (tol >= diff)
            return true;

        if (Double.isNaN(a)) {
            return Double.isNaN(b);
        } else
            return Double.isInfinite(a) && a == b;
    }

    public static boolean isIdentical( float a, float b, float tol ) {
        // if either is negative or positive infinity the result will be positive infinity
        // if either is NaN the result will be NaN
        double diff = Math.abs(a - b);

        // diff = NaN == false
        // diff = infinity == false
        if (tol >= diff)
            return true;

        if (Float.isNaN(a)) {
            return Float.isNaN(b);
        } else
            return Float.isInfinite(a) && a == b;
    }

    public static void memset( double[] data, double val, int length ) {
        for (int i = 0; i < length; i++) {
            data[i] = val;
        }
    }

    public static void memset( int[] data, int val, int length ) {
        for (int i = 0; i < length; i++) {
            data[i] = val;
        }
    }

    public static <T> void setnull( T[] array ) {
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }

    public static double max( double[] array, int start, int length ) {
        double max = array[start];
        final int end = start + length;

        for (int i = start + 1; i < end; i++) {
            double v = array[i];
            if (v > max) {
                max = v;
            }
        }

        return max;
    }

    public static float max( float[] array, int start, int length ) {
        float max = array[start];
        final int end = start + length;

        for (int i = start + 1; i < end; i++) {
            float v = array[i];
            if (v > max) {
                max = v;
            }
        }

        return max;
    }

    /**
     * Give a string of numbers it returns a DenseMatrix
     */
    @SuppressWarnings("StringSplitter")
    public static DMatrixRMaj parse_DDRM( String s, int numColumns ) {
        String[] vals = s.split("(\\s)+");

        // there is the possibility the first element could be empty
        int start = vals[0].isEmpty() ? 1 : 0;

        // covert it from string to doubles
        int numRows = (vals.length - start)/numColumns;

        DMatrixRMaj ret = new DMatrixRMaj(numRows, numColumns);

        int index = start;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                ret.set(i, j, Double.parseDouble(vals[index++]));
            }
        }

        return ret;
    }

 
    public static Integer[] sortByIndex( final double[] data, int size ) {
        Integer[] idx = new Integer[size];
        for (int i = 0; i < size; i++) {
            idx[i] = i;
        }

        Arrays.sort(idx, Comparator.comparingDouble(o -> data[o]));

        return idx;
    }

    public static int[] shuffled( int N, Random rand ) {
        return shuffled(N, N, rand);
    }

    public static int[] shuffled( int N, int shuffleUpTo, Random rand ) {
        int[] l = new int[N];
        for (int i = 0; i < N; i++) {
            l[i] = i;
        }
        shuffle(l, N, 0, shuffleUpTo, rand);
        return l;
    }

    public static int[] shuffledSorted( int N, int shuffleUpTo, Random rand ) {
        int[] l = new int[N];
        for (int i = 0; i < N; i++) {
            l[i] = i;
        }
        shuffle(l, N, 0, shuffleUpTo, rand);
        Arrays.sort(l, 0, shuffleUpTo);
        return l;
    }

    public static void shuffle( int[] list, int N, int start, int end, Random rand ) {
        int range = end - start;
        for (int i = 0; i < range; i++) {
            int selected = rand.nextInt(N - i) + i + start;
            int v = list[i];
            list[i] = list[selected];
            list[selected] = v;
        }
    }

    public static int[] pivotVector( int[] pivots, int length,  IGrowArray storage ) {
        if (storage == null) storage = new IGrowArray();
        storage.reshape(length);
        System.arraycopy(pivots, 0, storage.data, 0, length);
        return storage.data;
    }

    public static int permutationSign( int[] p, int N, int[] work ) {
        System.arraycopy(p, 0, work, 0, N);
        p = work;
        int cnt = 0;
        for (int i = 0; i < N; ++i) {
            while (i != p[i]) {
                ++cnt;
                int tmp = p[i];
                p[i] = p[p[i]];
                p[tmp] = tmp;
            }
        }
        return cnt%2 == 0 ? 1 : -1;
    }

    public static double[] randomVector_F64( Random rand, int length ) {
        double[] d = new double[length];
        for (int i = 0; i < length; i++) {
            d[i] = rand.nextDouble();
        }
        return d;
    }

    public static float[] randomVector_F32( Random rand, int length ) {
        float[] d = new float[length];
        for (int i = 0; i < length; i++) {
            d[i] = rand.nextFloat();
        }
        return d;
    }

    public static String stringShapes( Matrix A, Matrix B, Matrix C ) {
        return "( " + A.getNumRows() + "x" + A.getNumCols() + " ) " +
                "( " + B.getNumRows() + "x" + B.getNumCols() + " ) " +
                "( " + C.getNumRows() + "x" + C.getNumCols() + " )";
    }

    public static String stringShapes( Matrix A, Matrix B ) {
        return "( " + A.getNumRows() + "x" + A.getNumCols() + " ) " +
                "( " + B.getNumRows() + "x" + B.getNumCols() + " )";
    }

    /**
     * Fixed length fancy formatting for doubles. If possible decimal notation is used. If all the significant digits
     * can't be shown then it will switch to exponential notation.  If not all the space is needed then it will
     * be filled in to ensure it has the specified length.
     *
     * @param value value being formatted
     * @param format default format before exponential
     * @param length Maximum number of characters it can take.
     * @param significant Number of significant decimal digits to show at a minimum.
     * @return formatted string
     */
    public static String fancyStringF( double value, DecimalFormat format, int length, int significant ) {

        String formatted = fancyString(value, format, length, significant);

        final int n = length - formatted.length();
        if (n > 0) {
            StringBuilder builder = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                builder.append(' ');
            }
            return formatted + builder.toString();
        } else {
            return formatted;
        }
    }

    public static String fancyString( double value, DecimalFormat format, int length, int significant ) {

        return fancyString(value, format, true, length, significant);
    }

    public static String fancyString( double value, DecimalFormat format, boolean hasSpace, int length, int significant ) {

        String formatted;

        // see if the number is negative. Including negative zero
        boolean isNegative = Double.doubleToRawLongBits(value) < 0;

        if (value == 0) {
            formatted = isNegative ? "-0" : hasSpace ? " 0" : "0";
        } else {
            int digits = length - 1;
            String extraSpace = isNegative ? "" : hasSpace ? " " : "";
            double vabs = Math.abs(value);
            int a = (int)Math.floor(Math.log10(vabs));
            if (a >= 0 && a < digits) {
                format.setMaximumFractionDigits(digits - 2 - a);
                formatted = extraSpace + format.format(value);
            } else if (a < 0 && digits + a > significant) {
                format.setMaximumFractionDigits(digits - 1);
                formatted = extraSpace + format.format(value);
            } else {
                int exp = (int)Math.log10(Math.abs(a)) + 1;
                // see if there is room for all the requested significant digits
                significant = Math.min(significant, digits - significant - exp);
                if (significant > 0) {
                    formatted = extraSpace + String.format("%." + significant + "E", value);
                } else // I give up. time to break the length
                    formatted = extraSpace + String.format("%.0E", value);
            }
        }
        return formatted;
    }

    /**
     * Resizes the array to ensure that it is at least of length desired and returns its internal array
     */
    public static int[] adjust( IGrowArray gwork, int desired ) {
        if (gwork == null) gwork = new IGrowArray();
        gwork.reshape(desired);
        return gwork.data;
    }

    public static int[] adjust( IGrowArray gwork, int desired, int zeroToM ) {
        int[] w = adjust(gwork, desired);
        Arrays.fill(w, 0, zeroToM, 0);
        return w;
    }

    public static int[] adjustClear( IGrowArray gwork, int desired ) {
        return adjust(gwork, desired, desired);
    }

    public static int[] adjustFill( IGrowArray gwork, int desired, int value ) {
        int[] w = adjust(gwork, desired);
        Arrays.fill(w, 0, desired, value);
        return w;
    }

    /**
     * Resizes the array to ensure that it is at least of length desired and returns its internal array
     */
    public static double[] adjust(DGrowArray gwork, int desired ) {
        if (gwork == null) gwork = new DGrowArray();
        gwork.reshape(desired);
        return gwork.data;
    }

    /**
     * Returns true if any of the matrix arguments has 
     */
    public static boolean hasNullableArgument( Method func ) {
        Annotation[][] annotations = func.getParameterAnnotations();
        if (annotations.length == 0)
            return false;

        Class<?>[] types = func.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            Annotation[] argumentAnnotations = annotations[i];
            if (argumentAnnotations.length == 0)
                continue;
            if (!Matrix.class.isAssignableFrom(types[i]))
                continue;
            Annotation last = argumentAnnotations[argumentAnnotations.length - 1];
            if (last.toString().contains("Nullable"))
                return true;
        }
        return false;
    }

    /**
     * Checks to see if a matrix of this size will exceed the maximum possible value an integer can store, which is
     * the max possible array size in Java.
     */
    public static boolean exceedsMaxMatrixSize( int numRows, int numCols ) {
        if (numRows == 0 || numCols == 0)
            return false;
        return numCols > Integer.MAX_VALUE/numRows;
    }

    public static void printTime( String message, Process timer ) {
        printTime("Processing... ", message, timer);
    }

    public static void printTime( String pre, String message, Process timer ) {
        System.out.printf(pre);
        long time0 = System.nanoTime();
        timer.process();
        long time1 = System.nanoTime();
        System.out.println(message + " " + ((time1 - time0)*1e-6) + " (ms)");
    }

    public interface Process {
        void process();
    }

    /**
     * Intended for checking preconditions. Throws an exception if the two values are not equal.
     */
    public static void assertEq( int valA, int valB ) {
        assertEq(valA, valB, "");
    }

    /**
     * Intended for checking preconditions. Throws an exception if the two values are not equal.
     */
    public static void assertEq( int valA, int valB, String message ) {
        if (valA != valB)
            throw new IllegalArgumentException(valA + " != " + valB + " " + message);
    }

    /**
     * Intended for checking preconditions. Throws an exception if the input is not true
     */
    public static void assertTrue( boolean value, String message ) {
        if (!value)
            throw new IllegalArgumentException(message);
    }

    /**
     * Intended for checking preconditions. Throws an exception if the input is not true
     */
    public static void assertTrue( boolean value ) {
        if (!value)
            throw new IllegalArgumentException("Expected true");
    }

    /**
     * Intended for checking matrix shape preconditions. Throws an exception if the two values are not equal.
     */
    public static void assertShape( int valA, int valB, String message ) {
        if (valA != valB)
            throw new MatrixDimensionException(valA + " != " + valB + " " + message);
    }

    /**
     * Intended for checking matrix shape preconditions. Throws an exception if the input is not true
     */
    public static void assertShape( boolean value, String message ) {
        if (!value)
            throw new MatrixDimensionException(message);
    }
}
