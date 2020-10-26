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
package org.geotools.operation;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.geotools.operation.matrix.Matrix;

/**
 * A factory for creating {@linkplain MathTransform math transforms}. Static methods are provided 
 * for the create() methods.
 *
 * <p>A {@linkplain MathTransform math transform} is an object that actually does the work of
 * applying formulae to coordinate values. The math transform does not know or care how the
 * coordinates relate to positions in the real world. This lack of semantics makes implementing
 * {@code MathTransformFactory} significantly easier than it would be otherwise.
 *
 * <p>For example the affine transform applies a matrix to the coordinates without knowing how what
 * it is doing relates to the real world. So if the matrix scales <var>Z</var> values by a factor of
 * 1000, then it could be converting meters into millimeters, or it could be converting kilometers
 * into meters.
 *
 * <p>Because {@linkplain MathTransform math transforms} have low semantic value (but high
 * mathematical value), programmers who do not have much knowledge of how GIS applications use
 * coordinate systems, or how those coordinate systems relate to the real world can implement {@code
 * MathTransformFactory}. The low semantic content of {@linkplain MathTransform math transforms}
 * also means that they will be useful in applications that have nothing to do with GIS coordinates.
 * For example, a math transform could be used to map color coordinates between different color
 * spaces, such as converting (red, green, blue) colors into (hue, light, saturation) colors.
 *
 * <p>Since a {@linkplain MathTransform math transform} does not know what its source and target
 * coordinate systems mean, it is not necessary or desirable for a math transform object to keep
 * information on its source and target coordinate systems.
 *
 * @since 2.1
 * @author Martin Desruisseaux (IRD)
 */
public class MathTransformFactory  {

    /**
     * Creates an affine transform from a matrix. If the transform's input dimension is {@code M},
     * and output dimension is {@code N}, then the matrix will have size {@code [N+1][M+1]}. The +1
     * in the matrix dimensions allows the matrix to do a shift, as well as a rotation. The {@code
     * [M][j]} element of the matrix will be the j'th ordinate of the moved origin. The {@code
     * [i][N]} element of the matrix will be 0 for <var>i</var> less than {@code M}, and 1 for
     * <var>i</var> equals {@code M}.
     *
     * @param matrix The matrix used to define the affine transform.
     * @return The affine transform.
     * @throws FactoryException if the object creation failed.
     */
    public static MathTransform createAffineTransform(final Matrix matrix)  {
        return new ProjectiveTransform(matrix);
    }
    
    /**
     * Creates a transform by concatenating two existing transforms. A concatenated transform acts
     * in the same way as applying two transforms, one after the other.
     *
     * <p>The dimension of the output space of the first transform must match the dimension of the
     * input space in the second transform. If you wish to concatenate more than two transforms,
     * then you can repeatedly use this method.
     *
     * @param transform1 The first transform to apply to points.
     * @param transform2 The second transform to apply to points.
     * @return The concatenated transform.
     * @throws FactoryException if the object creation failed.
     */
    public static MathTransform createConcatenatedTransform(
            final MathTransform transform1, final MathTransform transform2) {
        MathTransform tr = new  ConcatenatedTransform(transform1, transform2);
        return tr;
    }


    /**
     * Creates a transform from a group of parameters. The method name is inferred from the
     * {@linkplain org.opengis.parameter.ParameterDescriptorGroup#getName parameter group name}.
     * Example:
     *
     * <blockquote> <pre>
     * ParameterValueGroup p = factory.getDefaultParameters("Transverse_Mercator");
     * p.parameter("semi_major").setValue(6378137.000);
     * p.parameter("semi_minor").setValue(6356752.314);
     * MathTransform mt = factory.createParameterizedTransform(p);
     * </pre> </blockquote>
     *
     * @param parameters The parameter values.
     * @return The parameterized transform.
     * @throws NoSuchIdentifierException if there is no transform registered for the method.
     * @throws FactoryException if the object creation failed. This exception is thrown if some
     *     required parameter has not been supplied, or has illegal value.
     * @see #getDefaultParameters
     * @see #getAvailableMethods
     * @see #getLastUsedMethod
     */
    public static MathTransform createParameterizedTransform(String classification, Map<String,Object> properties ) {
    	MathTransform transform;

    	final MathTransformProvider provider = getProvider(classification);
    	method = provider;
    	try {

    		transform = provider.createMathTransform(properties);
    	} catch (IllegalArgumentException exception) {
    		/*
    		 * Catch only exceptions which may be the result of improper parameter
    		 * usage (e.g. a value out of range). Do not catch exception caused by
    		 * programming errors (e.g. null pointer exception).
    		 */
    		throw new FactoryException(exception);
    	}
    	if (transform instanceof MathTransformProvider.Delegate) {
    		final MathTransformProvider.Delegate delegate =
    				(MathTransformProvider.Delegate) transform;
    		method = delegate.method;
    		transform = delegate.transform;
    	}
    	transform = pool.unique(transform);

    	return transform;
    }

    /**
     * Creates a transform which passes through a subset of ordinates to another transform. This
     * allows transforms to operate on a subset of ordinates. For example, if you have
     * (<var>Lat</var>,<var>Lon</var>,<var>Height</var>) coordinates, then you may wish to convert
     * the height values from meters to feet without affecting the (<var>Lat</var>,<var>Lon</var>)
     * values.
     *
     * @param firstAffectedOrdinate The lowest index of the affected ordinates.
     * @param subTransform Transform to use for affected ordinates.
     * @param numTrailingOrdinates Number of trailing ordinates to pass through. Affected ordinates
     *     will range from {@code firstAffectedOrdinate} inclusive to {@code
     *     dimTarget-numTrailingOrdinates} exclusive.
     * @return A pass through transform with the following dimensions:<br>
     *     <pre>
     * Source: firstAffectedOrdinate + subTransform.getSourceDimensions() + numTrailingOrdinates
     * Target: firstAffectedOrdinate + subTransform.getTargetDimensions() + numTrailingOrdinates
     *     </pre>
     *
     * @throws FactoryException if the object creation failed.
     */
    public static MathTransform createPassThroughTransform(
            final int firstAffectedOrdinate,
            final MathTransform subTransform,
            final int numTrailingOrdinates) {
        MathTransform tr =new PassThroughTransform(
                            firstAffectedOrdinate, subTransform, numTrailingOrdinates);

        return tr;
    }
    
    
}
