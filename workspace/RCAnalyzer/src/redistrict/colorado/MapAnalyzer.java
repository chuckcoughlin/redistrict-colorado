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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.MainMenuBar;
import redistrict.colorado.ui.MainSplitPane;
import redistrict.colorado.ui.common.UIConstants;

public class MapAnalyzer extends Application {
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
		
		// Wire the event paths
		mbar.registerEventReceiver(buttonPane.getRCEventDispatcher());   // Buttons receive menu selections
		mbar.registerEventReceiver(splitPane.getRCEventDispatcher());    // Panes receive menu selections

		root.setScene(mainScene);
		root.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
}
