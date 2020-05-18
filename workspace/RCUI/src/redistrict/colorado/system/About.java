/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.system;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Display a popup window with application versions and copyright info.
 * Provide a static method to display.
 */
public class About{
	private static final double HEIGHT = 220;
	private static final double WIDTH = 400;
	
	public static void display()
	{
		Stage stage=new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("About: "+Version.NAME);


		Label title= new Label(Version.NAME);
		title.setFont(Font.font ("Arial",FontWeight.BOLD, 18));
		TextFlow tf = new TextFlow();
		Text copyright = new Text(Version.COPYRIGHT);
		Text license = new Text(Version.LICENSE);
		Text link = new Text(Version.LINK);
		link.setFont(Font.font ("Arial",FontPosture.ITALIC, 10));
		tf.getChildren().addAll(copyright,license,link);

		Label date= new Label(Version.BUILD_DATE);
		Label version= new Label(Version.VERSION);

		Button button= new Button("Dismiss");
		button.setOnAction(e -> stage.close());


		VBox layout= new VBox(10);
		layout.setPadding(new Insets(10,30,10,30));   // top, right, bottom, left
		layout.getChildren().addAll(title,tf,date,version, button);
		layout.setAlignment(Pos.CENTER);

		Scene scene1= new Scene(layout, WIDTH, HEIGHT);
		stage.setScene(scene1);
		stage.showAndWait();

	}

}
