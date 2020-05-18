/**
 * Re-district Colorado core classes.
 */
module rc.core {
	requires java.base;
	requires java.logging;
	requires java.prefs;
	requires java.xml.crypto;
	requires java.sql;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive sqlite.jdbc;
	requires transitive org.geotools;
	requires org.locationtech.jts;
	
	exports redistrict.colorado.core;
	exports redistrict.colorado.db;
	exports redistrict.colorado.pref;
}