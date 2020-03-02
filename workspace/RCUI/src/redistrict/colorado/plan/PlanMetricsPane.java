/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.plan;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.locationtech.jts.geom.Polygon;
import org.openjump.feature.Feature;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.FeatureMetric;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.core.LayerRole;
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
public class PlanMetricsPane extends BasicRightSideNode{
	private final static String CLSS = "PlanMetricsPane";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private LayerModel primaryLayer;
	private PlanModel model;
	private final ObservableList<FeatureMetric> items;
	private final Label headerLabel = new Label("Plan Metrics");
	private final TableView<FeatureMetric> table;
	private final EventHandler<ActionEvent> eventHandler;

	public PlanMetricsPane() {
		super(ViewMode.PLAN,DisplayOption.PLAN_METRICS);
		this.model = hub.getSelectedPlan();
		this.items = FXCollections.observableArrayList();
		this.eventHandler = new ActionEventHandler();
		this.table = new TableView<FeatureMetric>();
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

		TableColumn<FeatureMetric,String> column = new TableColumn<>("Name");
		column.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        column.setResizable(true);
		column.setEditable(false);
		column.setCellValueFactory(stringValueFactory);
		table.getColumns().add(column);
		
		TableColumn<FeatureMetric,Number> dcol = new TableColumn<>("Area");
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

	@Override
	public void updateModel() {
		PlanModel selectedModel = hub.getSelectedPlan();
		if( selectedModel!=null) {
			this.model = selectedModel;
			this.headerLabel.setText(model.getName()+" Metrics");
			this.primaryLayer = Database.getInstance().getLayerTable().getPlanLayer(model.getId(), LayerRole.PRIMARY);
			LOGGER.info(String.format("%s.updateModel: Model is %s", CLSS,model.getName()));
			LOGGER.info(String.format("%s.updateModel: Primary is %s", CLSS,primaryLayer.getName()));
			if( primaryLayer.getFeatures()==null ) {
				try {
					primaryLayer.setFeatures(ShapefileReader.read(primaryLayer.getShapefilePath()));
				}
				catch( Exception ex) {
					primaryLayer.setFeatures(null);
					String msg = String.format("%s: Failed to parse shapefile %s (%s)",CLSS,primaryLayer.getShapefilePath(),ex.getLocalizedMessage());
					LOGGER.warning(msg);
					EventBindingHub.getInstance().setMessage(msg);
				}
				Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), primaryLayer.getFeatures().getFeatureSchema().getAttributeNames());
			}
			items.clear();
			// Create a metric for each feature
			String idName = Database.getInstance().getAttributeAliasTable().nameForAlias(primaryLayer.getId(), StandardAttributes.ID.name());
			String geoName = Database.getInstance().getAttributeAliasTable().nameForAlias(primaryLayer.getId(), StandardAttributes.GEOMETRY.name());
			
			for(Feature feat:primaryLayer.getFeatures().getFeatures()) {
				FeatureMetric metric = new FeatureMetric(model.getId(),feat.getID());
				if(idName!=null) metric.setName(feat.getString(idName).toString());
				if(geoName!=null) {
					try {
						Polygon geometry = (Polygon)(feat.getAttribute(geoName));
						metric.setArea(geometry.getArea());
						metric.setPerimeter(geometry.getExteriorRing().getLength());
					}
					catch(ClassCastException cce) {
						LOGGER.info(String.format("%s.updateModel: Geometry attribute wa not a polygon (%s)", CLSS,cce.getLocalizedMessage()));
					}
				}
				items.add(metric);
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
