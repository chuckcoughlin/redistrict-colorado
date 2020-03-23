/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import redistrict.colorado.core.AnalysisModel;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.PlanFeature;
import redistrict.colorado.core.PlanModel;
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
	private PlanModel model;
	private final CheckBox showAffilations;
	private final CheckBox showDemographics;
	private final CheckBox showGeometry;
	private final EventHandler<ActionEvent> eventHandler;
	private final ObservableList<PlanFeature> items;
	private final Label headerLabel = new Label("Plan Features");
	private final TableView<PlanFeature> table;

	public PlanFeaturesPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_FEATURES);
		this.model = hub.getSelectedPlan();
		this.items = FXCollections.observableArrayList();
		this.eventHandler = new ActionEventHandler();
		this.showAffilations = new CheckBox("Affiliations");
		this.showDemographics = new CheckBox("Demographics");
		this.showGeometry = new CheckBox("Geometry");
		this.table = new TableView<PlanFeature>();
		table.setEditable(true);
		table.setPrefSize(UIConstants.FEATURE_TABLE_WIDTH, UIConstants.FEATURE_TABLE_HEIGHT);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		showAffilations.setIndeterminate(false);
		showAffilations.setOnAction(eventHandler);
		showAffilations.setSelected(true);
		showDemographics.setIndeterminate(false);
		showDemographics.setOnAction(eventHandler);
		showDemographics.setSelected(true);
		showGeometry.setIndeterminate(false);
		showGeometry.setOnAction(eventHandler);
		showGeometry.setSelected(true);
		
		headerLabel.getStyleClass().add("list-header-label");
		getChildren().add(headerLabel);
		getChildren().add(showAffilations);
		getChildren().add(showDemographics);
		getChildren().add(showGeometry);
		getChildren().add(table);
		setTopAnchor(headerLabel,0.);
		setTopAnchor(showGeometry,6*UIConstants.BUTTON_PANEL_HEIGHT/5);
		setLeftAnchor(showGeometry,UIConstants.BUTTON_PANEL_HEIGHT/5);
		setTopAnchor(showDemographics,6*UIConstants.BUTTON_PANEL_HEIGHT/5);
		setLeftAnchor(showDemographics,120.);
		setTopAnchor(showAffilations,6*UIConstants.BUTTON_PANEL_HEIGHT/5);
		setLeftAnchor(showAffilations,240.);
		setTopAnchor(table,2*UIConstants.BUTTON_PANEL_HEIGHT);
		setLeftAnchor(headerLabel,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(headerLabel,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setLeftAnchor(table,UIConstants.LIST_PANEL_LEFT_MARGIN);
		setRightAnchor(table,UIConstants.LIST_PANEL_RIGHT_MARGIN);
		setBottomAnchor(table,0.);
		
		
		updateTableColumns();
		updateModel();
	}

	// NOTE: We can populate only when the datasets are configured with standard aliases.
	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			this.model = selectedModel;
			DatasetModel boundaryDataset = model.getBoundary();
			
			if( boundaryDataset==null) {
				LOGGER.warning(String.format("%s.updateModel: Model has no associated boundary dataset", CLSS));
				return;
			}
			this.headerLabel.setText(boundaryDataset.getName()+" Feature Attributes");
			if(model.getMetrics()==null || model.getMetrics().isEmpty()) {
				AnalysisModel am = hub.getAnalysisModel();
				PlanFeatureDialog dialog = new PlanFeatureDialog(model,am);
				dialog.initOwner(getScene().getWindow());
				Optional<List<PlanFeature>> result = dialog.showAndWait();
				if (result.isPresent() ) {
				     model.setMetrics(result.get());
				     Database.getInstance().getPlanTable().updatePlanMetrics(model);
				}
			}
			LOGGER.info(String.format("%s.updateModel: %s has %d attributes", CLSS,model.getName(),model.getMetrics().size()));
			items.clear();
			
			if( model.getMetrics()!=null) {
				for(PlanFeature feat:model.getMetrics()) {
					items.add(feat);
				}
			}
			LOGGER.info(String.format("%s.updateModel: Table has %d rows", CLSS,items.size()));
			table.setItems(items);
		}
	}
	
	/**
	 * Create table columns per the visibility check-boxes.
	 */
	private void updateTableColumns() {
		table.getColumns().clear();
		FMDoubleValueFactory dblValueFactory = new FMDoubleValueFactory();
		FMIntegerValueFactory intValueFactory = new FMIntegerValueFactory();
		FMStringValueFactory stringValueFactory = new FMStringValueFactory();

		TableColumn<PlanFeature,String> column = new TableColumn<>("Name");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(stringValueFactory);
		table.getColumns().add(column);

		if(showGeometry.isSelected()) {
			TableColumn<PlanFeature,Number> dcol = new TableColumn<>("Area");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(dblValueFactory);
			table.getColumns().add(dcol);

			dcol = new TableColumn<>("Perimeter");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(dblValueFactory);
			table.getColumns().add(dcol);
		}
		if(showDemographics.isSelected()) {
			TableColumn<PlanFeature,Number> dcol = new TableColumn<>("Population");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);

			dcol = new TableColumn<>("Black");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);

			dcol = new TableColumn<>("Hispanic");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);

			dcol = new TableColumn<>("White");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);
		}

		if(showAffilations.isSelected()) {
			TableColumn<PlanFeature,Number> dcol = new TableColumn<>("Democrat");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);

			dcol = new TableColumn<>("Republican");
			dcol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
			dcol.setResizable(true);
			dcol.setEditable(false);
			dcol.setCellValueFactory(intValueFactory);
			table.getColumns().add(dcol);
		}
	}
	/**
	 * The checkbox has been selected. Update the model
	 */
	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			updateTableColumns();
			updateModel();
		}
	}
}
