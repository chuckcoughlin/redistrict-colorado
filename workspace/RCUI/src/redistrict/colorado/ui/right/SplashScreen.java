/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import javafx.scene.control.Label;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * A launch pane is a "pretty" static window that forms a right-side backdrop
 * whenever there are not items selected on the left.
 */
public class SplashScreen extends BasicRightSideNode {
	private final static String CLSS = "SplashScreen";

	private Label headerLabel = new Label("Splash Screen");
	
	public SplashScreen() {
		super(ViewMode.UNSELECTED,DisplayOption.NONE);
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
	}

	@Override
	public void updateModel() {
	}
	
}
