/**  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.ui.right;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShapefileReader;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayer;
import org.geotools.renderer.ShapefileRenderer;
import org.geotools.renderer.style.SLD;
import org.geotools.renderer.style.Style;
import org.jfree.fx.FXGraphics2D;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
		
		private Canvas canvas;
		private MapContent content;
		private GraphicsContext gc;

		public MapRenderer(double width, double height) {
			canvas = new Canvas(width, height);
			gc = canvas.getGraphicsContext2D();
		}

		public Node getCanvas() {
			return canvas;
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
			initializeContent();
			drawMap();
		}
		
		private void initializeContent() {
			content = new MapContent();
			content.setTitle((model==null?"":model.getName()));
			// Outline, fill, alpha
			Style style = SLD.createPolygonStyle(Color.BLUE,Color.LIGHT_GRAY,1.0f);
			MapLayer layer = new MapLayer(model.getFeatures(), style);
			content.addLayer(layer);
			content.getViewport().setScreenArea(new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight()));
		}

		private void drawMap() {
			ShapefileRenderer renderer = new ShapefileRenderer();
			renderer.setMapContent(content);
			FXGraphics2D graphics = new FXGraphics2D(gc);
			graphics.setBackground(java.awt.Color.WHITE);
			renderer.paint(graphics, content.getViewport().getBounds(),content.getViewport().getScreenArea());
		}
}
