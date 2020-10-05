/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.wkt;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.Utilities;


/**
 * An element in a <cite>Well Known Text</cite> (WKT). A {@code Element} is made of {@link String},
 * {@link Number} and other {@link Element}. For example:
 *
 * <blockquote>
 *
 * <pre>
 * PRIMEM["Greenwich", 0.0, AUTHORITY["some authority", "Greenwich"]]
 * </pre>
 *
 * </blockquote>
 *
 * Each {@code Element} object can contains an arbitrary amount of other elements. The result is a
 * tree, which can be printed with {@link #print}. Elements can be pull in a <cite>first in, first
 * out</cite> order.
 *
 * @since 2.0
 * @version $Id$
 * @author Remi Eve
 * @author Martin Desruisseaux (IRD)
 */
public final class Element {
	private final static String CLSS = "Element";
    /** The position where this element starts in the string to be parsed. */
    private final int offset;

    /** Keyword of this entity. For example: "PRIMEM". */
    public final String keyword;

    /**
     * An ordered list of {@link String}s, {@link Number}s and other {@link Element}s. May be {@code
     * null} if the keyword was not followed by a pair of brackets (e.g. "NORTH").
     */
    private final List<Object> list;

    /**
     * Constructs a root element.
     *
     * @param singleton The only children for this root.
     */
    Element(final Element singleton) {
        offset = 0;
        keyword = null;
        list = new LinkedList<Object>();
        list.add(singleton);
    }

    /**
     * Constructs a new {@code Element}.
     *
     * @param text The text to parse.
     * @param position In input, the position where to start parsing from. In output, the first
     *     character after the separator.
     */
    Element(final AbstractParser parser, final String text, final ParsePosition position)
            throws ParseException {
        /*
         * Find the first keyword in the specified string. If a keyword is found, then
         * the position is set to the index of the first character after the keyword.
         */
        int lower = position.getIndex();
        final int length = text.length();
        while (lower < length && Character.isWhitespace(text.charAt(lower))) {
            lower++;
        }
        offset = lower;
        int upper = lower;
        while (upper < length && Character.isUnicodeIdentifierPart(text.charAt(upper))) {
            upper++;
        }
        if (upper <= lower) {
            position.setErrorIndex(lower);
            throw new ParseException(String.format("%s: Unparseable %s",CLSS,text),offset);
        }
        keyword = text.substring(lower, upper).toUpperCase(parser.symbols.locale);
        position.setIndex(upper);
        /*
         * Parse the opening bracket. According CTS's specification, two characters
         * are acceptable: '[' and '('.  At the end of this method, we will require
         * the matching closing bracket. For example if the opening bracket was '[',
         * then we will require that the closing bracket is ']' and not ')'.
         */
        int bracketIndex = -1;
        do {
            if (++bracketIndex >= parser.symbols.openingBrackets.length) {
                list = null;
                return;
            }
        } while (!parseOptionalSeparator(
                text, position, parser.symbols.openingBrackets[bracketIndex]));
        list = new LinkedList<Object>();
        /*
         * Parse all elements inside the bracket. Elements are parsed sequentially
         * and their type are selected according their first character:
         *
         *   - If the first character is a quote, then the element is parsed as a String.
         *   - Otherwise, if the first character is a unicode identifier start, then the
         *     element is parsed as a chidren Element.
         *   - Otherwise, the element is parsed as a number.
         */
        do {
            if (position.getIndex() >= length) {
            	 throw new ParseException(String.format("%s: Missing character %s",CLSS,parser.symbols.close), length);
            }
            //
            // Try to parse the next element as a quoted string. We will take
            // it as a string if the first non-blank character is a quote.
            //
            if (parseOptionalSeparator(text, position, parser.symbols.quote)) {
                lower = position.getIndex();
                upper = text.indexOf(parser.symbols.quote, lower);
                if (upper < lower) {
                    position.setErrorIndex(++lower);
                    throw new ParseException(String.format("%s: Missing character %s",CLSS,parser.symbols.quote), lower);
                }
                list.add(text.substring(lower, upper).trim());
                position.setIndex(upper + 1);
                continue;
            }
            //
            // Try to parse the next element as a number. We will take it as a number if
            // the first non-blank character is not the begining of an unicode identifier.
            //
            lower = position.getIndex();
            if (!Character.isUnicodeIdentifierStart(text.charAt(lower))) {
                final Number number = parser.parseNumber(text, position);
                if (number == null) {
                    // Do not update the error index; it is already updated by NumberFormat.
                	throw new ParseException(String.format("%s: Unparseable string %s",CLSS,text), lower);
                }
                list.add(number);
                continue;
            }
            // Otherwise, add the element as a child element.
            list.add(new Element(parser, text, position));
        } while (parseOptionalSeparator(text, position, parser.symbols.separator));
        parseSeparator(text, position, parser.symbols.closingBrackets[bracketIndex]);
    }

    /**
     * Returns {@code true} if this element is the root element. For example in a WKT like {@code
     * "GEOGCS["name", DATUM["name, ...]]"}, this is true for {@code "GEOGCS"} and false for all
     * other elements inside, like {@code "DATUM"}.
     *
     * @return {@code true} if this element is the root element.
     * @since 2.3
     */
    public boolean isRoot() {
        return this.offset == 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                      ////////
    ////////    Pull elements from the tree                                       ////////
    ////////                                                                      ////////
    //////////////////////////////////////////////////////////////////////////////////////
    /**
     * Removes the next {@link Number} from the list and returns it.
     *
     * @param key The parameter name. Used for formatting an error message if no number are found.
     * @return The next {@link Number} on the list as a {@code double}.
     * @throws ParseException if no more number is available.
     */
    public double pullDouble(final String key) throws ParseException {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Number) {
                iterator.remove();
                return ((Number) object).doubleValue();
            }
        }
        throw new ParseException(String.format("%s.pullDouble: Missing parameter %s",CLSS,key),0);
    }

    /**
     * Removes the next {@link Number} from the list and returns it as an integer.
     *
     * @param key The parameter name. Used for formatting an error message if no number are found.
     * @return The next {@link Number} on the list as an {@code int}.
     * @throws ParseException if no more number is available, or the number is not an integer.
     */
    public int pullInteger(final String key) throws ParseException {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Number) {
                iterator.remove();
                final Number number = (Number) object;
                if (number instanceof Float || number instanceof Double) {
                    throw new ParseException(String.format("%s.pullInteger: Illegal argument %s",CLSS,key),offset);
                }
                return number.intValue();
            }
        }
        throw new ParseException(String.format("%s.pullInteger: Missing parameter %s",CLSS,key),0);
    }

    /**
     * Removes the next {@link String} from the list and returns it.
     *
     * @param key The parameter name. Used for formatting an error message if no number are found.
     * @return The next {@link String} on the list.
     * @throws ParseException if no more string is available.
     */
    public String pullString(final String key) throws ParseException {
        String optionalString = pullOptionalString(key);
        if (optionalString != null) {
            return optionalString;
        }
        throw new ParseException(String.format("%s.pullString: Missing parameter %s", CLSS,key),0);
    }

    /**
     * Removes the next {@link String} from the list and returns it.
     *
     * @param key The parameter name. Used for formatting an error message if no number are found.
     * @return The next {@link String} on the list or {@code null} if no more element is available.
     */
    public String pullOptionalString(final String key) {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof String) {
                iterator.remove();
                return (String) object;
            }
        }
        return null;
    }

    /**
     * Removes the next {@link Element} from the list and returns it.
     *
     * @param key The element name (e.g. <code>"PRIMEM"</code>).
     * @return The next {@link Element} on the list.
     * @throws ParseException if no more element is available.
     */
    public Element pullElement(final String key) throws ParseException {
        final Element element = pullOptionalElement(key);
        if (element != null) {
            return element;
        }
        throw new ParseException(String.format("%s.pullElement: Missing parameter %s",CLSS,key),0);
    }

    /**
     * Removes the next {@link Element} from the list and returns it.
     *
     * @param key The element name (e.g. <code>"PRIMEM"</code>).
     * @return The next {@link Element} on the list, or {@code null} if no more element is
     *     available.
     */
    public Element pullOptionalElement(String key) {
        key = key.toUpperCase();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (element.list != null && element.keyword.equals(key)) {
                    iterator.remove();
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Removes and returns the next {@link Element} with no bracket. The key is used only for only
     * for formatting an error message.
     *
     * @param key The parameter name. Used only for formatting an error message.
     * @return The next {@link Element} in the list, with no bracket.
     * @throws ParseException if no more void element is available.
     */
    public Element pullVoidElement(final String key) throws ParseException {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (element.list == null) {
                    iterator.remove();
                    return element;
                }
            }
        }
        throw new ParseException(String.format("%s.pullVoidElement: Missing parameter %s",CLSS,key),0);
    }

    /**
     * Removes and returns the next {@link Element} with no bracket, if available, or null
     * otherwise.
     *
     * @return The next {@link Element} in the list, with no bracket, or null if none was found
     * @throws ParseException if no more void element is available.
     */
    public Element pullOptionalVoidElement() throws ParseException {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (element.list == null) {
                    iterator.remove();
                    return element;
                }
            }
        }
        return null;
    }
    /**
     * Moves to the next non-whitespace character and checks if this character is the specified
     * separator. If the separator is found, it is skipped. Otherwise, this method thrown a {@link
     * ParseException}.
     *
     * @param text The text to parse.
     * @param position In input, the position where to start parsing from. In output, the first
     *     character after the separator.
     * @param separator The character to search.
     * @throws ParseException if the separator was not found.
     */
    private void parseSeparator(
            final String text, final ParsePosition position, final char separator)
            throws ParseException {
        if (!parseOptionalSeparator(text, position, separator)) {
            position.setErrorIndex(position.getIndex());
            throw new ParseException(String.format("%s.parseSeparator: Unparseable string %s",CLSS,text), position.getIndex());
        }
    }
    /**
     * Returns {@code true} if the next non-whitespace character is the specified separator. Search
     * is performed in string {@code text} from position {@code position}. If the separator is
     * found, then the position is set to the first character after the separator. Otherwise, the
     * position is set on the first non-blank character.
     *
     * @param text The text to parse.
     * @param position In input, the position where to start parsing from. In output, the first
     *     character after the separator.
     * @param separator The character to search.
     * @return {@code true} if the next non-whitespace character is the separator, or {@code false}
     *     otherwise.
     */
    private static boolean parseOptionalSeparator(
            final String text, final ParsePosition position, final char separator) {
        final int length = text.length();
        int index = position.getIndex();
        while (index < length) {
            final char c = text.charAt(index);
            if (Character.isWhitespace(c)) {
                index++;
                continue;
            }
            if (c == separator) {
                position.setIndex(++index);
                return true;
            }
            break;
        }
        position.setIndex(index); // MANDATORY for correct working of the constructor.
        return false;
    }
    /**
     * Returns the next element, or {@code null} if there is no more element. The element is
     * <strong>not</strong> removed from the list.
     *
     * @return The next element, or {@code null} if there is no more elements.
     */
    public Object peek() {
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Close this element.
     *
     * @throws ParseException If the list still contains some unprocessed elements.
     */
    public void close() throws ParseException {
        if (list != null && !list.isEmpty()) {
            throw new ParseException(String.format("%s.close: unexpected parameter %s",CLSS,list.get(0)),
                    offset + keyword.length());
        }
    }

    /**
     * Returns the keyword. This overriding is needed for correct formatting of the error message in
     * {@link #close}.
     */
    @Override
    public String toString() {
        return keyword;
    }
}
