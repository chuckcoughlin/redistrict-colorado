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

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import redistrict.colorado.gmaps.javascript.JavascriptRuntime;
import redistrict.colorado.gmaps.javascript.object.GoogleMap;

/**
 *
 * @author Rob Terpilowski
 */
public class GoogleMapView extends AnchorPane {
	private static final String CLSS = "GoogleMapView";
	private static final boolean DEBUG = false;
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    public static final String GOOGLE_MAPS_API_LINK = "https://maps.googleapis.com/maps/api/js?v=3.exp";
    public static final String PAGE_PATH = "html/googlemaps.html"; 
    private final String key;
    protected WebView webview;
    protected WebEngine webengine;
    protected boolean disableDoubleClick = false;
    protected boolean initialized = false;
    protected GoogleMap map = null;
    protected final List<MapComponentInitializedListener> mapInitializedListeners = new ArrayList<>();


    /**
     * Creates a new map view using the API key.
     *
     * @param key Google Maps API key
     */
    public GoogleMapView(String api) {
    	this.key = api;
    }
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
                JavascriptRuntime.setDefaultWebEngine(webengine);
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
                    }
                    else if (newState == State.FAILED) {
                    	LOGGER.severe(String.format("%s.web engine worker: %s",CLSS,worker.getException()));
                    }
                });
 
                try (InputStream inputStream = getClass().getResourceAsStream(PAGE_PATH)) {
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
                    initialized = true;
                    //fireMapInitializedListeners();            
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            } 
            finally {
                latch.countDown();
            }
        };
       initWebView.run();
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
        if (initialized && map != null) {
            webengine.executeScript("google.maps.event.trigger(" + map.getVariableName() + ", 'resize')");
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
    private void dumpDocument(Document doc) {
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
