package org.openjump.ui.util;
/**
 * Broken off from ProjUtils
 */
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.io.FilenameUtils;
import org.openjump.common.I18N;
import org.openjump.coordsys.srid.SRSInfo;
import org.openjump.core.ccordsys.srid.SRIDStyle;
import org.openjump.core.rasterimage.RasterImageLayer;
import org.openjump.core.rasterimage.TiffTags;
import org.openjump.core.rasterimage.TiffTags.TiffReadingException;
import org.openjump.datasource.DataSourceQuery;
import org.openjump.geometry.feature.Feature;
import org.openjump.geometry.feature.FeatureCollection;

import com.sun.tools.javac.util.JDK9Wrappers.Layer;
import com.vividsolutions.jump.io.datasource.DataSource;
import com.vividsolutions.jump.workbench.imagery.ImageryLayerDataset;
import com.vividsolutions.jump.workbench.imagery.ReferencedImageStyle;
import com.vividsolutions.jump.workbench.ui.plugin.datastore.DataStoreQueryDataSource;

/**
 * Giuseppe Aruta [23_3_2016] This class is used to recognize file projection.
 * There are different methods A) a method to decode projection information from
 * GeoTIFF metadata. B) a method to decode projection info from auxiliary files
 * (.proj and .aux.xml).
 * http://landsathandbook.gsfc.nasa.gov/pdfs/geotiff_spec.pdf
 * http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/GeoTiff.html
 * http://www.remotesensing.org/geotiff/spec/geotiff6.html
 *
 * the following datasets have been used to test this library: QGIS
 * (http://qgis.org/downloads/data/) OSGEO data samples (
 * http://download.osgeo.org/geotiff/samples/)
 */
public class LayerUtils {

    private static final String PROJECTION_UNSPECIFIED = I18N
            .get("org.openjump.core.ui.plugin.raster.RasterImageLayerPropertiesPlugIn.unknown_projection");
    private static final String USER_DEFINED = I18N
            .get("org.openjump.core.ui.plugin.layer.LayerPropertiesPlugIn.User_defined");
    private static final String NOT_RECOGNIZED = I18N
            .get("org.openjump.core.ui.plugin.layer.LayerPropertiesPlugIn.Not_recognized");
    private static final String NO_SRS = I18N
            .get("org.openjump.core.ccordsys.No-srs");
    private static final String EMBEDDED_SRS = I18N
            .get("org.openjump.core.ccordsys.Embedded-srs");
    private static final String NOT_CONSISTENT_SRS = I18N
            .get("org.openjump.core.ccordsys.Not-consistent-srs");

   

    /**
     * Method to get SRID from a layer from Style or from auxiliary file. It
     * tries to check all possibilities: a) SRID is recorded from layer
     * auxiliary file b) SRID is recorded from data source c) No SRID is
     * available d) Layer SRID doesn't match auxilary file/datasource SRID
     * 
     * @param layer
     * @return SRID
     * @throws Exception
     */

    public static SRSInfo getSRSInfoFromLayerStyleOrSource(Layer layer)
            throws Exception {
        // read SRID layer recorded as STYLE (for WFS, Databases and newly
        // generated files)
        SRIDStyle sridStyle = (SRIDStyle) layer.getStyle(SRIDStyle.class);
        int SRID_style_int = sridStyle.getSRID();
        String SRID_style_string = String.valueOf(SRID_style_int);
        // read SRID layer from aux file or geotiff tag
        SRSInfo srsInfo = LayerUtils.getSRSInfoFromLayerSource(layer);
        String SRID_srs_string = srsInfo.getCode();
        srsInfo.setCode(SRID_style_string);
        if (SRID_srs_string.equals("0") & SRID_style_string.equals("0")
                & !isTemporaryLayer(layer))
            srsInfo.setSource(NO_SRS);
        if ((!SRID_style_string.equals(SRID_srs_string)
                & SRID_srs_string.equals("0") & !SRID_style_string.equals("0"))
                & !isTemporaryLayer(layer) || isImageGeoTIFFLayer(layer))
            srsInfo.setSource(EMBEDDED_SRS);
        if (!SRID_style_string.equals(SRID_srs_string)
                & !SRID_style_string.equals("0") & !SRID_srs_string.equals("0"))
            srsInfo.setSource(NOT_CONSISTENT_SRS);
        srsInfo.complete();
        return srsInfo;
    }

    /**
     * Method to get SRID from a layer from Style or from auxiliary file. First
     * scans SRIDStyle, than auxiliary file or GeoTIFF tag. If SRID does not
     * exist, it returns 0.
     *
     * @param layer
     * @return SRID
     * @throws Exception
     */
    public static SRSInfo getSRSInfoFromLayerStyleOrSource_old(Layer layer)
            throws Exception {
        SRSInfo srsInfo = new SRSInfo();

        // First we check if a SRID != 0 has already been recorded for this
        // Layer
        SRIDStyle sridStyle = (SRIDStyle) layer.getStyle(SRIDStyle.class);
        final int oldSRID = sridStyle.getSRID();
        if (oldSRID > 0) {
            srsInfo.setCode(Integer.toString(oldSRID));
            srsInfo.complete();
            // If no SRID has been identified, search for a SRID in geotiff tagg
            // or into an auxiliary file
        } else {
            srsInfo = getSRSInfoFromLayerSource(layer);
        }

        return srsInfo;

    }

    /**
     * Method to get SRID from a layer file from auxiliary files (.prj or .aux)
     * or GeoTIFFed tag. If the auxiliary file SRID does not exist, it returns
     * 0.
     *
     * @param layer
     * @return SRID
     * @throws Exception
     */
    public static SRSInfo getSRSInfoFromLayerSource(Layer layer)
            throws Exception {
        String fileSourcePath = "";
        SRSInfo srsInfo = new SRSInfo();
        // Raster layer case
        if (isImageFileLayer(layer)) {
            FeatureCollection featureCollection = layer
                    .getFeatureCollectionWrapper();
            String sourcePathImage = null;

            for (Iterator i = featureCollection.iterator(); i.hasNext();) {
                Feature feature = (Feature) i.next();
                sourcePathImage = feature
                        .getString(ImageryLayerDataset.ATTR_URI);

                if (sourcePathImage != null && !sourcePathImage.isEmpty()) {
                    File f = new File(URI.create(sourcePathImage).getPath());
                    if (f.exists()) {
                        fileSourcePath = f.getAbsolutePath();
                        break;
                    }
                }
            }

            String extension = FileUtil.getExtension(fileSourcePath)
                    .toUpperCase();
            if ((extension.equals("TIF") || extension.equals("TIFF"))) {
                // If TIFF file is a geotiff, it scans into embedded tag
                TiffTags.TiffMetadata metadata = TiffTags
                        .readMetadata(new File(fileSourcePath));
                if (metadata.isGeoTiff()) {
                    srsInfo = metadata.getSRSInfo();
                    // If the TIF file is not a GeoTIFF it looks
                    // for a proj code into aux files
                } else {
                    srsInfo = getSRSInfoFromAuxiliaryFile(fileSourcePath);
                }
            } else {
                srsInfo = getSRSInfoFromAuxiliaryFile(fileSourcePath);
            }
        }
        // Vector layer case
        else {
            if (!isDataBaseLayer(layer)) {
                DataSourceQuery dsq = layer.getDataSourceQuery();
                Map properties = dsq.getDataSource().getProperties();
                if (properties.get(DataSource.URI_KEY) != null) {
                    fileSourcePath = new URI(properties.get(DataSource.URI_KEY)
                            .toString()).getPath();
                    srsInfo = getSRSInfoFromAuxiliaryFile(fileSourcePath);
                } else if (properties.get(DataSource.FILE_KEY) != null) {
                    fileSourcePath = properties.get(DataSource.FILE_KEY)
                            .toString();
                    srsInfo = getSRSInfoFromAuxiliaryFile(fileSourcePath);
                }
            }
        }
        return srsInfo;
    }

    // Boolean. Selected layer is related to an image file
    private static boolean isImageFileLayer(Layer layer) {
        if (layer.getStyle(ReferencedImageStyle.class) != null
                && (layer.getDescription() != null)) {
            return true;
        } else {
            return false;
        }
    }

    // Boolean, Select layer is a temporary layer
    private static boolean isTemporaryLayer(Layer layer) {
        DataSourceQuery dsq = layer.getDataSourceQuery();
        String sclass = layer.getClass().getSimpleName();
        if (dsq == null && layer.getStyle(ReferencedImageStyle.class) == null
                && layer.getDescription() != null && !sclass.equals("WFSLayer")) {
            return true;
        } else {
            return false;
        }
    }

    // Boolean. Selected layer is related to a database
    private static boolean isDataBaseLayer(Layer layer) {
        DataSourceQuery dsq = layer.getDataSourceQuery();
        if (dsq == null
                || dsq.getDataSource() instanceof DataStoreQueryDataSource) {
            return true;
        } else {
            return false;
        }
    }

    // Boolean. Selected layer is related to GEoTIFF file
    public static boolean isImageGeoTIFFLayer(Layer layer) {
        // String sclass = layer.getClass().getSimpleName();
        // if (sclass.equals("ReferencedImagesLayer"))
        String fileSourcePath = "";
        if (layer.getStyle(ReferencedImageStyle.class) != null
                && (layer.getDescription() != null)) {

            FeatureCollection featureCollection = layer
                    .getFeatureCollectionWrapper();
            String sourcePathImage = null;
            for (Iterator i = featureCollection.iterator(); i.hasNext();) {
                Feature feature = (Feature) i.next();
                sourcePathImage = feature
                        .getString(ImageryLayerDataset.ATTR_URI);

                if (sourcePathImage != null && !sourcePathImage.isEmpty()) {
                    File f = new File(URI.create(sourcePathImage).getPath());
                    if (f.exists()) {
                        fileSourcePath = f.getAbsolutePath();
                        break;
                    }
                }
            }
            String extension = FileUtil.getExtension(fileSourcePath)
                    .toUpperCase();
            if ((extension.equals("TIF") || extension.equals("TIFF"))) {
                TiffTags.TiffMetadata metadata = null;
                try {
                    metadata = TiffTags.readMetadata(new File(fileSourcePath));
                } catch (ImageReadException | IOException
                        | TiffReadingException e) {
                    e.printStackTrace();
                }
                if (metadata.isGeoTiff()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    // [Giuseppe Aruta 04/01/2017] get SRS info for RasterImageLayer.class
    // [Giuseppe Aruta 14/11/2017] First it checks if selected raster is
    // a GeoTIFF and scan tiff tags for projection.
    // If selected file is not a GeoTIF, it checks if <Filename>.AUX.XML exists
    // and scans inside it.
    // If no <Filename>.AUX.XML if found then it scans into <filename>.PRJ file
    // If layer is temporary or srid=0, it sets srid source to null
    //
    public static SRSInfo getSRSInfoFromLayerSource(RasterImageLayer layer)
            throws Exception, URISyntaxException {
        String fileSourcePath = layer.getImageFileName();
        String extension = FileUtil.getExtension(fileSourcePath).toLowerCase();
        SRSInfo srsInfo;
        if (extension.equals("tif") || extension.equals("tiff")) {
            TiffTags.TiffMetadata metadata = TiffTags.readMetadata(new File(
                    fileSourcePath));
            if (metadata.isGeoTiff()) {
                srsInfo = metadata.getSRSInfo();
                srsInfo.setSource(EMBEDDED_SRS);
            } else {
                srsInfo = LayerUtils.getSRSInfoFromAuxiliaryFile(fileSourcePath);
            }
        } else {
            srsInfo = LayerUtils.getSRSInfoFromAuxiliaryFile(fileSourcePath);
        }
        // if srid=0 there must be no source for file projection.
        // if the layer is temporary (file saved into TEMP folder), srid source
        // is kept to null
        // to avoid to save projection as an aux file for this layer
        if (srsInfo.getCode().equals("0") || layer.isTemporaryLayer()) {
            srsInfo.setSource("null");
        }
        srsInfo.complete();
        return srsInfo;
    }

}
