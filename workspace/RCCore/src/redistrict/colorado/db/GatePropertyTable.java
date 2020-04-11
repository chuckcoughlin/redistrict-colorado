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
import java.util.List;
import java.util.logging.Logger;

import redistrict.colorado.core.GateProperty;
import redistrict.colorado.core.GateType;

/**
 * Gate properties include weights and measures of "goodness" for
 * each of the calculation methods. The rows are expected to pre-exist.
 */
public class GatePropertyTable {
	private static final String CLSS = "GatePropertiesTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public GatePropertyTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 *@return a gate object refreshed with properties from the database
	 */
	public GateProperty getGateProperty(GateType type) {
		GateProperty model = null;
		Statement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT weight,fair, unfair FROM GateProperties "+
					  String.format(" WHERE name='%s'",type.name()); 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			while(rs.next()) {
				model = new GateProperty(type,rs.getDouble("weight"),
										 rs.getDouble("fair"),rs.getDouble("unfair"));
				break;
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getGateProperty: Error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return model;
	}
	/**
	 * @return a list of property objects for all defined Gates.
	 */
	public List<GateProperty> getGateProperties() {
		List<GateProperty> list = new ArrayList<>();
		GateProperty model = null;
		Statement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT name, weight,fair, unfair FROM GateProperties ORDER BY name"; 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			while(rs.next()) {
				GateType name = GateType.valueOf(rs.getString("name").toUpperCase());
				model = new GateProperty(name,rs.getDouble("weight"),
									rs.getDouble("fair"),rs.getDouble("unfair"));
				list.add(model);
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.getGateProperties: Error (%s)",CLSS,e.getMessage()));
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
	 * Update the properties in the database for a given gate object. 
	 * @cxn an open database connection
	 * @param gate
	 */
	public boolean updateGateProperties(GateProperty properties) {
		PreparedStatement statement = null;
		String SQL = "UPDATE GateProperties SET weight=?, fair=?, unfair=? WHERE name = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.updateGateProperties: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setDouble(1,properties.getWeight());
			statement.setDouble(2, properties.getFairValue());
			statement.setDouble(3, properties.getUnfairValue());
			statement.setString(4, properties.getType().name());
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateGateProperties: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
