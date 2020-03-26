/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

import java.util.logging.Logger;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import redistrict.colorado.gate.Gate;

/**
 * Display an info dialog with text content. 
 *   info = new InfoDialog(html)
 *   info.showAndWait()
 *
 */
public class InfoDialog extends Dialog<Gate> {
	private final static String CLSS = "InfoDialog";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final double MAX_TEXT_WIDTH = 400.;
	private final ButtonType dismissButton;

	public InfoDialog(Gate gate) {
		setHeaderText(gate.getTitle());
		setTitle("Computation Details");  // On top bar of window
		setResizable(true);
		
		StackPane pane = new StackPane();
		TextFlow text = gate.getInfo();
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
	
	
}