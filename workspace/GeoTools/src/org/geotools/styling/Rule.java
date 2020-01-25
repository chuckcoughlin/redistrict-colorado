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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.util.Utilities;
import org.opengis.style.StyleVisitor;

/**
 * Provides the default implementation of Rule.
 *
 * @author James Macgill
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class Rule implements Cloneable {
    private List<Symbolizer> symbolizers = new ArrayList<>();

    private GraphicLegend legend;
    private String name;
    private String description = "";
    private Filter filter = null;
    private boolean hasElseFilter = false;
    private double maxScaleDenominator = Double.POSITIVE_INFINITY;
    private double minScaleDenominator = 0.0;
    private OnLineResource online = null;

    /** Creates a new instance of DefaultRule */
    protected Rule() {}

    /** Creates a new instance of DefaultRule */
    protected Rule(Symbolizer[] symbolizers) {
        this.symbolizers.addAll(Arrays.asList(symbolizers));
    }

    protected Rule(
            Symbolizer[] symbolizers,
            String desc,
            GraphicLegend legend,
            String name,
            Filter filter,
            boolean isElseFilter,
            double maxScale,
            double minScale) {
        this.symbolizers = new ArrayList<>(Arrays.asList(symbolizers));
        description.setAbstract(desc.getAbstract());
        description.setTitle(desc.getTitle());
        this.legend = legend;
        this.name = name;
        this.filter = filter;
        hasElseFilter = isElseFilter;
        this.maxScaleDenominator = maxScale;
        this.minScaleDenominator = minScale;
    }

    /** Copy constructor */
    public Rule(Rule rule) {
        this.symbolizers = new ArrayList<Symbolizer>();
        for (org.opengis.style.Symbolizer sym : rule.symbolizers()) {
            if (sym instanceof Symbolizer) {
                this.symbolizers.add((Symbolizer) sym);
            }
        }
        if (rule.getDescription() != null && rule.getDescription().getTitle() != null) {
            this.description.setTitle(rule.getDescription().getTitle());
        }
        if (rule.getDescription() != null && rule.getDescription().getAbstract() != null) {
            this.description.setTitle(rule.getDescription().getAbstract());
        }
        if (rule.getLegend() instanceof org.geotools.styling.Graphic) {
            this.legend = rule.getLegend();
        }
        this.name = rule.getName();
        this.filter = rule.getFilter();
        this.hasElseFilter = rule.isElseFilter();
        this.maxScaleDenominator = rule.getMaxScaleDenominator();
        this.minScaleDenominator = rule.getMinScaleDenominator();
    }

    public GraphicLegend getLegend() {
        return legend;
    }

    public void setLegend(GraphicLegend legend) {
        this.legend = legend;
    }

    public List<Symbolizer> symbolizers() {
        return symbolizers;
    }

    public org.geotools.styling.Symbolizer[] getSymbolizers() {

        final org.geotools.styling.Symbolizer[] ret;

        ret = new org.geotools.styling.Symbolizer[symbolizers.size()];
        for (int i = 0, n = symbolizers.size(); i < n; i++) {
            ret[i] = symbolizers.get(i);
        }

        return ret;
    }

    public DescriptionImpl getDescription() {
        return description;
    }

    public void setDescription(org.opengis.style.Description description) {
        this.description = DescriptionImpl.cast(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean isElseFilter() {
        return hasElseFilter;
    }

    public void setElseFilter(boolean defaultb) {
        hasElseFilter = defaultb;
    }

    /**
     * Getter for property maxScaleDenominator.
     *
     * @return Value of property maxScaleDenominator.
     */
    public double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    /**
     * Setter for property maxScaleDenominator.
     *
     * @param maxScaleDenominator New value of property maxScaleDenominator.
     */
    public void setMaxScaleDenominator(double maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    /**
     * Getter for property minScaleDenominator.
     *
     * @return Value of property minScaleDenominator.
     */
    public double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    /**
     * Setter for property minScaleDenominator.
     *
     * @param minScaleDenominator New value of property minScaleDenominator.
     */
    public void setMinScaleDenominator(double minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public Object accept(StyleVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void accept(org.geotools.styling.StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone of the rule.
     *
     * @see org.geotools.styling.Rule#clone()
     */
    public Object clone() {
        try {
            Rule clone = (Rule) super.clone();

            clone.name = name;
            clone.description.setAbstract(description.getAbstract());
            clone.description.setTitle(description.getTitle());
            if (filter == null) {
                clone.filter = null;
            } else {
                DuplicatingFilterVisitor visitor = new DuplicatingFilterVisitor();
                clone.filter =
                        (Filter)
                                filter.accept(visitor, CommonFactoryFinder.getFilterFactory2(null));
            }
            clone.hasElseFilter = hasElseFilter;
            clone.legend = legend;

            clone.symbolizers = new ArrayList<Symbolizer>(symbolizers);

            clone.maxScaleDenominator = maxScaleDenominator;
            clone.minScaleDenominator = minScaleDenominator;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This will never happen", e);
        }
    }

    /**
     * Generates a hashcode for the Rule.
     *
     * <p>For complex styles this can be an expensive operation since the hash code is computed
     * using all the hashcodes of the object within the style.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        result = (PRIME * result) + symbolizers.hashCode();

        if (legend != null) result = (PRIME * result) + legend.hashCode();

        if (name != null) {
            result = (PRIME * result) + name.hashCode();
        }

        if (description != null) {
            result = (PRIME * result) + description.hashCode();
        }

        if (filter != null) {
            result = (PRIME * result) + filter.hashCode();
        }

        result = (PRIME * result) + (hasElseFilter ? 1 : 0);

        long temp = Double.doubleToLongBits(maxScaleDenominator);
        result = (PRIME * result) + (int) (temp >>> 32);
        result = (PRIME * result) + (int) (temp & 0xFFFFFFFF);
        temp = Double.doubleToLongBits(minScaleDenominator);
        result = (PRIME * result) + (int) (temp >>> 32);
        result = (PRIME * result) + (int) (temp & 0xFFFFFFFF);

        return result;
    }

    /**
     * Compares this Rule with another for equality.
     *
     * <p>Two RuleImpls are equal if all their properties are equal.
     *
     * <p>For complex styles this can be an expensive operation since it checks all objects for
     * equality.
     *
     * @param oth The other rule to compare with.
     * @return True if this and oth are equal.
     */
    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth instanceof Rule) {
            Rule other = (Rule) oth;

            return Utilities.equals(name, other.name)
                    && Utilities.equals(description, other.description)
                    && Utilities.equals(filter, other.filter)
                    && (hasElseFilter == other.hasElseFilter)
                    && Utilities.equals(legend, other.legend)
                    && Utilities.equals(symbolizers, other.symbolizers)
                    && (Double.doubleToLongBits(maxScaleDenominator)
                            == Double.doubleToLongBits(other.maxScaleDenominator))
                    && (Double.doubleToLongBits(minScaleDenominator)
                            == Double.doubleToLongBits(other.minScaleDenominator));
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<RuleImpl");
        if (name != null) {
            buf.append(":");
            buf.append(name);
        }
        buf.append("> ");
        buf.append(filter);
        if (symbolizers != null) {
            buf.append("\n");
            for (Symbolizer symbolizer : symbolizers) {
                buf.append("\t");
                buf.append(symbolizer);
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    public OnLineResource getOnlineResource() {
        return online;
    }

    public void setOnlineResource(OnLineResource online) {
        this.online = online;
    }

    static Rule cast(Rule rule) {
        if (rule == null) {
            return null;
        } else if (rule instanceof Rule) {
            return (Rule) rule;
        } else {
            Rule copy = new Rule(rule); // replace with casting ...
            return copy;
        }
    }
}
