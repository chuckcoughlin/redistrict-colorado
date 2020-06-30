/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.pane;
import java.util.logging.Logger;

import org.geotools.render.FeatureFilter;
import org.geotools.render.MapLayer;
import org.geotools.render.ShapefileRenderer;
import org.geotools.style.Style;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;
import redistrict.colorado.pref.PreferenceKeys;

/**
 * Render shape as referenced by a plan.
 */
	public class PlanMapRenderer implements MapComponentInitializedListener {
		private final static String CLSS = "PlanMapRenderer";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private DatasetModel model = null;
		private GoogleMapView overlay;
		private boolean overlayReady = false;
		private ShapefileRenderer renderer;
		private final Canvas canvas;
		private FeatureFilter filter;
		private Style style;

		public PlanMapRenderer(Canvas cnvs) {
			this.canvas = cnvs;
			this.renderer = null;
			String key = Database.getInstance().getPreferencesTable().getParameter(PreferenceKeys.GOOGLE_API_KEY);
			this.overlay = new GoogleMapView(key,GoogleMapView.PLAN_PATH);
			overlay.addMapInitializedListener(this);
	        overlay.setDisableDoubleClick(true);
			
			// LineColor, LineWidth, FillColor
			this.style = new Style(Color.BLACK,0.01,Color.BLANCHEDALMOND);  // Initially
			this.filter = new FeatureFilter();
		}

		/**
		 * Modify the displayed image with a filter.
		 * @param f a filter to pan and/or zoom.
		 */
		public void updateFilter(FeatureFilter f) {
			this.filter = f;
			if( model.getFeatures()!=null ) {
				drawMap();
			}
		}
		/**
		 * When a new model is defined or old model modified, make sure that its features are populated on screen.
		 * If the model has not been refreshed from the file yet this session, then do so now.
		 * @param m the model
		 */
		public void updateModel(DatasetModel m,String region) {
			this.model = m;
			if(  model.getFeatures()!=null ) {
				// Make a "layer" with only a single feature
				
				MapLayer layer = new MapLayer(model.getFeatures());
				layer.setTitle(model.getName());
				this.renderer = new ShapefileRenderer(layer);
				drawMap();
			}
		}
		/**
		 * When a new model is defined or old model modified, make sure that its features are populated on screen.
		 * If the model has not been refreshed from the file yet this session, then do so now.
		 * @param m the model
		 */
		public void updateStyle(Style s) {
			this.style = s;
			if( model.getFeatures()!=null ) {
				drawMap();
			}
		}

		private void drawMap() {
			if( overlayReady ) {
				
			}
			if( renderer!=null) {
				Rectangle screenArea = new Rectangle((int)canvas.getWidth(), (int)canvas.getHeight());
				renderer.paint(canvas.getGraphicsContext2D(),screenArea,style,filter);
			}
		}
		
		// ------------------------- MapComponentInitializedListener -----------------------
		@Override
	    public void mapInitialized() {
			LOGGER.info(String.format("%s.mapInitialized: GoogleMap is ready"));
			overlayReady = true;
		}
}
