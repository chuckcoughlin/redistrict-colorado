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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The AttributeAlias table is used for setting default aliases for feature attribute names.
 * The aliases can be changed at any time by the user.
 */
public class AttributeAliasTable {
	private static final String CLSS = "AttributeAliasTable";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Connection cxn = null;
	/** 
	 * Constructor: 
	 */
	public AttributeAliasTable() {}
	public void setConnection(Connection connection) { this.cxn = connection; }
	
	/**
	 * Create a new alias. If there is already an alias for the specified name, do an update.
	 */
	public void createAlias(String name,String alias) {
		if( cxn==null ) return;
		
		String SQL = "INSERT INTO AttributeAlias(name,alias) values (?,?)";
		PreparedStatement statement = null;
		try {
			LOGGER.info(String.format("%s.createAlias: %s for %s",CLSS,alias,name));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1, name);
			statement.setString(2, alias);
			statement.executeUpdate(SQL); 
		}
		catch(SQLException e) {
			// Presumably the error is a duplicate key 
			LOGGER.severe(String.format("%s.createLayer: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
	}
	/**
	 * Delete the alias for the specified name.
	 */
	public boolean deleteAlias(String name) {
		PreparedStatement statement = null;
		String SQL = "DELETE FROM AttributeAlias WHERE name = ?";
		boolean success = false;
		try {
			LOGGER.info(String.format("%s.deleteLayer: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1, name);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.deleteAlias: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
	/**
	 * @return a map of all aliases keyed by name.
	 */
	public Map<String,String> aliasForName() {
		Map<String,String> map = new HashMap<>();
		Statement statement = null;
		ResultSet rs = null;
		String SQL = "SELECT name,alias from AttributeAlias ORDER BY name"; 
		try {
			statement = cxn.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			rs = statement.executeQuery(SQL);
			while(rs.next()) {
				map.put(rs.getString("name"), rs.getString("alias"));
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			LOGGER.severe(String.format("%s.aliasForName: Error (%s)",CLSS,e.getMessage()));
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
	 * Associate an attribute name with a new alias.
	 * @param name
	 * @param alias
	 * @return true if the name existed and was re-assigned an alias.
	 */
	public boolean updateAlias(String name,String alias) {
		PreparedStatement statement = null;
		String SQL = "UPDATE AttributeAlias SET alias=? WHERE name = ?";
		boolean success = false;
		try {
			//LOGGER.info(String.format("%s.updateAlias: \n%s",CLSS,SQL));
			statement = cxn.prepareStatement(SQL);
			statement.setString(1,alias);
			statement.setString(2,name);
			statement.executeUpdate();
			if( statement.getUpdateCount()>0) success = true;
		}
		catch(SQLException e) {
			LOGGER.severe(String.format("%s.updateAlias: error (%s)",CLSS,e.getMessage()));
		}
		finally {
			if( statement!=null) {
				try { statement.close(); } catch(SQLException ignore) {}
			}
		}
		return success;
	}
}
