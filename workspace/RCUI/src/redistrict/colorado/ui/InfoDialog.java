/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

/**
 * Display an info dialog with HTML content. 
 *   info = new InfoDialog(html)
 *   info.showAndWait()
 *
 */
public class InfoDialog extends Alert {
	//private final static String CLSS = "InfoDialog";
	//private final static Logger LOGGER = Logger.getLogger(CLSS);

	public InfoDialog(TextFlow text) {
		super(AlertType.INFORMATION);
		Label label = new Label(null,text);
		this.graphicProperty().set(label);
	}
}
