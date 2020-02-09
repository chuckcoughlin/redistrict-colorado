/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.map;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A Layer to be rendered.
 *
 * <p>Layers usually represent a single dataset; and arranged into a z-order by a Map for display.
 *
 * @since 2.7
 * @version 8.0
 */
public abstract class Layer {
	private final static String CLSS = "Layer";
	protected static Logger LOGGER = Logger.getLogger(CLSS);

    /** Human readable title for the layer. */
    protected String title;

    /** Flag to mark the layer as visible when being rendered */
    protected boolean visible;

    /** Flag to mark the layer as selected (for general use by clients) */
    protected boolean selected;

    /** Flag to record that {@linkplain #preDispose()} has been called. */
    private boolean preDispose;

    /** Map of application supplied information. */
    protected Map<String, Object> userData;

    /** Listeners to be notified when layer contents change. */
    protected CopyOnWriteArrayList<MapLayerListener> listenerList;

    /**
     * Notifies all registered listeners about the event.
     *
     * @param eventType The event to be fired
     */
    protected void fireMapLayerListenerLayerChanged(int eventType) {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, eventType);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerChanged(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /** Notifies all registered listeners about the event. */
    protected void fireMapLayerListenerLayerShown() {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, MapLayerEvent.VISIBILITY_CHANGED);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerShown(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /** Notifies all registered listeners about the event. */
    protected void fireMapLayerListenerLayerHidden() {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, MapLayerEvent.VISIBILITY_CHANGED);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerHidden(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /** Notifies all registered listeners about the selection event. */
    protected void fireMapLayerListenerLayerSelected() {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, MapLayerEvent.SELECTION_CHANGED);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerSelected(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /** Notifies all registered listeners about the deselection event. */
    protected void fireMapLayerListenerLayerDeselected() {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, MapLayerEvent.SELECTION_CHANGED);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerDeselected(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /** Notifies all registered listeners that the layer is scheduled to be disposed. */
    protected void fireMapLayerListenerLayerPreDispose() {
        if (listenerList == null) {
            return;
        }
        MapLayerEvent event = new MapLayerEvent(this, MapLayerEvent.PRE_DISPOSE);
        for (MapLayerListener mapListener : listenerList) {
            try {
                mapListener.layerPreDispose(event);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Layer Event failure:" + t, t);
                }
            }
        }
    }

    /**
     * Layer creation; please use a concrete subclass to work with specific content.
     * <p>Note you should dispose() a layer after use.
     */
    protected Layer() {
        visible = true;
        selected = true;
    }


    /**
     * Alerts listeners that this layer has been scheduled to be disposed to give them a chance to
     * finish or cancel any tasks using the layer.
     */
    public void preDispose() {
        if (!preDispose) {
            preDispose = true;
            fireMapLayerListenerLayerPreDispose();
        }
    }

    /**
     * Allows a Layer to clean up any listeners, or internal caches or resources it has added during
     * use.
     */
    public void dispose() {
        if (!preDispose) {
            LOGGER.severe("Layer preDispose was not called prior to calling dispose");
        }

        if (listenerList != null) {
            listenerList.clear();
            listenerList = null;
        }
    }

    /**
     * Get the title of this layer. If title has not been defined then an empty string is returned.
     *
     * @return The title of this layer.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of this layer. A {@link LayerEvent} is fired if the new title is different from
     * the previous one.
     *
     * @param title The title of this layer.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Determines whether this layer is visible or hidden.
     *
     * @return {@code true} if the layer is visible, or {@code false} if hidden
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether the layer is to be shown or hidden when rendering. {@link LayerEvent} is fired
     * if the visibility changed.
     *
     * @param visible {@code true} to show the layer; {@code false} to hide it
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            if (visible) {
                fireMapLayerListenerLayerShown();
            } 
            else {
                fireMapLayerListenerLayerHidden();
            }
        }
    }

    /**
     * Determines whether this layer is selected. Selection status can be used by clients such as
     * {@code JMapPane} for selective processing of layers.
     *
     * @return {@code true} if the layer is selected, or {@code false} otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets layer selection status. This can be used by clients such as {@code JMapPane} for
     * selective processing of layers.
     *
     * @param selected new selection status.
     */
    public void setSelected(boolean selected) {
        if (selected != this.selected) {
            this.selected = selected;
            if (selected) {
                fireMapLayerListenerLayerSelected();
            } else {
                fireMapLayerListenerLayerDeselected();
            }
        }
    }

    /**
     * Application supplied information; may be used as a scratch pad for information such selection.
     *
     * <p>Example:
     *
     * <pre>
     * layer.getUserData().put(&quot;selectable&quot;, true)
     * </pre>
     *
     * @return Application parameters
     */
    public synchronized Map<String, Object> getUserData() {
        if (userData == null) {
            userData = new HashMap<String, Object>();
        }
        return this.userData;
    }

    /**
     * The bounds of the Layer content (if known). The bounds can be used to determine if any of the
     * layers content is "on screen" when rendering the map; however often it is expensive to
     * calculate a layers bounds up front so we are allowing this value to be optional.
     *
     * <p>The returned bounds are a ReferencedEnvelope using the same CoordinateReferenceSystem as
     * the layers contents.
     *
     * @return layer bounds, null if unknown
     */
    public abstract ReferencedEnvelope getBounds();

    /**
     * The listener is notified when the layer information changes; or when the contents of the
     * layer changes. This is used by interactive map displays to detect when information is
     * modified and needs to be redrawn.
     *
     * @param listener The listener to add to the listener list.
     */
    public synchronized void addMapLayerListener(MapLayerListener listener) {
        if (listenerList == null) {
            listenerList = new CopyOnWriteArrayList<MapLayerListener>();
        }
        boolean added = listenerList.addIfAbsent(listener);
        if (added && listenerList.size() == 1) {
            // when first listener added we can start listening to data
            connectDataListener(true);
        }
    }

    /**
     * Removes a listener from the listener list for this layer.
     *
     * @param listener The listener to remove from the listener list.
     */
    public synchronized void removeMapLayerListener(MapLayerListener listener) {
        if (listenerList != null) {
            listenerList.remove(listener);
            if (listenerList.isEmpty()) {
                connectDataListener(false);
            }
        }
    }

    /**
     * Called in an interactive environment where a Layer is expected to listen to the data source
     * on behalf of the application.
     *
     * <p>This method is called as needed by addMapListener and removeMapListener to allow
     * subclasses to connect any data listeners required in order to issue fireMapLayerChanged
     * events.
     *
     * @param listen true to connect, false to disconnect
     */
    protected void connectDataListener(boolean listen) {}


    //
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName());
        buf.append("[");
        if (title != null && title.length() != 0) {
            buf.append(getTitle());
        }
        if (visible) {
            buf.append(", VISIBLE");
        } 
        else {
            buf.append(", HIDDEN");
        }
        buf.append("]");
        return buf.toString();
    }
}