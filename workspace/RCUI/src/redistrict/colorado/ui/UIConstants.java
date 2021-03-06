/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui;

/**
 * These are sizing constants for the UI.
 */
public interface UIConstants {
	public final static double BUTTON_PANEL_HEIGHT  = 40.;
	public final static double DETAIL_HEADER_SPACING= 60.;
	public static final double FRAME_HEIGHT = 600; 
	public final static double LIST_PANEL_LEFT_MARGIN  = 2.;
	public final static double LIST_PANEL_RIGHT_MARGIN = 2.;
	public final static double LIST_PANEL_WIDTH = 400;       // Preferred
	public final static double LIST_PANEL_HEIGHT = 180;       // Preferred
	public final static double STACK_PANE_MIN_HEIGHT = 200.; // Min size before +/- disappear
	public final static double STACK_PANE_MIN_WIDTH = 200.;
	public final static double SCENE_WIDTH  = 800.;  // Size for right-side panes
	public final static double SCENE_HEIGHT = 750.;
	public final static double STAGE_WIDTH  = 1200.; // Default size for main window
	public final static double STAGE_HEIGHT = 800.;  
	
			
	// UI Names for the left-side lists
	public static final String LIST_CELL_SELECTED_CLASS = "list-cell-selected";
	public static final String LIST_CELL_NOT_SELECTED_CLASS = "list-cell-not-selected";
	public static final String LIST_CELL_FIELD_CLASS = "list-cell-field";
	public static final String LIST_CELL_FIELD_CLASS_SMALL = "list-cell-field-small";
	public static final String LIST_CELL_NAME_CLASS = "list-cell-name";
	public static final String LIST_CELL_BUTTON_CLASS = "list-cell-button";
	public static final String LIST_CELL_ICON_CLASS = "list-cell-icon";
	public static final String LIST_CELL_FONT = "Arial";
    
	public final static double TABLE_ROW_HEIGHT = 40.;   
	public final static long UNSET_KEY = -1;         // Indicator for no key selected
}
