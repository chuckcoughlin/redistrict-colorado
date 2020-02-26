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
	requires transitive rc.core;
	requires org.geotools;
	requires org.locationtech.jts;
	
	exports redistrict.colorado.bind;
	exports redistrict.colorado.district;
	exports redistrict.colorado.layer;
	exports redistrict.colorado.pane;
	exports redistrict.colorado.plan;
	exports redistrict.colorado.ui;
}