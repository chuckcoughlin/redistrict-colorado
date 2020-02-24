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
import redistrict.colorado.bind.EventBindingHub;

/**
 * Create the menu hierarchy for the menubar.
 * The leaf nodes are class MenuItem. Initially show
 * "Plans".
 */
public class MainMenuBar extends MenuBar  {
	private static final String CLSS = "MainMenuBar";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final EventHandler<ActionEvent> eventHandler;

	
	private MenuItem plans;
	private MenuItem layers;
	private MenuItem districts;
	
	public MainMenuBar() {
		this.eventHandler = new MenuBarEventHandler();
		setUseSystemMenuBar(false);
		getMenus().add(systemMenu());
		getMenus().add(leftMenu());
	}
	
	public Menu systemMenu() {
		Menu menu =  new Menu("Analyzer");
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
		plans.setId(ComponentIds.MENU_PLAN);
		plans.setOnAction(eventHandler);
		plans.setDisable(true);
		layers = new MenuItem("Layers");
		layers.setId(ComponentIds.MENU_LAYER);
		layers.setOnAction(eventHandler);
		layers.setDisable(false);
		districts  = new MenuItem("Districts");
		districts.setId(ComponentIds.MENU_DISTRICT);
		districts.setOnAction(eventHandler);
		districts.setDisable(false);
		menu.getItems().addAll(plans,layers,districts);
		return menu;
	}
	
	
	/**
	 * One of the menu items has been selected. The source of the event is the item.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class MenuBarEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			EventBindingHub hub = EventBindingHub.getInstance();
			String src = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,src));
			layers.setDisable(src.equalsIgnoreCase(ComponentIds.MENU_LAYER));
			plans.setDisable(src.equalsIgnoreCase(ComponentIds.MENU_PLAN));
			districts.setDisable(src.equalsIgnoreCase(ComponentIds.MENU_DISTRICT));

			if( layers.isDisable() ) hub.setMode(ViewMode.LAYER);
			else if( plans.isDisable() ) hub.setMode(ViewMode.PLAN);
			else if( districts.isDisable() ) hub.setMode(ViewMode.DISTRICT);
		}
	}
}
