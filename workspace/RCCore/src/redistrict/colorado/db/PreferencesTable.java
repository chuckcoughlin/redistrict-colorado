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
import java.util.logging.Logger;

import redistrict.colorado.core.AnalysisModel;

/**
 * The preferences table holds name/value pairs for "well-known"
 * constants within the application.
 */
public class PreferencesTable {
	private static final String CLSS = "PreferencesTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final long MODEL_ID = 42;
	private static final String AFFILIATION_KEY = "AFFLIATIONID";
	private static final String DEMOGRAPHIC_KEY = "DEMOGRAPHICID";
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public PreferencesTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }

	/**
	 * Get the Id of the dataset that provides affliation information
	 */
	public AnalysisModel getAnalysisModel() {
		AnalysisModel model = new AnalysisModel(MODEL_ID);
		String SQL = String.format("SELECT name,value FROM Preferences");
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			rs = statement.executeQuery(SQL); 
			while(rs.next()) {
				String name = rs.getString(1);
				String text = rs.getString(2);
				if( name.equalsIgnoreCase(AFFILIATION_KEY)) model.setAffiliationId(Long.parseLong(text));
				else if( name.equalsIgnoreCase(DEMOGRAPHIC_KEY)) model.setDemographicId(Long.parseLong(text));
			}
		}
		catch(NumberFormatException nfe) {
			LOGGER.severe(String.format("%s.getAnalysisModel: Error (%s)",CLSS,nfe.getMessage()));
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.getAnalysisModel: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		model.updateAffiliationFeatures();
		model.updateDemographicFeatures();
		return model;

	}
	/**
	 * Set the Id of the dataset that provides affiliation information
	 */
	public void updateAnalysisModel(AnalysisModel model) {
		PreparedStatement statement = null;
		String SQL = "REPLACE INTO Preferences(name,value) VALUES(?,?)";
		try {
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,AFFILIATION_KEY);
			statement.setString(2, String.valueOf(model.getAffiliationId()));
			statement.executeUpdate();
			statement.setString(1,DEMOGRAPHIC_KEY);
			statement.setString(2, String.valueOf(model.getDemographicId()));
			statement.executeUpdate();
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateAnalysisModel: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
}
