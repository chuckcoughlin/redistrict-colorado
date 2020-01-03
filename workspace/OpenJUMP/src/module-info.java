module open.jump {
	requires java.base;
	requires java.logging;
	requires java.sql;
	requires transitive org.locationtech.jts;
	requires org.apache.commons.compress;
	
	exports org.geotools.shapefile;
	exports org.openjump.geometry.feature;
	exports org.openjump.io;
}