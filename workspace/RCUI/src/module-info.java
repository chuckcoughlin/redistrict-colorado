/**
 * This works for the Ant build.
 */
module rc.ui {
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires jdk.jfr;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires rc.core;
	
	exports redistrict.colorado.ui;
	exports redistrict.colorado.layer;
	exports redistrict.colorado.region;
	
}