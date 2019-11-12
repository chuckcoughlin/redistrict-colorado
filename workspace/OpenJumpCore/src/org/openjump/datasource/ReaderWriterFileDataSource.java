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

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.openjump.io.CompressedFile;
import org.openjump.io.DriverProperties;
import org.openjump.io.JUMPReader;
import org.openjump.io.JUMPWriter;
import org.openjump.util.UriUtil;

/**
 * Adapts the old JUMP I/O API (Readers and Writers) to the new JUMP I/O API
 * (DataSources).
 */
public class ReaderWriterFileDataSource extends FileDataSource {

  protected JUMPReader reader = null;
  protected JUMPWriter writer = null;
  protected String[] extensions = new String[0];


  public ReaderWriterFileDataSource(JUMPReader reader, JUMPWriter writer, String[] extensions) {
    this.reader = reader;
    this.writer = writer;
    this.extensions = extensions;
  }


  /**
   * return 'file.ext (archive.ext)' for archive members and 'file.ext' for all others
   */
  protected static String createDescriptiveName(URI uri) {
    if (CompressedFile.isArchive(uri))
      return UriUtil.getZipEntryName(uri) + " (" + UriUtil.getZipFileName(uri)
          + ")";

    return UriUtil.getFileName(uri);
  }

  protected DriverProperties getReaderDriverProperties() {
    return getDriverProperties();
  }

  protected DriverProperties getWriterDriverProperties() {
    return getDriverProperties();
  }

  protected DriverProperties getDriverProperties(){
    DriverProperties properties = new DriverProperties();
    Map<String,Object> map = getProperties();

    // explicitly copy into properties object or getProperty() returns null
    for (Map.Entry<String,Object> entry : map.entrySet()){
      properties.setProperty(entry.getKey(), (String.valueOf(entry.getValue())));
    }

    return properties;
  }

  private DriverProperties fixUpDriverProperties(DriverProperties dp)
      throws Exception {
    // fixup the properties: generate FILE from URI and the other way around
    // some _old_ Drivers expect a FILE property to be set, let's generate it
    // from URI in case it is missing
    String uri = dp.getProperty(DataSource.URI_KEY);
    String file = dp.getProperty(DataSource.FILE_KEY);
    if (file == null && uri != null)
      dp.setProperty(DataSource.FILE_KEY, new URI(uri).getPath());
    else if (file != null && uri == null)
      dp.setProperty(DataSource.URI_KEY, new File(file).toURI().toString());

    return dp;
  }

  @Override
  public boolean isReadable() {
    return reader instanceof JUMPReader;
  }

  @Override
  public boolean isWritable() {
    return writer instanceof JUMPWriter;
  }

  @Override
  public String[] getExtensions() {
    return extensions;
  }

}
