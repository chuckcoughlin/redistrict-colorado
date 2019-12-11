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
	
	exports redistrict.colorado.ui;
	exports redistrict.colorado.ui.common;
	exports redistrict.colorado.ui.layer;
	exports redistrict.colorado.ui.region;
	
}