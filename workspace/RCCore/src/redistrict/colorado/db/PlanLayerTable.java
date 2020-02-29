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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import javafx.scene.paint.Color;
import redistrict.colorado.core.FeatureConfiguration;
import redistrict.colorado.core.LayerRole;
import redistrict.colorado.core.PlanLayer;

/**
 * The FeatureAttribute table keeps track of the features associated with a given layer.
 * The Database class sets the connection once it is created.
 */
public class PlanLayerTable {
	private static final String CLSS = "PlanLayerTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;

	/** 
	 * Constructor: 
	 */
	public PlanLayerTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Map a new feature to a layer. The id must be the id of an existing layer. The database
	 * stores settings for display.
	 */
	public void createPlanLayer(long planId,long layerId,LayerRole role) {
		if( cxn==null ) return;
		String SQL = String.format("INSERT INTO PlanLayer(planId,layerId,role) values (%d,%d,'%s')",
															planId,layerId,role.name());
		Statement statement = null;
		try {
			//LOGGER.info(String.format("%s.createPlanLayer: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createPlanLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	/**
	 * Delete a row given its plan and layer ids.
	 */
	public boolean deletePlanLayer(long planId,long layerId) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM PlanLayer WHERE planId= ? AND layerId = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deletePlanLayer: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setLong(2, layerId);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deletePlanLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a list of roles for layers used by the specified plan. There may be none.
	 */
	public List<PlanLayer> getLayerRoles(long planId) {
		List<PlanLayer> list = new ArrayList<>();
		PlanLayer planLayer = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT Layer.id as id,Layer.name as name,PlanLayer.role as role from Layer,PlanLayer "+
					" WHERE PlanLayer.planId=? "+
					"   AND PlanLayer.layerId = Layer.layerId ORDER BY name";
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				planLayer = new PlanLayer(planId,rs.getLong("id"));
				planLayer.setName(rs.getString("name"));
				LayerRole role = LayerRole.BOUNDARIES;   // Default
				try {
					role = LayerRole.valueOf(rs.getString("role").toUpperCase());
				}
				catch(IllegalArgumentException ignore) {}
				planLayer.setRole(role);
				list.add(planLayer);	
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getFeatureAttributes: Error (%s)",CLSS,e.getMessage()));
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
	 * @return a list of roles for layers used by the specified plan. There may be none.
	 */
	public List<PlanLayer> getUnusedLayerRoles(long planId) {
		List<PlanLayer> list = new ArrayList<>();
		PlanLayer planLayer = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT id,name from Layer "+
					" WHERE id NOT in (SELECT layerId as id FROM PlanLayer WHERE planId=?)" +
					" ORDER BY name";
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, planId);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				planLayer = new PlanLayer(planId,rs.getLong("id"));
				planLayer.setName(rs.getString("name"));
				planLayer.setRole(LayerRole.NONE);
				list.add(planLayer);	
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getFeatureAttributes: Error (%s)",CLSS,e.getMessage()));
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
	 * Create or delete layer references as necessary in the plan layer table so that
	 * the entries accurately reflect the currently defined layers.
	 * @cxn an open database connection
	 * @param layerId the layer id
	 */
	public void synchronizePlanLayers(long planId) {
		LOGGER.info(String.format("%s.synchronizePlanLayers: plan %d",CLSS,planId));
		// Make a dictionary of plan layers per database
		Map<Long,PlanLayer> layerMap = new HashMap<>();
		List<PlanLayer> list = getLayerRoles(planId);
		for(PlanLayer planLayer: list) {
			layerMap.put(planLayer.getLayerId(), planLayer);
		}
		// Delete any layers not in the collection
		for(Long key:layerMap.keySet()) {
			if(!list.contains(key)) {
				LOGGER.info(String.format("%s.synchronizePlanLayers: delete layer %d, %s",CLSS,key));
				deletePlanLayer(planId,key);
			}
		}
		// Create database entries for new layers
		for(PlanLayer planLayer:list) {
			if(!layerMap.containsKey(planLayer.getLayerId())) {
				LOGGER.info(String.format("%s.synchronizeFeatureAttributes: create layer %d, %s",CLSS,planLayer.getLayerId()));
				createPlanLayer(planId,planLayer.getLayerId(),LayerRole.NONE);
			}
		}
	}
	/**
	 * Change the database attributes of the named layer.
	 * @cxn an open database connection
	 * @param config configuration object
	 */
	public boolean updatePlanLayer(long planId,long layerId,LayerRole role) {
		PreparedStatement statement = null;
		String SQL = "UPDATE PlanLayer SET role=? WHERE planId=? AND layerId=?";
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
			LOGGER.severe(String.format("%s.updatePlanLayer: role = %s error (%s)",CLSS,role,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
