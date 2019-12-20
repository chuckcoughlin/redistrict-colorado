/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import java.util.logging.Logger;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * This singleton class holds observable properties that allow scattered elements
 * of the UI to bind with each other (albeit indirectly). The singleton nature 
 * provides easy access to the instance from anywhere.
 * 
 * The listeners must support ChangeListener of the appropriate class. Nodes can also bind to this.
 * 
 * The entities that are tracked include:
 * 	ViewMode - plans, layers or routes
 *  Message  - string status message used for logging and status panel.
 */
public class EventRoutingHub {
	private static final String CLSS = "EventRoutingHub";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static EventRoutingHub instance = null;
	private final SimpleObjectProperty<ViewMode> mode;
	private final SimpleStringProperty message;
	private final SimpleLongProperty selectedLayer;
	private final SimpleLongProperty selectedPlan;
	private final SimpleLongProperty selectedRegion;
	/**
	 * Constructor is private per Singleton pattern.
	 */
	private EventRoutingHub() {
		this.mode = new SimpleObjectProperty<ViewMode>();
		this.message = new SimpleStringProperty();
		this.selectedLayer = new SimpleLongProperty(UIConstants.UNSET_KEY);
		this.selectedPlan = new SimpleLongProperty(UIConstants.UNSET_KEY);
		this.selectedRegion = new SimpleLongProperty(UIConstants.UNSET_KEY);
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static EventRoutingHub getInstance() {
		if( instance==null) {
			synchronized(EventRoutingHub.class) {
				instance = new EventRoutingHub();
			}
		}
		return instance;
	}
	
	// Application view mode is determined from the choose list.
	public ViewMode getMode() { return mode.get(); }
	// Run after current activity to avoid ConcurrentModification exception
	public void setMode(ViewMode mt) { mode.set(mt); }
	public void addModeListener(ChangeListener<ViewMode> listener) {
		mode.addListener(listener);
	}
	public SimpleObjectProperty<ViewMode> modeProperty(){return mode;}
	
	// Status message
	public String getMessage() { return message.get(); }
	public void setMessage(String msg) { message.set(msg); }
	public void addMessageListener(ChangeListener<String> listener) {
		message.addListener(listener);
	}
	public SimpleStringProperty messageProperty(){return message;}
	
	// Selected Layer
	public long getSelectedLayer() { return selectedLayer.get(); }
	public boolean isLayerSelected() { return (selectedLayer.get()!=UIConstants.UNSET_KEY); }
	public void setSelectedLayer(long key) { selectedLayer.set(key); }
	public void unselectLayer() { selectedLayer.set(UIConstants.UNSET_KEY); }
	public void addLayerListener(ChangeListener<Number> listener) {
		selectedLayer.addListener(listener);
	}
	public SimpleLongProperty selectedLayerProperty(){return selectedLayer;}
	// Selected Plan
	public long getSelectedPlan() { return selectedPlan.get(); }
	public boolean isPlanSelected() { return (selectedPlan.get()!=UIConstants.UNSET_KEY); }
	public void setSelectedPlan(long key) { selectedPlan.set(key); }
	public void unselectPlan() { selectedPlan.set(UIConstants.UNSET_KEY); }
	public void addPlanListener(ChangeListener<Number> listener) {
		selectedPlan.addListener(listener);
	}
	public SimpleLongProperty selectedPlanProperty(){return selectedPlan;}
	// Selected Region
	public long getSelectedRegion() { return selectedRegion.get(); }
	public boolean isRegionSelected() { return (selectedRegion.get()!=UIConstants.UNSET_KEY); }
	public void setSelectedRegion(long key) { selectedRegion.set(key); }
	public void unselectRegion() { selectedRegion.set(UIConstants.UNSET_KEY); }
	public void addRegionListener(ChangeListener<Number> listener) {
		selectedRegion.addListener(listener);
	}
	public SimpleLongProperty selectedRegionProperty(){return selectedRegion;}
}
