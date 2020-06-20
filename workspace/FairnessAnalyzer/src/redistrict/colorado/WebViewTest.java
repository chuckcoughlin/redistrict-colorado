package redistrict.colorado;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.w3c.dom.Document;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;

/**
 * Demonstrate that the java web view can display web pages.
 */
public class WebViewTest extends Application {
	private static final String CLSS = "WebViewTest";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();

    public void start(Stage primaryStage) {
    	LOGGER.info(String.format("%s.start: Port 80 is %s",CLSS,(available(80)?"OPEN":"CLOSED")));
        primaryStage.setTitle("JavaFX WebView Test");

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        LOGGER.info(String.format("%s.start: Javascript is %s",CLSS,(engine.isJavaScriptEnabled())?"ENABLED":"DISABLED"));
        Worker<Void> worker = engine.getLoadWorker();
        worker.stateProperty().addListener((observable, oldState, newState) -> {
        	LOGGER.info(String.format("%s.web engine state: %s",CLSS,newState.toString()));
            if (newState == State.SUCCEEDED) {
            	LOGGER.info(String.format("%s: location=%s",CLSS,engine.getLocation()));
            }
            else if (newState == State.FAILED) {
            	LOGGER.info(String.format("%s.web engine worker: %s",CLSS,worker.getMessage()));
            	LOGGER.info(String.format("%s.web engine worker: %s",CLSS,worker.getException()));
            }
        });
        engine.load("https://www.google.com");
  
        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public boolean available(int port) {
    	ServerSocket ss = null;
    	DatagramSocket ds = null;
    	try {
    		ss = new ServerSocket(port);
    		ss.setReuseAddress(true);
    		ds = new DatagramSocket(port);
    		ds.setReuseAddress(true);
    		return true;
    	} 
    	catch (IOException e) {
    	} finally {
    		if (ds != null) {
    			ds.close();
    		}

    		if (ss != null) {
    			try {
    				ss.close();
    			} catch (IOException e) {
    				/* should not be thrown */
    			}
    		}
    	}

    	return false;
    }
    
    public static void main(String[] args) {
    	String arg = args[0];
    	Path path = Paths.get(arg);
    	PathConstants.setHome(path);
    	// Logging setup routes to console and file within "log" directory
    	LoggerUtility.getInstance().configureRootLogger(LOG_ROOT);
        launch(args);
    }
}