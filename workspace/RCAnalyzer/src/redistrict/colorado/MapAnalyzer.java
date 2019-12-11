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
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.MainMenuBar;
import redistrict.colorado.ui.MapCanvas;
import redistrict.colorado.ui.layer.LayerConfigurationPage;
import redistrict.colorado.ui.layer.LayerListHolder;
import redistrict.colorado.ui.region.RegionListHolder;

public class MapAnalyzer extends Application {
	public final static String TITLE  = "Map Analyzer";
	public final static double BUTTON_PANEL_HEIGHT  = 40.;
	public static final double FRAME_HEIGHT = 2000;
	public final static double SCENE_WIDTH  = 900.;
	public final static double SCENE_HEIGHT = 1800.;
	public final static double STAGE_WIDTH  = 1800.;
	public final static double STAGE_HEIGHT = 1000.;
	/**
	 * Create the root stage and add left and right scenes in a split pane. Add scroll bars both sides
	 * and a menu bar at the top.
	 */
	@Override
	public void start(Stage root) {
		root.setTitle(TITLE);
		root.setWidth(STAGE_WIDTH);
		root.setHeight(STAGE_HEIGHT);
	
		Scene mainScene = new Scene(new VBox());  // Holds menu bar and split pane
		mainScene.setFill(Color.OLDLACE);
		
		MainMenuBar mbar = new MainMenuBar();
		SplitPane splitPane = new SplitPane();
		ButtonPane buttonPane = new ButtonPane();
		
		ScrollPane left = new ScrollPane();
		left.fitToWidthProperty().set(true);
		left.setCursor(Cursor.HAND);
		StackPane leftStack = new StackPane();
		//leftStack.setPrefHeight(FRAME_HEIGHT);
		leftStack.getChildren().addAll(new RegionListHolder(),new LayerListHolder());
		leftStack.getChildren().get(0).setVisible(true);
		leftStack.getChildren().get(1).setVisible(false);
		SubScene leftStackScene = new SubScene(leftStack,SCENE_WIDTH,SCENE_HEIGHT-2*BUTTON_PANEL_HEIGHT);    // Holds scroll area
		left.setContent(leftStackScene);
		
		
		ScrollPane right = new ScrollPane();
		right.pannableProperty().set(true);
		right.setCursor(Cursor.OPEN_HAND);
		StackPane rightStack = new StackPane();
		rightStack.getChildren().addAll(new MapCanvas(),new LayerConfigurationPage());
		Rectangle rect = new Rectangle(SCENE_WIDTH, SCENE_HEIGHT, Color.RED);
		right.setContent(rect);
			
		splitPane.getItems().addAll(left,right);
		((VBox) mainScene.getRoot()).getChildren().addAll(mbar,splitPane,buttonPane);
		
		// Wire the event paths
		mbar.registerEventReceiver(buttonPane.getRCEventDispatcher());   // Buttons receive menu selections

		root.setScene(mainScene);
		root.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
}
