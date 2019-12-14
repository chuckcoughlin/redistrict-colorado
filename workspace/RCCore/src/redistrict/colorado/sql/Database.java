/**
 * Copyright 2018-2019. Charles Coughlin. All Rights Reserved.
 *                 MIT License.
 *
 */
package redistrict.colorado.sql;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sqlite.JDBC;

/**
 * This class is a wrapper for the entire robot database. It is implemented
 * as a singleton for easy access. The startup() method must be called
 * before it can be used as it opens the database connection.
 * 
 * Call shutdown() when database access is no longer required.
 */
public class Database {
	private final static String CLSS = "Database";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	@SuppressWarnings("unused")
	private final static JDBC driver = new JDBC(); // Force driver to be loaded
	
	private Connection connection = null;
	private static Database instance = null;
	private final LayerConfigurationTable layerConfiguration;
 

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private Database() {
		this.layerConfiguration = new LayerConfigurationTable();
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static Database getInstance() {
		if( instance==null) {
			synchronized(Database.class) {
				instance = new Database();
			}
		}
		return instance;
	}

	
	/** Return a list of names of defined layers
	 * property.
	 * @param mcmap a map of configurations. Joints not present are ignored.
	 * @param pose
	 * @param parameter, e.g. "position","speed","torque"
	 * @return list of upper-case joint names.
	 */
	public Map<String,String> getAttributesForLayer(String key) {
		return layerConfiguration.getAttributesForLayer(connection,key);
	}
	
	/**
	 * Create a database connection. Use this for all subsequent queries. 
	 * @param path to database instance
	 */
	public void startup(Path path) {
		String connectPath = "jdbc:sqlite:"+path.toString();
		LOGGER.info(String.format("%s.startup: database path = %s",CLSS,path.toString()));

		try {
			connection = DriverManager.getConnection(connectPath);
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.log(Level.SEVERE,String.format("%s.startup: Database error (%s)",CLSS,e.getMessage()));
		}
	}

	/**
	 * Close the database connection prior to stopping the application.
	 * 
	 * @param path to database instance
	 */
	public void shutdown() {
		LOGGER.info(String.format("%s.shutdown",CLSS));

		if( connection!=null) {
			try {
				connection.close();
			}
			catch(SQLException e) {
				// if the error message is "out of memory", 
				// it probably means no database file is found
				LOGGER.warning(String.format("%s.shutdown: Error closing database (%s)",CLSS,e.getMessage()));
			}
		}
	}

}
