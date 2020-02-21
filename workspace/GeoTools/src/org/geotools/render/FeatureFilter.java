
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package org.geotools.render;

import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;


/**
 * A feature filter holds a transform to be applied when panning or zooming a feature
 * collection. By default the filter does nothing.
 */
public class FeatureFilter  {
    private static final long serialVersionUID = 2273446944516446540L;
    private Translate translate = null;
    private Scale scale = null;

    /**
     * Creates a FeatureFilter that has no effect, an identity matrix.
     */
    public FeatureFilter() {}
    
    
    public void setScale(Scale trans) { this.scale = trans; }
    public void setTranslation(Translate trans) { this.translate = trans; }
    /**
     * First apply the translation, then the scale.
     * @param shape
     */
    public void applyTransforms(Shape shape) { 
    	if(translate!=null) shape.getTransforms().add(translate);
    	if(scale!=null) shape.getTransforms().add(scale);
    }
    /**
     * Concatenate filter transformations to an existing transform
     * @param shape
     */
    public void concatenateTransforms(Transform trans) { 
    	if(translate!=null) trans.createConcatenation(translate);
    	if(scale!=null) trans.createConcatenation(scale);
    }
}
