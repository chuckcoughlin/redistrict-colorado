/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.dataset;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.ui.ButtonPane;
import redistrict.colorado.ui.ComponentIds;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.UIConstants;



public class DatasetListController extends AnchorPane 
							implements EventReceiver<ActionEvent>,ChangeListener<DatasetModel> {
	private final static String CLSS = "DatasetListController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Label headerLabel = new Label("Datasets");
	private ButtonPane buttons = new ButtonPane();
	private ListView<DatasetModel> datasetList;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	private final EventBindingHub hub;
	
	
	public DatasetListController() {
		this.auxEventHandler = new LayerListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		this.hub = EventBindingHub.getInstance();
		datasetList = new ListView<DatasetModel>();
		datasetList.setCellFactory(new DatasetRowFactory());
		datasetList.getSelectionModel().selectedItemProperty().addListener(this);
		datasetList.setMinWidth(UIConstants.LIST_PANEL_WIDTH);
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(buttons);
		getChildren().add(datasetList);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(datasetList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(datasetList,UIConstants.BUTTON_PANEL_HEIGHT);
		setBottomAnchor(buttons,0.);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(datasetList,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(datasetList,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(buttons,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(buttons,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		
		buttons.setDeleteDisabled(true);
		buttons.registerEventReceiver(this.auxEventDispatcher);
		updateUIFromDatabase();
	}


	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class LayerListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			String id = GuiUtil.idFromSource(event.getSource());
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,id));
			if( id.equalsIgnoreCase(ComponentIds.BUTTON_ADD))       {
				DatasetModel model = Database.getInstance().getDatasetTable().createDataset();
				DatasetCache.getInstance().addDataset(model);
				updateUIFromDatabase();
			}
			// Delete the selected layer, then refresh
			else if( id.equalsIgnoreCase(ComponentIds.BUTTON_DELETE)) {
				DatasetModel selectedModel = datasetList.getSelectionModel().getSelectedItem();
				if( selectedModel!=null) {
					Database.getInstance().getDatasetTable().deleteDataset(selectedModel.getId());
					datasetList.getItems().remove(selectedModel);
					DatasetCache.getInstance().removeDataset(selectedModel);
					updateUIFromDatabase();
				}
			}
		}
	}
	/**
	 * Query the Layer table and update the list accordingly. Retain the same selection, if any.
	 */
	private void updateUIFromDatabase() {
		DatasetModel selectedModel = datasetList.getSelectionModel().getSelectedItem();
		long selectedId = UIConstants.UNSET_KEY;
		if( selectedModel!=null ) selectedId = selectedModel.getId();
		selectedModel = null;
		DatasetCache cache = DatasetCache.getInstance();
		List<DatasetModel> layers = Database.getInstance().getDatasetTable().getDatasets();
		datasetList.getItems().clear();
		for(DatasetModel model:layers) {
			datasetList.getItems().add(model);
			cache.addDataset(model);  // No effect if model already in cache
			if( model.getId()==selectedId) selectedModel = model;
		}
		buttons.setDeleteDisabled(selectedModel==null);	
	}

	/**
	 * Listen for changes to the selected layer based on actions in the list.
	 */
	@Override
	public void changed(ObservableValue<? extends DatasetModel> source, DatasetModel oldValue, DatasetModel newValue) {
		LOGGER.info(String.format("%s.changed: selected = %s", CLSS,(newValue==null?"null":newValue.getName())));
		buttons.setDeleteDisabled(newValue==null);
		hub.setSelectedDataset(newValue);
	}
}
