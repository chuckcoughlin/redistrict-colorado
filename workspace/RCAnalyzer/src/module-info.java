/**
 * 
 */
module rc.analyzer {
	requires rc.core;
	requires rc.ui;
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.sql;
	requires java.xml;
	requires jdk.jfr;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	
	exports redistrict.colorado;
}