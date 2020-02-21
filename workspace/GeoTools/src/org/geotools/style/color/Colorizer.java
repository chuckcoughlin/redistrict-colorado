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
 * Colorizers hold logic to select a fill color based on a Feature. 
 * This allows us to color different sections of the grid differently.
 */
public interface Colorizer  {
    public Color getFeatureFill(Feature feat);
}
