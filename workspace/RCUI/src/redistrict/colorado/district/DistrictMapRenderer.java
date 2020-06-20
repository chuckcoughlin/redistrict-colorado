/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.district;
import java.util.Locale;
import java.util.logging.Logger;

import org.geotools.render.FeatureFilter;
import org.geotools.render.MapLayer;
import org.geotools.render.ShapefileRenderer;
import org.geotools.style.Style;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;
import org.openjump.feature.FeatureDataset;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.core.DatasetModel;
import redistrict.colorado.core.StandardAttributes;
import redistrict.colorado.db.Database;
import redistrict.colorado.gmaps.GoogleMapView;
import redistrict.colorado.gmaps.MapComponentInitializedListener;

/**
 * Render a shape that is a single region of the entire shapefile.
 */
	public class DistrictMapRenderer  implements MapComponentInitializedListener {
		private final static String CLSS = "DistrictMapRenderer";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private DatasetModel model = null;
		private String region;
		private final GoogleMapView overlay;
		private ShapefileRenderer renderer;
		private final Canvas canvas;
		private boolean overlayReady = false;
		private FeatureFilter filter;
		private Style style;

		public DistrictMapRenderer(Canvas cnvs) {
			this.canvas = cnvs;
			this.renderer = null;
			this.overlay = new GoogleMapView("API KEY");
			overlay.addMapInitializedListener(this);
	        overlay.setDisableDoubleClick(true);
			
			// LineColor, LineWidth, FillColor
			this.style = new Style(Color.BLACK,0.001,Color.BLANCHEDALMOND);  // Initially
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
		public void updateModel(DatasetModel m,String regionName) {
			this.model = m;
			this.region = regionName;
			FeatureCollection fc = new FeatureDataset(model.getFeatures().getFeatureSchema());
			String nameAttribute = Database.getInstance().getAttributeAliasTable().nameForAlias(model.getId(), StandardAttributes.ID.name());
			for(Feature feat:model.getFeatures().getFeatures()) {
				if(feat.getAttribute(nameAttribute).equals(regionName)) {
					fc.add(feat);
					break;  // There should only be one
				}
			}
			MapLayer layer = new MapLayer(fc);
			layer.setTitle(model.getName());
			this.renderer = new ShapefileRenderer(layer);
			drawMap();
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
				canvas.getGraphicsContext2D().fillRect(0,0,screenArea.getWidth(),screenArea.getHeight());
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
