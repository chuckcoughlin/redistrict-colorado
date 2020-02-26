
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

package redistrict.colorado.core;

import org.openjump.feature.AttributeType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

/**
 *  Hold attributes for display corresponding to a layer Feature. 
 */
public class FeatureConfiguration  {
    private static final long serialVersionUID = -871137208054228529L;
    private final long layerId; // Layer ID
    private final String name;  // Feature name
    private String alias;
    private AttributeType type;
    private final BooleanProperty visible;
    private Color background;
    private int rank;

    /**
     * Constructs a feature configuration that determines how to illustrate a layer feature.
     */
    public FeatureConfiguration(long id,String nm) {
        this.layerId = id;
        this.name = nm;
        this.alias = name;
        this.type = AttributeType.DOUBLE;
        this.visible = new SimpleBooleanProperty(false);;
        this.background = Color.BLACK;
        this.rank = 1;
    }
    
    public String getAlias() { return this.alias; }
    public AttributeType getAttributeType() { return this.type; }
    public Color getBackground() { return this.background; }
    public long getLayerId() { return this.layerId; }
    public String getName()  { return this.name; }
    public boolean isVisible() { return this.visible.get(); }
    public int getRank() { return this.rank; }
    
    public void setAlias(String a) { this.alias = a; }
    public void setAttributeType(AttributeType a) { this.type = a; }
    public void setBackground(Color color) { this.background= color; }
    public void setVisible(boolean flag) { this.visible.set(flag); }
    public void setRank(int r) { this.rank = r; }

 }
