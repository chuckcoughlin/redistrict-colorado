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

package org.openjump.workbench;


import org.openjump.ui.action.ActionContext;
import org.openjump.util.Blackboard;
import org.openjump.workbench.component.LayerViewPanel;
import org.openjump.workbench.component.LayerableNamePanel;
import org.openjump.workbench.model.LayerManager;
import org.openjump.workbench.model.Task;
import org.openjump.workbench.ui.ErrorHandler;
import org.openjump.workbench.ui.LayerManagerProxy;
import org.openjump.workbench.ui.LayerNamePanelProxy;
import org.openjump.workbench.ui.LayerViewPanelProxy;
import org.openjump.workbench.ui.LayerableNamePanelProxy;

/**
 * Convenience methods for accessing the various elements in the Workbench
 * structure. Some getters return null -- subclasses may choose to override them
 * or leave them unimplemented, depending on their needs.
 */
public abstract class WorkbenchContext implements LayerViewPanelProxy,
        LayerNamePanelProxy, LayerableNamePanelProxy, LayerManagerProxy {


    abstract public JUMPWorkbench getWorkbench();

    abstract public ErrorHandler getErrorHandler();

    abstract public Blackboard getBlackboard();


    abstract public LayerableNamePanel getLayerableNamePanel();

    public LayerViewPanel getLayerViewPanel() {
        return null;
    }

    //Sometimes you can have a layer manager but no layer view panel
    //e.g. when the attribute window is at the forefront. [Jon Aquino]
    public LayerManager getLayerManager() {
        return null;
    }

    public Task getTask() {
        return null;
    }

    /**
     * Creates a snapshot of the system for use by plug-ins.
     */
    public ActionContext createPlugInContext() {
        return new ActionContext(this, getTask(), this, getLayerableNamePanel(),
                getLayerViewPanel());
    }

    public FeatureTextWriterRegistry getFeatureTextWriterRegistry() {
        return featureTextWriterRegistry;
    }

    private Registry registry = new Registry();

    private FeatureTextWriterRegistry featureTextWriterRegistry = new FeatureTextWriterRegistry();

    public Registry getRegistry() {
        return registry;
    }
}