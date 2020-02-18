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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.referencing.ReferencedEnvelope;
import org.openjump.coordsys.CoordinateSystem;

/**
 * Hold the contents of a map for display, including a list of layers, and a {@linkplain MapViewport}
 * defining the device and world bounds of display area.
 *
 * <p>Methods are provided to add, remove and reorder layers. Alternatively, the list of layers can
 * be accessed directly with the {@linkplain #layers()}. 
 * 
 * Operations on the list returned by the {@code layers{}} method are guaranteed to be thread safe,
 * and modifying the list contents will result in {@code MapLayerListEvents} being published.
 *
 * @author Jody Garnett
 * @since 2.7
 * @version $Id$
 */
public class MapContent {
	private final static String CLSS = "MapContent";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    static final String UNDISPOSED_MAPCONTENT_ERROR = "Call MapContent dispose() to prevent memory leaks";

    private final LayerList layerList;        /** Layers to be rendered */
    private final CopyOnWriteArrayList<MapLayerListListener> mapListeners;    
    private String title;
    
    /**
     * Viewport for map rendering.
     *
     * <p>While the map maintains one viewport internally to better reflect a map context document
     * you are free to maintain a separate viewport; or indeed construct many viewports representing
     * tiles to be rendered.
     */
    private MapViewport viewport;
    private final ReadWriteLock monitor;

    /** 
     * Creates a new map content. */
    public MapContent() {
    	this.layerList = new LayerList();
        this.monitor = new ReentrantReadWriteLock();
        this.mapListeners = new CopyOnWriteArrayList<MapLayerListListener>();
    }

    /**
     * Clean up any listeners or cached state associated with this MapContent.
     *
     * <p>Please note that open connections (FeatureSources and GridCoverage readers) are the
     * responsibility of your application and are not cleaned up by this method.
     */
    public void dispose() {
        for (MapLayer layer : layerList) {
            if (layer != null) {
                layer.dispose();
            }
        }
        layerList.clear();

        this.mapListeners.clear();
        this.title = null;
    }

    /**
     * Register interest in receiving a {@link LayerListEvent}. A <code>LayerListEvent</code> is
     * sent if a layer is added or removed, but not if the data within a layer changes.
     *
     * @param listener The object to notify when Layers have changed.
     */
    public void addMapLayerListListener(MapLayerListListener listener) {
        monitor.writeLock().lock();
        try {
            mapListeners.addIfAbsent(listener);
        } 
        finally {
            monitor.writeLock().unlock();
        }
    }

 
    /**
     * Remove interest in receiving {@link LayerListEvent}.
     *
     * @param listener The object to stop sending <code>LayerListEvent</code>s.
     */
    public void removeMapLayerListListener(MapLayerListListener listener) {
        monitor.writeLock().lock();
        try {
            mapListeners.remove(listener);
        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Add a new layer (if not already present).
     *
     * <p>In an interactive setting this will trigger a {@link LayerListEvent}
     *
     * @param layer
     * @return true if the layer was added
     */
    public boolean addLayer(MapLayer layer) {
        monitor.writeLock().lock();
        try {
            return layerList.addIfAbsent(layer);
        } 
        finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Adds all layers from the input collection that are not already present in this map content.
     *
     * @param layers layers to add (may be {@code null} or empty)
     * @return the number of layers added
     */
    public int addLayers(Collection<? extends MapLayer> layers) {
        monitor.writeLock().lock();
        try {
            if (layers == null || layers.isEmpty()) {
                return 0;
            }

            return layerList.addAllAbsent(layers);

        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Removes the given layer, if present, and publishes a {@linkplain MapLayerListEvent}.
     *
     * @param layer the layer to be removed
     * @return {@code true} if the layer was removed
     */
    public boolean removeLayer(MapLayer layer) {
        monitor.writeLock().lock();
        try {
            return layerList.remove(layer);
        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Moves a layer in the layer list. Will fire a MapLayerListEvent.
     *
     * @param sourcePosition existing position of the layer
     * @param destPosition new position of the layer
     */
    public void moveLayer(int sourcePosition, int destPosition) {
        monitor.writeLock().lock();
        try {
            layerList.move(sourcePosition, destPosition);
        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Gets the list of layers for this map content. The returned list has the following
     * characteristics:
     *
     * <ul>
     *   <li>It is "live", ie. changes to its contents will be reflected in this map content.
     *   <li>It is thread-safe. Accessing list elements directly or via a {@linkplain
     *       java.util.ListIterator} returns a snapshot view of the list contents (as per Java's
     *       {@linkplain CopyOnWriteArrayList} class).
     *   <li>Adding a layer to the list, or removing a layer from it, results in a {@linkplain
     *       MapLayerListEvent} being published by the map content.
     * </ul>
     *
     * For these reasons, you should always work directly with the list returned by this method and
     * avoid making copies since they will not have the above behaviour.
     *
     * @return a "live" reference to the layer list for this map content
     */
    public List<MapLayer> layers() {
        monitor.readLock().lock();
        try {
            return layerList;
        } finally {
            monitor.readLock().unlock();
        }
    }

    protected void fireLayerChanged(MapLayer element) {
        monitor.readLock().lock();
        try {
            if (mapListeners == null) {
                return;
            }
            MapLayerListEvent event = new MapLayerListEvent(this, element);
            for (MapLayerListListener mapLayerListListener : mapListeners) {
                try {
                    mapLayerListListener.layerModified(event);
                } catch (Throwable t) {
                    if (LOGGER.isLoggable(Level.FINER)) {
                        LOGGER.logp(
                                Level.FINE,
                                mapLayerListListener.getClass().getName(),
                                "layerMoved",
                                t.getLocalizedMessage(),
                                t);
                    }
                }
            }
        } finally {
            monitor.readLock().unlock();
        }
    }
    


    protected void fireLayerListChanged() {
        monitor.readLock().lock();
        try {
            if (mapListeners == null) {
                return;
            }
            MapLayerListEvent event = new MapLayerListEvent(this);
            for (MapLayerListListener mapLayerListListener : mapListeners) {
                try {
                    mapLayerListListener.layerListModified(event);
                } catch (Throwable t) {
                    if (LOGGER.isLoggable(Level.FINER)) {
                        LOGGER.logp(
                                Level.FINE,
                                mapLayerListListener.getClass().getName(),
                                "layerMoved",
                                t.getLocalizedMessage(),
                                t);
                    }
                }
            }
        } finally {
            monitor.readLock().unlock();
        }
    }


   
    /**
     * Get the bounding box of all the layers in this Map. If all the layers cannot determine the
     * bounding box in the speed required for each layer, then null is returned. We assume that the
     * coordinate system is the same for all layers.
     *
     * @return The bounding box of the features or null if unknown and too expensive for the method
     *     to calculate.
     * @throws IOException if an IOException occurs while accessing the FeatureSource bounds
     */
    public ReferencedEnvelope getMaxBounds() {
        monitor.readLock().lock();
        try {
            CoordinateSystem mapCrs = null;
            if (viewport != null) {
                mapCrs = viewport.getCoordinateSystem();
            }
            ReferencedEnvelope maxBounds = null;

            for (MapLayer layer : layerList) {
                if (layer == null) {
                    continue;
                }
                try {
                    ReferencedEnvelope layerBounds = layer.getBounds();
                    if (layerBounds == null || layerBounds.isEmpty() || layerBounds.isNull()) {
                        continue;
                    }
                    if (maxBounds == null) {
                        maxBounds = new ReferencedEnvelope(layerBounds);
                    }
                    else {
                    	maxBounds.expandToInclude(layerBounds);
                    }
                } 
                catch (Throwable eek) {
                    LOGGER.log(Level.WARNING, "Unable to determine bounds of " + layer, eek);
                }
            }
            if (maxBounds == null) {
                maxBounds = new ReferencedEnvelope(mapCrs);
            }

            return maxBounds;

        } 
        finally {
            monitor.readLock().unlock();
        }
    }

    //
    // Viewport Information
    //

    /**
     * Viewport describing the area visible on screen.
     *
     * <p>Applications may create multiple viewports (perhaps to render tiles of content); the
     * viewport recorded here is intended for interactive applications where it is helpful to have a
     * single viewport representing what the user is seeing on screen.
     *
     * <p>With that in mind; if the user has not already supplied a viewport one will be created:
     *
     * <ul>
     *   <li>The viewport will be configured to show the extent of the current layers as provided by
     *       {@link #getMaxBounds()}.
     *   <li>The viewport will have an empty {@link MapViewport#getBounds()} if no layers have been
     *       added yet.
     * </ul>
     *
     * @return MapViewport describing how to draw this map
     */
    public MapViewport getViewport() {
        monitor.readLock().lock();
        try {
            if (viewport == null) {
                viewport = new MapViewport(getMaxBounds());
            }
            return viewport;
        } finally {
            monitor.readLock().unlock();
        }
    }

    /**
     * Sets the viewport for this map content. The {@code viewport} argument may be {@code null}, in
     * which case a subsequent to {@linkplain #getViewport()} will return a new instance with
     * default settings.
     *
     * @param viewport the new viewport
     */
    public void setViewport(MapViewport viewport) {
        monitor.writeLock().lock();
        try {
            this.viewport = viewport;
        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * The extent of the map currently (sometimes called the map "viewport".
     *
     * <p>Note Well: The bounds should match your screen aspect ratio (or the map will appear
     * squashed). Please note this only covers spatial extent; you may wish to use the user data map
     * to record the current viewport time or elevation.
     */
    ReferencedEnvelope getBounds() {
        monitor.readLock().lock();
        try {
            return getViewport().getBounds();
        } finally {
            monitor.readLock().unlock();
        }
    }

    /**
     * The coordinate reference system used for rendering the map.
     *
     * <p>The coordinate reference system used for rendering is often considered to be the "world"
     * coordinate reference system; this is distinct from the coordinate reference system used for
     * each layer (which is often data dependent).
     *
     * @return coordinate reference system used for rendering the map.
     */
    public CoordinateSystem getCoordinateSystem() {
        monitor.readLock().lock();
        try {
            return getViewport().getCoordinateSystem();
        } 
        finally {
            monitor.readLock().unlock();
        }
    }

    /**
     * Set the <code>CoordinateReferenceSystem</code> for this map's internal viewport.
     *
     * @param crs
     * @throws FactoryException
     * @throws TransformException
     */
    public void setCoordinateSystem(CoordinateSystem crs) {
        monitor.writeLock().lock();
        try {
            getViewport().setCoordinateSystem(crs);
        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Get the title, returns an empty string if it has not been set yet.
     *
     * @return the title, or an empty string if it has not been set.
     */
    public String getTitle() {
        monitor.readLock().lock();
        try {
            return title;
        } finally {
            monitor.readLock().unlock();
        }
    }

    /**
     * Set the title of this context.
     *
     * @param title the title.
     */
    public void setTitle(String title) {
        monitor.writeLock().lock();
        try {
            String old = this.title;
            this.title = title;

        } finally {
            monitor.writeLock().unlock();
        }
    }

    /**
     * Sets the CRS of the viewport, if one exists, based on the first Layer with a non-null CRS.
     * This is called when a new Layer is added to the Layer list. Does nothing if the viewport
     * already has a CRS set or if it has been set as non-editable.
     */
    private void checkViewportCRS() {
        if (viewport != null && getCoordinateSystem() == null && viewport.isEditable()) {

            for (MapLayer layer : layerList) {
                ReferencedEnvelope bounds = layer.getBounds();
                if (bounds != null) {
                    CoordinateSystem crs = bounds.getCoordinateSystem();
                    if (crs != null) {
                        viewport.setCoordinateSystem(crs);
                        return;
                    }
                }
            }
        }
    }

    private class LayerList extends CopyOnWriteArrayList<MapLayer> {

        private static final long serialVersionUID = 8011733882551971475L;

        /**
         * Adds a layer at the specified position in this list. Does nothing if the layer is already
         * present.
         *
         * @param index position for the layer
         * @param element the layer to add
         */
        @Override
        public void add(int index, MapLayer element) {
            if (!contains(element)) {
                super.add(index, element);
                checkViewportCRS();
                fireLayerListChanged();
            }
        }

        /**
         * Adds a layer if it is not already present. Equivalent to {@linkplain
         * #addIfAbsent(Layer)}.
         *
         * @param element the layer to add
         * @return {@code true} if the layer was added; {@code false} if it was already present in
         *     this list
         */
        @Override
        public boolean add(MapLayer element) {
            return addIfAbsent(element);
        }

        /**
         * Adds all layers from the input collection that are not already present in this list.
         * Equivalent to {@code addAllAbsent(layers) > 0}.
         *
         * @param layers candidate layers to add
         * @return {@code true} is any layers were added; {@code false} otherwise
         */
        @Override
        public boolean addAll(Collection<? extends MapLayer> layers) {
            return addAllAbsent(layers) > 0;
        }

        /**
         * Adds all layers from the input collection that are not already present in this list, with
         * the first added layer taking position {@code index}.
         *
         * @param index position of the first added layer in this list
         * @param layers candidate layers to add
         * @return {@code true} if any layers were added; {@code false} otherwise
         */
        @Override
        public boolean addAll(int index, Collection<? extends MapLayer> layers) {
            boolean added = false;
            int pos = index;

            for (MapLayer layer : layers) {
                if (!contains(layer)) {
                    add(pos, layer);
                    added = true;
                    pos++;
                }
            }

            if (added) {
                checkViewportCRS();
                fireLayerListChanged();
            }

            return added;
        }

        /**
         * Adds all layers from the input collection that are not already present in this list.
         *
         * @param layers candidate layers to add
         * @return the number of layers added
         */
        @Override
        public int addAllAbsent(Collection<? extends MapLayer> layers) {
            int added = super.addAllAbsent(layers);
            if (added > 0) {
                checkViewportCRS();
                fireLayerListChanged();
            }
            return added;
        }

        /**
         * Adds a layer if it is not already present.
         *
         * @param element the layer to add
         * @return {@code true} if the layer was added; {@code false} if it was already present in
         *     this list
         */
        @Override
        public boolean addIfAbsent(MapLayer element) {
            boolean added = super.addIfAbsent(element);
            if (added) {
                checkViewportCRS();
                fireLayerListChanged();
            }
            return added;
        }

        /** Removes all layers from this list and calls their {@code dispose} methods. */
        @Override
        public void clear() {
            for (MapLayer element : this) {
                element.dispose();
            }
            super.clear();
            fireLayerListChanged();
        }

        /**
         * Removes the layer at position {@code index} from this list. Note: removing a layer causes
         * its {@code dispose} method to be called, so although a reference to the removed layer is
         * returned by this method it should not be used subsequently.
         *
         * @param index the position of the layer to be removed
         * @return the layer that was removed (will have been disposed)
         */
        @Override
        public MapLayer remove(int index) {
            MapLayer removed = super.remove(index);
            fireLayerListChanged();
            removed.dispose();
            return removed;
        }

        /**
         * Removes the specified element, which much be a Layer, from this list if present. This
         * method calls the layer's {@code dispose} method, so any external references to the layer
         * should be discarded.
         *
         * @param element the element to remove
         * @return {@code true} if removed; {@code false} if not present in this list
         */
        @Override
        public boolean remove(Object element) {
            boolean removed = super.remove(element);
            if (removed) {
            	fireLayerListChanged();
                if (element instanceof MapLayer) {
                    MapLayer layer = (MapLayer) element;
                    layer.dispose();
                }
            }
            return removed;
        }

        /**
         * Removes all layers in the input collection from this list, if present.
         *
         * @param layers the candidate layers to remove
         * @return {@code true} if any layers were removed; {@code false} otherwise
         */
        @Override
        public boolean removeAll(Collection<?> layers) {
            for (Object obj : layers) {
                MapLayer element = (MapLayer) obj;
                if (!contains(element)) {
                    continue;
                }
                element.dispose();
            }
            boolean removed = super.removeAll(layers);
            fireLayerListChanged();
            return removed;
        }

        /**
         * Removes any layers from this list that are not contained in the input collection.
         *
         * @param layers the layers which should not be removed
         * @return {@code true} if any layers were removed; {@code false} otherwise
         */
        @Override
        public boolean retainAll(Collection<?> layers) {
            for (MapLayer element : this) {
                if (!layers.contains(element)) {
                    element.dispose();
                }
            }
            boolean removed = super.retainAll(layers);
            if (removed) {
            	fireLayerListChanged();
            }
            return removed;
        }

        /**
         * Replaces the layer at the given position with another. Equivalent to:
         *
         * <pre><code>
         * remove(index);
         * add(index, element);
         * </code></pre>
         *
         * The same events will be sent to {@link MapLayerListListener} objects as if the above code
         * had been called.
         *
         * @param index position of the layer to be replaced
         * @param element the new layer
         * @return the layer that was replaced
         */
        @Override
        public MapLayer set(int index, MapLayer element) {
            /*
             * Note: rather than calling the superclass set method here
             * we call remove followed by add to ensure correct event
             * and listener handling.
             */
            MapLayer removed = remove(index);
            add(index, element);
            checkViewportCRS();
            return removed;
        }

        /**
         * Moves a layer in this list.
         *
         * @param sourcePosition existing position of the layer
         * @param destPosition new position of the layer
         */
        private void move(int sourcePosition, int destPosition) {
            MapLayer layer = super.remove(sourcePosition);
            super.add(destPosition, layer);
            fireLayerListChanged();
        }
    }
}
