/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * A launch pane is a "pretty" static window that forms a right-side backdrop
 * whenever there are not items selected on the left.
 */
public class SplashScreen extends AnchorPane {
	private final static String CLSS = "SplashScreen";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Splash Screen");
	
	public SplashScreen() {
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
	}
}
