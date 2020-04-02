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
import java.util.Random;
import java.util.logging.Logger;

import javafx.scene.paint.Color;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;

/**
 * A Plan is a named re-districting strategy based on a boundary dataset. Evaluation of a plan
 * is dependent on datasets from the AnalysisModel that are aggregated into the districts of the plan.
 * These "metrics" are cached in the PlanFeature table and are saved/stored in separate methods of
 * this class.
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
	 * Delete plan metrics given its id. These values are cached from an aggregation step
	 * and so must be cleared whenever the underlying datasets are changed.
	 */
	public boolean clearMetrics(long key) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM PlanFeature WHERE planId = ?";
		boolean success = false;
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.clearMetrics: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * Create a new row. This will fail if there is already a row with the default name.
	 * Initially, the metrics are empty, the boundaryId is null.
	 */
	public PlanModel createPlan() {
		PlanModel model = null;
		if( cxn==null ) return model;
		// Create a random color for the bar that represents this plan
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		int rgb = 256*256*r+256*g+b;

		String SQL = String.format("INSERT INTO Plan(name,description,fill,active) values ('%s','',%d,0)",DEFAULT_NAME,rgb);
		Statement statement = null;
		try {
			LOGGER.info(String.format("%s.createPlan: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
			ResultSet rs = statement.getGeneratedKeys();
		    if (rs.next()) {
		        model = new PlanModel(rs.getLong(1));
		        model.setActive(false);
		        model.setName(DEFAULT_NAME);
		        model.setFill(Color.rgb(r,g,b));
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
	 * NOTE: entries in PlanFeature will be automatically
	 * removed via cascading delete.
	 */
	public boolean deletePlan(long key) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM Plan WHERE id = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.deletePlan: \n%s",CLSS,SQL));
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
	 * Refresh a plan model with metrics cached in the database.
	 * Any existing metrics are cleared.
	 */
	public void getMetrics(PlanModel plan) {
		plan.setMetrics(null);
		//LOGGER.info(String.format("%s.getMetrics: plan %d = %s",CLSS,plan.getId(),plan.getName()));
		List<PlanFeature> list = new ArrayList<>();
		Statement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT featureId,name,area,perimeter,population,democrat,republican,black,hispanic,white "+
					  String.format("FROM PlanFeature WHERE planId=%d",plan.getId()); 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			while(rs.next()) {
				long fid = rs.getLong("featureId");
				PlanFeature pfeat = new PlanFeature(plan.getId(),fid);;
				pfeat.setName(rs.getString("name"));
				pfeat.setArea(rs.getDouble("area"));
				pfeat.setPerimeter(rs.getDouble("perimeter"));
				pfeat.setPopulation(rs.getDouble("population"));
				pfeat.setDemocrat(rs.getDouble("democrat"));
				pfeat.setRepublican(rs.getDouble("republican"));
				pfeat.setBlack(rs.getDouble("black"));
				pfeat.setHispanic(rs.getDouble("hispanic"));
				pfeat.setWhite(rs.getDouble("white"));
				list.add(pfeat);
				//LOGGER.info(String.format("%s.getMetrics for %d: name = %s",CLSS,fid,pfeat.getName()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getMetrics: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		plan.setMetrics(list);
	}
	/**
	 * @return a list of all defined Plans. It may be empty.
	 * 			The plan metrics, if any, are refreshed from the database.
	 * 			Since this creates plan instances, it should only be called
	 * 			when the plan list is originally populated.
	 */
	public List<PlanModel> getPlans() {
		List<PlanModel> list = new ArrayList<>();
		PlanModel model = null;
		Statement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id, name, description, boundaryId, fill, active FROM Plan ORDER BY name"; 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			while(rs.next()) {
				long id = rs.getLong("id");
				model = new PlanModel(id);
				model.setBoundary(DatasetCache.getInstance().getDataset(model.getId()));
				model.setActive((rs.getInt("active")==1));
				model.setName(rs.getString("name"));
				model.setDescription(rs.getString("description"));
				int rgb = rs.getInt("fill");
				int r = (rgb & 0xFF0000) >> 16;
	            int g = (rgb & 0xFF00) >> 8;
	            int b = (rgb & 0xFF);
				model.setFill(Color.rgb(r,g,b));
				long boundaryId = rs.getLong("boundaryId");
				DatasetModel boundaryModel = DatasetCache.getInstance().getDataset(boundaryId);
				if( boundaryModel!=null ) model.setBoundary(boundaryModel);
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
		for(PlanModel plan:list) {
			getMetrics(plan);
		}
		return list;
	}

	/**
	 * Update the database for a plan giving it a new name,
	 * description or active flag. 
	 * @cxn an open database connection
	 * @param model
	 */
	public boolean updatePlan(PlanModel model) {
		PreparedStatement statement = null;
		String SQL = "UPDATE Plan SET active=?, name=?, description=?, boundaryId=?, fill=? WHERE id = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.updatePlan: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setInt(1,(model.isActive()?1:0));
			statement.setString(2, model.getName());
			statement.setString(3, model.getDescription());
			statement.setLong(4, model.getBoundary().getId());
			int r = (int)(model.getFill().getRed()*255);
			int g = (int)(model.getFill().getGreen()*255);
			int b = (int)(model.getFill().getBlue()*255);
			int rgb = b + 256*g + 256*256*r;
			statement.setInt(5, rgb);
			statement.setLong(6, model.getId());
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
	
	/**
	 * Completely replace PlanFeatures for a plan.
	 * @cxn an open database connection
	 * @param model
	 */
	public boolean updatePlanMetrics(PlanModel model) {
		clearMetrics(model.getId());
		PreparedStatement statement = null;
		String SQL = "INSERT INTO PlanFeature(planId,featureId,name,area,perimeter,population,democrat,republican,black,hispanic,white)"+
					 " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		boolean success = true;
		try {
			statement = cxn.prepareStatement(SQL);
			long featureId = 0;
			for(PlanFeature pfeat:model.getMetrics()) {
				//LOGGER.info(String.format("%s.updatePlan: \n%s",CLSS,SQL));		
				statement.setLong(1,model.getId());
				statement.setLong(2,featureId);
				statement.setString(3, pfeat.getName());
				statement.setDouble(4,pfeat.getArea());
				statement.setDouble(5, pfeat.getPerimeter());
				statement.setDouble(6, pfeat.getPopulation());
				statement.setDouble(7, pfeat.getDemocrat());
				statement.setDouble(8, pfeat.getRepublican());
				statement.setDouble(9, pfeat.getBlack());
				statement.setDouble(10, pfeat.getHispanic());
				statement.setDouble(11, pfeat.getWhite());
				statement.executeUpdate();
				featureId++;
			}
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updatePlanMetrics: error (%s)",CLSS,e.getMessage()));
			success = false;
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
