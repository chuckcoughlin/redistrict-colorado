/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.opengis.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * List of codes that may be used to identify the interpolation mechanisms. As a code list, there is
 * no intention of limiting the potential values of {@code CurveInterpolation}. Subtypes of {@link
 * CurveSegment} can be spawned directly through subclassing, or indirectly by specifying an
 * interpolation method and an associated control parameters record to support it.
 */
public enum CurveInterpolation
{
	LINEAR,
	GEODESIC,
	CIRCULAR_ARC_3_POINTS,
	CIRCULAR_ARC_2_POINTS_WITH_BULGE,
	ELLIPTICAL,
	CLOTHOID,
	CONIC,
	POLYNOMIAL_SPLINE,
	CUBIC_SPLINE,
	RATIONAL_SPLINE
	;

	/**
	 * @return interpolation method names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (CurveInterpolation role : CurveInterpolation.values())
		{
			names.add(role.name());
		}
		return names;
	}
}
