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
 * Display an help dialog with information about limit settings. 
 *   info = new SettingsDialog()
 *   info.showAndWait()
 *
 */
public class SettingsInformation extends Dialog<Gate> {
	private final static String CLSS = "SettingsInformation";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final double MAX_TEXT_WIDTH = 600.;
	private final ButtonType dismissButton;

	public SettingsInformation() {
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
		Text intro = new Text("The meaning and numeric ranges for \"unfair\" to \"fair\" depend on the metric. This page prvides some context. ");
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
		
		header = new Text("County Crossings:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 60   (60 counties in multiple districts)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (no countoes split by district boundaries)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
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
		
		header = new Text("Partisan Asymmetry (lopsided wins):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 0   (no chance that vote-margins are random)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 1.   (certainty of random vote margins.)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));

		header = new Text("Partisan Asymmetry (mean-median):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 5   (<45% or >55% of vote to get 1/2 the seats)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (mean is same as median)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Partisan Asymmetry (partisan bias):");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 5   (<45% or >55% of seats when you have 1/2 the votes)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (50% of seats with 50% of vote)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Population Balance:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 5   (maximum std deviation between districts)");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (all districts same population)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Proportionality:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		unfair = new Text("   unfair: 1   (one seat more than entitled to )");
		info.getChildren().add(unfair); info.getChildren().add(new Text(System.lineSeparator()));
		fair   = new Text("      fair: 0   (seat percentage matches vote percentage)");
		info.getChildren().add(fair); info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Racial Vote Dilution:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		
		header = new Text("Voting Power Imbalance:");
		header.setStyle("-fx-font-weight: bold");
		info.getChildren().add(header);
		info.getChildren().add(new Text(System.lineSeparator()));
		return info;
	}
	
}