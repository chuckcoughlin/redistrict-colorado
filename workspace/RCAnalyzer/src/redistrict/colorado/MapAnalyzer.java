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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import redistrict.colorado.core.common.LoggerUtility;
import redistrict.colorado.core.common.PathConstants;
import redistrict.colorado.sql.Database;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.MainMenuBar;
import redistrict.colorado.ui.MainSplitPane;
import redistrict.colorado.ui.common.UIConstants;

public class MapAnalyzer extends Application {
	private final static String CLSS = "MapAnalyzer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final String LOG_ROOT = CLSS.toLowerCase();
	public final static String TITLE  = "Map Analyzer";

	/**
	 * Create the root stage and add left and right scenes in a split pane. Add scroll bars both sides
	 * and a menu bar at the top.
	 */
	@Override
	public void start(Stage root) {
		root.setTitle(TITLE);
		root.setWidth(UIConstants.STAGE_WIDTH);
		root.setHeight(UIConstants.STAGE_HEIGHT);
	
		Scene mainScene = new Scene(new VBox());  // Holds menu bar and split pane
		mainScene.setFill(Color.OLDLACE);
		
		MainMenuBar mbar = new MainMenuBar();
		MainSplitPane splitPane = new MainSplitPane();
		ButtonPane buttonPane = new ButtonPane();
		((VBox) mainScene.getRoot()).getChildren().addAll(mbar,splitPane,buttonPane);
		

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
