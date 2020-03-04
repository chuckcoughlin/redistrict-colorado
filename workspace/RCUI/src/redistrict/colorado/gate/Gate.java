/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.gate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * This is the base container for gates that display results of comparisons 
 * between different plans. We expect a small number of comparisons (1-4).
 */
public abstract class Gate extends VBox {
	private static final Color BASIC_FILL = Color.web("#c9f5c4");   // pale green
	private static final double HEIGHT = 150.;
	private static final double WIDTH = 220.;
	private final Label header;
	private final StackPane body;
	private final Text text;
	private final Rectangle rectangle;
	
	public Gate() {
		super(0.);     // No spacing
		this.header = new Label(getTitle());
		header.setAlignment(Pos.CENTER);
		header.setPrefWidth(WIDTH);
		header.getStyleClass().add("graph-header");
		// KLUDGE: For an unknown reason header didn't align with body
		VBox.setMargin(header,new Insets(0,0,0,15));  // top right bottom left
		this.body = new StackPane();
		//body.setAlignment(Pos.CENTER);
		this.text = new Text("...");
		this.rectangle = new Rectangle(WIDTH,HEIGHT-40.);
		rectangle.setFill(BASIC_FILL);
		rectangle.getStyleClass().add("graph-box");
		body.getChildren().addAll(rectangle,text);
		getChildren().addAll(header,body);	
	}
	public abstract String getTitle(); 
}
