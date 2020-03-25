/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.FlowPane;
import redistrict.colorado.ui.ComponentIds;

/**
 * Display this dialog by clicking on the bar chart. It only makes sense once the
 * calculations have been completed.
 */
public class ComparisonResultsDialog extends Dialog<Gate> {
	private final static String CLSS = "ComparisonResultsDialog";
	private final static Logger LOGGER = Logger.getLogger(CLSS);
	private final FlowPane root;
	private final Button dismissButton;
	
	public ComparisonResultsDialog(Gate gate) {
		
		this.setTitle(gate.getTitle());
		this.setHeaderText("Header text");
		this.dismissButton= new Button("Dismiss");
		dismissButton.setDisable(false);
		dismissButton.setId(ComponentIds.BUTTON_CANCEL);
		dismissButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});

	    
	    
	    this.root = new FlowPane();
	    FlowPane root = new FlowPane();
	    root.setPadding(new Insets(10));
	    root.setHgap(10);
	    root.setPrefWrapLength(500);
	    root.setColumnHalignment(HPos.RIGHT); 
	    root.getChildren().addAll(dismissButton);
        getDialogPane().setContent(root);
	}
}
