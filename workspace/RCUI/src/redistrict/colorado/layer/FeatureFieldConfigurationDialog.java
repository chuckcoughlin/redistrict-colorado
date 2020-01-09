/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import redistrict.colorado.db.FeatureConfiguration;
import redistrict.colorado.ui.UIConstants;

public class FeatureFieldConfigurationDialog extends Dialog<List<FeatureConfiguration>> {
	private final static String CLSS = "FeatureFieldConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final ObservableList<FeatureConfiguration> items;
	private final TableView<FeatureConfiguration> table;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);


	public FeatureFieldConfigurationDialog(List<FeatureConfiguration> configurations) {
		items = FXCollections.observableArrayList();
		//LOGGER.info(String.format("%s: Adding %d feature fields",CLSS,configurations.size()));
		for(FeatureConfiguration fc:configurations) {
			items.add(fc);
		}
        setTitle("LayerFeatures Editor");
        setHeaderText(String.format("Configure Feature Fields"));
        setResizable(true);
        
        table = new TableView<FeatureConfiguration>();
        table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        TableColumn<FeatureConfiguration,String> column;
        FeatureConfigurationDataFactory factory = new FeatureConfigurationDataFactory();
 

        column = new TableColumn<>("Name");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);

        column = new TableColumn<>("Alias");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Type");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Visible");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Background");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Rank");
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        
        table.setItems(items);
        
        ScrollPane sp = new ScrollPane(table);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        //sp.setPrefSize(2000., 2000.);
        
		DialogPane dialog = this.getDialogPane();
		dialog.setContent(sp);
		dialog.getButtonTypes().add(buttonCancel);
		dialog.getButtonTypes().add(buttonOK);
		dialog.getStyleClass().add(UIConstants.LAYER_EDITOR_CLASS);

		setResultConverter(new Callback<ButtonType, List<FeatureConfiguration>>() {
			@Override
			public List<FeatureConfiguration> call(ButtonType b) {
				if (b == buttonOK) {
					// Set the configuration list from the table.
					
					return configurations;
				}
				return null;
			}
		});
	}
	
	private ScrollBar getVerticalScrollbar(TableView<?> table) {
        ScrollBar result = null;
        for (Node n : table.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }       
        return result;
    }
	
	// In case there's anything to do on a scroll.
	public void scrolled(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double value = newValue.doubleValue();
        LOGGER.info("Scrolled to " + value);
        ScrollBar bar = getVerticalScrollbar(table);
        if (value == bar.getMax()) {
            double targetValue = value * items.size();
            bar.setValue(targetValue / items.size());
        }
    }
}
