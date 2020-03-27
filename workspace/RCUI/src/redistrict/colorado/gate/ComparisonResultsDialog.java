/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Display this dialog by clicking on the bar chart. It only makes sense once the
 * calculations have been completed.
 */
public class ComparisonResultsDialog extends Dialog<Gate> {
	private final static String CLSS = "ComparisonResultsDialog";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final ButtonType dismissButton;

	public ComparisonResultsDialog(Gate gate) {
		this.setHeaderText(gate.getTitle());
		this.setTitle("Comparison Result Details");

		Node pane = gate.getResultsContents();
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
