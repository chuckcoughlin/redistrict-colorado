package org.openjump.geometry.feature;

import java.util.logging.Logger;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.openjump.util.GeometryUtils;

/**
 *  a FlexibleFeatureSchema originally used by the GeoJSON reader.
 *  extends the basic {@link FeatureSchema} by
 *  - allow changing attrib types on the fly
 *  - creates empty geoms matching a previous set geomType
 */
public class FlexibleFeatureSchema extends FeatureSchema {
	private static final long serialVersionUID = -5320927515483523031L;
	private static final String CLSS = "FlexibleFeatureSchema";
	private static final Logger LOGGER = Logger.getLogger(CLSS); 
	Class geometryClass = null;
	GeometryFactory geometryFactory = new GeometryFactory();

	public FlexibleFeatureSchema() {
	}

	public FlexibleFeatureSchema(FeatureSchema featureSchema) {
		super(featureSchema);
	}

	public void setAttributeType(int attributeIndex, AttributeType type) {
		attributeTypes.set(attributeIndex, type);
	}

	public void setAttributeType(String name, AttributeType type) {
		setAttributeType(super.getAttributeIndex(name), type);
	}

	public void setGeometryType(Class clazz) {
		geometryClass = clazz;
	}

	public Class getGeometryType() {
		return geometryClass;
	}

	/**
	 * creates an empty geometry matching the geom type set already or an empty
	 * geom collection if that fails
	 * 
	 * @return geometry
	 */
	public Geometry createEmptyGeometry() {
		if (geometryClass != null) {
			try {
				return GeometryUtils
						.createEmptyGeometry(geometryClass, geometryFactory);
			} catch (Exception e) {
				LOGGER.severe(String.format("%s.createEmptyGeometry: ERROR (%s)",CLSS,e.getLocalizedMessage()));
			}
		}

		return geometryFactory.createGeometryCollection(null);
	}

}
