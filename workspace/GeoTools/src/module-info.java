module org.geotools {
	requires java.base;
	requires java.logging;
	requires java.naming;
	requires java.sql;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive javax.measurement;
	requires transitive org.locationtech.jts;
	requires org.apache.commons.compress;
	requires commons.math3;
	requires geographic.lib;
	requires ejml;
	
	exports org.geotools.data.dbf;
	exports org.geotools.data.shapefile;
	exports org.geotools.render;
	exports org.geotools.style;
	exports org.geotools.util;
	exports org.openjump.coordsys;
	exports org.openjump.feature;
	exports org.openjump.io;
} 