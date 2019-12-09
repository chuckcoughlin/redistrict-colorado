/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Create the menu hierarchy for the menubar.
 * The leaf nodes are class MenuItem.
 */
public class MainMenuBar extends MenuBar {


	public MainMenuBar() {
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
		menu.getItems().addAll(about,quit);
		return menu;
	}
	public Menu leftMenu() {
		Menu menu =  new Menu("Selections");
		MenuItem layers = new MenuItem("Laters");
		MenuItem regions  = new MenuItem("Regions");
		menu.getItems().addAll(layers,regions);
		return menu;
	}
	public Menu rightMenu() {
		return new Menu("Detail");
	}
}
