/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;

import javafx.scene.control.TableView;
import redistrict.colorado.core.LayerModel;

public class LayerTableView extends TableView<LayerModel>{
	private LayerModel model;
	
	public LayerTableView(LayerModel lm) {
		this.model = lm;
	}
	
	/**
	 * Populate the table. The flag determines whether or not to use hidden columns.
	 * @param useHidden
	 */
	public void populate(boolean useHidden) {
		getColumns().clear();
		
		// Name and geometry are fixed.
		
		// Add a column for each feature.
		
		// Now add the rows
		
	}

	public void setModel(LayerModel mdl) { this.model = mdl; }
}
