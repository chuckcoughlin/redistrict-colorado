/**
 * Copyright 2019. Charles Coughlin. All Rights Reserved.
 *                 MIT License.
 *
 */
package redistrict.colorado.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A pose is a list of positions for each motor. There are up to
 * three rows in the database for each pose. A row each for:
 * 		position, speed and torque
 * This class serves as a Java interface to the Pose and PoseMap tables. It provides 
 * methods for finding and reading a pose
 */
public class LayerConfigurationTable {
	private static final String CLSS = "PoseTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	/** 
	 * Constructor: 
	 */
	public LayerConfigurationTable() {

	}
	
	/**
	 * List the names of all defined Layers.
	 * @cxn an open database connection
	 * @return the corresponding list. It may be empty.
	 */
	public Map<String,String> getAttributesForLayer(Connection cxn,String key) {
		Map<String,String> map = new HashMap<>();
		String attribute = "";
		PreparedStatement statement = null;
		ResultSet rs = null;
		String SQL = "select name from LayerConfiguration where name = ?"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			statement.setString(1,key);
			rs = statement.executeQuery();
			while(rs.next()) {
				attribute = rs.getString("name");
				map.put("name",attribute);
				LOGGER.info(String.format("%s.getAttributesForLayer %s: %s is %s",CLSS,key,"name",attribute));
				break;
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getPoseForCommand: Error (%s)",CLSS,e.getMessage()));
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
	 * Associate a layer giving it a new name
	 * it will be updated.
	 * @cxn an open database connection
	 * @param oldName
	 * @param newName
	 */
	public void updateLayerName(Connection cxn,String oldName,String newName) {
		PreparedStatement statement = null;
		oldName = oldName.toLowerCase();
		newName = newName.toLowerCase();

		String SQL = "UPDATE LayerConfiguration SET name=? WHERE name = ?";
		
		try {
			LOGGER.info(String.format("%s.updateLayerName: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,newName);
			statement.setString(2,oldName);
			statement.executeUpdate();
			if( statement.getUpdateCount()==0) {
				statement.close();
				SQL = "INSERT INTO LayerConfiguration (name) VALUES(?)";
				LOGGER.info(String.format("%s.updateLayerName: \n%s",CLSS,SQL));
				statement = cxn.prepareStatement(SQL);
				statement.setString(1,newName);
				statement.executeUpdate();
			}
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateLayerName: updateLayerName error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	
}
