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

import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;

/**
 * A Dataset is a component a Plan based on a specified shapefile. This class contains
 * convenience methods to query,create and update them. It encapsulates the Dataset SQLite 
 * table. The Database class sets the connection once it is created.
 */
public class DatasetTable {
	private static final String CLSS = "DatasetTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final static String DEFAULT_NAME = "New dataset";
	private final static String DEFAULT_DESCRIPTION = "";
	private Connection cxn = null;
	private final DatasetCache cache = DatasetCache.getInstance();
	/** 
	 * Constructor: 
	 */
	public DatasetTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Create a new row. If there is already a row called "New dataset", a null will be returned.
	 */
	public DatasetModel createDataset() {
		DatasetModel model = null;
		if( cxn==null ) return model;
		
		String SQL = String.format("INSERT INTO Dataset(name,description,shapefilePath,role) values ('%s','%s','','%s')",
				DEFAULT_NAME,DEFAULT_DESCRIPTION,DatasetRole.BOUNDARIES.name());
		Statement statement = null;
		try {
			//LOGGER.info(String.format("%s.createDataset: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
			ResultSet rs = statement.getGeneratedKeys();
		    if (rs.next()) {
		        model = new DatasetModel(rs.getInt(1),DEFAULT_NAME);
		        cache.addDataset(model);
		    } 
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createDataset: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return model;
	}
	/**
	 * Delete a dataset given its id. 
	 * NOTE: entries in PlanDataset table should be automatically
	 * removed via cascading delete.
	 */
	public boolean deleteDataset(long key) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM Dataset WHERE id = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deleteDataset: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
			cache.removeDataset(key);
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deleteDataset: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of all defined datasets. It may be empty.
	 */
	public List<DatasetModel> getDatasets() {
		List<DatasetModel> list = new ArrayList<>();
		DatasetModel model = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id,name,description,shapefilePath,role from Dataset ORDER BY name"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				long id = rs.getLong("id");
				model = cache.getDataset(id);
				if( model==null  ) {
					model = new DatasetModel(id,rs.getString("name"));
					model.setDescription(rs.getString("description"));
					model.setShapefilePath(rs.getString("shapefilePath"));
					DatasetRole role = DatasetRole.BOUNDARIES;   // Default
					try {
						role = DatasetRole.valueOf(rs.getString("role"));
					}
					catch(IllegalArgumentException ignore) {}
					model.setRole(role);
					cache.addDataset(model);
				}
				list.add(model);
				//LOGGER.info(String.format("%s.getDatasets %d: %s is %s",CLSS,model.getId(),model.getName(),model.getRole().name()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getDatasets: Error (%s)",CLSS,e.getMessage()));
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
	 * @return a list of defined datasets that are assigned to the specified role.
	 *         If the model object does not exist, create it.
	 */
	public List<String> getDatasetNamesForRole(DatasetRole role) {
		List<String> list = new ArrayList<>();
		DatasetModel model = null;
		Statement statement = null;
		ResultSet rs = null;
		String SQL = String.format(
				"SELECT id,name,description,shapefilePath from Dataset WHERE role ='%s' ORDER BY name",role.name()); 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			//LOGGER.info(String.format("%s.getDatasetNamesForRole: %s\n%s",CLSS,role.name(),SQL));
			while(rs.next()) {
				long id = rs.getLong("id");
				model = cache.getDataset(id);
				if( model==null  ) {
					model = new DatasetModel(id,rs.getString("name"));
					model.setDescription(rs.getString("description"));
					model.setShapefilePath(rs.getString("shapefilePath"));
					model.setRole(role);
					cache.addDataset(model);
				}
				list.add(model.getName());
				LOGGER.info(String.format("%s.getDatasetNamesForRole %d: %s is %s",CLSS,model.getId(),model.getName(),model.getRole().name()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getDatasetNamesForRole: Error (%s)",CLSS,e.getMessage()));
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
	 * Update the database from a model object.
	 * @cxn an open database connection
	 * @param oldName
	 * @param newName
	 */
	public boolean updateDataset(DatasetModel model) {
		PreparedStatement statement = null;
		String SQL = "UPDATE Dataset SET name=?,description=?,shapefilePath=?,role=? WHERE id = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.updateDataset: \n%s",CLSS,SQL));
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
			LOGGER.severe(String.format("%s.updateDataset: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
