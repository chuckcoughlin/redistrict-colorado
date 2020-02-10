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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.Decimator;
import org.geotools.geometry.jts.GeometryClipper;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.NumberRange;
import org.locationtech.jts.awt.PolygonShape;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.util.GeometryEditor.CoordinateOperation;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.feature.Feature;
import org.openjump.io.ShapefileReader;

/**
 * A LiteRenderer Implementations that is optimized for shapefiles. We have removed 
 * the IndexInfo features.
 * 
 * @author jeichar
 * @since 2.1.x
 *
 * @source $URL$
 */
public class ShapefileRenderer implements GTRenderer {
	private final static String CLSS = "ShapefileRenderer";
	private static Logger LOGGER = Logger.getLogger(CLSS);

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
    private RenderingHints hints;
    private boolean renderingStopRequested;
    private boolean concatTransforms;
    private List<RenderListener> renderListeners = new CopyOnWriteArrayList<RenderListener>();
    /** If we are caching styles; by default this is false */
    boolean caching = false;
    private double scaleDenominator;
    private Object defaultGeom;


    /**
     * Maps between the AttributeType index of the new generated FeatureType and the real
     * attributeType
     */
    int[] attributeIndexing;

    /** The painter class we use to depict shapes onto the screen */
    private StyledShapePainter painter = new StyledShapePainter();
    private Map decimators = new HashMap();
    
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
    
	public static final String LABEL_CACHE_KEY = "labelCache";
	public static final String FORCE_CRS_KEY = "forceCRS";
	public static final String DPI_KEY = "dpi";
	public static final String DECLARED_SCALE_DENOM_KEY = "declaredScaleDenominator";
	public static final String MEMORY_PRE_LOADING_KEY = "memoryPreloadingEnabled";
	public static final String OPTIMIZED_DATA_LOADING_KEY = "optimizedDataLoadingEnabled";
	public static final String SCALE_COMPUTATION_METHOD_KEY = "scaleComputationMethod";
    
    /**
     * "optimizedDataLoadingEnabled" - Boolean  yes/no (see default optimizedDataLoadingEnabledDEFAULT)
     * "memoryPreloadingEnabled"     - Boolean  yes/no (see default memoryPreloadingEnabledDEFAULT)
     * "declaredScaleDenominator"    - Double   the value of the scale denominator to use by the renderer.  
     *                                          by default the value is calculated based on the screen size 
     *                                          and the displayed area of the map.
     *  "dpi"                        - Integer  number of dots per inch of the display 90 DPI is the default (as declared by OGC)      
     *  "forceCRS"                   - CoordinateReferenceSystem declares to the renderer that all layers are of the CRS declared in this hint                               
     *  "labelCache"                 - Declares the label cache that will be used by the renderer.                               
     */
    private Map rendererHints = null;
    
    
    

    public ShapefileRenderer() {
    }

    /**
     * This is the paint method used to actually draw the map.
     */
    public void paint( Graphics2D graphics, Rectangle paintArea, ReferencedEnvelope mapArea ) {
        if (mapArea == null || paintArea == null) {
            LOGGER.info("renderer passed null arguments");
            return;
        } // Other arguments get checked later
        paint(graphics, paintArea, mapArea, RendererUtilities.worldToScreenTransform(mapArea,paintArea));
    }

    /**
     * Return provided geom; or use a default value if null.
     * 
     * @param geom Provided Geometry as read from record.shape()
     * @param defaultGeometry GeometryDescriptor used to determine default value
     * @return provided geom or default value if null
     */
    private Object getGeom( Object geom, GeometryDescriptor defaultGeometry ) {
        if( geom instanceof Geometry){
            return geom;
        }
        return getGeom( defaultGeometry );
    }

    /**
     * This class keeps a couple of default geometries on hand to use
     * when making a feature with default values.
     * 
     * @param defaultGeometry
     * @return placeholder to use as a default while waiting for a real geometry.
     */
    private Object getGeom(GeometryDescriptor defaultGeometry) {
        Class binding = defaultGeometry.getType().getBinding();
        if (MultiPolygon.class.isAssignableFrom(binding)) {
            return MULTI_POLYGON_GEOM;
        }
        else if (MultiLineString.class.isAssignableFrom(binding)) {
            return MULTI_LINE_GEOM;
        }
        else if (Point.class.isAssignableFrom(binding)) {
            return POINT_GEOM;
        }
        else if (MultiPoint.class.isAssignableFrom(binding)) {
            return MULTI_POINT_GEOM;
        }
        return null; // we don't have a good default value - null will need to do
    }
    
    /**
     * DOCUMENT ME!
     * 
     * @param query
     * @param style
     * @param schema DOCUMENT ME!
     * @return
     * @throws FactoryConfigurationError
     * @throws SchemaException
     */
    public SimpleFeatureType createFeatureType( Query query, Style style, ShapefileDataStore ds)
            throws SchemaException, IOException {
        SimpleFeatureType schema = ds.getSchema();
        String[] attributes = findStyleAttributes((query == null) ? Query.ALL : query, style,
                schema);
        AttributeDescriptor[] types = new AttributeDescriptor[attributes.length];
        attributeIndexing = new int[attributes.length];
        
        if (attributes.length == 1
                && attributes[0].equals(schema.getGeometryDescriptor().getLocalName())) {
            types[0] = schema.getDescriptor(attributes[0]);
            
            // the symbolizer might be referring "THE_GEOM" for example
            if (types[0] == null)
                throw new IllegalArgumentException("Attribute " + attributes[0]
                        + " does not exist. Maybe it has just been spelled wrongly?");
        } else {
            for( int i = 0; i < types.length; i++ ) {
                types[i] = schema.getDescriptor(attributes[i]);
    
                if (types[i] == null)
                    throw new IllegalArgumentException("Attribute " + attributes[i]
                            + " does not exist. Maybe it has just been spelled wrongly?");
                for( int j = 0; j < schema.getAttributeCount(); j++ ) {
                    if (schema.getDescriptor(j).getLocalName().equals(attributes[i])) {
                        attributeIndexing[i] = j - 1;
    
                        break;
                    }
                    
                }
            }
        }

        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName( schema.getName() );
        tb.addAll( types );
        tb.setDefaultGeometry( schema.getGeometryDescriptor().getLocalName() );
        
        return tb.buildFeatureType();
    }

    /**
     * Inspects the <code>MapLayer</code>'s style and retrieves it's needed attribute names,
     * returning at least the default geometry attribute name.
     * 
     * @param query DOCUMENT ME!
     * @param style the <code>Style</code> to determine the needed attributes from
     * @param schema the SimpleFeatureSource schema
     * @return the minimun set of attribute names needed to render <code>layer</code>
     */
    private String[] findStyleAttributes( final Query query, Style style, SimpleFeatureType schema ) {
        StyleAttributeExtractor sae = new StyleAttributeExtractor();
        sae.visit(style);

        
        FilterAttributeExtractor qae = new FilterAttributeExtractor();
        query.getFilter().accept(qae,null);
        Set ftsAttributes = new LinkedHashSet(sae.getAttributeNameSet());
        ftsAttributes.addAll(qae.getAttributeNameSet());
        if (sae.getDefaultGeometryUsed()
				&& (!ftsAttributes.contains(schema.getGeometryDescriptor().getLocalName()))) {
        	ftsAttributes.add(schema.getGeometryDescriptor().getLocalName());
		} else {
	        // the code following assumes the geometry column is the last one
		    // make sure it's the last for good
	        ftsAttributes.remove(schema.getGeometryDescriptor().getLocalName());
	        ftsAttributes.add(schema.getGeometryDescriptor().getLocalName());
		}
        return (String[]) ftsAttributes.toArray(new String[0]);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param graphics
     * @param feature DOCUMENT ME!
     * @param geom
     * @param symbolizers
     * @param scaleRange
     * @param layerId 
     */
    private void processSymbolizers( Graphics2D graphics, SimpleFeature feature, Object geom,
            Symbolizer[] symbolizers, NumberRange scaleRange, boolean isJTS, String layerId, Rectangle screenSize) {
        for( int m = 0; m < symbolizers.length; m++ ) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer("applying symbolizer " + symbolizers[m]);
            }

            if (renderingStopRequested) {
                break;
            }

            if (symbolizers[m] instanceof TextSymbolizer) {
                try {
                    labelCache.put(layerId,(TextSymbolizer) symbolizers[m], 
                            feature, 
                            new LiteShape2((Geometry)feature.getDefaultGeometry(), null, null, false, false),
                            scaleRange);
                } catch (Exception e) {
                    fireErrorEvent(e);
                }
            } else {
                Shape shape;
                try {
                    Style2D style = styleFactory.createStyle(feature, symbolizers[m], scaleRange);
                    if( isJTS ){
                        Geometry g;
                        if(symbolizers[m] instanceof PointSymbolizer) {
                            g = RendererUtilities.getCentroid((Geometry) geom);
                        } else {
                            g = (Geometry) geom;
                        }
                        
                        // clip to the visible area + the size of the symbolizer (with some extra 
                        // to make sure we get no artifacts from polygon new borders)
                        double size = RendererUtilities.getStyle2DSize(style) + 10;
                        Envelope env = new Envelope(screenSize.getMinX(), screenSize.getMaxX(), screenSize.getMinY(), screenSize.getMaxY());
                        env.expandBy(size);
                        final GeometryClipper clipper = new GeometryClipper(env);
                        Geometry clipped = clipper.clip(g, false);
                        if(clipped == null) 
                            continue;
                        shape = new LiteShape2(clipped, null, null, false);
                        
                        painter.paint(graphics, shape, style, scaleDenominator);
                    }else{
                        if(symbolizers[m] instanceof PointSymbolizer) {
                            shape = new LiteShape2(RendererUtilities.getCentroid((Geometry) feature.getDefaultGeometry()), null, null, false, false);
                        } else {
                            shape = getShape((SimpleGeometry) geom);
                        }
                            
                        painter.paint(graphics, shape, style, scaleDenominator);
                    }
                } catch (Exception e) {
                    fireErrorEvent(e);
                }            
            }

        }
        fireFeatureRenderedEvent(feature);
    }

    /**
     * Applies each of a set of symbolizers in turn to a given feature.
     * <p>
     * This is an internal method and should only be called by processStylers.
     * </p>
     * 
     * @param graphics
     * @param feature The feature to be rendered
     * @param symbolizers An array of symbolizers which actually perform the rendering.
     * @param scaleRange The scale range we are working on... provided in order to make the style
     *        factory happy
     * @param transform DOCUMENT ME!
     * @param layerId 
     * @throws TransformException
     * @throws FactoryException
     */
    private void processSymbolizers( final Graphics2D graphics, final SimpleFeature feature,
            final Symbolizer[] symbolizers, NumberRange scaleRange, MathTransform transform, String layerId ) {
        LiteShape2 shape;

        for( int m = 0; m < symbolizers.length; m++ ) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer("applying symbolizer " + symbolizers[m]);
            }

            Geometry g = (Geometry) feature.getDefaultGeometry();
            if(symbolizers[m] instanceof PointSymbolizer)
                g = RendererUtilities.getCentroid(g);
            shape = new LiteShape2(g, transform, getDecimator(transform), false);

            
                Style2D style = styleFactory.createStyle(feature, symbolizers[m], scaleRange);
                painter.paint(graphics, shape, style, scaleDenominator);
            
        }

        fireFeatureRenderedEvent(feature);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param mathTransform DOCUMENT ME!
     * @return
     * @throws org.opengis.referencing.operation.NoninvertibleTransformException
     */
    private Decimator getDecimator( MathTransform mathTransform  )
            throws NoninvertibleTransformException {
        Decimator decimator=null;
        
        if( mathTransform!=null )
            decimator = (Decimator) decimators.get(mathTransform);

        if (decimator == null) {
            decimator = new Decimator(mathTransform.inverse());

            decimators.put(mathTransform, decimator);
        }

        return decimator;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param geom
     * @return
     */
    private Shape getShape( SimpleGeometry geom ) {
        if ((geom.type == ShapeType.ARC) || (geom.type == ShapeType.ARCM)
                || (geom.type == ShapeType.ARCZ)) {
            return new MultiLineShape(geom);
        }

        if ((geom.type == ShapeType.POLYGON) || (geom.type == ShapeType.POLYGONM)
                || (geom.type == ShapeType.POLYGONZ)) {
            return new PolygonShape(geom);
        }

        if ((geom.type == ShapeType.POINT) || (geom.type == ShapeType.POINTM)
                || (geom.type == ShapeType.POINTZ) || (geom.type == ShapeType.MULTIPOINT)
                || (geom.type == ShapeType.MULTIPOINTM) || (geom.type == ShapeType.MULTIPOINTZ)) {
            return new MultiPointShape(geom);
        }
        
        

        return null;
    }

    /**
     * Checks if a rule can be triggered at the current scale level
     * 
     * @param r The rule
     * @return true if the scale is compatible with the rule settings
     */
    private boolean isWithInScale( Rule r ) {
        return ((r.getMinScaleDenominator() - TOLERANCE) <= scaleDenominator)
                && ((r.getMaxScaleDenominator() + TOLERANCE) > scaleDenominator);
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

    private void fireFeatureRenderedEvent( Feature feature ) {
        if (renderListeners.size() > 0) {
            RenderListener listener;
            for (int i = 0; i < renderListeners.size(); i++) {
                listener = renderListeners.get(i);
                listener.featureRenderer((Feature) feature);
            }
        }
    }

    private void fireErrorEvent(Exception e) {
        if (renderListeners.size() > 0) {
            RenderListener listener;
            for (int i = 0; i < renderListeners.size(); i++) {
                try {
                    listener = renderListeners.get(i);
                    listener.errorOccurred(e);
                } catch (RuntimeException ignore) {
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

    /**
     * If you call this method from another thread than the one that called <code>paint</code> or
     * <code>render</code> the rendering will be forcefully stopped before termination
     */
    public void stopRendering() {
        renderingStopRequested = true;
        labelCache.stop();
    }

    /**
     * True if we are caching styles.
     * 
     * @return <code>ture </code>if caching
     */
    public boolean isCaching() {
        return caching;
    }

    /**
     * Set to true to cache styles.
     * 
     * @param caching The caching to set.
     */
    public void setCaching( boolean caching ) {
        this.caching = caching;
    }


    public boolean isConcatTransforms() {
        return concatTransforms;
    }

    public void setConcatTransforms( boolean concatTransforms ) {
        this.concatTransforms = concatTransforms;
    }



    /**
     * By default ignores all feature renderered events and logs all exceptions as severe.
     */
    private static class DefaultRenderListener implements RenderListener {
        /**
         * @see org.geotools.renderer.lite.RenderListener#featureRenderer(org.geotools.feature.Feature)
         */
        public void featureRenderer( Feature feature ) {
            // do nothing.
        }

        /**
         * @see org.geotools.renderer.lite.RenderListener#errorOccurred(java.lang.Exception)
         */
        public void errorOccurred( Exception e ) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void setJava2DHints( RenderingHints hints ) {
        this.hints = hints;
    }

    public RenderingHints getJava2DHints() {
        return hints;
    }

    public void setRendererHints(Map hints) {
        rendererHints = hints;
    }
    
    public Map getRendererHints() {
        return rendererHints;
    }

    public void paint( Graphics2D graphics, Rectangle paintArea, AffineTransformation worldToScreen ) {
        if (worldToScreen == null || paintArea == null) {
            LOGGER.info("renderer passed null arguments");
            return;
        } // Other arguments get checked later
        // First, create the bbox in real world coordinates
        ReferencedEnvelope mapArea;
        try {
            mapArea = RendererUtilities.createMapEnvelope(paintArea, worldToScreen, getContext().getCoordinateSystem());
            paint(graphics, paintArea, mapArea, worldToScreen);
        } catch (NoninvertibleTransformException e) {
            fireErrorEvent(new Exception("Can't create pixel to world transform", e));
        }
    }

    public void paint( Graphics2D graphics, Rectangle paintArea, ReferencedEnvelope envelope,
            AffineTransform transform ) {
        if( transform == null ){
            throw new NullPointerException("Transform is required");
        }
        if (hints != null) {
            graphics.setRenderingHints(hints);
        }

        if ((graphics == null) || (paintArea == null)) {
            LOGGER.info("renderer passed null arguments");

            return;
        }

        // reset the abort flag
        renderingStopRequested = false;

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
            // graphics.setTransform(new AffineTransform());
            atg.concatenate(transform);
            transform = atg;
        }

        try {
            setScaleDenominator(  
                    computeScale(
                            envelope,
                            context.getCoordinateReferenceSystem(),
                            paintArea, 
                            transform,
                            this.rendererHints));
        } catch (Exception e) // probably either (1) no CRS (2) error xforming
        {
            LOGGER.throwing("RendererUtilities", "calculateScale(envelope, coordinateReferenceSystem, imageWidth, imageHeight, hints)", e);
            setScaleDenominator(1 / transform.getScaleX()); // DJB old method - the best we can do            
        }

        MapLayer[] layers = context.getLayers();

        // get detstination CRS
        CoordinateSystem destinationCrs = context.getCoordinateSystem();

        for( int i = 0; i < layers.length; i++ ) {
            MapLayer currLayer = layers[i];

            if (!currLayer.isVisible()) {
                // Only render layer when layer is visible
                continue;
            }

            if (renderingStopRequested) {
                return;
            }
            
            labelCache.startLayer(""+i);

            ReferencedEnvelope bbox = envelope;

            try {
                FeatureSource featureSource = currLayer.getFeatureSource();
                if(featureSource instanceof DirectoryFeatureSource) {
                    featureSource = ((DirectoryFeatureSource) featureSource).unwrap();
                }
                
                GeometryDescriptor geom = featureSource.getSchema().getGeometryDescriptor();
                
                CoordinateReferenceSystem dataCRS;
                if (getForceCRSHint() == null) {
                    dataCRS = geom.getCoordinateReferenceSystem();
                } else {
                    dataCRS = getForceCRSHint();
                }
                MathTransform mt = null;
                CoordinateOperation op = null;
                if( dataCRS != null ){
                    try {
                        if( dataCRS != null ){
                            op = CRS.getCoordinateOperationFactory(true).createOperation(dataCRS, destinationCrs);
                            mt = op.getMathTransform();
                            bbox = bbox.transform(dataCRS, true, 10);
                        }
                        else {
                            LOGGER.log(Level.WARNING, "Could not reproject the bounding boxes as data CRS was null, proceeding in non reprojecting mode");
                            op = null;
                            mt = null;
                        }
                    } catch (Exception e) {
                        fireErrorEvent(e);
                        LOGGER.log(Level.WARNING, "Could not reproject the bounding boxes, proceeding in non reprojecting mode", e);
                        op = null;
                        mt = null;
                    }
                }
                else {
                    LOGGER.log(Level.WARNING, "Data CRS is unknown, proceeding in non reprojecting mode");
                }
                
                MathTransform at = ReferencingFactoryFinder.getMathTransformFactory(null)
                        .createAffineTransform(new GeneralMatrix(transform));

                if (mt == null) {
                    mt = at;
                } else {
                    mt = ReferencingFactoryFinder.getMathTransformFactory(null).createConcatenatedTransform(
                            mt, at);
                }

                // dbfheader must be set so that the attributes required for theming can be read in.
                ShapefileDataStore ds = (ShapefileDataStore) featureSource.getDataStore();

                // graphics.setTransform(transform);
                // extract the feature type stylers from the style object
                // and process them

                Transaction transaction = Transaction.AUTO_COMMIT;

                if (featureSource instanceof FeatureStore) {
                    transaction = ((SimpleFeatureStore) featureSource).getTransaction();
                }

                DefaultQuery query = new DefaultQuery(currLayer.getQuery());
                if( query.getFilter() !=null ){
                    // now reproject the geometries in filter because geoms are retrieved projected to screen space
                    FilterTransformer transformer= new  FilterTransformer(mt);
                    Filter transformedFilter = (Filter) query.getFilter().accept(transformer, null);
                    query.setFilter(transformedFilter);
                }
                
                // by processing the filter we can further restrict the maximum bounds that are
                // required.  For example if a filter 
                //BoundsExtractor extractor=new BoundsExtractor(bbox);
                //if( query.getFilter()!=null )
                //    query.getFilter().accept(extractor);
                //
                //processStylers(graphics, ds, query, extractor.getIntersection(), paintArea,
                //        mt, currLayer.getStyle(), layerIndexInfo[i], transaction);
                processStylers(graphics, ds, query, bbox, paintArea,
                        mt, currLayer.getStyle(), layerIndexInfo[i], transaction, ""+i);
            } catch (Exception exception) {
                Exception e = new Exception("Exception rendering layer " + currLayer, exception);
                fireErrorEvent(e);
            }

            labelCache.endLayer(""+i, graphics, paintArea);
        }

        labelCache.end(graphics, paintArea);
    }
    
    /**
     * Returns the text rendering method
     */
    private String getTextRenderingMethod() {
        if (rendererHints == null)
            return textRenderingModeDEFAULT;
        String result = (String) rendererHints.get(TEXT_RENDERING_KEY);
        if (result == null)
            return textRenderingModeDEFAULT;
        return result;
    }
    
    /**
     * <p>
     * Returns scale computation algorithm to be used. 
     * </p>
     */
    private String getScaleComputationMethod() {
        if (rendererHints == null)
            return scaleComputationMethodDEFAULT;
        String result = (String) rendererHints.get(SCALE_COMPUTATION_METHOD_KEY);
        if (result == null)
            return scaleComputationMethodDEFAULT;
        return result;
    }
    
    // Does not consider rotation
    private double computeScale(ReferencedEnvelope envelope, CoordinateSystem crs, Rectangle paintArea,
            AffineTransform worldToScreen) {
        if(getScaleComputationMethod().equals(SCALE_ACCURATE)) {
            try {
               return RendererUtilities.calculateScale(envelope, paintArea.width, paintArea.height);
            } 
            catch (Exception e) // probably either (1) no CRS (2) error xforming
            {
                LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        } 
        return RendererUtilities.calculateOGCScale(envelope, paintArea.width, hints);
    }
    
    
	/**
     * If the forceCRS hint is set then return the value.
     * @return the value of the forceCRS hint or null
     */
    private CoordinateSystem getForceCRSHint() {
    	if ( rendererHints==null )
    		return null;
    	Object crs=this.rendererHints.get("forceCRS");
    	if( crs instanceof CoordinateSystem )
    		return (CoordinateSystem) crs;
    	
    	return null;
	}


	@Override
	public void setMapContent(MapContent mapContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MapContent getMapContent() {
		// TODO Auto-generated method stub
		return null;
	}
}