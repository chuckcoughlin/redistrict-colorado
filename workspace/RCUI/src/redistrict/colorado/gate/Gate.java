/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private static final Color BASIC_FILL = Color.web("#c9f5c4");   // pale green
	private static final double HEIGHT = 150.;
	private static final double WIDTH = 220.;
	private final Label header;
	private final Rectangle body;
	
	public Gate() {
		super(0.);     // No spacing
		setPrefHeight(HEIGHT);
		setPrefWidth(WIDTH);
		setFillWidth(true);
		this.header = new Label(getTitle());
		header.setStyle("-fx-background-color:LIGHTGREY");
		getChildren().add(header);
		
		this.body = new Rectangle(WIDTH,HEIGHT-40.);
		body.setFill(BASIC_FILL);
		getChildren().add(body);
		
	}
	public abstract String getTitle(); 
}
