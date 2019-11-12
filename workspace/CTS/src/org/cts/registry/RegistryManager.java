/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the OrbisGIS code repository.
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/orbisgis/cts/>
 */
package org.cts.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class manages all supported registry. It permits to declare a custom
 * registry or remove one.
 *
 * @author Erwan Bocher
 */
public final class RegistryManager {
	private static final String CLSS = "RegistryManager";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
    private final Map<String, Registry> registries = new HashMap<String, Registry>();
    private final List<RegistryManagerListener> listeners = new ArrayList<RegistryManagerListener>();

    /**
     * Creates a default registry manager without any registered {@link Registry}.
     * To load registries, use {@code addRegistry} method
     * (eg : addRegistry(new IGNFRegistry()));
     */
    public RegistryManager() {
    }

    /**
     * Adds a listener able to process add/remove registry events.
     * @param listener
     */
    public void addRegistryManagerListener(RegistryManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the listener if it is present in the listener list.
     *
     * @param listener
     * @return true if the listener was successfully removed. False if the
     * specified parameter was not a listener
     */
    public boolean removeRegistryManagerListener(RegistryManagerListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Register a {@link Registry} in this {@code RegistryManager}.
     * @param registryClass
     */
    public void addRegistry(Registry registryClass) {
        addRegistry(registryClass, false);
    }

    /**
     * Register a {@link Registry} in this {@code RegistryManager}.
     * An existing registry can be replaced by a new one.
     * Registries are stored in a case-insensitive map (keys are uppercase)
     * 
     * @param registry the Registry to add
     * @param replace whether an existing Registry with the same name should be
     *                replaced  or not.
     */
    public void addRegistry(Registry registry, boolean replace) {
        LOGGER.fine("Adding a new registry " + registry.getRegistryName());
        String registryName = registry.getRegistryName().toUpperCase();
        if (!replace && registries.containsKey(registryName)) {
            throw new IllegalArgumentException("Registry " + registryName
                    + " already exists");
        }
        registries.put(registryName, registry);
        fireRegistryAdded(registry.getRegistryName());
    }

    /**
     * Informs listeners that a registry has been added.
     *
     * @param registryName name of the registry
     */
    private void fireRegistryAdded(String registryName) {
        for (RegistryManagerListener listener : listeners) {
            listener.registryAdded(registryName);
        }
    }

    /**
     * Returns whether a registry with the given name is already
     * registered or not.
     *
     * @param name a registry name ie epsg, ignf, esri...
     * @return true if name is already registered
     */
    public boolean contains(String name) {
        return registries.containsKey(name.toUpperCase());
    }

    /**
     * Gets all registered registry names
     * The returned array contains a case-sensitive version of registry names.
     *
     * @return an array of names
     */
    public String[] getRegistryNames() {
        LOGGER.fine("Getting all registry names");
        List<String> names = new ArrayList<String>();
        for (Registry r : registries.values()) {
            names.add(r.getRegistryName());
        }
        return names.toArray(new String[0]);
    }

    /**
     * Gets the {@link Registry} registered with this name or
     * null if no Registry has been registered with this name.
     * @param registryName
     * @return 
     */
    public Registry getRegistry(String registryName) {
        LOGGER.fine("Getting the registry " + registryName);
        return registries.get(registryName.toUpperCase());
    }
}
