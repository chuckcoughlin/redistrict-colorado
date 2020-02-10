/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.openjump.coordsys;


/**
 * Definition of a coordinate system axis. This is used to label axes, and indicate the orientation.
 * See {@linkplain org.opengis.referencing.cs#AxisNames axis name constraints}.
 * Taken from DefaultCoordinateSystemAxis.
 *
 * <p>In some case, the axis name is constrained by ISO 19111 depending on the {@linkplain
 * org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} type. These
 * constraints are identified in the javadoc by "<cite>ISO 19111 name is...</cite>" sentences. This
 * constraint works in two directions; for example the names "<cite>geodetic latitude</cite>" and
 * "<cite>geodetic longitude</cite>" shall be used to designate the coordinate axis names associated
 * with a {@linkplain org.opengis.referencing.crs.GeographicCRS geographic coordinate reference
 * system}. Conversely, these names shall not be used in any other context.
 *
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @see AbstractCS
 * @see Unit
 */
public class CoordinateSystemAxis  {
	/** Serial number for interoperability with different versions. */
	private static final long serialVersionUID = -7883614853277827689L;
	private final String name;
	/**
	 * Direction of this coordinate system axis. In the case of Cartesian projected coordinates,
	 * this is the direction of this coordinate system axis locally.
	 */
	private final AxisDirection direction;

	/** Minimal and maximal value for this axis. */
	private double minimum, maximum;

	/**
	 * Default axis info for geodetic longitudes in a {@linkplain
	 * org.opengis.referencing.crs.GeographicCRS geographic CRS}.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#EAST East} and units are
	 * {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
	 *
	 * <p>This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
	 * {@link #ELLIPSOIDAL_HEIGHT} set.
	 *
	 * @see #LONGITUDE
	 */
	public static final CoordinateSystemAxis GEODETIC_LONGITUDE =
			new CoordinateSystemAxis("GEODETIC_LONGITUDE",AxisDirection.EAST);

	/**
	 * Default axis info for geodetic latitudes in a {@linkplain
	 * org.opengis.referencing.crs.GeographicCRS geographic CRS}.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#NORTH North} and units are
	 * {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
	 *
	 * <p>The ISO 19111 name is "<cite>geodetic latitude</cite>" and the abbreviation is "&phi;"
	 * (phi).
	 *
	 * <p>This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
	 * {@link #ELLIPSOIDAL_HEIGHT} set.
	 *
	 * @see #LATITUDE
	 */
	public static final CoordinateSystemAxis GEODETIC_LATITUDE =
			new CoordinateSystemAxis("GEODETIC_LATITUDE",AxisDirection.NORTH);

	/**
	 * Default axis info for longitudes.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#EAST East} and units are
	 * {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
	 *
	 * <p>The abbreviation is "&lambda;" (lambda).
	 *
	 * <p>This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE}
	 * set.
	 *
	 * @see #GEODETIC_LONGITUDE
	 * @see #SPHERICAL_LONGITUDE
	 * @see #LATITUDE
	 */
	public static final CoordinateSystemAxis LONGITUDE =
			new CoordinateSystemAxis("LONGITUDE", AxisDirection.EAST);

	/**
	 * Default axis info for latitudes.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#NORTH North} and units are
	 * {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
	 *
	 * <p>This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE}
	 * set.
	 *
	 * @see #GEODETIC_LATITUDE
	 * @see #LONGITUDE
	 */
	public static final CoordinateSystemAxis LATITUDE =
			new CoordinateSystemAxis("LATITUDE", AxisDirection.NORTH);

	/**
	 * The default axis for height values above the ellipsoid in a {@linkplain
	 * org.opengis.referencing.crs.GeographicCRS geographic CRS}.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#UP up} and units are {@linkplain
	 * SI#METER metres}.
	 *
	 * <p>The ISO 19111 name is "<cite>ellipsoidal heigt</cite>" and the abbreviation is lower case
	 * "<var>h</var>".
	 *
	 * <p>This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
	 * {@link #ELLIPSOIDAL_HEIGHT} set.
	 *
	 * @see #ALTITUDE
	 * @see #GEOCENTRIC_RADIUS
	 */
	public static final CoordinateSystemAxis ELLIPSOIDAL_HEIGHT =
			new CoordinateSystemAxis("ELLIPSOIDAL_HEIGHT", AxisDirection.UP);

	/**
	 * The default axis for altitude values.
	 *
	 * <p>Increasing ordinates values go {@linkplain AxisDirection#UP up} and units are {@linkplain
	 * SI#METER metres}.
	 *
	 * <p>The abbreviation is lower case "<var>h</var>".
	 *
	 * <p>This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE}
	 * set.
	 *
	 * @see #ELLIPSOIDAL_HEIGHT
	 * @see #GEOCENTRIC_RADIUS
	 * @see #GRAVITY_RELATED_HEIGHT
	 * @see #DEPTH
	 */
	public static final CoordinateSystemAxis ALTITUDE =
			new CoordinateSystemAxis("ALTITUDE", AxisDirection.UP);

	/**
	 * Constructs an axis with a name as an {@linkplain InternationalString international string}
	 * and an abbreviation. The {@linkplain #getName name of this identified object} is set to the
	 * unlocalized version of the {@code name} argument, as given by <code>
	 * name.{@linkplain InternationalString#toString(Locale) toString}(null)</code>. The same {@code
	 * name} argument is also stored as an {@linkplain #getAlias alias}, which allows fetching
	 * localized versions of the name.
	 *
	 * @param name The name of this axis. Also stored as an alias for localization purpose.
	 * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this coordinate
	 *     system axis.
	 * @param direction The {@linkplain #getDirection direction} of this coordinate system axis.
	 * @param unit The {@linkplain #getUnit unit of measure} used for this coordinate system axis.
	 */
	public CoordinateSystemAxis(
			final String name,
			final AxisDirection direction) {
		this.name = name;
		this.direction = direction;
		this.maximum = Double.MAX_VALUE;
		this.minimum = Double.MIN_VALUE;
	}


	/**
	 * Direction of this coordinate system axis. In the case of Cartesian projected coordinates,
	 * this is the direction of this coordinate system axis locally. Examples: {@linkplain
	 * AxisDirection#NORTH north} or {@linkplain AxisDirection#SOUTH south}, {@linkplain
	 * AxisDirection#EAST east} or {@linkplain AxisDirection#WEST west}, {@linkplain
	 * AxisDirection#UP up} or {@linkplain AxisDirection#DOWN down}.
	 *
	 * <p>Within any set of coordinate system axes, only one of each pair of terms can be used. For
	 * earth-fixed coordinate reference systems, this direction is often approximate and intended to
	 * provide a human interpretable meaning to the axis. When a geodetic datum is used, the precise
	 * directions of the axes may therefore vary slightly from this approximate direction.
	 *
	 * <p>Note that an {@link org.geotools.referencing.crs.DefaultEngineeringCRS} often requires
	 * specific descriptions of the directions of its coordinate system axes.
	 */
	public AxisDirection getDirection() {
		return direction;
	}
	public String getName() { return this.name; }
	/**
	 * Returns the minimum value normally allowed for this axis, in the {@linkplain #getUnit unit of
	 * measure for the axis}. If there is no minimum value, then this method returns {@linkplain
	 * Double#NEGATIVE_INFINITY negative infinity}.
	 *
	 */
	public double getMinimumValue() {
		return minimum;
	}
	public void setMinimumValue(double val) { this.minimum = val; }
	/**
	 * Returns the maximum value normally allowed for this axis, in the {@linkplain #getUnit unit of
	 * measure for the axis}. If there is no maximum value, then this method returns {@linkplain
	 * Double#POSITIVE_INFINITY negative infinity}.
	 *
	 * @since 2.3
	 */
	public double getMaximumValue() {
		return maximum;
	}

	public void setMaximumValue(double val) { this.maximum = val; }


	/**
	 * Returns an axis with the opposite direction of this one, or {@code null} if unknown. This
	 * method is not public because only a few predefined constants have this information.
	 */
	final CoordinateSystemAxis getOpposite() {
		CoordinateSystemAxis opposite = null;
		String name = getName();
		if( direction.equals(AxisDirection.EAST)) opposite = new CoordinateSystemAxis(name,AxisDirection.WEST);
		
		opposite.setMaximumValue(getMaximumValue());
		opposite.setMinimumValue(getMinimumValue());
		return opposite;
	}

	/**
	 * Compares the specified object with this axis for equality, with optional comparaison of
	 * units. Units should always be compared (they are not just metadata), except in the particular
	 * case of {@link AbstractCS#axisColinearWith}, which is used as a first step toward units
	 * conversions through {@link AbstractCS#swapAndScaleAxis}.
	 */
	public boolean equals(final CoordinateSystemAxis that) {

		final String thatName = that.getName();
		if (!thatName.equalsIgnoreCase(name) ) {
			return false;
		} 
		else {
			final AxisDirection dir = that.getDirection();
			return dir.equals(direction);
		}
	}

	/**
	 * Returns a hash value for this axis. This value doesn't need to be the same in past or future
	 * versions of this class.
	 */
	@Override
	@SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
	public int hashCode() {
		int code = (int) serialVersionUID;
		code = code * 37 + direction.hashCode();
		code = code * 37 + name.hashCode();
		return code;
	}

}
