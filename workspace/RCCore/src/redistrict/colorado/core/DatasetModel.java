/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.openjump.feature.FeatureCollection;

import redistrict.colorado.db.Database;

/**
 * A dataset powers an overlay on the map defined by a Shapefile. 
 * The features are always read from the file, never the database. 
 */
public class DatasetModel  {
	private final static String CLSS = "DatasetModel";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final long id;
	private String name;
	private String description;
	private String shapefilePath;
	private DatasetRole role;
	private String districtColumn;
	private FeatureCollection features;
	
	public DatasetModel(long id,String nam) {
		this.id = id;
		this.name = nam;
		this.description = "";
		this.shapefilePath = "";
		this.role = DatasetRole.BOUNDARIES;
		this.districtColumn = null;  // By default we do not aggregate
		this.features = null;
	}
	
	public long getId() { return this.id; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getShapefilePath() { return this.shapefilePath; }
	public DatasetRole getRole() { return this.role; }
	public String getDistrictColumn() { return this.districtColumn; }
	/**
	 * As a way of lazy initialization, read from the shapefile when features
	 * are currently null.
	 * @return the layer's features as a collection
	 */
	public FeatureCollection getFeatures() { 
		if(features==null && shapefilePath!=null && !shapefilePath.isEmpty() ) {
			String idColumn = Database.getInstance().getAttributeAliasTable().nameForAlias(id, StandardAttributes.ID.name());
			try {
				FeatureCollection fc = ShapefileReader.read(shapefilePath,idColumn,districtColumn);
				setFeatures(fc);
				if( fc!=null) {
					Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(id, features.getFeatureSchema().getAttributeNames());
				}
				else {
					String msg = String.format("%s: Failed to parse shapefile %s (No features found)",CLSS,shapefilePath);
					LOGGER.warning(msg);
				}
			}
			catch( Exception ex) {
				String msg = String.format("%s: Failed to parse shapefile %s (%s)",CLSS,shapefilePath,ex.getLocalizedMessage());
				LOGGER.warning(msg);
			}
			
		}
		return this.features; 
	}
	public void setName(String nam) { this.name = nam; }
	public void setDescription(String desc) { this.description = desc; }
	public void setShapefilePath(String path) { this.shapefilePath = path; }
	public void setRole(DatasetRole r) { this.role = r; }
	public void setDistrictColumn(String att) { this.districtColumn = att; }
	public void setFeatures(FeatureCollection fc) { this.features = fc; }
	
	/**
	 * Make comparable for use with the cache.
	 */
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatasetModel that = (DatasetModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int)(id&0XFFF);
    }
}
