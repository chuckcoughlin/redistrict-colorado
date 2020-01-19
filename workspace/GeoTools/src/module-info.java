module org.geotools {
	requires java.base;
	requires java.logging;
	requires java.naming;
	requires java.sql;
	requires transitive org.locationtech.jts;
	requires org.apache.commons.compress;
	
	exports org.geotools.data;
	exports org.geotools.data.dbf;
	exports org.geotools.data.shapefile;
	exports org.geotools.data.simple;
	exports org.geotools.map;
	exports org.openjump.feature;
	exports org.openjump.io;
}