/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.util.Utilities;

/**
 * Implementation of Feature Type Style; care is taken to ensure everything is mutable.
 *
 * @author James Macgill
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 */
public class FeatureTypeStyle implements Cloneable {

    /** This option influences how multiple rules matching the same feature are evaluated */
    public static String KEY_EVALUATION_MODE = "ruleEvaluation";

    /** The standard behavior, all the matching rules are executed */
    public static String VALUE_EVALUATION_MODE_ALL = "all";

    /** Only the first matching rule gets executed, all the others are skipped */
    public static String VALUE_EVALUATION_MODE_FIRST = "first";

    private List<Rule> rules = new ArrayList<Rule>();
    private Set<SemanticType> semantics = new LinkedHashSet<SemanticType>();
    private Set<String> featureTypeNames = new LinkedHashSet<String>();

    private String description = "";
    private String name = "name";
    private URL online = null;
    private String transformation = null;

    protected Map<String,String> options;

    /** Creates a new instance of FeatureTypeStyle */
    protected FeatureTypeStyle(Rule[] rules) {
        this(Arrays.asList(rules));
    }

    protected FeatureTypeStyle(List<Rule> arules) {
        rules = new ArrayList<Rule>();
        rules.addAll(arules);
    }

    /** Creates a new instance of FeatureTypeStyleImpl */
    protected FeatureTypeStyle() {
        rules = new ArrayList<Rule>();
    }

    public FeatureTypeStyle(FeatureTypeStyle fts) {
        this.description = fts.getDescription();
        this.featureTypeNames = new LinkedHashSet<String>(fts.featureTypeNames());
        this.name = fts.getName();
        this.rules = new ArrayList<Rule>();
        if (fts.rules() != null) {
            for (Rule rule : fts.rules()) {
                rules.add(rule); // need to deep copy?
            }
        }
        this.semantics = new LinkedHashSet<SemanticType>(fts.semanticTypeIdentifiers());
        this.online = fts.getURL();
        this.transformation = fts.getTransformation();
    }

    public List<Rule> rules() {
        return rules;
    }

    public Set<SemanticType> semanticTypeIdentifiers() {
        return semantics;
    }

    public Set<String> featureTypeNames() {
        return featureTypeNames;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void accept(StyleVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a deep copy clone of the FeatureTypeStyle.
     *
     * @see org.geotools.styling.FeatureTypeStyle#clone()
     */
    public Object clone() {
        FeatureTypeStyle clone = new FeatureTypeStyle();

        final List<Rule> rulesCopy = new ArrayList<Rule>();

        for (final Rule rl : rules) {
            rulesCopy.add((Rule) (rl.clone()));
        }

        clone.rules = new ArrayList<Rule>();
        clone.featureTypeNames = new LinkedHashSet<String>();
        clone.semantics = new LinkedHashSet<SemanticType>();
        final List<Rule> cloneRules = (List<Rule>) clone.rules();
        cloneRules.addAll(rulesCopy);
        clone.featureTypeNames().addAll(featureTypeNames);
        clone.semanticTypeIdentifiers().addAll(semantics);

        return clone;
    }

    /**
     * Overrides hashCode.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;

        if (rules != null) {
            result = (PRIME * result) + rules.hashCode();
        }
        if (semantics != null) {
            result = (PRIME * result) + semantics.hashCode();
        }
        if (featureTypeNames != null) {
            result = (PRIME * result) + featureTypeNames.hashCode();
        }
        if (name != null) {
            result = (PRIME * result) + name.hashCode();
        }
        if (description != null) {
            result = (PRIME * result) + description.hashCode();
        }
        if (options != null) {
            result = PRIME * result + options.hashCode();
        }
        if (transformation != null) {
            result = PRIME * result + transformation.hashCode();
        }
        if (online != null) {
            result = PRIME * result + online.hashCode();
        }
        return result;
    }

    /**
     * Compares this FeatureTypeStyleImpl with another.
     *
     * <p>Two FeatureTypeStyles are equal if they contain equal properties and an equal list of
     * Rules.
     *
     * @param oth The other FeatureTypeStyleImpl to compare with.
     * @return True if this and oth are equal.
     */
    public boolean equals(Object oth) {

        if (this == oth) {
            return true;
        }

        if (oth instanceof FeatureTypeStyle) {
            FeatureTypeStyle other = (FeatureTypeStyle) oth;

            return Utilities.equals(name, other.name)
                    && Utilities.equals(description, other.description)
                    && Utilities.equals(rules, other.rules)
                    && Utilities.equals(featureTypeNames, other.featureTypeNames)
                    && Utilities.equals(semantics, other.semantics)
                    && Utilities.equals(getOptions(), other.getOptions())
                    && Utilities.equals(getTransformation(), other.getTransformation())
                    && Utilities.equals(getURL(), other.getURL());
        }

        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("FeatureTypeStyleImpl");
        buf.append("[");
        if (name != null) {
            buf.append(" name=");
            buf.append(name);
        } else {
            buf.append(" UNNAMED");
        }
        buf.append(", ");
        buf.append(featureTypeNames);
        buf.append(", rules=<");
        buf.append(rules.size());
        buf.append(">");
        if (rules.size() > 0) {
            buf.append("(");
            buf.append(rules.get(0));
            if (rules.size() > 1) {
                buf.append(",...");
            }
            buf.append(")");
        }
        if (options != null) {
            buf.append(", options=" + options);
        }
        buf.append("]");
        return buf.toString();
    }

    public void setURL(URL online) {
        this.online = online;
    }

    public URL getURL() {
        return online;
    }


    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public boolean hasOption(String key) {
        return options != null && options.containsKey(key);
    }

    public Map<String, String> getOptions() {
        if (options == null) {
            options = new LinkedHashMap<String, String>();
        }
        return options;
    }
}