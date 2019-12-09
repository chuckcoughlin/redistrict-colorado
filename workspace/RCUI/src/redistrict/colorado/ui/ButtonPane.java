/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 * Hold the add and delete buttons, Insets are top,right,bottom,left
 *
 */
public class ButtonPane extends FlowPane {
	private static final double HGAP = 8.;
	private static final double VGAP = 8.;
	Button addButton = new Button("Add");
	Button deleteButton = new Button("Delete");
	Label statusLabel = new Label("Status:");
	Label message = new Label("");  // Most recent message
	
	public ButtonPane() {
		super(Orientation.HORIZONTAL,HGAP,VGAP);

		this.getChildren().add(addButton);
		this.getChildren().add(deleteButton);
		this.getChildren().add(statusLabel);
		this.getChildren().add(message);
		
		setMargin(addButton,new Insets(VGAP,HGAP,VGAP,30.));
		
		
	}
}
