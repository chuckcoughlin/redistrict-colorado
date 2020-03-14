/**  
 * Copyright (C) 2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.List;
import java.util.logging.Logger;

import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

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
import redistrict.colorado.bind.EventReceiver;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.ui.GuiUtil;

public class DistrictTreeController extends StackPane implements EventReceiver<ActionEvent>,ChangeListener<TreeItem<String>> {
	private final static String CLSS = "DistrictListController";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final GuiUtil guiu = new GuiUtil();
	private Label headerLabel = new Label("Districts");
	private final TreeView<String> tree; 
	private final TreeItem<String> root;
	private final Node openFolderIcon;
    private final Node closedFolderIcon;
	private final BasicEventDispatcher<ActionEvent> auxEventDispatcher;
	private final EventHandler<ActionEvent> auxEventHandler;
	private final ReadOnlyObjectProperty<TreeItem<String>> selectionModel;
	
	public DistrictTreeController() {
		this.auxEventHandler = new RegionListHolderEventHandler();
		this.auxEventDispatcher = new BasicEventDispatcher<ActionEvent>(auxEventHandler);
		openFolderIcon = guiu.loadImage("images/folder.png");
		closedFolderIcon = guiu.loadImage("images/folder_closed.png");
		this.root = new TreeItem<String> ("Datasets");
        root.setExpanded(false);       
        this.tree = new TreeView<String>(root);
        this.selectionModel = tree.getSelectionModel().selectedItemProperty();
        selectionModel.addListener(this);
        tree.setEditable(false);
        tree.addEventHandler(ActionEvent.ACTION, auxEventHandler);
        populateDatasets();
        getChildren().add(tree);
	}
	
	private void populateDatasets() {
		if(root.isExpanded()) {
			root.setGraphic(openFolderIcon);
		}
		else {
			root.setGraphic(closedFolderIcon);
		}
		List<DatasetModel> datasets = Database.getInstance().getDatasetTable().getDatasets();
		for(DatasetModel datasetModel:datasets) {
			if( DatasetRole.BOUNDARIES.equals(datasetModel.getRole()) ) {
				TreeItem<String> item = new TreeItem<String> (datasetModel.getName());
				root.getChildren().add(item);
				FeatureCollection collection = datasetModel.getFeatures();
				if(collection!=null) {
					String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(datasetModel.getId(), StandardAttributes.ID.name());
					for(Feature feature:collection.getFeatures() ) {
						TreeItem<String> leaf = new TreeItem<String> (feature.getAttribute(nameAttribute).toString());
						item.getChildren().add(leaf);
					}
				}
			}
		}
	}
	@Override
	public BasicEventDispatcher<ActionEvent> getAuxillaryEventDispatcher() {
		return auxEventDispatcher;
	}

	// ================================================ Change Listener =========================================
	@Override
	public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,TreeItem<String> newValue) {
		TreeItem<String> selectedItem = (TreeItem<String>) newValue;
		LOGGER.info(String.format("%s.changed %s: Arg 1 = %s Arg2 = %s", CLSS,observable,oldValue,newValue));
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

	
}
