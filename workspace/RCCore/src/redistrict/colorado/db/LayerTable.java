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

import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;

/**
 * A Layer is an overlay within a Plan. This class contains convenience methods to query,
 * create and update them. It encapsulates the Layer SQLite table. The Database class 
 * sets the connection once it is created.
 */
public class LayerTable {
	private static final String CLSS = "LayerTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static String DEFAULT_NAME = "New layer";
	private final static String DEFAULT_DESCRIPTION = "";
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public LayerTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Create a new row. If there is already a row called "New layer", a null will be returned.
	 */
	public LayerModel createLayer() {
		LayerModel model = null;
		if( cxn==null ) return model;
		
		String SQL = String.format("INSERT INTO Layer(name,description,shapefilePath,role) values ('%s','%s','','%s')",
				DEFAULT_NAME,DEFAULT_DESCRIPTION,LayerRole.BOUNDARIES.name());
		Statement statement = null;
		try {
			LOGGER.info(String.format("%s.createLayer: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
			ResultSet rs = statement.getGeneratedKeys();
		    if (rs.next()) {
		        model = new LayerModel(rs.getInt(1),DEFAULT_NAME);
		    } 
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return model;
	}
	/**
	 * Delete a layer given its id. 
	 * NOTE: entries in FeatureAttribute should be automatically
	 * removed via cascading delete.
	 */
	public boolean deleteLayer(long key) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM Layer WHERE id = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deleteLayer: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deleteLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of all defined Layers. It may be empty.
	 */
	public List<LayerModel> getLayers() {
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
				//LOGGER.info(String.format("%s.getLayers %d: %s is %s",CLSS,model.getId(),model.getName(),model.getRole().name()));
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
	 * @return a LayerModel that corresponds to a layer in a plan that 
	 * has a specified role.
	 */
	public LayerModel getPlanLayer(long planId,LayerRole role) {
		LayerModel model = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id,name,description,shapefilePath,Layer.role from Layer,PlanLayer "+
		              " WHERE Layer.id = PlanLayer.planId "+
				      "   AND planLayer.planId = ? " +
		              "   AND planLayer.role = ?"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			statement.setLong(1, planId);
			statement.setString(2,role.name());
			rs = statement.executeQuery();
			// There should only be one result.
			while(rs.next()) {
				model = new LayerModel(
							rs.getLong("id"),
							rs.getString("name")
						);
				model.setDescription(rs.getString("description"));
				model.setShapefilePath(rs.getString("shapefilePath"));
				model.setRole(role);
				//LOGGER.info(String.format("%s.getLayers %d: %s is %s",CLSS,model.getId(),model.getName(),model.getRole().name()));
				break;
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getPlanLayer: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return model;
	}
	/**
	 * Associate a layer giving it a new name
	 * it will be updated.
	 * @cxn an open database connection
	 * @param oldName
	 * @param newName
	 */
	public boolean updateLayer(LayerModel model) {
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
			LOGGER.severe(String.format("%s.updateLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
