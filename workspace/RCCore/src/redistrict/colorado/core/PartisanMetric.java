/**  
 * Copyright (C) 2019-2020 Charles Coughlin
 * 
 * This program is free software; you may redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Specify the available algorithms for Partisan Asymmetry.
 * Create lookup maps between labels and enums. Go both directions.
 */
public enum PartisanMetric
{
	DECLINATION,
	EFFICIENCY_GAP,
	MEAN_MEDIAN,
	PARTISAN_BIAS
	;

	// Create a map for reverse lookup by string
	private static final Map<String, PartisanMetric> LABEL_MAP = createLabelMap();
	private static final Map<PartisanMetric,String> ENUM_MAP = createEnumMap();
    private static Map<String, PartisanMetric> createLabelMap() {
        Map<String, PartisanMetric> map = new HashMap<>();
        map.put("Declination",DECLINATION);
        map.put("Efficiency Gap",EFFICIENCY_GAP);
        map.put("Mean-Median",MEAN_MEDIAN);
        map.put("Partisan Bias",PARTISAN_BIAS);
        return map;
    }  
    private static Map<PartisanMetric,String> createEnumMap() {
        Map<PartisanMetric,String> map = new HashMap<>();
        map.put(DECLINATION,"Declination");
        map.put(EFFICIENCY_GAP,"Efficiency Gap");
        map.put(MEAN_MEDIAN,"Mean-Median");
        map.put(PARTISAN_BIAS,"Partisan Bias");
        return map;
    }  
	/**
	 * @return the basic asymmetry types in a list.
	 */
	public static List<PartisanMetric> getTypes() {
		List<PartisanMetric> types = new ArrayList<>();
		for (PartisanMetric type : PartisanMetric.values()){	
			types.add(type);
		}
		return types;
	}
	
	/**
	 * @return metric type names in a list.
	 */
	public static List<String> names() {
		List<String> names = new ArrayList<>();
		for (PartisanMetric type : PartisanMetric.values()) {
			names.add(type.name());
		}
		return names;
	}
	/**
	 * @return metric labels in a list.
	 */
	public static List<String> labels() {
		List<String> list = new ArrayList<>();
		for(PartisanMetric metric:PartisanMetric.values() ) {
			list.add(labelForMetric(metric));
		}
		return list;
	}

	public static PartisanMetric metricForLabel(String label) {
		return LABEL_MAP.get(label);
	}
	public static String labelForMetric(PartisanMetric metric) {
		return ENUM_MAP.get(metric);
	}
}
