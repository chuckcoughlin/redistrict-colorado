/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.List;
import java.util.logging.Logger;

import org.openjump.feature.AttributeType;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import redistrict.colorado.bind.BasicEventDispatcher;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.bind.LeftSelectionEvent;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.DatasetCache;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.GuiUtil;
import redistrict.colorado.ui.ViewMode;

public class DistrictTreeController extends StackPane implements EventReceiver<ActionEvent>,ChangeListener<TreeItem<String>> {
	private final static String CLSS = "DistrictTreeController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final GuiUtil guiu = new GuiUtil();
	private Label headerLabel = new Label("Districts");
	private final TreeView<String> tree; 
	private final TreeItem<String> root;
    private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final ExpansionListener  expansionListener;
	private final EventHandler<ActionEvent> regionEventHandler;
	private final ReadOnlyObjectProperty<TreeItem<String>> selectionModel;
	
	public DistrictTreeController() {
		this.regionEventHandler = new RegionListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(regionEventHandler);
		this.expansionListener = new ExpansionListener();
		this.root = new TreeItem<String> ("Datasets",guiu.loadImage("images/folder_closed.png"));
        root.setExpanded(false);       
        this.tree = new TreeView<String>(root);
        this.selectionModel = tree.getSelectionModel().selectedItemProperty();
        selectionModel.addListener(this);
        tree.setEditable(false);
        tree.addEventHandler(ActionEvent.ACTION, regionEventHandler);
        root.expandedProperty().addListener(expansionListener); 
        populateDatasets();
        getChildren().add(tree);
	}
	
	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}
	public void populateDatasets() {
		List<DatasetModel> datasets = Database.getInstance().getDatasetTable().getDatasets();
		root.getChildren().clear();
		for(DatasetModel datasetModel:datasets) {
			if( DatasetRole.BOUNDARIES.equals(datasetModel.getRole()) ) {
				TreeItem<String> item = new TreeItem<String> (datasetModel.getName(),guiu.loadImage("images/folder_closed.png"));
				item.expandedProperty().addListener(expansionListener);
				root.getChildren().add(item);
				FeatureCollection collection = datasetModel.getFeatures();
				if(collection!=null) {
					String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(datasetModel.getId(), StandardAttributes.ID.name());
					if( nameAttribute!=null) {
						for(Feature feature:collection.getFeatures() ) {
							try {
								TreeItem<String> leaf = new TreeItem<String> (feature.getAttribute(nameAttribute).toString());
								item.getChildren().add(leaf);
								leaf.addEventHandler(ActionEvent.ACTION, regionEventHandler);
							}
							catch( IllegalArgumentException iae) {
								// An exception here means that the name attribute is not in the schema.
								// This may be due to a change in the shapefile for the dataset
								LOGGER.warning(String.format("%s.populateDaatasets: %s has no ID attribute %s. Delete datasets, re-define and re-save", CLSS,datasetModel.getName(),nameAttribute));
							}
						}
					}
					else {
						LOGGER.warning(String.format("%s.populateDaatasets: %s has no ID attribute specified", CLSS,datasetModel.getName()));
					}
				}
			}
		}
	}

	// ================================================ Change Listener =========================================
	// This is what is triggered on select of a leaf node. We inform the hub of the current dataset and leaf name
	@Override
	public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,TreeItem<String> newValue) {
		if( observable==null || observable.getValue()==null || observable.getValue().getParent()==null || newValue==null ) return;
		//LOGGER.info(String.format("%s.changed %s:%s", CLSS,observable.getValue().getParent().getValue(),newValue.getValue()));
		EventBindingHub hub = EventBindingHub.getInstance();
		String datasetName = observable.getValue().getParent().getValue();
		DatasetModel dm = DatasetCache.getInstance().getDataset(datasetName);
		hub.setSelectedDataset(dm);
		hub.setSelectedRegion(newValue.getValue());
		hub.setLeftSideSelection(new LeftSelectionEvent(ViewMode.DISTRICT,DisplayOption.MODEL_MAP));
	}


	// ================================================ Event Handler =========================================
	/**
	 * We've received an event from the button panel (or other). React.
	 */
	public class RegionListHolderEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			LOGGER.info(String.format("%s.handle: Action event: source = %s", CLSS,((Node)event.getSource()).getId()));
		}
	}

	// ================================================ Change Listener =========================================
	/**
	 * Expansion change listener. Require separate class because main already listens on items.
	 */
	public class ExpansionListener implements ChangeListener<Boolean> {
		@Override
	    public void changed(ObservableValue<? extends  Boolean> observable, Boolean oldValue, Boolean newValue) {
	        BooleanProperty bb = (BooleanProperty) observable;
	        LOGGER.info(String.format("%s.changed: %s= %s",CLSS,bb.getBean(),newValue));
	        @SuppressWarnings("unchecked")
			TreeItem<String> t = (TreeItem<String>) bb.getBean();
			if(t.isExpanded()) {
				t.setGraphic(guiu.loadImage("images/folder.png"));
			}
			else {
				t.setGraphic(guiu.loadImage("images/folder_closed.png"));
			}
	    }
	}
}
