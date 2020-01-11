/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.layer;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import redistrict.colorado.db.Database;
import redistrict.colorado.db.FeatureConfiguration;
import redistrict.colorado.ui.UIConstants;

public class FeatureAttributeConfigurationDialog extends Dialog<List<FeatureConfiguration>> implements EventHandler<TableColumn.CellEditEvent<FeatureConfiguration,String>> {
	private final static String CLSS = "FeatureAttributeConfigurationDialog";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private final ObservableList<FeatureConfiguration> items;
	private final TableView<FeatureConfiguration> table;
	private ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private ButtonType buttonOK = new ButtonType("Save", ButtonData.OK_DONE);


	public FeatureAttributeConfigurationDialog(List<FeatureConfiguration> configurations) {
		items = FXCollections.observableArrayList();
		//LOGGER.info(String.format("%s: Adding %d feature fields",CLSS,configurations.size()));
		for(FeatureConfiguration fc:configurations) {
			items.add(fc);
		}
        setTitle("FeatureAttributes Editor");
        setHeaderText(String.format("Configure Feature Fields"));
        setResizable(true);
        
        table = new TableView<FeatureConfiguration>();
        table.setEditable(true);
        table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        TableColumn<FeatureConfiguration,String> column;
        FeatureConfigurationCellValueFactory valueFactory = new FeatureConfigurationCellValueFactory();
        FeatureConfigurationCellFactory cellFactory = new FeatureConfigurationCellFactory();

        column = new TableColumn<>("Name");
        column.setEditable(false);
        column.setCellValueFactory(valueFactory);
        table.getColumns().add(column);

        column = new TableColumn<>("Alias");
        column.setEditable(true);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(valueFactory);
        column.setOnEditCommit(this);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Type");
        column.setEditable(true);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(valueFactory);
        column.setOnEditCommit(this);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Visible");
        column.setEditable(true);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(valueFactory);
        column.setOnEditCommit(this);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Background");
        column.setEditable(true);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(valueFactory);
        column.setOnEditCommit(this);
        table.getColumns().add(column);
        
        column = new TableColumn<>("Rank");
        column.setEditable(true);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(valueFactory);
        column.setOnEditCommit(this);
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
					// The configuration list is updated as cells are edited.
					// Use these to update the database before returning.
					Database.getInstance().getFeatureAttributeTable().updateFeatureAttributes(configurations);
					return configurations;
				}
				return null;
			}
		});
	}

	// ======================================== Event Handler ========================================

	/**
	 * The event source is a table column ... A cell edit requires a <ENTER> to complete.
	 * Loss of focus is not enough.
	 */
	@Override
	public void handle(CellEditEvent<FeatureConfiguration, String> event) {
		int row = event.getTablePosition().getRow();
		String column = event.getTableColumn().getText();
		String newValue = event.getNewValue();
		List<FeatureConfiguration> items = event.getTableView().getItems();
		LOGGER.info(String.format("%s.handle %s: row %d = %s",CLSS,column,row,newValue));
		FeatureConfiguration item = items.get(row);
		if( column.equalsIgnoreCase("Alias") ) {
			item.setAlias(newValue);
		}
		else if( column.equalsIgnoreCase("Type") ) {
			
		}
		else if( column.equalsIgnoreCase("Visible") ) {
			item.setVisible((newValue.equalsIgnoreCase("true")?true:false));
		}
		else if( column.equalsIgnoreCase("Color") ) {
			
		}
		else if( column.equalsIgnoreCase("Rank") ) {
			try {
				int rank = Integer.parseInt(newValue);
				item.setRank(rank);
			}
			catch(NumberFormatException nfe) {}
		}
		
	}
	
	public class CheckboxChangeListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
			LOGGER.info(String.format("%s.changed: %s  %s %s",CLSS,arg0.toString(),arg1.toString(),arg2.toString()));
		}
		
	}
}
