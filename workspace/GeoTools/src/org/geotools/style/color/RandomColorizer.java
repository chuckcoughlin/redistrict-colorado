/**
 *  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package org.geotools.style.color;

import org.openjump.feature.Feature;
import javafx.scene.paint.Color;

/**
 * This colorizer simply returns a random fill color.
 */
public class RandomColorizer implements Colorizer  {
	@Override
    public Color getFeatureFill(Feature feat) {
		int red = (int)(Math.random()*255);
		int green = (int)(Math.random()*255);
		int blue = (int)(Math.random()*255);
		return Color.rgb(red,green,blue);  // Opaque
	}
}
