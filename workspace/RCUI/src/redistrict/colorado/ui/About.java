/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Display a popup window with application versions and copyright info.
 * Provide a static method to display.
 */
public class About{

	public static void display()
	{
		Stage stage=new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("About: MapAnalyzeer");


		Label label1= new Label("Pop up window now displayed");


		Button button1= new Button("Close this pop up window");


		button1.setOnAction(e -> stage.close());



		VBox layout= new VBox(10);


		layout.getChildren().addAll(label1, button1);

		layout.setAlignment(Pos.CENTER);

		Scene scene1= new Scene(layout, 300, 250);

		stage.setScene(scene1);
		stage.showAndWait();

	}

}
