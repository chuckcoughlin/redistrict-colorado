/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AddDeleteButtonHolder extends HBox {
	Button addButton = new Button("Add");
	Button deleteButton = new Button("Delete");
	Label label = new Label("Hi Mom");
	
	public AddDeleteButtonHolder() {
		this.getChildren().add(label);
		//this.getChildren().add(addButton);
		//this.getChildren().add(deleteButton);
	}
}
