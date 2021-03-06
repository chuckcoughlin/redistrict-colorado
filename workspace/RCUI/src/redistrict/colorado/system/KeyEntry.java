/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.system;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import redistrict.colorado.db.Database;
import redistrict.colorado.pref.PreferenceKeys;

/**
 * Display a popup window for the purpose of entering a key for Google Maps.
 * My key = AIzaSyCAP3nDr VJ4i7MjtjOzP6A fRaz_Kmbwb7A
 */
public class KeyEntry { 
	private static final double HEIGHT = 200;
	private static final double WIDTH = 500;
	private final static double GRID0_WIDTH = 120.;    // Grid widths
	private final static double GRID1_WIDTH = 300.;
	private static TextField keyField;
	
	public static void display() {
		Stage stage=new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Key Entry");
		
		Label header= new Label("The Google API Key must be obtained separately for each installation. This is required before the Google Map overlay feature will be available.");
		header.setFont(Font.font ("Arial", 12));
		header.setWrapText(true);
		
		Label label= new Label("Google API Key: ");
		keyField = new TextField();
		keyField.setText(Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY));

		GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
		grid.getColumnConstraints().clear();
		ColumnConstraints col0 = new ColumnConstraints(GRID0_WIDTH);
		col0.setHalignment(HPos.RIGHT);
		ColumnConstraints col1 = new ColumnConstraints(GRID1_WIDTH,GRID1_WIDTH,Double.MAX_VALUE);
		col1.setHalignment(HPos.CENTER);
		col1.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col0,col1); 
		grid.add(label, 0, 0);
		grid.add(keyField, 1, 0);
		grid.setAlignment(Pos.BASELINE_CENTER);

		Button button= new Button("Dismiss");
		button.setOnAction(e -> stage.close());
		Button saveButton = new Button("Save");
		saveButton.setOnAction(new SaveButtonEventHandler());
		GridPane buttongrid = new GridPane();
		buttongrid.setHgap(10);
		buttongrid.setVgap(4);
		buttongrid.setAlignment(Pos.BOTTOM_RIGHT);
		buttongrid.getColumnConstraints().clear();
		buttongrid.add(button, 0, 0);
		buttongrid.add(saveButton, 1, 0);

		VBox layout= new VBox(12);
		layout.setPadding(new Insets(0,30,0,30));   // top, right, bottom, left
		layout.getChildren().addAll(header,grid, buttongrid);
		layout.setAlignment(Pos.CENTER);

		Scene scene1= new Scene(layout, WIDTH, HEIGHT);
		stage.setScene(scene1);
		stage.showAndWait();

	}
	/**
	 * One of the buttons has been pressed. The source of the event is the button.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public static class SaveButtonEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Database.getInstance().getPreferencesTable().setParameter(PreferenceKeys.GOOGLE_API_KEY, keyField.getText());
		}
	}

}
