module fxgraphics {
	requires java.base;
	requires java.desktop;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires javafx.swing;
	
	exports org.jfree.fx;
}