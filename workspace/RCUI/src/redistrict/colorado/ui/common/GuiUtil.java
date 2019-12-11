/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.common;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Provide a few static methods that perform common UI tasks
 */
public class GuiUtil {
	private static final String CLSS = "GuiUtil";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	
	/**
	 * Load an image given its path within the RC-UI jar. The path is if the form "images/x.png".
	 * @param path
	 * @return the image view, else null.
	 */
	public ImageView loadImage(String path) {
		ImageView iv = null;
		
		try {
			InputStream is = getClass().getModule().getResourceAsStream(path);
			iv = new ImageView(new Image(is));
		} 
		catch (IOException ioe) {
			LOGGER.warning(String.format("%s.loadImage: Failed to read %s (%s)",CLSS,path,ioe.getLocalizedMessage()));
		}
		return iv;
	}
}
