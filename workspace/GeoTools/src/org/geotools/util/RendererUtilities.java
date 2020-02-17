/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.util;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.geotools.geometry.DirectPosition;
import org.geotools.map.GridToEnvelopeMapper;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.ReferencedEnvelope;
import org.geotools.renderer.style.GraphicStyle;
import org.geotools.renderer.style.IconStyle;
import org.geotools.renderer.style.LineStyle;
import org.geotools.renderer.style.MarkStyle;
import org.geotools.renderer.style.Style;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.openjump.coordsys.AxisDirection;
import org.openjump.coordsys.CoordinateSystem;

/**
 * Class for holding static utility functions that are common tasks for people using the
 * "StreamingRenderer/Renderer".
 *
 * @author dblasby
 * @author Simone Giannecchini
 */
public final class RendererUtilities {
	private final static String CLSS = "RendererUtilities";
	private static Logger LOGGER = Logger.getLogger(CLSS);

    /** Utilities classes should not be instantiated. */
    private RendererUtilities() {};

    /**
     * Sets up the affine transform for projecting a GEODESIC coordinate system to a flat view coordinate
     * system.
     *
     * <p>NOTE It is worth to note that here we do not take into account the half a pixel
     * translation stated by ogc for coverage bounds. One reason is that WMS 1.1.1 does not follow
     * it!!!
     *
     * @param mapExtent the map extent
     * @param paintArea the size of the rendering output area
     * @return a transform that maps from real world coordinates to the screen
     */
    public static AffineTransform worldToScreenTransform(ReferencedEnvelope mapExtent, Rectangle paintArea) {
        final ReferencedEnvelope genvelope = new ReferencedEnvelope(mapExtent);
        final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
        try {
        	mapper.setGridRange(new ReferencedEnvelope(paintArea,null));
            mapper.setEnvelope(genvelope);
            mapper.setPixelAnchor(GridToEnvelopeMapper.ANCHOR_CELL_CORNER);
            LOGGER.info(String.format("%s.worldToScreen: %s %s",CLSS,RendererUtilities.toText("mapExtent",mapExtent),RendererUtilities.toText("paintArea",paintArea)));
            AffineTransform at = mapper.createAffineTransform();
            AffineTransform inverse = at.createInverse();
            return inverse;
        } 
        catch (MismatchedDimensionException e) {
        	LOGGER.log(Level.WARNING, String.format("%s.worldtoScreenTransform (%s)",CLSS,e.getLocalizedMessage()),e);
            return null;
        } 
        catch (NoninvertibleTransformException e) {
            LOGGER.log(Level.WARNING, String.format("%s.worldtoScreenTransform (%s)",CLSS,e.getLocalizedMessage()), e);
            return null;
        }
    }

    /**
     * Creates the map's bounding box in real world coordinates.
     *
     * @param worldToScreen a transform which converts World coordinates to screen pixel
     *     coordinates. No assumptions are done on axis order as this is assumed to be
     *     pre-calculated. The affine transform may specify an rotation, in case the envelope will
     *     encompass the complete (rotated) world polygon.
     * @param paintArea the size of the rendering output area
     * @return the envelope in world coordinates corresponding to the screen rectangle.
     */
    public static Envelope createMapEnvelope(Rectangle paintArea, AffineTransform worldToScreen)
            throws NoninvertibleTransformException {
        double[] pts = new double[8];
        pts[0] = paintArea.getMinX();
        pts[1] = paintArea.getMinY();
        pts[2] = paintArea.getMaxX();
        pts[3] = paintArea.getMinY();
        pts[4] = paintArea.getMaxX();
        pts[5] = paintArea.getMaxY();
        pts[6] = paintArea.getMinX();
        pts[7] = paintArea.getMaxY();
        worldToScreen.inverseTransform(pts, 0, pts, 0, 4);
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            xMin = Math.min(xMin, pts[2 * i]);
            yMin = Math.min(yMin, pts[2 * i + 1]);
            xMax = Math.max(xMax, pts[2 * i]);
            yMax = Math.max(yMax, pts[2 * i + 1]);
        }
        return new Envelope(xMin, xMax, yMin, yMax);
    }

    /**
     * Creates the map's bounding box in real world coordinates
     *
     * <p>NOTE It is worth to note that here we do not take into account the half a pixel
     * translation stated by ogc for coverages bounds. One reason is that WMS 1.1.1 does not follow
     * it!!!
     *
     * @param worldToScreen a transform which converts World coordinates to screen pixel
     *     coordinates.
     * @param paintArea the size of the rendering output area
     */
    public static ReferencedEnvelope createMapEnvelope(
            Rectangle paintArea, AffineTransform worldToScreen, final CoordinateSystem crs)
            throws NoninvertibleTransformException {

        Envelope env = createMapEnvelope(paintArea, worldToScreen);
        return new ReferencedEnvelope(env,crs);
    }

    static final double OGC_DEGREE_TO_METERS = 6378137.0 * 2.0 * Math.PI / 360;

    /**
     * Calculates the pixels per meter ratio based on a scale denominator.
     *
     * @param scaleDenominator The scale denominator value.
     * @param hints The hints used in calculation. if "dpi" key is present, it uses it's Integer
     *     value as the dpi of the current device. if not it uses 90 that is the OGC default value.
     * @return The pixels per meter ratio for the given scale denominator.
     */
    public static double calculatePixelsPerMeterRatio(double scaleDenominator, Map hints) {
        if (scaleDenominator <= 0.0)
            throw new IllegalArgumentException("The scale denominator must be positive.");
        double scale = 1.0 / scaleDenominator;
        return scale * (getDpi() / 0.0254);
    }

    /**
     * This method performs the computation using the methods suggested by the OGC SLD
     * specification, page 26.
     *
     * <p>In GeoTools 12 this method started to take into account units of measure, if this is not
     * desirable in your application you can set the system variable
     * "org.geotoools.render.lite.scale.unitCompensation" to false.
     *
     * @param envelope
     * @param imageWidth
     * @return
     */
    public static double calculateOGCScale(ReferencedEnvelope envelope, int imageWidth, Map hints) {
        // if it's geodetic, we're dealing with lat/lon unit measures
        CoordinateSystem crs = envelope.getCoordinateSystem();
        double width = envelope.getWidth();
        double widthMeters = toMeters(width, crs);
        return widthMeters / (imageWidth / getDpi() * 0.0254);
    }

    /**
     * Method used by the OGC scale calculation to turn a given length in the specified CRS towards
     * meters.
     *
     * <p>GeographicCRS uses {@link #OGC_DEGREE_TO_METERS} for conversion of lat/lon measures
     *
     * <p>Otherwise the horizontal component of the CRS is assumed to have a uniform axis unit of
     * measure providing the Unit used for conversion. To ignore unit disable {@link
     * #SCALE_UNIT_COMPENSATION} to for the unaltered size.
     *
     * @param size
     * @param crs
     * @return size adjusted for GeographicCRS or CRS units
     */
    private static double toMeters(double size, CoordinateSystem cs) {
        if (cs == null) {
            LOGGER.finer("toMeters: assuming the original size is in meters already, as crs is null");
            return size;
        }
        return size;
    }

    public static String toText(String label,Rectangle rect) {
		String text = String.format("%s (%2.1f-%2.1f,%2.1f-%2.1f)",label,rect.getMinX(),rect.getMaxX(),rect.getMinY(),rect.getMaxY()); 
		return text;
	}
    public static String toText(String label,Rectangle2D rect) {
		String text = String.format("%s (%2.1f-%2.1f,%2.1f-%2.1f)",label,rect.getMinX(),rect.getMaxX(),rect.getMinY(),rect.getMaxY()); 
		return text;
	}
    public static String toText(String label,Envelope rect) {
		String text = String.format("%s (%2.1f-%2.1f,%2.1f-%2.1f)",label,rect.getMinX(),rect.getMaxX(),rect.getMinY(),rect.getMaxY()); 
		return text;
	}

    /**
     * First searches the hints for the scale denominator hint otherwise calls {@link
     * #calculateScale(org.geotools.util.SoftValueHashMap.Reference, int, int, double)}. If the
     * hints contains a DPI then that DPI is used otherwise 90 is used (the OGS default).
     */
    public static double calculateScale(ReferencedEnvelope envelope, int imageWidth, int imageHeight) {
        Double scale = (Double) getDpi();
        return scale.doubleValue();
    }

    /**
     * Return the OGC standard, stating that a pixel is 0.28 mm
     * (the result is a non integer DPI...)
     *
     * @return DPI as doubles, to avoid issues with integer trunking in scale computation expression
     */
    public static double getDpi() {
        return 25.4 / 0.28; // 90 = OGC standard
    }

    /**
     * Find the scale denominator of the map. Method: 1. find the diagonal distance (meters) 2. find
     * the diagonal distance (pixels) 3. find the diagonal distance (meters) -- use DPI 4. calculate
     * scale (#1/#2)
     *
     * <p>NOTE: return the scale denominator not the actual scale (1/scale = denominator)
     *
     * <p>TODO: (SLD spec page 28): Since it is common to integrate the output of multiple servers
     * into a single displayed result in the web-mapping environment, it is important that different
     * map servers have consistent behaviour with respect to processing scales, so that all of the
     * independent servers will select or deselect rules at the same scales. To insure consistent
     * behaviour, scales relative to coordinate spaces must be handled consistently between map
     * servers. For geographic coordinate systems, which use angular units, the angular coverage of
     * a map should be converted to linear units for computation of scale by using the circumference
     * of the Earth at the equator and by assuming perfectly square linear units. For linear
     * coordinate systems, the size of the coordinate space should be used directly without
     * compensating for distortions in it with respect to the shape of the real Earth.
     *
     * <p>NOTE: we are actually doing a a much more exact calculation, and accounting for non-square
     * pixels (which are allowed in WMS) ADDITIONAL NOTE from simboss: I added soe minor fixes. See
     * below.
     *
     * @param envelope
     * @param imageWidth
     * @param imageHeight
     * @param DPI screen dots per inch (OGC standard is 90)
     *     <p>TODO should I take into account also the destination CRS? Otherwise I am just assuming
     *     that the final crs is lon,lat that is it maps lon to x (n raster space) and lat to y (in
     *     raster space).
     */
    public static double calculateScale(ReferencedEnvelope envelope, int imageWidth, int imageHeight, double DPI) {

    	final double diagonalGroundDistance;

    	DirectPosition uc = envelope.getUpperCorner();
    	DirectPosition lc = envelope.getLowerCorner();
    	diagonalGroundDistance = Math.sqrt((uc.x-lc.x)*(uc.x-lc.x)+(uc.y-lc.y)*(uc.y-lc.y));


    	// //
        //
        // Compute the distances on the requested image using the provided DPI.
        //
        // //
        // pythagorian theorm
        double diagonalPixelDistancePixels =
                Math.sqrt(imageWidth * imageWidth + imageHeight * imageHeight);
        double diagonalPixelDistanceMeters =
                diagonalPixelDistancePixels / DPI * 2.54 / 100; // 2.54 = cm/inch, 100= cm/m
        return diagonalGroundDistance / diagonalPixelDistanceMeters;
    }

    private static double geodeticDiagonalDistance(Envelope env) {
        if (env.getWidth() < 180 && env.getHeight() < 180) {
            return getGeodeticSegmentLength(
                    env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
        } else {
            // we cannot compute geodetic distance for distances longer than a hemisphere,
            // we have to build a set of lines connecting the two points that are smaller to
            // get a value that makes any sense rendering wise by crossing the original line with
            // a set of quadrants that are 180x180
            double distance = 0;
            GeometryFactory gf = new GeometryFactory();
            LineString ls =
                    gf.createLineString(
                            new Coordinate[] {
                                new Coordinate(env.getMinX(), env.getMinY()),
                                new Coordinate(env.getMaxX(), env.getMaxY())
                            });
            int qMinX = -1;
            int qMaxX = 1;
            int qMinY = -1;
            int qMaxY = 1;
            // we must consider at least a pair of quadrants in each direction other wise lines
            // which don't cross both the equator and prime meridian are
            // measured as 0 length. But for some cases we need to consider still more hemispheres.
            qMinX =
                    Math.min(
                            qMinX,
                            (int)(Math.signum(env.getMinX())
                                            * Math.ceil(Math.abs(env.getMinX() / 180.0))));
            qMaxX =
                    Math.max(
                            qMaxX,
                            (int)(Math.signum(env.getMaxX())
                                            * Math.ceil(Math.abs(env.getMaxX() / 180.0))));
            qMinY =
                    Math.min(
                            qMinY,
                            (int) (Math.signum(env.getMinY())
                                            * Math.ceil(Math.abs((env.getMinY() + 90) / 180.0))));
            qMaxY =
                    Math.max(
                            qMaxY,
                            (int) (Math.signum(env.getMaxY())
                                            * Math.ceil(Math.abs((env.getMaxY() + 90) / 180.0))));
            for (int i = qMinX; i < qMaxX; i++) {
                for (int j = qMinY; j < qMaxY; j++) {
                    double minX = i * 180.0;
                    double minY = j * 180.0 - 90;
                    double maxX = minX + 180;
                    double maxY = minY + 180;
                    LinearRing ring =
                            gf.createLinearRing(
                                    new Coordinate[] {
                                        new Coordinate(minX, minY),
                                        new Coordinate(minX, maxY),
                                        new Coordinate(maxX, maxY),
                                        new Coordinate(maxX, minY),
                                        new Coordinate(minX, minY)
                                    });
                    Polygon p = gf.createPolygon(ring, null);
                    Geometry intersection = p.intersection(ls);
                    if (!intersection.isEmpty()) {
                        if (intersection instanceof LineString) {
                            LineString ils = ((LineString) intersection);
                            double d = getGeodeticSegmentLength(ils);
                            distance += d;
                        } else if (intersection instanceof GeometryCollection) {
                            GeometryCollection igc = ((GeometryCollection) intersection);
                            for (int k = 0; k < igc.getNumGeometries(); k++) {
                                Geometry child = igc.getGeometryN(k);
                                if (child instanceof LineString) {
                                    double d = getGeodeticSegmentLength((LineString) child);
                                    distance += d;
                                }
                            }
                        }
                    }
                }
            }

            return distance;
        }
    }

    private static double getGeodeticSegmentLength(LineString ls) {
        Coordinate start = ls.getCoordinateN(0);
        Coordinate end = ls.getCoordinateN(1);
        return getGeodeticSegmentLength(start.x, start.y, end.x, end.y);
    }

    private static double getGeodeticSegmentLength(
            double minx, double miny, double maxx, double maxy) {
        final GeodeticCalculator calculator = new GeodeticCalculator();  // Use default calculator
        double rminx = rollLongitude(minx);
        double rminy = rollLatitude(miny);
        double rmaxx = rollLongitude(maxx);
        double rmaxy = rollLatitude(maxy);
        calculator.setStartingGeographicPoint(rminx, rminy);
        calculator.setDestinationGeographicPoint(rmaxx, rmaxy);
        return calculator.getOrthodromicDistance();
    }

    protected static double rollLongitude(final double x) {
        double rolled = x - (((int) (x + Math.signum(x) * 180)) / 360) * 360.0;
        return rolled;
    }

    protected static double rollLatitude(final double x) {
        double rolled = x - (((int) (x + Math.signum(x) * 90)) / 180) * 180.0;
        return rolled;
    }

    /**
     * This worldToScreenTransform method makes the assumption that the crs is in Lon,Lat or
     * Lat,Lon. If the provided envelope does not carry along a crs the assumption that the map
     * extent is in the classic Lon,Lat form. In case the provided envelope is of type.
     *
     * <p>Note that this method takes into account also the OGC standard with respect to the
     * relation between pixels and sample.
     *
     * @param mapExtent The envelope of the map in lon,lat
     * @param paintArea The area to paint as a rectangle
     * @param destinationCrs
     * @todo add georeferenced envelope check when merge with trunk will be performed
     */
    public static AffineTransform worldToScreenTransform(
            Envelope mapExtent, Rectangle paintArea, CoordinateSystem destinationCrs) {

        final boolean lonFirst =
        		destinationCrs
                        .getAxis(0)
                        .getDirection()
                        .equals(AxisDirection.EAST);
        final ReferencedEnvelope newEnvelope =
                lonFirst? new ReferencedEnvelope(mapExtent.getMinX(), mapExtent.getMinY(),
                                mapExtent.getMaxX(), mapExtent.getMaxY(),destinationCrs)
                        : new ReferencedEnvelope(
                                mapExtent.getMinY(), mapExtent.getMinX(),
                                mapExtent.getMaxY(), mapExtent.getMaxX(),destinationCrs);

        //
        // with this method I can build a world to grid transform
        // without adding half of a pixel translations. The cost
        // is a hashtable lookup. The benefit is reusing the last
        // transform (instead of creating a new one) if the grid
        // and envelope are the same one than during last invocation.
        final GridToEnvelopeMapper m = new GridToEnvelopeMapper();
        m.setGridRange(new ReferencedEnvelope(paintArea));
        m.setEnvelope(newEnvelope);
        AffineTransform result = null;
        try {
			result = m.createTransform().createInverse();
		} 
        catch (IllegalStateException e) {
			e.printStackTrace();
		} 
        catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
        return result;
    }

    /**
     * Finds the centroid of the input geometry if input = point, line, polygon --> return a point
     * that represents the centroid of that geom if input = geometry collection --> return a
     * multipoint that represents the centoid of each sub-geom
     *
     * @param g
     */
    public static Geometry getCentroid(Geometry g) {
        if (g instanceof Point || g instanceof MultiPoint) {
            return g;
        } 
        else if (g instanceof GeometryCollection) {
            final GeometryCollection gc = (GeometryCollection) g;
            final Coordinate[] pts = new Coordinate[gc.getNumGeometries()];
            final int length = gc.getNumGeometries();
            for (int t = 0; t < length; t++) {
                pts[t] = pointInGeometry(gc.getGeometryN(t)).getCoordinate();
            }
            return g.getFactory().createMultiPoint(new CoordinateArraySequence(pts));
        } 
        else if (g != null) {
            return pointInGeometry(g);
        }
        return null;
    }

    private static Geometry pointInGeometry(Geometry g) {
        Point p = g.getCentroid();
        if (g instanceof Polygon) {
            // if the geometry is heavily generalized centroid computation may fail and return NaN
            if (Double.isNaN(p.getX()) || Double.isNaN(p.getY()))
                return g.getFactory().createPoint(g.getCoordinate());
            // otherwise let's check if the point is inside. Again, this check and
            // "getInteriorPoint"
            // will work only if the geometry is valid
            if (g.isValid() && !g.contains(p)) {
                try {
                    p = g.getInteriorPoint();
                } catch (Exception e) {
                    // generalized geometries might make interior point go bye bye
                    return p;
                }
            } else {
                return p;
            }
        }
        return p;
    }

    public static double getStyleSize(Style style) {
        if (style instanceof GraphicStyle) {
            final BufferedImage image = ((GraphicStyle) style).getImage();
            return maxSize(image.getWidth(), image.getHeight());
        } 
        else if (style instanceof IconStyle) {
            final Icon icon = ((IconStyle) style).getIcon();
            return maxSize(icon.getIconWidth(), icon.getIconHeight());
        } 
        else if (style instanceof LineStyle) {
            LineStyle ls = ((LineStyle) style);
            double gsSize = getStyleSize(ls.getGraphicStroke());
            double strokeSize = 0;
            if (ls.getStroke() instanceof BasicStroke) {
                strokeSize = ((BasicStroke) ls.getStroke()).getLineWidth();
            }
            double offset = ls.getPerpendicularOffset();
            double lineSize = maxSize(maxSize(gsSize, strokeSize), offset);
            // a MarkStyle2D is also a LineStyle2D, but we have to account for the symbol size
            if (style instanceof MarkStyle) {
                MarkStyle mark = (MarkStyle) style;
                return mark.getSize() + lineSize;
            } else {
                return lineSize;
            }
        } else {
            return 0;
        }
    }

    private static double maxSize(double d1, double d2) {
        if (Double.isNaN(d1)) {
            d1 = 0;
        }
        if (Double.isNaN(d2)) {
            d2 = 0;
        }
        return Math.max(d1, d2);
    }

    /**
     * Finds a centroid for a polygon catching any exceptions resulting from generalization or other
     * polygon irregularities.
     *
     * @param geom The polygon.
     * @return The polygon centroid, or null if it can't be found.
     */
    public static Point getPolygonCentroid(Polygon geom) {
        Point centroid;
        try {
            centroid = geom.getCentroid();
        } catch (Exception e) {
            // generalized polygons causes problems - this
            // tries to hide them.
            try {
                centroid = geom.getExteriorRing().getCentroid();
            } catch (Exception ee) {
                try {
                    centroid = geom.getFactory().createPoint(geom.getCoordinate());
                } catch (Exception eee) {
                    return null; // we're hooped
                }
            }
        }
        return centroid;
    }

    /**
     * Uses a sampling technique to obtain a central point that lies inside the specified polygon.
     *
     * <p>Sampling occurs horizontally along the middle of the polygon obtained from the y
     * coordinate of the polygon centroid.
     *
     * @param geom The polygon.
     * @param centroid The centroid of the polygon, can be null in which case it will be computed
     *     from {@link #getPolygonCentroid(Polygon)}.
     * @param pg The prepared version of geom, can be null in which case it will be computed on
     *     demand.
     * @param gf The geometry factory, can be null in which case the polygons factory will be used.
     * @return A central point that lies inside of the polygon, or null if one could not be found.
     */
    public static Point sampleForInternalPoint(
            Polygon geom,
            Point centroid,
            PreparedGeometry pg,
            GeometryFactory gf,
            double stepSize,
            int numSamples) {

        if (centroid == null) {
            centroid = getPolygonCentroid(geom);
        }
        if (pg == null) {
            pg = PreparedGeometryFactory.prepare(geom);
        }
        if (gf == null) {
            gf = geom.getFactory();
        }

        if (pg.contains(centroid)) {
            return centroid;
        }

        Envelope env = geom.getEnvelopeInternal();
        if (stepSize > 0) {
            numSamples = (int) Math.round(env.getWidth() / stepSize);
        } else if (numSamples > 0) {
            stepSize = env.getWidth() / numSamples;
        } else {
            throw new IllegalArgumentException(
                    "One of stepSize or numSamples must be greater than zero");
        }

        Coordinate c = new Coordinate();
        Point pp = gf.createPoint(c);
        c.y = centroid.getY();
        int max = -1;
        int maxIdx = -1;
        int containCounter = -1;
        for (int i = 0; i < numSamples; i++) {
            c.x = env.getMinX() + stepSize * i;
            pp.geometryChanged();
            if (!pg.contains(pp)) {
                containCounter = 0;
            } else if (i == 0) {
                containCounter = 1;
            } else {
                containCounter++;
                if (containCounter > max) {
                    max = containCounter;
                    maxIdx = i;
                }
            }
        }

        if (maxIdx != -1) {
            int midIdx = max > 1 ? maxIdx - max / 2 : maxIdx;
            c.x = env.getMinX() + stepSize * midIdx;
            pp.geometryChanged();
            return pp;
        } else {
            return null;
        }
    }
}
