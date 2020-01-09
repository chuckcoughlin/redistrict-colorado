/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.awt.Color;
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
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureConstants;

/**
 * The LayerFeature table keeps track of the features associated with a given layer.
 * The Database class sets the connection once it is created.
 */
public class LayerFeatureTable {
	private static final String CLSS = "LayerFeatureTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;
	public static final Map<String,String> fieldAliases = new HashMap<>();
	// Create a map of Common names for the various common field designamtions
	static {
		fieldAliases.put("COUNTYFP", "COUNTY");  // 3 character county FIPS code
		fieldAliases.put("NAME", "GEO NAME");    // Geographic name
		fieldAliases.put("NAMELSAD", "NAME");    // Legal statistical area description
		fieldAliases.put("STATEFP", "STATE");    // 2 character state code
	}
	/** 
	 * Constructor: 
	 */
	public LayerFeatureTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Map a new feature to a layer. The id must be the id of an existing layer. The database
	 * stores settings for display.
	 */
	public void createLayerFeature(long id,String name,AttributeType type) {
		if( cxn==null ) return;
		// Java 'Color' class takes 3 floats, from 0 to 1.
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		String alias = fieldAliases.get(name);
		if( alias==null ) alias = name;
		String SQL = String.format("INSERT INTO LayerFeature(layerId,name,alias,type,background) values (%d,'%s','%s','%s',%d)",
															id,name,alias,type.name(),new Color(r,g,b).getRGB());
		Statement statement = null;
		try {
			//LOGGER.info(String.format("%s.createLayerFeature: \n%s",CLSS,SQL));
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
		String SQL = "DELETE FROM LayerFeature WHERE layerId = ? and name = ?";
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
	 * The returned configuration list corresponds to Features associated with the layer.
	 * @return a list of all configurations defined for the given Layer. There may be none.
	 */
	public List<FeatureConfiguration> getLayerFeatures(long key) {
		List<FeatureConfiguration> list = new ArrayList<>();
		FeatureConfiguration configuration = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT name,alias,type,visible,background,rank from LayerFeature WHERE layerId=? ORDER BY rank"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setLong(1, key);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery();
			while(rs.next()) {
				configuration = new FeatureConfiguration(key,rs.getString("name"));
				configuration.setVisible((rs.getInt("visible")==1?true:false));
				configuration.setBackground(new Color(rs.getInt("background")));
				configuration.setRank(rs.getInt("rank"));
				AttributeType type = AttributeType.DOUBLE;   // Default
				try {
					type = AttributeType.valueOf(rs.getString("type"));
				}
				catch(IllegalArgumentException ignore) {}
				configuration.setAttributeType(type);
				list.add(configuration);
				//LOGGER.info(String.format("%s.getLayerFeatures for %d: %s (%s)",CLSS,key,configuration.getName(),configuration.getAlias()));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getLayerFeatures: Error (%s)",CLSS,e.getMessage()));
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
	 * the schema for the specified layer.
	 * @cxn an open database connection
	 * @param layerId the layer id
	 * @param attributes a list of attribute names recently read from the Shapefile
	 * 		  for that layer
	 */
	public void synchronizeLayerFeatures(long layerId,List<String> attributes) {
		LOGGER.info(String.format("%s.synchronizeLayerFeatures: layer %d, %d attributes",CLSS,layerId,attributes.size()));
		// Make a dictionary of features per database
		Map<String,FeatureConfiguration> configMap = new HashMap<>();
		List<FeatureConfiguration> configList = getLayerFeatures(layerId);
		for(FeatureConfiguration config: configList) {
			configMap.put(config.getName(), config);
		}
		// Delete any features not in the collection
		for(String key:configMap.keySet()) {
			if(!attributes.contains(key)) {
				LOGGER.info(String.format("%s.synchronizeLayerFeatures: delete layer %d, %s",CLSS,layerId,key));
				deleteLayerFeature(layerId,key);
			}
		}
		// Create database entries for new features
		for(String name:attributes) {
			if(!configMap.containsKey(name)) {
				LOGGER.info(String.format("%s.synchronizeLayerFeatures: create layer %d, %s",CLSS,layerId,name));
				createLayerFeature(layerId,name,AttributeType.DOUBLE);
			}
		}
	}
	/**
	 * Change the database attributes of the named layer.
	 * @cxn an open database connection
	 * @param config configuration object
	 */
	public boolean updateLayerFeature(FeatureConfiguration config) {
		PreparedStatement statement = null;
		String SQL = "UPDATE LayerFeature SET alias=?,type=?,visible=?,background=?,rank=? WHERE layerId = ? AND name=?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.updateLayerFeature: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,config.getAlias());
			statement.setString(2,config.getAttributeType().name());
			statement.setInt(3,(config.isVisible()?1:0));
			statement.setInt(4,config.getBackground().getRGB());
			statement.setInt(5,config.getRank());
			statement.setLong(6, config.getLayerId());
			statement.setString(7,config.getName());
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateLayerFeature: updateLayerName error (%s)",CLSS,e.getMessage()));
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
	public boolean updateLayerFeatures(List<FeatureConfiguration> configs) {
		boolean success = true;
		for(FeatureConfiguration config:configs) {
			success = success && updateLayerFeature(config);	
		}
		return success;
	}
}
