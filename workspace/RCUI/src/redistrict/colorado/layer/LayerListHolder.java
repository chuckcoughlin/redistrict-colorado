/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.UIConstants;



public class LayerListHolder extends AnchorPane {
	public static final double FRAME_HEIGHT = 2000;
	private Label headerLabel = new Label("===== Layer Definitions =======");
	private ButtonPane buttons = new ButtonPane();
	private ListView<String> layerList;
	
	
	public LayerListHolder() {
		layerList = new ListView<String>();
		headerLabel.setPrefHeight(50.);
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(layerList);
		//layerList.setMinHeight(100);
		//this.setPrefHeight(UIConstants.SCENE_HEIGHT/2);
		setTopAnchor(headerLabel,20.);
		setTopAnchor(layerList,100.);
		setBottomAnchor(buttons,10.);
		setLeftAnchor(buttons,10.);
		setRightAnchor(buttons,10.);
	}
}
