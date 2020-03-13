/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * The preferences table holds name/value pairs for "well-known"
 * constants within the application.
 */
public class PreferencesTable {
	private static final String CLSS = "PreferencesTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final String AFFILIATION = "AFFLIATIONID";
	private static final String DEMOGRAPHICS = "DEMOGRAPHICSID";
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public PreferencesTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }

	/**
	 * Get the Id of the dataset that provides affliation information
	 */
	public long getAffliationId() {
		long affiliationDataset = -1;
		long id = -1;
		String SQL = String.format("SELECT value FROM Preferences WHERE name = '%s'",AFFILIATION);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);  // should be only 1 row
			while(rs.next()) {
				id = rs.getLong("id");
				break;
			}
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.getAffliationId: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return affiliationDataset;

	}
	/**
	 * Set the Id of the dataset that provides affliation information
	 */
	public void setAffliationId(long id) {
		Statement statement = null;
		String SQL = String.format("DELETE FROM Preferences WHERE name = '%s'",id);
		try {
			statement = cxn.createStatement();
			statement.executeUpdate(SQL);
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.setAffliationId: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
}
