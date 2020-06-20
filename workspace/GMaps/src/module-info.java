module google.maps {
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.xml;
	requires java.xml.bind;
	requires jdk.jsobject;
	requires javafx.base;
	requires jakarta.activation;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.swing;
	requires transitive javafx.web;
	
	exports redistrict.colorado.gmaps;
	exports redistrict.colorado.gmaps.javascript;
	exports redistrict.colorado.gmaps.javascript.event;
	exports redistrict.colorado.gmaps.javascript.object;
}