/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotools.operation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.measure.unit.Unit;

import org.geotools.data.wkt.IdentifiedObject;
import org.geotools.datum.Citation;



/**
 * The definition of a parameter used by an operation method. Most
 * parameter values are numeric, but other types of parameter values are possible.
 *
 * <p>For numeric values, the getValueClass value class} is usually <code>
 * {@linkplain Double}.class</code>, <code>{@linkplain Integer}.class</code> or some other Java
 * wrapper class.
 *
 * <p>This class contains numerous convenience constructors. But all of them ultimately invoke
 * {@linkplain
 * #ParameterDescriptor(Map,Class,Object[],Object,Comparable,Comparable,Unit,boolean) a
 * single, full-featured constructor}. All other constructors are just shortcuts.
 *
 * @param <T> The type of elements to be returned by {@link ParameterValue#getValue}.
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public class ParameterDescriptor<T>  {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -295668622297737705L;
    private final int minimumOccurs;
    private final Map<String,Object> properties;
    /**
     * The class that describe the type of the parameter. This is the value class that the user
     * specified at construction time.
     */
    private final Class<T> valueClass;

    /**
     * A immutable, finite set of valid values (usually from a {linkplain org.opengis.util.CodeList
     * code list}) or {@code null} if it doesn't apply. This set is immutable.
     */
    private final Set<T> validValues;

    /** The default value for the parameter, or {@code null}. */
    private final T defaultValue;

    /** The minimum parameter value, or {@code null}. */
    private final Comparable<T> minimum;

    /** The maximum parameter value, or {@code null}. */
    private final Comparable<T> maximum;

    /** The unit for default, minimum and maximum values, or {@code null}. */
    private final Unit<?> unit;


    /**
     * Constructs a mandatory parameter for a set of predefined values.
     *
     * @param name The parameter name.
     * @param valueClass The class that describe the type of the parameter.
     * @param validValues A finite set of valid values (usually from a {linkplain
     *     org.opengis.util.CodeList code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null}.
     */
    public ParameterDescriptor(
            final String name,
            final Class<T> valueClass,
            final T[] validValues,
            final T defaultValue) {
        this(
                Collections.singletonMap(IdentifiedObject.NAME_KEY, name),
                valueClass,
                validValues,
                defaultValue,
                null,
                null,
                null,
                true);
    }

    /**
     * Constructs a parameter from an authority and a name.
     *
     * @param authority The authority (e.g. {@link org.geotools.metadata.iso.citation.Citations#OGC
     *     OGC}).
     * @param name The parameter name.
     * @param valueClass The class that describe the type of the parameter.
     * @param validValues A finite set of valid values (usually from a {linkplain
     *     org.opengis.util.CodeList code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null}.
     * @param minimum The minimum parameter value, or {@code null}.
     * @param maximum The maximum parameter value, or {@code null}.
     * @param unit The unit for default, minimum and maximum values.
     * @param required {@code true} if this parameter is required, or {@code false} if it is
     *     optional.
     * @since 2.2
     */
    public ParameterDescriptor(
            final Citation authority,
            final String name,
            final Class<T> valueClass,
            final T[] validValues,
            final T defaultValue,
            final Comparable<T> minimum,
            final Comparable<T> maximum,
            final Unit<?> unit,
            final boolean required) {
        this(
                Collections.singletonMap(IdentifiedObject.NAME_KEY,authority.getTitle()),
                valueClass,
                validValues,
                defaultValue,
                minimum,
                maximum,
                unit,
                required);
    }

    /**
     * Constructs a parameter from a set of properties.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param valueClass The class that describe the type of the parameter.
     * @param validValues A finite set of valid values (usually from a {linkplain
     *     org.opengis.util.CodeList code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null}.
     * @param minimum The minimum parameter value, or {@code null}.
     * @param maximum The maximum parameter value, or {@code null}.
     * @param unit The unit for default, minimum and maximum values.
     * @param required {@code true} if this parameter is required, or {@code false} if it is
     *     optional.
     */
    public ParameterDescriptor(
            final Map<String,Object> properties,
            final Class<T> valueClass,
            final T[] validValues,
            final T defaultValue,
            final Comparable<T> minimum,
            final Comparable<T> maximum,
            final Unit<?> unit,
            final boolean required) {
        this(properties, required, valueClass, validValues, defaultValue, minimum, maximum, unit);
    }

    /**
     * Constructs a parameter from a set of properties. 
     *
     * <p>This constructor assumes that minimum, maximum and default values are already replaced by
     * their cached values, if available.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param required {@code true} if this parameter is required, or {@code false} if it is
     *     optional.
     * @param valueClass The class that describe the type of the parameter.
     * @param validValues A finite set of valid values (usually from a {linkplain
     *     org.opengis.util.CodeList code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null}.
     * @param minimum The minimum parameter value, or {@code null}.
     * @param maximum The maximum parameter value, or {@code null}.
     * @param unit The unit for default, minimum and maximum values.
     */
    private ParameterDescriptor(
            final Map<String, Object> props,
            final boolean required,
            final Class<T> valueClass,
            final T[] validValues,
            final T defaultValue,
            final Comparable<T> minimum,
            final Comparable<T> maximum,
            final Unit<?> unit) {
        this.properties = props;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
        this.minimum = minimum;
        this.maximum = maximum;
        this.unit = unit;
        this.minimumOccurs = 0;
        
        if (validValues != null) {
            final Set<T> valids =
                    new HashSet<T>(Math.max(validValues.length * 4 / 3 + 1, 8), 0.75f);
            for (int i = 0; i < validValues.length; i++) {
                final T value = validValues[i];
                valids.add(value);
            }
            this.validValues = Collections.unmodifiableSet(valids);
        } 
        else {
            this.validValues = null;
        }
    }

    /**
     * Constructs a descriptor for a mandatory parameter in a range of integer values.
     *
     * @param name The parameter name.
     * @param defaultValue The default value for the parameter.
     * @param minimum The minimum parameter value, or {@link Integer#MIN_VALUE} if none.
     * @param maximum The maximum parameter value, or {@link Integer#MAX_VALUE} if none.
     * @return The parameter descriptor for the given range of values.
     * @since 2.5
     */
    public static ParameterDescriptor<Integer> create(
            final String name, final int defaultValue, final int minimum, final int maximum) {
        return create(
                Collections.singletonMap(IdentifiedObject.NAME_KEY, name), defaultValue, minimum, maximum, true);
    }

    /**
     * Constructs a descriptor for a parameter in a range of integer values.
     *
     * @param properties The parameter properties (name, identifiers, alias...).
     * @param defaultValue The default value for the parameter.
     * @param minimum The minimum parameter value, or {@link Integer#MIN_VALUE} if none.
     * @param maximum The maximum parameter value, or {@link Integer#MAX_VALUE} if none.
     * @param required {@code true} if this parameter is required, {@code false} otherwise.
     * @return The parameter descriptor for the given range of values.
     * @since 2.5
     */
    public static ParameterDescriptor<Integer> create(
            final Map<String, Object> properties,
            final int defaultValue,
            final int minimum,
            final int maximum,
            final boolean required) {
        return new ParameterDescriptor<Integer>(
                properties,
                required,
                Integer.class,
                null,
                Integer.valueOf(defaultValue),
                minimum == Integer.MIN_VALUE ? null : Integer.valueOf(minimum),
                maximum == Integer.MAX_VALUE ? null : Integer.valueOf(maximum), null);
    }

    /**
     * Constructs a descriptor for a mandatory parameter in a range of floating point values.
     *
     * @param name The parameter name.
     * @param defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param minimum The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param maximum The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param unit The unit for default, minimum and maximum values.
     * @return The parameter descriptor for the given range of values.
     * @since 2.5
     */
    public static ParameterDescriptor<Double> create(
            final String name,
            final double defaultValue,
            final double minimum,
            final double maximum,
            final Unit<?> unit) {
        return create(
                Collections.singletonMap(IdentifiedObject.NAME_KEY, name),
                defaultValue,
                minimum,
                maximum,
                unit,
                true);
    }

    /**
     * Constructs a descriptor for a parameter in a range of floating point values.
     *
     * @param properties The parameter properties (name, identifiers, alias...).
     * @param defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param minimum The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param maximum The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param unit The unit of measurement for default, minimum and maximum values.
     * @param required {@code true} if this parameter is required, {@code false} otherwise.
     * @return The parameter descriptor for the given range of values.
     * @since 2.5
     */
    public static ParameterDescriptor<Double> create(
            final Map<String, Object> properties,
            final double defaultValue,
            final double minimum,
            final double maximum,
            final Unit<?> unit,
            final boolean required) {
        return new ParameterDescriptor<Double>(
                properties,
                required,
                Double.class,
                null,
                Double.isNaN(defaultValue) ? null : Double.valueOf(defaultValue),
                minimum == Double.NEGATIVE_INFINITY ? null : Double.valueOf(minimum),
                maximum == Double.POSITIVE_INFINITY ? null : Double.valueOf(maximum),
                unit);
    }


    /**
     * The minimum number of times that values for this parameter group or parameter are required.
     * The default value is one. A value of 0 means an optional parameter.
     *
     * @see #getMaximumOccurs
     */
    public int getMinimumOccurs() {
        return minimumOccurs;
    }
    /**
     * The maximum number of times that values for this parameter group or parameter can be
     * included. For a {@linkplain ParameterDescriptor single parameter}, the value is always
     * 1.
     *
     * @return The maximum occurence.
     * @see #getMinimumOccurs
     */
    public int getMaximumOccurs() {
        return 1;
    }

    /**
     * Returns the class that describe the type of the parameter.
     *
     * @return The parameter value class.
     */
    public Class<T> getValueClass() {
        return valueClass;
    }

    /**
     * If this parameter allows only a finite set of values, returns this set. This set is usually a
     * {linkplain org.opengis.util.CodeList code list} or enumerations. This method returns {@code
     * null} if this parameter doesn't limits values to a finite set.
     *
     * @return A finite set of valid values (usually from a {linkplain org.opengis.util.CodeList
     *     code list}), or {@code null} if it doesn't apply.
     */
    public Set<T> getValidValues() {
        return validValues;
    }

    /**
     * Returns the default value for the parameter. The return type can be any type including a
     * Number or String. If there is no default value, then this method returns
     * {@code null}.
     *
     * @return The default value, or {@code null} in none.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the minimum parameter value. If there is no minimum value, or if minimum value is
     * inappropriate for the {@linkplain #getValueClass parameter type}, then this method returns
     * {@code null}.
     *
     * @return The minimum parameter value (often an instance of {@link Double}), or {@code null}.
     */
    public Comparable<T> getMinimumValue() {
        return minimum;
    }

    /**
     * Returns the maximum parameter value. If there is no maximum value, or if maximum value is
     * inappropriate for the {@linkplain #getValueClass parameter type}, then this method returns
     * {@code null}.
     *
     * @return The minimum parameter value (often an instance of {@link Double}), or {@code null}.
     */
    public Comparable<T> getMaximumValue() {
        return maximum;
    }

    /**
     * Returns the unit for {@linkplain #getDefaultValue default}, {@linkplain #getMinimumValue
     * minimum} and {@linkplain #getMaximumValue maximum} values. This attribute apply only if the
     * values is of numeric type (usually an instance of {@link Double}).
     *
     * @return The unit for numeric value, or {@code null} if it doesn't apply to the value type.
     */
    public Unit<?> getUnit() {
        return unit;
    }
}
