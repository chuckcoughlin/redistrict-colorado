/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.geotools.render.FeatureFilter;
import org.geotools.render.MapLayer;
import org.geotools.render.ShapefileRenderer;
import org.geotools.style.Style;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.core.LayerModel;
import redistrict.colorado.db.Database;

/**
 * Render shapes as referenced by a single layer model in a panel on the screen.
 */
	public class MapRenderer  {
		private final static String CLSS = "MapRenderer";
		private static Logger LOGGER = Logger.getLogger(CLSS);
		private LayerModel model = null;
		private ShapefileRenderer renderer;
		private final Canvas canvas;
		private FeatureFilter filter;
		private Style style;

		public MapRenderer(Canvas cnvs) {
			this.canvas = cnvs;
			this.renderer = null;
			
			// LineColor, LineWidth, FillColor
			this.style = new Style(Color.BLACK,2.0,Color.AQUAMARINE);  // Initially
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
		public void updateModel(LayerModel m) {
			this.model = m;
			if(  model.getFeatures()==null && !model.getShapefilePath().isEmpty()) {
				try {
					model.setFeatures(ShapefileReader.read(model.getShapefilePath()));
					LOGGER.info(String.format("%s.onSave: Shapefile has %d records, %d attributes", CLSS,model.getFeatures().getFeatures().size(),model.getFeatures().getFeatureSchema().getAttributeCount()));
				}
				catch( Exception ex) {
					model.setFeatures(null);
					String msg = String.format("%s.onSave: Failed to parse shapefile %s (%s)",CLSS,model.getShapefilePath(),ex.getLocalizedMessage());
					LOGGER.warning(msg);
					EventBindingHub.getInstance().setMessage(msg);
				}
				Database.getInstance().getFeatureAttributeTable().synchronizeFeatureAttributes(model.getId(), model.getFeatures().getFeatureSchema().getAttributeNames());
			}
			MapLayer layer = new MapLayer(model.getFeatures());
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
			if( renderer!=null) {
				Rectangle screenArea = new Rectangle((int)canvas.getWidth(), (int)canvas.getHeight());
				renderer.paint(canvas.getGraphicsContext2D(),screenArea,style,filter);
			}
		}
}
