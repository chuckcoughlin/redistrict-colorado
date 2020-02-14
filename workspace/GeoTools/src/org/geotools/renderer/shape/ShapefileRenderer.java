/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.renderer.shape;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.FeatureShape;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayer;
import org.geotools.map.MapLayerListEvent;
import org.geotools.map.MapLayerListListener;
import org.geotools.referencing.ReferencedEnvelope;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.Style;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.feature.Feature;
import org.openjump.feature.FeatureCollection;

/**
 * A LiteRenderer Implementations that is optimized for shapefiles. We have removed 
 * the IndexInfo features and label caching.
 * 
 * @author jeichar
 * @since 2.1.x
 *
 * @source $URL$
 */
public class ShapefileRenderer implements GTRenderer, MapLayerListListener {
	private final static String CLSS = "ShapefileRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private Graphics2D graphics = null;
    private boolean concatTransforms;
    private List<RenderListener> renderListeners = new CopyOnWriteArrayList<RenderListener>();
    private MapContent content = null;

    /** Tolerance used to compare doubles for equality */
    private static final double TOLERANCE = 1e-6;
    private static final GeometryFactory geomFactory = new GeometryFactory();
    private static final Coordinate[] 	COORDS;
    private static final MultiPolygon 	MULTI_POLYGON_GEOM;
    private static final Polygon 		POLYGON_GEOM;
    private static final LinearRing 	LINE_GEOM;
    private static final MultiLineString MULTI_LINE_GEOM;
    private static final Point 			POINT_GEOM;
    private static final MultiPoint 	MULTI_POINT_GEOM;
    
    /**
     * Computes the scale as the ratio between map distances and real world distances,
     * assuming 90dpi and taking into consideration projection deformations and actual
     * earth shape. <br>
     * Use this method only when in need of accurate computation. Will break if the
     * data extent is outside of the currenct projection definition area. 
     */
    public static final String SCALE_ACCURATE = "ACCURATE";
    
    /**
     * Very simple and lenient scale computation method that conforms to the OGC SLD 
     * specification 1.0, page 26. <br>This method is quite approximative, but should
     * never break and ensure constant scale even on lat/lon unprojected maps (because
     * in that case scale is computed as if the area was along the equator no matter
     * what the real position is).
     */
    public static final String SCALE_OGC = "OGC";
    
    private String scaleComputationMethodDEFAULT = SCALE_ACCURATE;
    static {
        COORDS = new Coordinate[5];
        COORDS[0] = new Coordinate(0.0, 0.0);
        COORDS[1] = new Coordinate(5.0, 0.0);
        COORDS[2] = new Coordinate(5.0, 5.0);
        COORDS[3] = new Coordinate(0.0, 5.0);
        COORDS[4] = new Coordinate(0.0, 0.0);
        LINE_GEOM 		= geomFactory.createLinearRing(COORDS);
        MULTI_LINE_GEOM = geomFactory.createMultiLineString(new LineString[]{LINE_GEOM});
        POLYGON_GEOM	 = geomFactory.createPolygon(LINE_GEOM, new LinearRing[0]);
        MULTI_POLYGON_GEOM 	= geomFactory.createMultiPolygon(new Polygon[]{POLYGON_GEOM});
        POINT_GEOM 			= geomFactory.createPoint(COORDS[2]);
        MULTI_POINT_GEOM 	= geomFactory.createMultiPoint(COORDS);
    }


    static int NUM_SAMPLES = 200;

    private double scaleDenominator = 1.0;


    /**
     * Maps between the AttributeType index of the new generated FeatureType and the real
     * attributeType
     */
    int[] attributeIndexing;

    /** The painter class we use to depict shapes onto the screen */
    private final StyledShapePainter painter = new StyledShapePainter();
    
    /**
     * Text will be rendered using the usual calls gc.drawString/drawGlyphVector.
     * This is a little faster, and more consistent with how the platform renders
     * the text in other applications. The downside is that on most platform the label
     * and its eventual halo are not properly centered.
     */
    public static final String TEXT_RENDERING_STRING = "STRING";
    
    /**
     * Text will be rendered using the associated {@link GlyphVector} outline, that is, a {@link Shape}.
     * This ensures perfect centering between the text and the halo, but introduces more text aliasing.
     */
    public static final String TEXT_RENDERING_OUTLINE = "OUTLINE";
    
    /**
     * The text rendering method, either TEXT_RENDERING_OUTLINE or TEXT_RENDERING_STRING
     */
    public static final String TEXT_RENDERING_KEY = "textRenderingMethod";
    private String textRenderingModeDEFAULT = TEXT_RENDERING_STRING;
    
	public static final String FORCE_CRS_KEY = "forceCRS";
	public static final String DPI_KEY = "dpi";
	public static final String DECLARED_SCALE_DENOM_KEY = "declaredScaleDenominator";
	public static final String MEMORY_PRE_LOADING_KEY = "memoryPreloadingEnabled";
	public static final String OPTIMIZED_DATA_LOADING_KEY = "optimizedDataLoadingEnabled";
	public static final String SCALE_COMPUTATION_METHOD_KEY = "scaleComputationMethod";
    


    public ShapefileRenderer() {
    }

    /**
     * This is the paint method used to actually draw the map. We always go from geodesic coordinates
     * to view coordinates.
     */
    public void paint( Graphics2D graphics, Rectangle paintArea, ReferencedEnvelope mapArea ) {
        if (mapArea == null || paintArea == null) {
            LOGGER.info(String.format("%s.paint: paint or mapping area is null",CLSS));
            return;
        } 
        this.graphics = graphics;
        paint(paintArea, mapArea, RendererUtilities.worldToScreenTransform(mapArea,paintArea));
    }

    /**
     * adds a listener that responds to error events of feature rendered events.
     * 
     * @param listener the listener to add.
     * @see RenderListener
     */
    public void addRenderListener( RenderListener listener ) {
        renderListeners.add(listener);
    }

    /**
     * Removes a render listener.
     * 
     * @param listener the listener to remove.
     * @see RenderListener
     */
    public void removeRenderListener( RenderListener listener ) {
        renderListeners.remove(listener);
    }

    private void fireErrorEvent(Exception e) {
        if (renderListeners.size() > 0) {
            RenderListener listener;
            for (int i = 0; i < renderListeners.size(); i++) {
                try {
                    listener = renderListeners.get(i);
                    listener.errorOccurred(e);
                } 
                catch (RuntimeException ignore) {
                    LOGGER.fine("Provided RenderListener could not handle error message:" + ignore);
                    LOGGER.throwing(getClass().getName(), "fireErrorEvent", ignore);
                }
            }
        }
    }

    /**
     * Setter for property scaleDenominator.
     * 
     * @param scaleDenominator New value of property scaleDenominator.
     */
    protected void setScaleDenominator( double scaleDenominator ) {
        this.scaleDenominator = scaleDenominator;
    }


    public boolean isConcatTransforms() {
        return concatTransforms;
    }

    public void setConcatTransforms( boolean concatTransforms ) {
        this.concatTransforms = concatTransforms;
    }


    private void paint(Rectangle paintArea, ReferencedEnvelope envelope,AffineTransform transform ) {
        if( transform == null ){
            throw new NullPointerException("Transform is required");
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Affine Transform is " + transform);
        }

        /*
         * If we are rendering to a component which has already set up some form of transformation
         * then we can concatenate our transformation to it. An example of this is the ZoomPane
         * component of the swinggui module.
         */
        if (concatTransforms) {
            AffineTransform atg = graphics.getTransform();
            atg.concatenate(transform);
            transform = atg;
        }

        try {
            setScaleDenominator(  
                    computeScale(
                            envelope,
                            content.getCoordinateSystem(),     // Coordinate system
                            paintArea, 
                            transform));
        } 
        catch (Exception e)  {  // probably either (1) no CRS (2) error xforming
            LOGGER.throwing("RendererUtilities", "calculateScale(envelope, coordinateReferenceSystem, imageWidth, imageHeight, hints)", e);
            setScaleDenominator(1 / transform.getScaleX()); // DJB old method - the best we can do            
        }

        List<MapLayer> layers = content.layers();
        for( MapLayer layer:layers ) {

        	if (layer.isVisible()) {
        		try {
        			layer.getBounds();
        			Style style = layer.getStyle();
        			FeatureCollection collection = layer.getFeatures();
        			for( Feature feature:collection.getFeatures()) {
        				FeatureShape shape = new FeatureShape(feature,transform);
        				painter.paint(graphics, shape, style, scaleDenominator);
        			}
        		} 
        		catch (Exception exception) {
        			Exception e = new Exception(String.format("%s.paint: Exception rendering layer %s",CLSS,layer.getTitle()), exception);
        			fireErrorEvent(e);
        		}
        	}
        }
    }
    /**
     * <p>
     * Returns scale computation algorithm to be used. 
     * </p>
     */
    private String getScaleComputationMethod() {
        return scaleComputationMethodDEFAULT;
    }
    
    // Does not consider rotation
    private double computeScale(ReferencedEnvelope envelope, CoordinateSystem crs, Rectangle paintArea,
            AffineTransform worldToScreen) {
        if(getScaleComputationMethod().equals(SCALE_ACCURATE)) {
            try {
               return RendererUtilities.calculateScale(envelope, paintArea.width, paintArea.height);
            } 
            catch (Exception e) { // probably either (1) no CRS (2) error xforming
                LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        } 
        return RendererUtilities.calculateOGCScale(envelope, paintArea.width,null);
    }


	@Override
	public void setMapContent(MapContent mapContent) {
		this.content = mapContent;
	}

	@Override
	public MapContent getMapContent() {return this.content;}

	/**
	 * The layer element has been modified. If it is visible, re-paint.
	 */
	@Override
	public void layerModified(MapLayerListEvent event) {
		MapLayer layer = event.getLayer();
		if( layer.isVisible()) {
			//paint( graphics, Rectangle paintArea, ReferencedEnvelope envelope)
		}
		
	}

	/**
	 * The layer list has been modified. Re-paint.
	 */
	@Override
	public void layerListModified(MapLayerListEvent event) {
		//paint(graphics, Rectangle paintArea, ReferencedEnvelope envelope)
		
	}
}