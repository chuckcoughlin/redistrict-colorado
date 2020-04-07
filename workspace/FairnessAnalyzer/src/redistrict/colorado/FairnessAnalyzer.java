/**
 * MapAnalyzer is an interactive tool to load and display a ESRI Shape file. It has tools specially
 * designed to evaluate submissions for the State of Colorado 2020 re-districting activity. The display
 * makes use of Google maps in addition to publicly available maps and voter registration data.
 *  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import redistrict.colorado.core.LoggerUtility;
import redistrict.colorado.core.PathConstants;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.MainSplitPane;
import redistrict.colorado.ui.MainMenuBar;
import redistrict.colorado.ui.StatusPane;
import redistrict.colorado.ui.UIConstants;

public class FairnessAnalyzer extends Application {
	private final static String CLSS = "FairnessAnalyzer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	public final static String TITLE  = "Fairness Analyzer";

	/**
	 * Create the root stage and add left and right scenes in a split pane. Add scroll bars both sides
	 * and a menu bar at the top.
	 */
	@Override
	public void start(Stage root) {
		root.setTitle(TITLE);
		root.setHeight(UIConstants.STAGE_HEIGHT);
		root.setWidth(UIConstants.STAGE_WIDTH);
		
		VBox vbox = new VBox();  // Holds menu bar,split pane and status
		Scene mainScene = new Scene(vbox);  
		mainScene.setFill(Color.OLDLACE);
		mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		
		MainMenuBar mbar = new MainMenuBar();
		MainSplitPane splitPane = new MainSplitPane();
		StatusPane statusPane = new StatusPane();
		vbox.getChildren().addAll(mbar,splitPane,statusPane);
		VBox.setVgrow(splitPane, Priority.ALWAYS);
		
		root.setScene(mainScene);
		root.show();
	}
	
	@Override
	public void stop() {
		Database.getInstance().shutdown();
	}

	// The installation home directory can be altered via command-line argument (usually "")
	// Data, database, and log locations are fixed relative to home.
    public static void main(String[] args) {
    	String arg = args[0];
    	Path path = Paths.get(arg);
    	PathConstants.setHome(path);
    	// Logging setup routes to console and file withing "log" directory
    	LoggerUtility.getInstance().configureRootLogger(LOG_ROOT);
		Database.getInstance().startup(PathConstants.DB_PATH);
		
        launch(args);
    }
}
