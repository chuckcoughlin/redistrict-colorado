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
import redistrict.colorado.core.PartisanMetric;

/**
 * The preferences table holds name/value pairs for "well-known"
 * constants within the application. These values should match the
 * values used to initialize the table. 
 */
public class PreferencesTable {
	private static final String CLSS = "PreferencesTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final long MODEL_ID = 42;
	private static final String AFFILIATION_KEY = "AffiliationId";
	public static final String PARTISAN_METRIC_KEY = "PartisanAsymmetryMetric";
	private static final String DEMOGRAPHIC_KEY = "DemographicId";
	private static final String COUNTY_BOUNDARIES_KEY = "CountyBoundariesId";
	public static final String COMPETITIVENESS_THRESHOLD_KEY = "CompetitivenessThreshold";
	
	public static final double DEFAULT_COMETITIVE_THRESHOLD = 15.0;   // Store as string, convert when extracted
	
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public PreferencesTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }

	/**
	 * Configure an AnalysisModel with IDs from Preferences
	 */
	public AnalysisModel getAnalysisModel() {
		AnalysisModel model = new AnalysisModel(MODEL_ID);
		String SQL = "SELECT name,value FROM Preferences";
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			rs = statement.executeQuery(SQL); 
			while(rs.next()) {
				String name = rs.getString(1);
				String text = rs.getString(2);
				if( text!=null && !text.isEmpty()) {
					if( name.equalsIgnoreCase(AFFILIATION_KEY)) model.setAffiliationId(Long.parseLong(text));
					else if( name.equalsIgnoreCase(DEMOGRAPHIC_KEY)) model.setDemographicId(Long.parseLong(text));
					else if( name.equalsIgnoreCase(COUNTY_BOUNDARIES_KEY)) model.setCountyBoundariesId(Long.parseLong(text));
					else if( name.equalsIgnoreCase(COMPETITIVENESS_THRESHOLD_KEY)) model.setCompetitiveThreshold(Double.parseDouble(text));
					else if( name.equalsIgnoreCase(PARTISAN_METRIC_KEY)) model.setPartisanMetric(PartisanMetric.valueOf(text));
				}
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
	/*
	 * We assume that the existence of the row is ensured by the original creation of the database
	 */
	public String getParameter(String key) {
		String value = ""; 
		String SQL = String.format("SELECT value FROM Preferences WHERE name = '%s'",key);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			rs = statement.executeQuery(SQL); 
			while(rs.next()) {
				value = rs.getString(1);
				break; 
			}
		}
		catch(NumberFormatException nfe) {
			LOGGER.severe(String.format("%s.getParameter: Error (%s)",CLSS,nfe.getMessage()));
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.getParameter: Error (%s)",CLSS,e.getMessage()));
		}
		return value;
	}
	
	/*
	 * We assume that the existence of the row is ensured by the original creation of the database
	 */
	public double getWeight(String key) {
		double weight = 0.;
		String SQL = String.format("SELECT value FROM Preferences WHERE name = '%s'",key);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			rs = statement.executeQuery(SQL); 
			while(rs.next()) {
				String value = rs.getString(1);
				weight = Double.parseDouble(value);
				break; 
			}
		}
		catch(NumberFormatException nfe) {
			LOGGER.severe(String.format("%s.getWeight: Error (%s)",CLSS,nfe.getMessage()));
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.getWeight: Error (%s)",CLSS,e.getMessage()));
		}
		return weight;
	}
	
	public void setParameter(String key,String value) {
		String SQL = String.format("UPDATE Preferences SET value = '%s' WHERE name = '%s'",value,key);
		Statement statement = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			statement.executeUpdate(SQL); 
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.setParameter: Error (%s)",CLSS,e.getMessage()));
		}
	}
	public void setWeight(String key,double value) {
		String SQL = String.format("UPDATE Preferences SET value = %s WHERE name = '%s'",String.valueOf(value),key);
		Statement statement = null;
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);     // set timeout to 10 sec.
			statement.executeUpdate(SQL); 
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.setWeight: Error (%s)",CLSS,e.getMessage()));
		}
	}
	/**
	 * Update preferences based on the analysis model object.
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
			statement.setString(1,COUNTY_BOUNDARIES_KEY);
			statement.setString(2, String.valueOf(model.getCountyBoundariesId()));
			statement.executeUpdate();
			statement.setString(1,COMPETITIVENESS_THRESHOLD_KEY);
			statement.setString(2, String.valueOf(model.getCompetitiveThreshold()));
			statement.executeUpdate();
			statement.setString(1,PARTISAN_METRIC_KEY);
			statement.setString(2, model.getPartisanMetric().name());
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
