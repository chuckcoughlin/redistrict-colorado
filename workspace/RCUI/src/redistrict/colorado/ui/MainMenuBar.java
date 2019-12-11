/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import redistrict.colorado.ui.common.ComponentIds;
import redistrict.colorado.ui.common.EventSource;
import redistrict.colorado.ui.common.RCEventDispatchChain;
import redistrict.colorado.ui.common.RCEventDispatcher;

/**
 * Create the menu hierarchy for the menubar.
 * The leaf nodes are class MenuItem.
 */
public class MainMenuBar extends MenuBar implements EventSource<ActionEvent> {
	private static final String CLSS = "MainMenuBar";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private final EventHandler<ActionEvent> eventHandler;
	private final RCEventDispatchChain<ActionEvent> eventChain;
	private final RCEventDispatcher<ActionEvent> eventDispatcher;
	
	private MenuItem layers;
	private MenuItem regions;
	
	public MainMenuBar() {
		this.eventHandler = new MenuBarEventHandler();
		this.eventChain   = new RCEventDispatchChain<ActionEvent>();
		this.eventDispatcher = new RCEventDispatcher<ActionEvent>(eventHandler);
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
		quit.setOnAction(e -> System.exit(0) );
		menu.getItems().addAll(about,quit);
		return menu;
	}
	public Menu leftMenu() {
		Menu menu =  new Menu("Selections");
		layers = new MenuItem("Layers");
		layers.setId(ComponentIds.MENU_LAYER);
		layers.setDisable(true);
		regions  = new MenuItem("Regions");
		regions.setId(ComponentIds.MENU_REGION);
		menu.getItems().addAll(layers,regions);
		return menu;
	}
	public Menu rightMenu() {
		return new Menu("Detail");
	}
	
	/**
	 * One of the buttons has been pressed. The source of the event is the button.
	 * Dispatch to receivers. Receivers can sort things out by the ID.
	 */
	public class MenuBarEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String src = ((Node)event.getSource()).getId();
			if( src.equalsIgnoreCase(ComponentIds.MENU_LAYER)) {
				layers.setDisable(true);
				regions.setDisable(false);
			}
			else if( src.equalsIgnoreCase(ComponentIds.MENU_REGION)) {
				layers.setDisable(false);
				regions.setDisable(true);
			}
			LOGGER.info(String.format("%s.handle: ActionEvent source = %s",CLSS,src));
			eventChain.dispatchEvent(event);
		}
	}
	
	@Override
	public void registerEventReceiver(RCEventDispatcher<ActionEvent> rce) {
		eventChain.append(rce);
	}
}
