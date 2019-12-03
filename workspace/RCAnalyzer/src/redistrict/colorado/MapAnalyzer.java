/*
 * The viewer is an interactive tool to load aand display a ESRI Shape file. The display
 * makes use of Google maps and an embedded web server.
 *  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you msy redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MapAnalyzer extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

