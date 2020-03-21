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
import redistrict.colorado.core.PlanModel;

/**
 * A Plan is a named collection of layers. The different layers are given different
 * roles within the plan.
 */
public class PlanTable {
	private static final String CLSS = "PlanTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static String DEFAULT_NAME = "New plan";
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public PlanTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Create a new row. This will fail if there is already a row with the default name.
	 */
	public PlanModel createPlan() {
		PlanModel model = null;
		if( cxn==null ) return model;
		
		String SQL = String.format("INSERT INTO Plan(name,description,active) values ('%s','',0)",DEFAULT_NAME);
		Statement statement = null;
		try {
			LOGGER.info(String.format("%s.createPlan: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
			ResultSet rs = statement.getGeneratedKeys();
		    if (rs.next()) {
		        model = new PlanModel(rs.getInt(1));
		        model.setActive(false);
		        model.setName(DEFAULT_NAME);
		        // Set a default boundary dataset
		        List<DatasetModel> datasets = DatasetCache.getInstance().getDatasetsInRole(DatasetRole.BOUNDARIES);
		        if( datasets.size()>0 ) {
		        	model.setBoundary(datasets.get(0));
		        }
		    } 
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createPlan: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return model;
	}
	/**
	 * Delete a plan given its id. 
	 * NOTE: entries in PlanLayer should be automatically
	 * removed via cascading delete.
	 */
	public boolean deletePlan(long key) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM Plan WHERE id = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deletePlan: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deletePlan: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of all defined Plans. It may be empty.
	 */
	public List<PlanModel> getPlans() {
		List<PlanModel> list = new ArrayList<>();
		PlanModel model = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id, name, description, active FROM Plan"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				long id = rs.getLong("id");
				model = new PlanModel(id);
				model.setBoundary(DatasetCache.getInstance().getDataset(model.getId()));
				model.setActive((rs.getInt("active")==1));
				model.setName(rs.getString("name"));
				model.setDescription(rs.getString("description"));
				list.add(model);
				//LOGGER.info(String.format("%s.getPlans: id = %d",CLSS,model.getId()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getPlans: Error (%s)",CLSS,e.getMessage()));
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
	 * Associate a plan giving it a new name,
	 * description or active flag.
	 * @cxn an open database connection
	 * @param model
	 */
	public boolean updatePlan(PlanModel model) {
		PreparedStatement statement = null;
		String SQL = "UPDATE Plan SET active=?, name=?, description=? WHERE id = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.updatePlan: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setInt(1,(model.isActive()?1:0));
			statement.setString(2, model.getName());
			statement.setString(3, model.getDescription());
			statement.setLong(4, model.getId());
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updatePlan: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
