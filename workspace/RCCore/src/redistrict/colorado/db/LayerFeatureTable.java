/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;

/**
 * The LayerFeature table keeps track of the features associated with a given layer.
 * The Database class sets the connection once it is created.
 */
public class LayerFeatureTable {
	private static final String CLSS = "LayerFeatureTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public LayerFeatureTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	
	/**
	 * Map a new feature to a layer. The id must be the id of an existing layer.
	 */
	public void createLayerFeature(int id,String name,AttributeType type) {
		if( cxn==null ) return;
		
		String SQL = String.format("INSERT INTO LayerFeature(featureAlias,featureName,type) values (%d,'%s','%s','','%s')",
															id,name,name,type.name());
		Statement statement = null;
		try {
			LOGGER.info(String.format("%s.createLayerFeature: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createLayerFeature: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	/**
	 * Delete a row given its layer id.
	 */
	public boolean deleteLayerFeature(long key,String name) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM LayerFeature WHERE layerId = ? and featureAlias = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deleteLayerFeature: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.setString(2, name);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deleteLayerName: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of all features defined for the given Layer. There may be none.
	 */
	public List<LayerModel> getLayerFeatures() {
		List<LayerModel> list = new ArrayList<>();
		LayerModel model = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id,name,description,shapefilePath,role from Layer ORDER BY name"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				model = new LayerModel(
							rs.getLong("id"),
							rs.getString("name")
						);
				model.setDescription(rs.getString("description"));
				model.setShapefilePath(rs.getString("shapefilePath"));
				LayerRole role = LayerRole.BOUNDARIES;   // Default
				try {
					role = LayerRole.valueOf(rs.getString("role"));
				}
				catch(IllegalArgumentException ignore) {}
				model.setRole(role);
				list.add(model);
				LOGGER.info(String.format("%s.getLayers %d: %s is %s",CLSS,model.getId(),model.getName(),model.getRole().name()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getLayers: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return list;
	}
	

	/**
	 * Associate a layer giving it a new name
	 * it will be updated.
	 * @cxn an open database connection
	 * @param oldName
	 * @param newName
	 */
	public boolean updateLayerFeature(LayerModel model) {
		PreparedStatement statement = null;
		String SQL = "UPDATE Layer SET name=?,description=?,shapefilePath=?,role=? WHERE id = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.updateLayer: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,model.getName());
			statement.setString(2,model.getDescription());
			statement.setString(3,model.getShapefilePath());
			statement.setString(4,model.getRole().name());
			statement.setLong(5, model.getId());
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateLayerName: updateLayerName error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
