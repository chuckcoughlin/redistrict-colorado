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
	requires javafx.controls;
	
}