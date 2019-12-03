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
	 * Find the pose associated with a command.
	 * @cxn an open database connection
	 * @param command user entered string
	 * @return the corresponding pose name if it exists, otherwise NULL
	 */
	public String getPoseForCommand(Connection cxn,String command) {
		PreparedStatement statement = null;
		ResultSet rs = null;
		command = command.toLowerCase();
		String pose = null;
		String SQL = "select pose from PoseMap where command = ?"; 
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			statement.setString(1,command);
			rs = statement.executeQuery();
			while(rs.next()) {
				pose = rs.getString("pose");
				LOGGER.info(String.format("%s.getPoseForCommand: %s is %s",CLSS,command,pose));
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
		return pose;
	}
	

	/**
	 * Associate a pose with the specified command. If the command already exists
	 * it will be updated.
	 * @cxn an open database connection
	 * @param command user entered string
	 * @param pose the name of the pose to assume
	 */
	public void mapCommandToPose(Connection cxn,String command,String pose) {
		PreparedStatement statement = null;
		command = command.toLowerCase();
		pose = pose.toLowerCase();

		String SQL = "UPDATE PoseMap SET pose=? WHERE command = ?";
		
		try {
			LOGGER.info(String.format("%s.mapCommandToPose: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,pose);
			statement.setString(2,command);
			statement.executeUpdate();
			if( statement.getUpdateCount()==0) {
				statement.close();
				SQL = "INSERT INTO PoseMap (command,pose) VALUES(?,?)";
				LOGGER.info(String.format("%s.mapCommandToPose: \n%s",CLSS,SQL));
				statement = cxn.prepareStatement(SQL);
				statement.setString(1,command);
				statement.setString(2,pose);
				statement.executeUpdate();
			}
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.mapCommandToPose: Database error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	
}
