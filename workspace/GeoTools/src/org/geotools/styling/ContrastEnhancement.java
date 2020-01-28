/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2015, Open Source Geospatial Foundation (OSGeo)
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
 *
 * Created on 13 November 2002, 13:52
 */
package org.geotools.styling;

import java.util.HashMap;
import java.util.Map;

/**
 * The ContrastEnhancement object defines contrast enhancement for a channel of a false-color image
 * or for a color image. Its format is:
 *
 * <pre>
 * &lt;xs:element name=&quot;ContrastEnhancement&quot;&gt;
 *   &lt;xs:complexType&gt;
 *     &lt;xs:sequence&gt;
 *       &lt;xs:choice minOccurs=&quot;0&quot;&gt;
 *         &lt;xs:element ref=&quot;sld:Normalize&quot;/&gt;
 *         &lt;xs:element ref=&quot;sld:Histogram&quot;/&gt;
 *       &lt;/xs:choice&gt;
 *       &lt;xs:element ref=&quot;sld:GammaValue&quot; minOccurs=&quot;0&quot;/&gt;
 *     &lt;/xs:sequence&gt;
 *   &lt;/xs:complexType&gt;
 * &lt;/xs:element&gt;
 * &lt;xs:element name=&quot;Normalize&quot;&gt;
 *   &lt;xs:complexType/&gt;
 * &lt;/xs:element&gt;
 * &lt;xs:element name=&quot;Histogram&quot;&gt;
 *   &lt;xs:complexType/&gt;
 * &lt;/xs:element&gt;
 * &lt;xs:element name=&quot;GammaValue&quot; type=&quot;xs:double&quot;/&gt;
 * </pre>
 *
 * In the case of a color image, the relative grayscale brightness of a pixel color is used.
 * ?Normalize? means to stretch the contrast so that the dimmest color is stretched to black and the
 * brightest color is stretched to white, with all colors in between stretched out linearly.
 * ?Histogram? means to stretch the contrast based on a histogram of how many colors are at each
 * brightness level on input, with the goal of producing equal number of pixels in the image at each
 * brightness level on output. This has the effect of revealing many subtle ground features. A
 * ?GammaValue? tells how much to brighten (value greater than 1.0) or dim (value less than 1.0) an
 * image. The default GammaValue is 1.0 (no change). If none of Normalize, Histogram, or GammaValue
 * are selected in a ContrastEnhancement, then no enhancement is performed.
 *
 * @author iant
 */
public class ContrastEnhancement {
    private double gamma;
    private ContrastMethod method;
    private Map<String, Object> options;

    public ContrastEnhancement() {
    }


    public ContrastEnhancement(ContrastMethod method) {
        this.method = method;
    }

    public ContrastEnhancement(ContrastEnhancement ce) {
        this.method = ce.getMethod();
        this.gamma = ce.getGamma();
        this.options = new HashMap<>();
        this.options.putAll(ce.getOptions());
    }

    public ContrastEnhancement(double gamma, ContrastMethod method) {
        this.gamma = gamma;
        this.method = method;
    }

    public double getGamma() { return gamma;}
    public void setGamma(double gamma) {this.gamma = gamma;}

    public ContrastMethod getMethod() {
        return method;
    }

    public void setMethod(ContrastMethod method) {
        this.method = method;
    }

    public Map<String, Object> getOptions() {
        if (this.options == null) {
            this.options = new HashMap<>();
        }
        return this.options;
    }

    public boolean hasOption(String key) {
        if (this.options == null) {
            this.options = new HashMap<>();
        }
        return options.containsKey(key);
    }

    public Object getOption(String key) {
        return this.options.get(key);
    }

    public void addOption(String key, Object value) {
        if (this.options == null) {
            this.options = new HashMap<>();
        }
        options.put(key, value);
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }


    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)gamma;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((options == null) ? 0 : options.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ContrastEnhancement)) {
            return false;
        }
        ContrastEnhancement other = (ContrastEnhancement) obj;
        if(gamma!=other.gamma) {
            return false;
        }
        if (method == null) {
            if (other.method != null) {
                return false;
            }
        } 
        else if(!method.equals(other.method)) {
            return false;
        }
        if (options == null) {
            if (other.options != null) {
                return false;
            }
        }
        else if (!options.equals(other.options)) {
            return false;
        }
        return true;
    }
}
