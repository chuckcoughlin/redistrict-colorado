/**
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */

package redistrict.colorado.core.common;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  These paths can be considered static constants once BERT_HOME has been set.
 *  This should be done soon after startup.
 */
public class PathConstants   {   
	public static Path HOME = Paths.get(System.getProperty("user.dir")).getRoot();
	public static Path DATA_PATH  = null; 
	public static Path DB_PATH    = null; 
	public static Path LOG_DIR    = null;
	
	static {
		setHome(HOME);
	}


	public static void setHome(Path home) {
		HOME = home;
		DATA_PATH= Paths.get(HOME.toFile().getAbsolutePath(),"data"); 
		DB_PATH    = Paths.get(HOME.toFile().getAbsolutePath(),"db","rc.db"); 
		LOG_DIR    = Paths.get(HOME.toFile().getAbsolutePath(),"logs");  
	}
}
