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
package org.geotools.styling;

import org.geotools.util.Utilities;

/** Default implementation of SelectedChannelType. */
public class SelectedChannelType {

    // private Expression contrastEnhancement;
    private ContrastEnhancement contrastEnhancement;
    private String name = "";

    public SelectedChannelType() {
    }


    public SelectedChannelType(ContrastEnhancement contrast) {
        contrastEnhancement = contrast;
    }

    public SelectedChannelType(SelectedChannelType gray) {
        name = gray.getChannelName();
        if (gray.getContrastEnhancement() != null) {
            contrastEnhancement = new ContrastEnhancement(gray.getContrastEnhancement());
        }
    }

    public String getChannelName() {
        return name;
    }

    public ContrastEnhancement getContrastEnhancement() {
        return contrastEnhancement;
    }

    public void setChannelName(String name) {
        this.name = name;
    }


    public void setContrastEnhancement(ContrastEnhancement enhancement) {
        this.contrastEnhancement = enhancement;
    }

   
    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (name != null) {
            result = (PRIME * result) + name.hashCode();
        }

        if (contrastEnhancement != null) {
            result = (PRIME * result) + contrastEnhancement.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SelectedChannelType) {
            SelectedChannelType other = (SelectedChannelType) obj;

            return Utilities.equals(name, other.name)
                    && Utilities.equals(contrastEnhancement, other.contrastEnhancement);
        }

        return false;
    }
}
