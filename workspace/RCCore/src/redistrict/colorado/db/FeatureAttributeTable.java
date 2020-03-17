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
import java.util.Random;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;

import javafx.scene.paint.Color;
import redistrict.colorado.core.FeatureConfiguration;

/**
 * The FeatureAttribute table keeps track of the features associated with a given dataset.
 * The Database class sets the connection once it is created.
 */
public class FeatureAttributeTable {
	private static final String CLSS = "FeatureAttributeTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;
	public static final Map<String,String> fieldAliases = new HashMap<>();

	/** 
	 * Constructor: 
	 */
	public FeatureAttributeTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Map a new feature to a dataset. The id must be the id of an existing dataset. The database
	 * stores settings for display.
	 */
	public void createFeatureAttribute(long id,String name,AttributeType type) {
		if( cxn==null ) return;
		// Java 'Color' class takes 3 floats, from 0 to 1.
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		int rgb = 256*256*r+256*g+b;
		String SQL = String.format("INSERT INTO FeatureAttribute(datasetId,name,alias,type,background,rank) values (%d,'%s','%s','%s',%d,10)",
															id,name,name,type.name(),rgb);
		String UPDSQL = String.format("UPDATE FeatureAttribute SET alias = (SELECT alias FROM AttributeAlias WHERE name='%s') WHERE datasetId=%d AND name='%s'",
										name,id,name);
		
		Statement statement = null;
		try {
			//LOGGER.info(String.format("%s.createFeatureAttribute: \n%s",CLSS,SQL));
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
			// This statement attempts to set aliases for common names. It will fail harmlessly if there is no corresponding alias.
			try {
				statement.executeUpdate(UPDSQL);
				LOGGER.info(String.format("%s.createFeatureAttribute: %s updated to alias",CLSS,name));
			}
			catch(SQLException ignore) {
				//LOGGER.info(String.format("%s.createFeatureAttribute: %s has no standard alias",CLSS,name));
			}
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.createFeatureAttribute: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	/**
	 * Delete a row given its dataset id.
	 */
	public boolean deleteFeatureAttribute(long key,String name) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM FeatureAttribute WHERE datasetId = ? and name = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deleteFeatureAttribute: \n%s",CLSS,SQL));
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
	 * The returned map provides for the lookup of feature names given the alias. For a given
	 * layer, the aliases should be unique.
	 * @param key the Id for the relevant layer.
	 * @return a map of feature names by alias.
	 */
	public Map<String,String> getNamesForFeatureAliases(long key) {
		Map<String,String> map = new HashMap<>();
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT name,alias from FeatureAttribute WHERE datasetId=?"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				map.put(rs.getString("alias"),rs.getString("name"));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getNamesForFeatureAliases: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return map;
	}
	/**
	 * The returned configuration list corresponds to Features associated with the layer.
	 * @return a list of all configurations defined for the given Layer. There may be none.
	 */
	public List<FeatureConfiguration> getFeatureAttributes(long key) {
		List<FeatureConfiguration> list = new ArrayList<>();
		FeatureConfiguration configuration = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT name,alias,type,visible,background,rank from FeatureAttribute WHERE datasetId=? ORDER BY rank"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				configuration = new FeatureConfiguration(key,rs.getString("name"));
				configuration.setAlias(rs.getString("alias"));
				configuration.setVisible((rs.getInt("visible")==1?true:false));
				int rgb = rs.getInt("background");
				int r = (rgb & 0xFF0000) >> 16;
	            int g = (rgb & 0xFF00) >> 8;
	            int b = (rgb & 0xFF);
	            //LOGGER.info(String.format("%s.getFeatureAttributes background = %d (%02x%02x%02x)",CLSS,rgb,r,g,b));
				configuration.setBackground(Color.rgb(r,g,b));
				configuration.setRank(rs.getInt("rank"));
				AttributeType type = AttributeType.DOUBLE;   // Default
				try {
					type = AttributeType.valueOf(rs.getString("type"));
				}
				catch(IllegalArgumentException ignore) {}
				configuration.setAttributeType(type);
				list.add(configuration);
				
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
	 * Create or delete feature attributes as necessary so that the database accurately reflects
	 * the schema for the specified dataset.
	 * @cxn an open database connection
	 * @param datasetId the id of the dataset
	 * @param attributes a list of attribute names recently read from the Shapefile
	 * 		  for that dataset
	 */
	public void synchronizeFeatureAttributes(long datasetId,List<String> attributes) {
		LOGGER.info(String.format("%s.synchronizeFeatureAttributes: dataset %d, %d attributes",CLSS,datasetId,attributes.size()));
		// Make a dictionary of features per database
		Map<String,FeatureConfiguration> configMap = new HashMap<>();
		List<FeatureConfiguration> configList = getFeatureAttributes(datasetId);
		for(FeatureConfiguration config: configList) {
			configMap.put(config.getName(), config);
		}
		// Delete any features not in the collection
		for(String key:configMap.keySet()) {
			if(!attributes.contains(key)) {
				//LOGGER.info(String.format("%s.synchronizeFeatureAttributes: delete dataset %d, %s",CLSS,datasetId,key));
				deleteFeatureAttribute(datasetId,key);
			}
		}
		// Create database entries for new features
		for(String name:attributes) {
			if(!configMap.containsKey(name)) {
				//LOGGER.info(String.format("%s.synchronizeFeatureAttributes: create dataset %d, %s",CLSS,datasetId,name));
				createFeatureAttribute(datasetId,name,AttributeType.DOUBLE);
			}
		}
	}
	/**
	 * Change the database attributes of the named layer.
	 * @cxn an open database connection
	 * @param config configuration object
	 */
	public boolean updateFeatureAttribute(FeatureConfiguration config) {
		PreparedStatement statement = null;
		String SQL = "UPDATE FeatureAttribute SET alias=?,type=?,visible=?,background=?,rank=? WHERE datasetId = ? AND name=?";
		boolean success = false;
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,config.getAlias());
			statement.setString(2,config.getAttributeType().name());
			statement.setInt(3,(config.isVisible()?1:0));
			int r = (int)(config.getBackground().getRed()*255);
			int g = (int)(config.getBackground().getGreen()*255);
			int b = (int)(config.getBackground().getBlue()*255);
			int rgb = b + 256*g + 256*256*r;
			statement.setInt(4,rgb);
			//LOGGER.info(String.format("%s.updateFeatureAttribute: background = %d (%02x%02x%02x)",CLSS,rgb,r,g,b));
			statement.setInt(5,config.getRank());
			statement.setLong(6, config.getLayerId());
			statement.setString(7,config.getName());
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateFeatureAttribute: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * Update display characteristics for the features in the supplied list.
	 * @cxn an open database connection
	 * @param config configuration object
	 */
	public boolean updateFeatureAttributes(List<FeatureConfiguration> configs) {
		boolean success = true;
		for(FeatureConfiguration config:configs) {
			success = success && updateFeatureAttribute(config);	
		}
		return success;
	}
}
