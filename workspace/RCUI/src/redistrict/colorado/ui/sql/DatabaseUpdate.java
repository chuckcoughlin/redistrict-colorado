/**
 * Copyright 2018-2019. Charles Coughlin. All Rights Reserved.
 *                 MIT License.
 *
 */
package redistrict.colorado.ui.sql;

import java.util.logging.Logger;

/**
 * This class contains SQL commands that change the database.
 * The expectation is that individual UI elements that execute
 * these requests will emit DatabaseEvents to other elements
 * that depend on a database change.
 * 
 * This class is a singleton for easy access throughout.
 */
public class DatabaseUpdate {
	private final static String CLSS = "Database";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	private static DatabaseUpdate instance = null;

 

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private DatabaseUpdate() {
		
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static DatabaseUpdate getInstance() {
		if( instance==null) {
			synchronized(DatabaseUpdate.class) {
				instance = new DatabaseUpdate();
			}
		}
		return instance;
	}

	

}
