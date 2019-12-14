/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import redistrict.colorado.ui.common.ComponentIds;
import redistrict.colorado.ui.common.GuiUtil;
import redistrict.colorado.ui.common.PropertyBindingHub;
import redistrict.colorado.ui.common.ViewMode;

/**
 * Create the menu hierarchy for the menubar.
 * The leaf nodes are class MenuItem.
 */
public class MainMenuBar extends MenuBar  {
	private static final String CLSS = "MainMenuBar";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final EventHandler<ActionEvent> eventHandler;

	
	private MenuItem plans;
	private MenuItem layers;
	private MenuItem regions;
	
	public MainMenuBar() {
		this.eventHandler = new MenuBarEventHandler();
		setUseSystemMenuBar(false);
		getMenus().add(systemMenu());
		getMenus().add(leftMenu());
		getMenus().add(rightMenu());
	}
	
	public Menu systemMenu() {
		Menu menu =  new Menu("MapAnalyzer");
		MenuItem about = new MenuItem("About");
		about.setOnAction(e -> About.display() );
		MenuItem quit  = new MenuItem("Quit");
		quit.setOnAction(e -> Platform.exit() );
		menu.getItems().addAll(about,quit);
		return menu;
	}
	public Menu leftMenu() {
		Menu menu =  new Menu("View");
		plans = new MenuItem("Plans");
		plans.setId(ComponentIds.MENU_LAYER);
		plans.setOnAction(eventHandler);
		plans.setDisable(true);
		layers = new MenuItem("Layers");
		layers.setId(ComponentIds.MENU_LAYER);
		layers.setOnAction(eventHandler);
		layers.setDisable(true);
		regions  = new MenuItem("Regions");
		regions.setId(ComponentIds.MENU_REGION);
		regions.setOnAction(eventHandler);
		menu.getItems().addAll(layers,regions);
		return menu;
	}
	public Menu rightMenu() {
		return new Menu("Detail");
	}
	
	/**
	 * One of the menu items has been selected. The source of the event is the item.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class MenuBarEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			PropertyBindingHub hub = PropertyBindingHub.getInstance();
			String src = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,src));
			if( src.equalsIgnoreCase(ComponentIds.MENU_LAYER)) {
				plans.setDisable(true);
				layers.setDisable(true);
				regions.setDisable(false);
				hub.setMode(ViewMode.LAYER);
			}
			else if( src.equalsIgnoreCase(ComponentIds.MENU_PLAN)) {
				plans.setDisable(false);
				layers.setDisable(true);
				regions.setDisable(true);
				hub.setMode(ViewMode.PLAN);
			}
			else if( src.equalsIgnoreCase(ComponentIds.MENU_REGION)) {
				plans.setDisable(true);
				layers.setDisable(false);
				regions.setDisable(true);
				hub.setMode(ViewMode.REGION);
			}
		}
	}
	
}
