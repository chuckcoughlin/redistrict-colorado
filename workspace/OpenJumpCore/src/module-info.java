/**
 * 
 */
module jump.core {
	requires java.logging;
	requires java.sql;
	requires org.locationtech.jts;
	
	requires org.apache.commons.compress;
	
	exports com.vividsolutions.jump;
	exports com.vividsolutions.jump.feature;
	exports com.vividsolutions.jump.task;
	exports com.vividsolutions.jump.util;
	exports org.geotools.dbffile;
	exports org.geotools.shapefile;
	exports org.openjump.core;
}