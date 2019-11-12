package org.openjump.workbench.renderer.style;

import java.awt.Graphics2D;

import org.locationtech.jts.util.Assert;
import org.openjump.coordsys.CoordinateSystem;
import org.openjump.geometry.feature.Feature;
import org.openjump.workbench.model.Layer;
import org.openjump.workbench.ui.Viewport;
/**
 * Ensures that all geometries have a given SRID. Because it is a Style, it will
 * be saved to the task file.
 */
public class SRIDStyle implements Style {

    private int srid = 0;
    private int lastUpdateSrid = srid;

    public SRIDStyle() {
      super();
    }

    public void paint(Feature f, Graphics2D g, Viewport viewport)
            throws Exception {
    }

    private boolean initialized = false;

    public void initialize(Layer layer) {
        if (initialized) {
            return;
        }

        updateSRIDs(layer);
        // mmichaud 2018-06-03 : This is wrong. Every time a feature is added or change,
        // it is updated as many times as the layerManager contains layers.
        // Responsability of updating the  the srid is moved to the LayerListener
        /*
        layer.getLayerManager().addLayerListener(new LayerListener() {
            public void featuresChanged(FeatureEvent e) {
                for (Feature feature : e.getFeatures()) {
                    // No need to set SRID on deleted features
                    if (e.getType() == FeatureEventType.DELETED) continue;
                    feature.getGeometry().setSRID(srid);
                }
            }
            public void layerChanged(LayerEvent e) {
            }
            public void categoryChanged(CategoryEvent e) {
            }
        });
        */
        initialized = true;
    }

    public void updateSRIDs(Layer layer) {
      // nothing to do
      if (lastUpdateSrid == srid)
        return;
      
      // apply srid to whole layer (btw. of FeatureSchema)
      layer.getFeatureCollectionWrapper().getFeatureSchema().setCoordinateSystem(new CoordinateSystem("", srid, null));
      // apply srid for each geometry
      for (Object feature : layer.getFeatureCollectionWrapper().getFeatures()) {
          ((Feature)feature).getGeometry().setSRID(srid);
      }
      
      lastUpdateSrid = srid;
    }

    public Object clone() {
        try {
            SRIDStyle clone = (SRIDStyle)super.clone();
            clone.initialized = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            Assert.shouldNeverReachHere();
            return null;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public int getSRID() {
        return srid;
    }

    public void setSRID(int srid) {
        this.srid = srid;
    }

}
