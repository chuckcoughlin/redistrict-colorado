/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.bind;

import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DistrictModel;
import redistrict.colorado.core.PlanModel;
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
public class EventBindingHub  {
	private static final String CLSS = "EventRoutingHub";
	private static final Logger LOGGER = Logger.getLogger(CLSS);
	private static EventBindingHub instance = null;
	private final SimpleObjectProperty<LeftSelectionEvent> leftSideSelection;
	private final SimpleObjectProperty<ViewMode> mode;
	private final SimpleStringProperty message;
	private final SimpleObjectProperty<DatasetModel> selectedDataset;
	private final SimpleObjectProperty<List<PlanModel>> activePlans;
	private final SimpleObjectProperty<PlanModel> selectedPlan;
	private final SimpleObjectProperty<DistrictModel> selectedDistrict;
	private final SimpleObjectProperty<AnalysisModel> analysisModel;
	/**
	 * Constructor is private per Singleton pattern.
	 */
	private EventBindingHub() {
		this.mode = new SimpleObjectProperty<ViewMode>();
		this.message = new SimpleStringProperty();
		this.leftSideSelection = new SimpleObjectProperty<LeftSelectionEvent>();
		this.selectedDataset = new SimpleObjectProperty<DatasetModel>();
		this.selectedPlan = new SimpleObjectProperty<PlanModel>();
		this.activePlans = new SimpleObjectProperty<List<PlanModel>>();
		this.selectedDistrict = new SimpleObjectProperty<DistrictModel>();
		this.analysisModel = new SimpleObjectProperty<AnalysisModel>();
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static EventBindingHub getInstance() {
		if( instance==null) {
			synchronized(EventBindingHub.class) {
				instance = new EventBindingHub();
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
	public boolean issetLeftSideSelectionLeftSideSelected() { return (leftSideSelection.get()!=null); }
	public void setLeftSideSelection(LeftSelectionEvent event) { leftSideSelection.set(event); }
	public void unselectLeftSide() { leftSideSelection.set(null); }
	public void addLeftSideSelectionListener(ChangeListener<LeftSelectionEvent> listener) {leftSideSelection.addListener(listener);}
	public SimpleObjectProperty<LeftSelectionEvent> leftSideSelectionProperty(){return leftSideSelection;}
	// Selected Layer
	public DatasetModel getSelectedDataset() { return selectedDataset.get(); }
	public boolean isDatasetSelected() { return (selectedDataset.get()!=null); }
	public void setSelectedDataset(DatasetModel model) { selectedDataset.set(model); }
	public void unselectDataset() { selectedDataset.set(null); }
	public void addDatasetListener(ChangeListener<DatasetModel> listener) {selectedDataset.addListener(listener);}
	public SimpleObjectProperty<DatasetModel> selectedDatasetProperty(){return selectedDataset;}
	// Selected Plan(s)
	public List<PlanModel> getActivePlans() { return activePlans.get(); }
	public void setActivePlans(List<PlanModel> models) { activePlans.set(models); }
	public PlanModel getSelectedPlan() { return selectedPlan.get(); }
	public boolean isPlanSelected() { return (selectedPlan.get()!=null); }
	public void setSelectedPlan(PlanModel model) {selectedPlan.set(model);}
	public void unselectPlan() { selectedPlan.set(null); }
	public SimpleObjectProperty<PlanModel> selectedPlanProperty(){return selectedPlan; }
	public void addPlanListener(ChangeListener<PlanModel> listener) {selectedPlan.addListener(listener);}
	// Analysis Model
	public AnalysisModel getAnalysisModel() { return analysisModel.get(); }
	public SimpleObjectProperty<AnalysisModel> analysisModelProperty(){return analysisModel; }
	
	// Selected District
	public DistrictModel getSelectedDistrict() { return selectedDistrict.get(); }
	public boolean isDistrictSelected() { return (selectedDistrict.get()!=null); }
	public void setSelectedDistrict(DistrictModel model) { selectedDistrict.set(model); }
	public void unselectDistrict() { selectedDistrict.set(null); }
	public void addDistrictListener(ChangeListener<DistrictModel> listener) {selectedDistrict.addListener(listener);}
	public SimpleObjectProperty<DistrictModel> selectedDistrictProperty(){return selectedDistrict;}
}
