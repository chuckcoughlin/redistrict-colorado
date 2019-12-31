/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.RegionModel;
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
	private final SimpleObjectProperty<LeftSelectionEvent> leftSideSelection;
	private final SimpleObjectProperty<ViewMode> mode;
	private final SimpleStringProperty message;
	private final SimpleObjectProperty<LayerModel> selectedLayer;
	private final SimpleObjectProperty<PlanModel> selectedPlan;
	private final SimpleObjectProperty<RegionModel> selectedRegion;
	/**
	 * Constructor is private per Singleton pattern.
	 */
	private EventRoutingHub() {
		this.mode = new SimpleObjectProperty<ViewMode>();
		this.message = new SimpleStringProperty();
		this.leftSideSelection = new SimpleObjectProperty<LeftSelectionEvent>();
		this.selectedLayer = new SimpleObjectProperty<LayerModel>();
		this.selectedPlan = new SimpleObjectProperty<PlanModel>();
		this.selectedRegion = new SimpleObjectProperty<RegionModel>();
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
	// Left-side Selection
	public LeftSelectionEvent getSelection() { return leftSideSelection.get(); }
	public boolean isLeftSideSelected() { return (leftSideSelection.get()!=null); }
	public void setLeftSideSelection(LeftSelectionEvent event) { leftSideSelection.set(event); }
	public void unselectLeftSide() { leftSideSelection.set(null); }
	public void addLeftSideSelectionListener(ChangeListener<LeftSelectionEvent> listener) {leftSideSelection.addListener(listener);}
	public SimpleObjectProperty<LeftSelectionEvent> leftSideSelectionProperty(){return leftSideSelection;}
	// Selected Layer
	public LayerModel getSelectedLayer() { return selectedLayer.get(); }
	public boolean isLayerSelected() { return (selectedLayer.get()!=null); }
	public void setSelectedLayer(LayerModel model) { selectedLayer.set(model); }
	public void unselectLayer() { selectedLayer.set(null); }
	public void addLayerListener(ChangeListener<LayerModel> listener) {selectedLayer.addListener(listener);}
	public SimpleObjectProperty<LayerModel> selectedLayerProperty(){return selectedLayer;}
	// Selected Plan
	public PlanModel getSelectedPlan() { return selectedPlan.get(); }
	public boolean isPlanSelected() { return (selectedPlan.get()!=null); }
	public void setSelectedPlan(PlanModel model) { selectedPlan.set(model); }
	public void unselectPlan() { selectedPlan.set(null); }
	public void addPlanListener(ChangeListener<PlanModel> listener) {selectedPlan.addListener(listener);}
	public SimpleObjectProperty<PlanModel> selectedPlanProperty(){return selectedPlan;}
	// Selected Region
	public RegionModel getSelectedRegion() { return selectedRegion.get(); }
	public boolean isRegionSelected() { return (selectedRegion.get()!=null); }
	public void setSelectedRegion(RegionModel model) { selectedRegion.set(model); }
	public void unselectRegion() { selectedRegion.set(null); }
	public void addRegionListener(ChangeListener<RegionModel> listener) {selectedRegion.addListener(listener);}
	public SimpleObjectProperty<RegionModel> selectedRegionProperty(){return selectedRegion;}
}
