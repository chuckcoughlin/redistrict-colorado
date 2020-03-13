/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.DatasetRole;
import redistrict.colorado.core.PlanModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.pane.BasicRightSideNode;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.UIConstants;
import redistrict.colorado.ui.ViewMode;

/**
 * Display metrics by feature
 */
public class PlanFeaturesPane extends BasicRightSideNode{
	private final static String CLSS = "PlanFeaturesPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private DatasetModel primaryDataset;
	private PlanModel model;
	private final ObservableList<PlanFeature> items;
	private final Label headerLabel = new Label("Plan Features");
	private final TableView<PlanFeature> table;
	private final EventHandler<ActionEvent> eventHandler;

	public PlanFeaturesPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_FEATURES);
		this.model = hub.getSelectedPlan();
		this.items = FXCollections.observableArrayList();
		this.eventHandler = new ActionEventHandler();
		this.table = new TableView<PlanFeature>();
		table.setEditable(true);
		table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(table);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(table,UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(table,0.);
		
		FMDoubleValueFactory valueFactory = new FMDoubleValueFactory();
		FMStringValueFactory stringValueFactory = new FMStringValueFactory();
		FMStringCellFactory cellFactory = new FMStringCellFactory();

		TableColumn<PlanFeature,String> column = new TableColumn<>("Name");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(stringValueFactory);
		table.getColumns().add(column);
		
		TableColumn<PlanFeature,Number> dcol = new TableColumn<>("Area");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Perimeter");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Population");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Black");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Hispanic");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("White");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Democrat");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		dcol = new TableColumn<>("Republican");
		dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		dcol.setResizable(true);
		dcol.setEditable(false);
		dcol.setCellValueFactory(valueFactory);
		table.getColumns().add(dcol);
		
		updateModel();
	}

	// NOTE: We can populate only when the datasets are configured with standard aliases.
	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			this.model = selectedModel;
			this.headerLabel.setText(model.getName()+" Feature Attributes");
			this.primaryDataset = Database.getInstance().getDatasetTable().getPlanDataset(model.getId(), DatasetRole.BOUNDARIES);
			if( primaryDataset==null) return;
			LOGGER.info(String.format("%s.updateModel: Model is %s", CLSS,model.getName()));
			LOGGER.info(String.format("%s.updateModel: Primary is %s", CLSS,primaryDataset.getName()));
			items.clear();
			// Populate attributes for each feature
			String idName = Database.getInstance().getAttributeAliasTable().nameForAlias(primaryDataset.getId(), StandardAttributes.ID.name());
			String geoName = Database.getInstance().getAttributeAliasTable().nameForAlias(primaryDataset.getId(), StandardAttributes.GEOMETRY.name());
			
			for(Feature feat:primaryDataset.getFeatures().getFeatures()) {
				PlanFeature attribute = new PlanFeature(model.getId(),feat.getID());
				if(idName!=null) attribute.setName(feat.getString(idName).toString());
				if(geoName!=null) {
					try {
						Polygon geometry = (Polygon)(feat.getAttribute(geoName));
						attribute.setArea(geometry.getArea());
						attribute.setPerimeter(geometry.getExteriorRing().getLength());
					}
					catch(ClassCastException cce) {
						LOGGER.info(String.format("%s.updateModel: Geometry attribute wa not a polygon (%s)", CLSS,cce.getLocalizedMessage()));
					}
				}
				items.add(attribute);
			}
			LOGGER.info(String.format("%s.updateModel: Table has %d rows", CLSS,items.size()));
			table.setItems(items);
		}
	}
	
	/**
	 * The checkbox has been selected. Update the model
	 */
	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			updateModel();
		}
	}
}
