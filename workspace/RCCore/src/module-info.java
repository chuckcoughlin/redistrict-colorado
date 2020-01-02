/**
 * Re-district Colorado core classes.
 */
module rc.core {
	requires java.base;
	requires java.logging;
	requires java.prefs;
	requires java.sql;
	requires transitive sqlite.jdbc;
	requires open.jump;
	requires org.locationtech.jts;
	
	exports redistrict.colorado.core;
	exports redistrict.colorado.db;
}