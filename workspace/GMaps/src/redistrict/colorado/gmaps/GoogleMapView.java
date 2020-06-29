/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 * 
 * Inspired by GMapsFX by Rob Terpilowski. This version is much 
 * simplified and streamlined.
 */
package redistrict.colorado.gmaps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author Rob Terpilowski
 */
public class GoogleMapView extends AnchorPane {
	private static final String CLSS = "GoogleMapView";
	private static final boolean DEBUG = false;
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    public static final String BOUNDS_PATH   = "html/googlemapbounds.html";  // specify initial bounds
    public static final String DISTRICT_PATH = "html/googlemaps.html";  // "vanilla" test version
    public static final String PAGE_PATH     = "html/googlemaps.html";  // "vanilla" test version
    public static final String PLAN_PATH     = "html/googlemaps.html";  // "vanilla" test version
    private final String key;
    private final String path;
    protected WebView webview;
    protected WebEngine webengine;
    protected boolean disableDoubleClick = false;
    protected boolean initialized = false;
    protected final List<MapComponentInitializedListener> mapInitializedListeners = new ArrayList<>();


    /**
     * Use this constructor for the "vanilla" map view that has only a map type control.
     * Creates a new map view using the API key.
     *
     * @param key Google Maps API key
     */
    public GoogleMapView(String api) {
    	this.path = PAGE_PATH;
    	this.key = api;
    }
    
    /**
     * Use this constructor to specify a map that encloses specified bounds.
     * Creates a new map view using the API key.
     *
     * @param key Google Maps API key
     */
    public GoogleMapView(String api,double north,double east,double south,double west) {
    	this.path = BOUNDS_PATH;
    	this.key = api;
    }
    
    public WebEngine getEngine() { return this.webengine; }
    
    /**
     * Start the web-engine and display the first version of the map.
     */
    public void start() {
    	CountDownLatch latch = new CountDownLatch(1);
        Runnable initWebView = () -> {
        	try {
                webview = new WebView();
                EventDispatcher dispatcher = webview.getEventDispatcher();
                webview.setEventDispatcher(new DoubleClickSuppressor(dispatcher));
                webengine = webview.getEngine();
                LOGGER.info(String.format("%s: new google view ...",CLSS));
                setTopAnchor(webview, 0.0);
                setLeftAnchor(webview, 0.0);
                setBottomAnchor(webview, 0.0);
                setRightAnchor(webview, 0.0);
                getChildren().add(webview);

                webview.widthProperty().addListener(e -> mapResized());
                webview.heightProperty().addListener(e -> mapResized());

                webengine.setOnAlert(e -> LOGGER.info("ALERT: " + e.getData()));
                webengine.setOnError(e -> LOGGER.severe("ERROR: " + e.getMessage()));

                Worker<Void> worker = webengine.getLoadWorker();
                worker.stateProperty().addListener((observable, oldState, newState) -> {
                    if (newState == State.SUCCEEDED) {
                    	LOGGER.info(String.format("%s.web engine SUCCEEDED: %s",CLSS,webengine.getLocation()));
                    	if( DEBUG ) {
                    		Document doc = webengine.getDocument();
                    		if( doc!=null ) dumpDocument(doc);
                    	}
                        initialized = true;
                        fireMapInitializedListeners(); 
                    }
                    else if (newState == State.FAILED) {
                    	LOGGER.severe(String.format("%s.web engine worker: %s",CLSS,worker.getException()));
                    }
                });
 
                try (InputStream inputStream = getClass().getResourceAsStream(path)) {
                	ByteArrayOutputStream into = new ByteArrayOutputStream();
                    byte[] buf = new byte[4096];
                    for (int n; 0 < (n = inputStream.read(buf));) {
                        into.write(buf, 0, n);
                    }
                    into.close();
                    String page = new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
                    page = page.replace("GOOGLE_API_KEY", key);
                    //LOGGER.info(String.format("%s.web engine page: %s",CLSS,page));
                    webengine.loadContent(page);           
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
        	 } 
            finally {
                latch.countDown();
            }
        };
        // Guarantee that we run on the FX Application thread.
        if (Platform.isFxApplicationThread()) {
            initWebView.run();
        } 
        else {
            Platform.runLater(initWebView);
        }

        try {
            latch.await();
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    
    public void addMapInitializedListener(MapComponentInitializedListener listener) {
        synchronized (mapInitializedListeners) {
            mapInitializedListeners.add(listener);
        }
    }
    protected void fireMapInitializedListeners() {
        synchronized (mapInitializedListeners) {
            for (MapComponentInitializedListener listener : mapInitializedListeners) {
                listener.mapInitialized();
            }
        }
    }
    private void mapResized() {
        if (initialized ) {
            //webengine.executeScript("google.maps.event.trigger(" + map.getVariableName() + ", 'resize')");
        }
    }
    public void removeMacpInitializedListener(MapComponentInitializedListener listener) {
        synchronized (mapInitializedListeners) {
            mapInitializedListeners.remove(listener);
        }
    }
    public void setDisableDoubleClick(boolean disableDoubleClick) {
        this.disableDoubleClick = disableDoubleClick;
    }
    
    /**
     * Swallow double-click events if so configured in the main class
     */
    public class DoubleClickSuppressor implements EventDispatcher {

        private final EventDispatcher dispatcher;

        public DoubleClickSuppressor(EventDispatcher original) {
            this.dispatcher = original;
        }

        @Override
        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                if (mouseEvent.getClickCount() > 1) {
                    if (disableDoubleClick) {
                        mouseEvent.consume();
                    }
                }
            }
            return dispatcher.dispatchEvent(event, tail);
        }
    }
    
    // Dump a Document, for debugging only
    public void dumpDocument(Document doc) {
        try {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           LOGGER.info(writer.toString());
        }
        catch(TransformerException ex) {
           ex.printStackTrace();
        }
    }
}
