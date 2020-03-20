/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * A launch pane is a "pretty" static window that forms a right-side backdrop
 * whenever there are not items selected on the left.
 */
public class SplashScreen extends BasicRightSideNode {
	private final static String BACKDROP = "images/ColoradoCapitol.png";
	private Label headerLabel = new Label("Colorado Voting District Analyzer");
	private static final GuiUtil guiu = new GuiUtil();
	
	public SplashScreen() {
		super(ViewMode.UNSELECTED,DisplayOption.NONE);
		
		ImageView imageView = guiu.loadImage(BACKDROP);
		imageView.getStyleClass().add("splash-screen");
		getChildren().add(imageView);
		setTopAnchor(imageView,0.);
		setLeftAnchor(imageView,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(imageView,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(imageView,0.);
		
		headerLabel.getStyleClass().add("splash-label");
		getChildren().add(headerLabel);
		setTopAnchor(headerLabel,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
	}

	@Override
	public void updateModel() {
	}
	
}
