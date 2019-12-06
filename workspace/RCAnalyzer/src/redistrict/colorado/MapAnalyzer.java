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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MapAnalyzer extends Application {
	public final static String TITLE  = "Map Analyzer";
	public final static int STAGE_WIDTH  = 1200;
	public final static int STAGE_HEIGHT = 1000;
	/**
	 * Create the root stage and add left and right scenes.
	 */
    @Override
    public void start(Stage root) {
        Label layerLabel = new Label("===== Layer Definitions =======");
        Scene left = new Scene(new StackPane(layerLabel), STAGE_WIDTH, STAGE_HEIGHT);
        root.setTitle(TITLE);
        root.setScene(left);
        root.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
