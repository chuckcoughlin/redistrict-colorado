/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.util;



/**
 * A range of numbers. {@linkplain #union Union} and {@linkplain #intersect intersection} are
 * computed as usual, except that widening conversions will be applied as needed.
 *
 * @since 2.0
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class NumberRange<T extends Number & Comparable<? super T>> extends Range<T> {
	private final static String CLSS = "NumberRange";
    //
    // IMPLEMENTATION NOTE: This class is full of @SuppressWarnings("unchecked") annotations.
    // Nevertheless we should never get ClassCastException - if we get some this is a bug in
    // this implementation. Users may get IllegalArgumentException however.
    //

    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -818167965963008231L;

    /**
     * Constructs an inclusive range of {@code byte} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Byte> create(final byte minimum, final byte maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code byte} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Byte> create(
            final byte minimum,
            final boolean isMinIncluded,
            final byte maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Byte>(
                Byte.class,
                Byte.valueOf(minimum),
                isMinIncluded,
                Byte.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code short} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Short> create(final short minimum, final short maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code short} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Short> create(
            final short minimum,
            final boolean isMinIncluded,
            final short maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Short>(
                Short.class,
                Short.valueOf(minimum),
                isMinIncluded,
                Short.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code int} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Integer> create(final int minimum, final int maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code int} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Integer> create(
            final int minimum,
            final boolean isMinIncluded,
            final int maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Integer>(
                Integer.class,
                Integer.valueOf(minimum),
                isMinIncluded,
                Integer.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code long} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Long> create(final long minimum, final long maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code long} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Long> create(
            final long minimum,
            final boolean isMinIncluded,
            final long maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Long>(
                Long.class,
                Long.valueOf(minimum),
                isMinIncluded,
                Long.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code float} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Float> create(final float minimum, final float maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code float} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Float> create(
            final float minimum,
            final boolean isMinIncluded,
            final float maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Float>(
                Float.class,
                Float.valueOf(minimum),
                isMinIncluded,
                Float.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code double} values.
     *
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @since 2.5
     */
    public static NumberRange<Double> create(final double minimum, final double maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code double} values.
     *
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     * @since 2.5
     */
    public static NumberRange<Double> create(
            final double minimum,
            final boolean isMinIncluded,
            final double maximum,
            final boolean isMaxIncluded) {
        return new NumberRange<Double>(
                Double.class,
                Double.valueOf(minimum),
                isMinIncluded,
                Double.valueOf(maximum),
                isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@link Comparable} objects. This constructor is used by
     * {@link RangeSet#newRange} only.
     *
     * @param type The element class, usually one of {@link Byte}, {@link Short}, {@link Integer},
     *     {@link Long}, {@link Float} or {@link Double}.
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     * @throws IllegalArgumentException if at least one argument is not of the expected type.
     */
    @SuppressWarnings("unchecked")
    NumberRange(Class<T> type, Comparable<T> minimum, Comparable<T> maximum)
            throws IllegalArgumentException {
        super(type, (T) minimum, (T) maximum);
    }

    /**
     * Constructs an inclusive range of {@link Number} objects.
     *
     * @param type The element class, usually one of {@link Byte}, {@link Short}, {@link Integer},
     *     {@link Long}, {@link Float} or {@link Double}.
     * @param minimum The minimum value, inclusive.
     * @param maximum The maximum value, <strong>inclusive</strong>.
     */
    public NumberRange(final Class<T> type, final T minimum, final T maximum) {
        super(type, minimum, maximum);
    }

    /**
     * Constructs a range of {@link Number} objects.
     *
     * @param type The element class, usually one of {@link Byte}, {@link Short}, {@link Integer},
     *     {@link Long}, {@link Float} or {@link Double}.
     * @param minimum The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the range.
     * @param maximum The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the range.
     */
    public NumberRange(
            final Class<T> type,
            final T minimum,
            final boolean isMinIncluded,
            final T maximum,
            final boolean isMaxIncluded) {
        super(type, minimum, isMinIncluded, maximum, isMaxIncluded);
    }



    /**
     * Constructs a range with the same type and the same values than the specified range. This is a
     * copy constructor.
     *
     * @param range The range to copy. The elements must be {@link Number} instances.
     * @since 2.4
     */
    public NumberRange(final Range<T> range) {
        super(
                range.getElementClass(),
                range.getMinValue(),
                range.isMinIncluded(),
                range.getMaxValue(),
                range.isMaxIncluded());
    }

    /**
     * Creates a new range using the same element class than this range. This method will be
     * overriden by subclasses in order to create a range of a more specific type.
	 */
    @Override
    NumberRange<T> create(
            final T minValue,
            final boolean isMinIncluded,
            final T maxValue,
            final boolean isMaxIncluded) {
        return new NumberRange<T>(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    /**
     * Ensures that {@link #elementClass} is compatible with the type expected by this range class.
     * Invoked for argument checking by the super-class constructor.
     */
    @Override
    void checkElementClass() throws IllegalArgumentException {
        ensureNumberClass(elementClass);
    }

    /** Returns the type of minimum and maximum values. */
    private static Class<? extends Number> getElementClass(final Range<?> range)
            throws IllegalArgumentException {
        ensureNonNull("range", range);
        final Class<?> type = range.elementClass;
        ensureNumberClass(type);
        /*
         * Safe because we checked in the above line. We could have used Class.asSubclass(Class)
         * instead but we want an IllegalArgumentException in case of failure rather than a
         * ClassCastException.
         */
        @SuppressWarnings("unchecked")
        final Class<? extends Number> result = (Class<? extends Number>) type;
        return result;
    }

    /** Ensures that the given class is {@link Number} or a subclass. */
    private static void ensureNumberClass(final Class<?> type) throws IllegalArgumentException {
        if (!Number.class.isAssignableFrom(type)) {
        	throw new IllegalArgumentException(String.format("%s.ensureNumberClass:%s is not a number",CLSS,type.getCanonicalName()));
        }
    }

    /**
     * Wraps the specified {@link Range} in a {@code NumberRange} object. If the specified range is
     * already an instance of {@code NumberRange}, then it is returned unchanged.
     *
     * @param <N> The type of elements in the given range.
     * @param range The range to wrap.
     * @return The same range than {@code range} as a {@code NumberRange} object.
     */
    public static <N extends Number & Comparable<? super N>> NumberRange<N> wrap(
            final Range<N> range) {
        if (range instanceof NumberRange) {
            final NumberRange<N> cast = (NumberRange<N>) range;
            return cast;
        }
        // The constructor will ensure that the range element class is a subclass of Number.
        return new NumberRange<N>(range);
    }

    /** Returns an initially empty array of the given length. */
    @Override
    @SuppressWarnings("unchecked") // Generic array creation.
    NumberRange<T>[] newArray(final int length) {
        return new NumberRange[length];
    }

   
    /**
     * Returns the {@linkplain #getMinValue minimum value} as a {@code double}. If this range is
     * unbounded, then {@link Double#NEGATIVE_INFINITY} is returned.
     *
     * @return The minimum value.
     */
    public double getMinimum() {
        final Number value = (Number) getMinValue();
        return (value != null) ? value.doubleValue() : Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns the {@linkplain #getMaxValue maximum value} as a {@code double}. If this range is
     * unbounded, then {@link Double#POSITIVE_INFINITY} is returned.
     *
     * @return The maximum value.
     */
    public double getMaximum() {
        final Number value = (Number) getMaxValue();
        return (value != null) ? value.doubleValue() : Double.POSITIVE_INFINITY;
    }

}
