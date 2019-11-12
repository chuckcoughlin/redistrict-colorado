/**
 * 
 */
module jump.core {
	requires java.logging;
	requires java.sql;
	requires java.xml;
	requires transitive org.locationtech.jts;
	requires com.fasterxml.jackson.databind;
	requires org.apache.commons.compress;
	requires org.apache.commons.imaging;
	requires org.apache.commons.lang3;
	requires org.cts;
	requires json.simple;
	
	exports org.openjump.coordsys;
	exports org.openjump.common;
	exports org.openjump.core;
	exports org.openjump.datasource;
	exports org.openjump.geometry;
	exports org.openjump.geometry.feature;
	exports org.openjump.image;
	exports org.openjump.io;
	exports org.openjump.task;
	exports org.openjump.util;
	exports org.geotools.dbffile;
	exports org.geotools.shapefile;

}