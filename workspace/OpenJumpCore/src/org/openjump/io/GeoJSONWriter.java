package org.openjump.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.Assert;
import org.openjump.datasource.DataSource;
import org.openjump.geometry.feature.FeatureCollection;
import org.openjump.util.FileUtil;

public class GeoJSONWriter extends AbstractJUMPWriter {
	private boolean isEncodeCRS;
	private double scale;

	public GeoJSONWriter() {
		this.isEncodeCRS = true;
		this.scale = 1.0;
	}


	public void setEncodeCRS(boolean flag) { this.isEncodeCRS = flag; }

	@Override
	public void write(FeatureCollection featureCollection, DriverProperties dp)
			throws Exception {

		FileOutputStream fileStream = null;
		Writer w = null;
		try {
			GeoJSONFeatureCollectionWrapper fcw = new GeoJSONFeatureCollectionWrapper(
					featureCollection);

			String uriString = dp.getProperty(DataSource.URI_KEY);
			if (uriString == null) {
				throw new IllegalParametersException(
						"call to GeoJSONReader.write() has DataProperties w/o an Uri specified");
			}
			URI uri = new URI(uriString);

			fileStream = new FileOutputStream(new File(uri));
			w = new OutputStreamWriter(fileStream, GeoJSONConstants.CHARSET);

			fcw.writeJSONString(w,getTaskMonitor());
		} finally {
			FileUtil.close(w);
			FileUtil.close(fileStream);
		}
	}
	public String write(Geometry paramGeometry) {
		StringWriter stringWriter = new StringWriter();
		try {
			write(paramGeometry, stringWriter);
		} catch (IOException iOException) {
			Assert.shouldNeverReachHere();
		} 

		return stringWriter.toString();
	}

	public void write(Geometry paramGeometry, Writer paramWriter) throws IOException {
		Map map = create(paramGeometry, this.isEncodeCRS);
		JSONObject.writeJSONString(map, paramWriter);
		paramWriter.flush();
	}

	private Map<String, Object> create(Geometry paramGeometry, boolean paramBoolean) {
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put("type", paramGeometry.getGeometryType());

		if (paramGeometry instanceof Point) {
			Point point = (Point)paramGeometry;

			final String jsonString = getJsonString(point.getCoordinateSequence());

			linkedHashMap.put("coordinates", new JSONAware()
			{
				public String toJSONString() {
					return jsonString;
				}
			});
		}
		else if (paramGeometry instanceof LineString) {
			LineString lineString = (LineString)paramGeometry;

			final String jsonString = getJsonString(lineString
					.getCoordinateSequence());

			linkedHashMap.put("coordinates", new JSONAware()
			{
				public String toJSONString() {
					return jsonString;
				}
			});
		}
		else if (paramGeometry instanceof Polygon) {
			Polygon polygon = (Polygon)paramGeometry;

			linkedHashMap.put("coordinates", makeJsonAware(polygon));
		}
		else if (paramGeometry instanceof MultiPoint) {
			MultiPoint multiPoint = (MultiPoint)paramGeometry;

			linkedHashMap.put("coordinates", makeJsonAware(multiPoint));
		}
		else if (paramGeometry instanceof MultiLineString) {
			MultiLineString multiLineString = (MultiLineString)paramGeometry;

			linkedHashMap.put("coordinates", makeJsonAware(multiLineString));
		}
		else if (paramGeometry instanceof MultiPolygon) {
			MultiPolygon multiPolygon = (MultiPolygon)paramGeometry;

			linkedHashMap.put("coordinates", makeJsonAware(multiPolygon));
		}
		else if (paramGeometry instanceof GeometryCollection) {
			GeometryCollection geometryCollection = (GeometryCollection)paramGeometry;

			ArrayList arrayList = new ArrayList(
					geometryCollection.getNumGeometries());

			for (byte b = 0; b < geometryCollection.getNumGeometries(); b++) {
				arrayList.add(create(geometryCollection.getGeometryN(b), false));
			}

			linkedHashMap.put("geometries", arrayList);
		} else {

			throw new IllegalArgumentException("Unable to encode geometry " + paramGeometry.getGeometryType());
		} 

		if (paramBoolean) {
			linkedHashMap.put("crs", createCRS(paramGeometry.getSRID()));
		}

		return linkedHashMap;
	}

	private Map<String, Object> createCRS(int paramInt) {
		LinkedHashMap<String, Object> linkedHashMap1 = new LinkedHashMap<>();
		linkedHashMap1.put("type", "name");

		LinkedHashMap<String, Object> linkedHashMap2 = new LinkedHashMap<>();
		linkedHashMap2.put("name", "EPSG:" + paramInt);

		linkedHashMap1.put("properties", linkedHashMap2);

		return linkedHashMap1;
	}
	private List<JSONAware> makeJsonAware(Polygon paramPolygon) {
		ArrayList arrayList = new ArrayList();


		final String jsonString = getJsonString(paramPolygon.getExteriorRing()
				.getCoordinateSequence());
		arrayList.add(new JSONAware()
		{
			public String toJSONString() {
				return jsonString;
			}
		});

		for (byte b = 0; b < paramPolygon.getNumInteriorRing(); b++) {
			final String jsString = getJsonString(paramPolygon.getInteriorRingN(b)
					.getCoordinateSequence());
			arrayList.add(new JSONAware()
			{
				public String toJSONString() {
					return jsString;
				}
			});
		} 

		return arrayList;
	}
	private List<Object> makeJsonAware(GeometryCollection paramGeometryCollection) {
		ArrayList<Object> arrayList = new ArrayList<>(
				paramGeometryCollection.getNumGeometries());
		for (byte b = 0; b < paramGeometryCollection.getNumGeometries(); b++) {
			Geometry geometry = paramGeometryCollection.getGeometryN(b);

			if (geometry instanceof Polygon) {
				Polygon polygon = (Polygon)geometry;
				arrayList.add(makeJsonAware(polygon));
			}
			else if (geometry instanceof LineString) {
				LineString lineString = (LineString)geometry;
				final String jsonString = getJsonString(lineString
						.getCoordinateSequence());
				arrayList.add(new JSONAware()
				{
					public String toJSONString() {
						return jsonString;
					}
				});
			}
			else if (geometry instanceof Point) {
				Point point = (Point)geometry;
				final String jsonString = getJsonString(point.getCoordinateSequence());
				arrayList.add(new JSONAware()
				{
					public String toJSONString() {
						return jsonString;
					}
				});
			} 
		} 

		return arrayList;
	}

	private String getJsonString(CoordinateSequence paramCoordinateSequence) {
		StringBuffer stringBuffer = new StringBuffer();

		if (paramCoordinateSequence.size() > 1) {
			stringBuffer.append("[");
		}
		for (byte b = 0; b < paramCoordinateSequence.size(); b++) {
			if (b!=0) {
				stringBuffer.append(",");
			}
			stringBuffer.append("[");
			stringBuffer.append(formatOrdinate(paramCoordinateSequence.getOrdinate(b, 0)));
			stringBuffer.append(",");
			stringBuffer.append(formatOrdinate(paramCoordinateSequence.getOrdinate(b, 1)));

			if (paramCoordinateSequence.getDimension() > 2) {
				double d = paramCoordinateSequence.getOrdinate(b, 2);
				if (!Double.isNaN(d)) {
					stringBuffer.append(",");
					stringBuffer.append(formatOrdinate(d));
				} 
			} 

			stringBuffer.append("]");
		} 


		if (paramCoordinateSequence.size() > 1) {
			stringBuffer.append("]");
		}

		return stringBuffer.toString();
	}

	private String formatOrdinate(double paramDouble) {
		String str = null;

		if (Math.abs(paramDouble) >= Math.pow(10.0D, -3.0D) && paramDouble < Math.pow(10.0D, 7.0D)) {
			paramDouble = Math.floor(paramDouble * this.scale + 0.5D) / this.scale;
			long l = (long)paramDouble;
			if (l == paramDouble) {
				str = Long.toString(l);
			} else {
				str = Double.toString(paramDouble);
			} 
		} else {
			str = Double.toString(paramDouble);
		} 

		return str;
	}
}
