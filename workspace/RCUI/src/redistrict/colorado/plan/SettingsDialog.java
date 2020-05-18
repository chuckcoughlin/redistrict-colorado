/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import redistrict.colorado.gate.Gate;

/**
 * Display an help dialog with iformation about limit settings. 
 *   info = new SettingsDialog()
 *   info.showAndWait()
 *
 */
public class SettingsDialog extends Dialog {
	private final static String CLSS = "SettingsDialog";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final double MAX_TEXT_WIDTH = 600.;
	private final ButtonType dismissButton;

	public SettingsDialog() {
		setHeaderText("Explanation of Metrics Ranges");
		setTitle("Settings Information");  // On top bar of window
		setResizable(true);
		
		StackPane pane = new StackPane();
		TextFlow text = getInfo();
		text.setMaxWidth(MAX_TEXT_WIDTH);
		pane.getChildren().add(text);
		StackPane.setAlignment(text, Pos.CENTER);
		getDialogPane().setContent(pane);
		
		dismissButton = new ButtonType("Dismiss",ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(dismissButton);

		// We never return anything useful from this dialog
		setResultConverter(new Callback<ButtonType, Gate>() {
		    @Override
		    public Gate call(ButtonType b) {
		    	return null;
		    }
		});
	}
	
	// List the different metrics and explain the fair / unfair values
	public TextFlow getInfo() { 
		TextFlow info = new TextFlow();
		Text intro = new Text("The meaning and numeric ranges for \"unfair\" to \"fair\" depend on the metric. This page prvides some context for their meaning. ");
		info.getChildren().add(intro);
		info.getChildren().add(new Text(System.lineSeparator()));
		
		Text header = new Text("Compactness:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header); info.getChildren().add(new Text(System.lineSeparator()));
		Text unfair = new Text("   unfair: 0   (small area, vary long perimeter)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		Text fair   = new Text("      fair: 1   (value for a circle, as compact as possible)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Competitive Districts:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 0   (no districts competitive)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 7   (all districts competitive)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Partisan Asymmetry (mean-median):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Partisan Asymmetry (declination):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 0.3   (absolute value, radians)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0  ");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Partisan Asymmetry (efficiency gap):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 15   (maximum percentage for a plan to be considered non-gerrymandered)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (equal numbers of wasted votes)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		return info;
	}
	
}