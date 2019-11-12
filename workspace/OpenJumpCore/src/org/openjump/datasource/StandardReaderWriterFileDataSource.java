/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.openjump.datasource;

import java.util.Arrays;
import java.util.Collection;

import org.locationtech.jts.io.gml2.GMLWriter;
import org.openjump.io.DriverProperties;
import org.openjump.io.FMEGMLReader;
import org.openjump.io.FMEGMLWriter;
import org.openjump.io.GMLReaderWrapper;
import org.openjump.io.GMLWriterWrapper;
import org.openjump.io.GeoJSONReader;
import org.openjump.io.GeoJSONWriter;
import org.openjump.io.JMLReader;
import org.openjump.io.JMLWriter;
import org.openjump.io.JUMPReader;
import org.openjump.io.JUMPWriter;
import org.openjump.io.ShapefileReader;
import org.openjump.io.ShapefileWriter;
import org.openjump.io.WKTReaderWrapper;
import org.openjump.io.WKTWriterWrapper;
import org.openjump.util.Block;
import org.openjump.util.CollectionUtil;



/**
 * Contains DataSource classes for the standard JUMP Readers and
 * Writers. DataSource implementations cannot be anonymous classes if they are
 * to be saved to a project file (because the class name is saved).  
 */
public abstract class StandardReaderWriterFileDataSource extends ReaderWriterFileDataSource {

    public static final String[] GML_EXTENSIONS = new String[] { "gml", "xml" };

    public static String TEMPLATE_FILE_KEY  = "TemplateFile";

    public static final String OUTPUT_TEMPLATE_FILE_KEY = "Output Template File";

    public static final String INPUT_TEMPLATE_FILE_KEY = "Input Template File";

    public StandardReaderWriterFileDataSource(
        JUMPReader reader,
        JUMPWriter writer,
        String[] extensions) {
        super(reader, writer, extensions);
    }
    
    
    //DataSources must have a parameterless constructor so they can be
    //reconstructed by Java2XML. [Jon Aquino] 

    public String[] getExtensions() {
        return extensions;
    }

    private static GMLWriterWrapper createGMLWriter() {
        return new GMLWriterWrapper();
    }

    private static JUMPReader createGMLReader() {
        return new GMLReaderWrapper();
//        new DelegatingCompressedFileHandler(
//            new GMLReader(),
//            toEndings(StandardReaderWriterFileDataSource.GML_EXTENSIONS)) {
//            public FeatureCollection read(DriverProperties dp) throws Exception {
//                mangle(
//                    dp,
//                    "TemplateFile",
//                    "CompressedFileTemplate",
//                    Arrays.asList("_input.xml", ".input", ".template"));
//                return super.read(dp);
//            }
//    
//        };
    }

    public static Collection<String> toEndings(String[] extensions) {
        return CollectionUtil.collect(Arrays.asList(extensions), new Block() {
            public Object yield(Object extension) {
                return "." + extension;
            }
        });
    }

    public static class JML extends ClassicReaderWriterFileDataSource {
        public JML() {
            super(new JMLReader(), new JMLWriter(), new String[] { "jml" });
        }
    }

    public static class WKT extends ClassicReaderWriterFileDataSource {
        public WKT() {
            super(new WKTReaderWrapper(), new WKTWriterWrapper(), new String[] { "wkt", "txt" });
        }
    }

    public static class Shapefile extends ClassicReaderWriterFileDataSource {
        public Shapefile() {
            super(new ShapefileReader(), new ShapefileWriter(), new String[] { "shp" });
        }
    }

    public static class GeoJSON extends ClassicReaderWriterFileDataSource {
      public GeoJSON() {
          super(new GeoJSONReader(), new GeoJSONWriter(), new String[] { "json" });
      }
    }

    public static class FMEGML extends ClassicReaderWriterFileDataSource {
        public FMEGML() {
            super(new FMEGMLReader(), new FMEGMLWriter(), new String[] { "gml", "xml", "fme" });
        }
    }

    public static class GML extends ClassicReaderWriterFileDataSource {
        public GML() {
            super(createGMLReader(), createGMLWriter(), StandardReaderWriterFileDataSource.GML_EXTENSIONS);
        }
        protected DriverProperties getReaderDriverProperties() {
            return super.getReaderDriverProperties().set(
                    TEMPLATE_FILE_KEY,
                    (String) getProperties().get(StandardReaderWriterFileDataSource.INPUT_TEMPLATE_FILE_KEY));
        }

        protected DriverProperties getWriterDriverProperties() {
            return super.getWriterDriverProperties().set(
                    TEMPLATE_FILE_KEY,
                    (String) getProperties().get(StandardReaderWriterFileDataSource.OUTPUT_TEMPLATE_FILE_KEY));
        }        
        
        public boolean isReadable() {
            return getProperties().containsKey(StandardReaderWriterFileDataSource.INPUT_TEMPLATE_FILE_KEY);
        }
        
        public boolean isWritable() {
            return getProperties().containsKey(StandardReaderWriterFileDataSource.OUTPUT_TEMPLATE_FILE_KEY);
        }

    }    
       
    /**
     * The first JUMP Readers took responsibility for handling .zip and
     * .gz files (a more modular design choice would have been to handle 
     * compression outside of the Readers); this class uses a
     * DelegatingCompressedFileHandler to ensure that these JUMP Readers
     * receive the properties they need to do decompression.
     */
     private static class ClassicReaderWriterFileDataSource extends StandardReaderWriterFileDataSource {
         ClassicReaderWriterFileDataSource(
             JUMPReader reader,
             JUMPWriter writer,
             String[] extensions) {
             super(reader, writer, extensions);
             this.extensions = extensions;
         }
     }
}
