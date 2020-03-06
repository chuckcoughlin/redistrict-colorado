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

import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanDataset;
import redistrict.colorado.core.PlanModel;

/**
 * The PlanDataset table keeps track of aggregated features associated with a given plan.
 * The Database class sets the connection once it is created.
 */
public class PlanDatasetTable {
	private static final String CLSS = "PlanDatasetTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;

	/** 
	 * Constructor: 
	 */
	public PlanDatasetTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Map a new dataset to a plan. The id must be the id of an existing dataset. 
	 */
	public void createPlanDataset(long planId,long datasetId,DatasetRole role) {
		if( cxn==null ) return;
		String SQL = String.format("INSERT INTO PlanDataset(planId,datasetId,role) values (%d,%d,'%s')",
															planId,datasetId,role.name());
		Statement statement = null;
		try {
			LOGGER.info(String.format("%s.createPlanDataset: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createPlanDataset: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	/**
	 * Delete a row given its plan and dataset ids.
	 */
	public boolean deletePlanDataset(long planId,long datasetId) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM PlanDataset WHERE planId= ? AND datasetId = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deletePlanDataset: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setLong(2, datasetId);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deletePlanDataset: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * Delete all datasets for a given plan
	 */
	public boolean deletePlanDatasets(long planId) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM PlanDataset WHERE planId = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deletePlanDatasets: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deletePlanDatasets: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of roles for dataset used by the specified plan. There may be none.
	 */
	public List<PlanDataset> getDatasetRoles(long planId) {
		List<PlanDataset> list = new ArrayList<>();
		PlanDataset planDataset = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT Dataset.id as id,Dataset.name as name,PlanDataset.role as role from Dataset,PlanDataset "+
					" WHERE PlanDataset.planId=? "+
					"   AND PlanDataset.datasetId = Dataset.id ORDER BY name";
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				planDataset = new PlanDataset(planId,rs.getLong("id"));
				planDataset.setName(rs.getString("name"));
				DatasetRole role = DatasetRole.BOUNDARIES;   // Default
				try {
					role = DatasetRole.valueOf(rs.getString("role").toUpperCase());
				}
				catch(IllegalArgumentException ignore) {}
				planDataset.setRole(role);
				LOGGER.info(String.format("%s.getDatasetRoles: %d %d %s %s",CLSS,planId,planDataset.getLayerId(),planDataset.getName(),planDataset.getRole().name()));
				list.add(planDataset);	
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getDatasetRoles: Error %s (%s)",CLSS,SQL,e.getMessage()));
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
	 * @return a list of roles for datasets used by the specified plan. There may be none.
	 */
	public List<PlanDataset> getUnusedDatasetRoles(long planId) {
		List<PlanDataset> list = new ArrayList<>();
		PlanDataset planDataset = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id,name from Dataset "+
					" WHERE id NOT in (SELECT datasetId as id FROM PlanDataset WHERE planId=?)" +
					" ORDER BY name";
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				planDataset = new PlanDataset(planId,rs.getLong("id"));
				planDataset.setName(rs.getString("name"));
				planDataset.setRole(DatasetRole.NONE);
				list.add(planDataset);	
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getUnusedDatasetRoles: Error (%s)",CLSS,e.getMessage()));
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
	 * Clear the database of plan datasets for the specified model, then re-create.
	 * @cxn an open database connection
	 * @param model the plan model
	 */
	public void synchronizePlanDatasets(PlanModel model) {
		LOGGER.info(String.format("%s.synchronizePlanDatasets: plan %d",CLSS,model.getId()));
		// Delete any layers currently associated with the plan
		deletePlanDatasets(model.getId());
		// Create database entries for layers in the mpodel's list
		for(PlanDataset planDataset:model.getLayers()) {
			createPlanDataset(model.getId(),planDataset.getLayerId(),planDataset.getRole());
		}
	}
	/**
	 * Change the role of the named dataset.
	 * @cxn an open database connection
	 * @param config configuration object
	 */
	public boolean updatePlanDataset(long planId,long layerId,DatasetRole role) {
		PreparedStatement statement = null;
		String SQL = "UPDATE PlanDataset SET role=? WHERE planId=? AND datasetId=?";
		boolean success = false;
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,role.name());
			statement.setLong(2,planId);
			statement.setLong(3,planId);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updatePlanDataset: role = %s error (%s)",CLSS,role,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
