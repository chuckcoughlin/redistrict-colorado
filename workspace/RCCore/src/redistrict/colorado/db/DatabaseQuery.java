/**
 * Copyright 2018-2019. Charles Coughlin. All Rights Reserved.
 *                 MIT License.
 *
 */
package redistrict.colorado.db;

import java.util.logging.Logger;

/**
 * This class contains queries usable throughout the application.
 * The expectation is that individual UI elements will execute the
 * requests with no need to notify other elements.
 * 
 * This class is a singleton for easy access throughout.
 */
public class DatabaseQuery {
	private final static String CLSS = "DatabaseQuery";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	private static DatabaseQuery instance = null;
 

	/**
	 * Constructor is private per Singleton pattern.
	 */
	private DatabaseQuery() {
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static DatabaseQuery getInstance() {
		if( instance==null) {
			synchronized(DatabaseQuery.class) {
				instance = new DatabaseQuery();
			}
		}
		return instance;
	}

	

}
