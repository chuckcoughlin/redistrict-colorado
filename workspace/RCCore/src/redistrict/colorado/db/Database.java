/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
	private final AttributeAliasTable attributeAliasTable;
	private final FeatureAttributeTable featureAttributeTable;
	private final DatasetTable datasetTable;
	private final PlanTable planTable;
	private final PlanDatasetTable planDatasetTable;

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private Database() {
		this.attributeAliasTable = new AttributeAliasTable();
		this.featureAttributeTable = new FeatureAttributeTable();
		this.datasetTable = new DatasetTable();
		this.planTable = new PlanTable();
		this.planDatasetTable = new PlanDatasetTable();
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
	public boolean isConnected() { return connection!=null; }
	public AttributeAliasTable getAttributeAliasTable() { return this.attributeAliasTable; }
	public FeatureAttributeTable getFeatureAttributeTable() { return this.featureAttributeTable; }
	public PlanDatasetTable getPlanLayerTable() { return this.planDatasetTable; }
	public DatasetTable getDatasetTable() { return this.datasetTable; }
	public PlanTable getPlanTable() { return this.planTable; }
	
	/**
	 * Create a database connection. Use this for all subsequent queries.
	 * The expectation is that this method is called in the main() method
	 * before any class requires database access.
	 *  
	 * @param path to database instance
	 */
	public void startup(Path path) {
		String connectPath = "jdbc:sqlite:"+path.toString();
		LOGGER.info(String.format("%s.startup: database path = %s",CLSS,path.toString()));

		Statement statement = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			attributeAliasTable.setConnection(connection);
			featureAttributeTable.setConnection(connection);
			datasetTable.setConnection(connection);
			planTable.setConnection(connection);
			planDatasetTable.setConnection(connection);
			
			String SQL = "PRAGMA foreign_keys = ON";
			statement = connection.createStatement();
			statement.executeUpdate(SQL);
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.log(Level.SEVERE,String.format("%s.startup: Database error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
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
