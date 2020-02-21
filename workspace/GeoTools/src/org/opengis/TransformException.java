/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.opengis;

/**
 * Common superclass for a number of transformation-related exceptions. 
 *
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 */
public class TransformException extends Exception {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -8923944544398567533L;


    /** Constructs an exception with no detail message. */
    public TransformException() {}

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     */
    public TransformException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with the specified detail message and cause.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     * @param cause The cause for this exception. The cause is saved for later retrieval by the
     *     {@link #getCause()} method.
     */
    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }
}
