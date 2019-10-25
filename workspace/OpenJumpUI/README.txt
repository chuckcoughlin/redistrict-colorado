OpenJUMP ReadMe file
--------------------
Version 1.14.1 release rev.6147 (04. March 2019)

Contents
--------
1. Licensing
2. Installation instructions
3. Running OpenJUMP
4. Support
5. OpenJUMP history
6. Credits
7. Changes


1. Licensing
------------
OpenJUMP is distributed under the GNU General Public License version 2 (GPLv2).
The license text can be found in the file "licenses/gpl-2.txt".

OpenJUMP uses and distributes the following (in alphabetical order) formatted
as (Component name - License name - License file in "licenses/" folder or link)
 BeanShell - LGPL2.1 - lgpl-2.1.txt
 Buoy Framework - Public Domain
 Code2000 Unicode font - GPL2 - gpl-2.txt
 Commons Codec, Compress, IO, Lang, Logging
   - Apache License Version 2.0 - apache_license-2.0.txt
 Extensible-TIFF-JAI (xtiff jai) - MIT License - MIT-license.txt
 Icons (some original or based fully or in part on the following)
   FAMFAMFAM Silk by http://www.famfamfam.com - CC BY 2.5
     - http://creativecommons.org/licenses/by/2.5/
   Fugue by Yusuke Kamiyamane http://p.yusukekamiyamane.com - CC BY 3.0
     - http://creativecommons.org/licenses/by/3.0/
   OJ icon v3 and others by Edgar Soldin http://soldin.de - GPL2 - gpl-2.txt
   [ok|ko]_pg.png PostgreSQL icon - Trademark Policy
     - https://wiki.postgresql.org/wiki/Trademark_Policy
   [ok|ko]_mariadb.png MariaDB icon - MariaDB Branding Guidelines
     - https://mariadb.com/kb/en/mariadb/branding-guidelines/
   ok_spatialite.png Spatialite icon, dolphin_icon.png (Edgar Soldin)
     - GPL3 - gpl-3.txt
 ImageIO Ext - LGPL3 - lgpl-3.0.txt
   except the following dependencies/components:
   commons-codec, commons-io, commons-lang, commons-logging (see above)
   imageio-ext-streams - partly LGPL2 or 3 - imageio-ext-streams-LICENSE.txt
   imageio-ext-tiff - BSD style license - imageio-ext-tiff-BSD-LICENSE.txt
 JAI core, codec - Java Advanced Imaging Distribution License - jdl-jai.pdf
 JAI imageio core - BSD style license - jai-core.BSD-LICENSE.txt
 JAMA - Public Domain
 Jdom - Apache-style open source jdom license, 
       with the acknowledgment clause removed - jdom_license.txt
 JMath and its successor projects JMathTools (IO,Plot,Array) - BSD license 
     - jmath_license.txt
 Javascript library RHINO - GPL2 - gpl-2.txt
 JSON-simple - Apache License Version 2.0 - apache_license-2.0.txt
 JTS Topology Suite - LGPL2 - lgpl-2.1.txt
 JUnit - Eclipse Public License v1.0 - epl-v10.txt
 Jython - Jython license - jython_license.txt
 Log4J - Apache License Version 2.0 - apache_license-2.0.txt
 Logo
   Splash Logo designed by Paola Zanetti (paoladorileo<AT>gmail.com)
   Vertical Design used in the installer by Stefan Steiniger 2012
 Postgresql JDBC driver - Postgresql BSD license - postgresql-BSD_license.txt
 Xerces2 Java Parser - Apache License Version 2.0 - apache_license-2.0.txt
 XZ for Java - Public Domain
 Outline Swing Component by Netbeans.org, GPL2 - gpl-2.txt

and the following plugins
( Component name - License name - License file in "licenses/" folder
   list of files and dependencies with license if any )
 Oracle Datastore (Nicolas Ribot) - GPL2 - gpl-2.txt
 SkyPrinter - GPL2 - gpl-2.txt
  SkyPrinterPlugIn-*.jar
  itext-*.jar - LGPL2.1 - lgpl-2.1.txt


Additionally PLUS distribution contains
( Component name (Author) - License name - License file in "licenses/" folder
   list of files and dependencies with license if any )
 Batik SVG Toolkit - Apache License Version 2.0 - apache_license-2.0.txt
 ImageIO Ext (mainly libs needing native support) - LGPL3 - lgpl-3.0.txt
  except the following dependencies/components:
  imageio-ext-imagereadmt - BSD style license -
   imageio-ext-imagereadmt-BSD-LICENSE.txt
  turbojpeg-wrapper - BSD style license - LICENSE.libjpegturbo.txt
 Geoarbores Vertex Symbols Collection (Giuseppe Aruta) - GPL2 - gpl-2.txt
  lib/ext/VertexImages/*.wkt
 ECW and JPEG2000 Read Support based on ECW SDK 3.3 for 
  Windows x86/x64, Linux, Mac OSX with 32bit java runtime - 
  ERDAS ECW JPEG2000 SDK license - ecw license.txt
  lib/native/[os/]{jecw-*.jar,NCS*.dll,ermapper.jar}
 JGraphT - LGPL2 - lgpl-2.1.txt
  lib/jgrapht-*.jar
 Postgis driver (postgis-*.jar) - LGPL2 - lgpl-2.1.txt
 MariaDB Connector/J (mariadb-java-client-*.jar) - LGPL2 - lgpl-2.1.txt
 MySQL Connector/J (mysql-connector-java-*-bin.jar) - GPL2 - gpl-2.txt
 Simple Logging Facade for Java (SLF4J) - MIT License - MIT-license.txt
  lib/slf4j-api-*.jar,slf4j-api-*.jar
 Xerial SQLite JDBC driver (sqlite-jdbc-*.jar) - apache_license-2.0.txt
 
and the following plugins
 Aggregation PlugIn (Michaël Michaud) - GPL2 - gpl-2.txt
  aggregation-*.jar
 BeanShell Editor (Michaël Michaud) - GPL2 - gpl-2.txt
  bsheditor4jump-*.jar, buoy.jar(see above)
 CAD Tools (Giuseppe Aruta) - GPL3 - gpl-3.0.txt
  CadTools-*.jar
 CSV driver (Michaël Michaud) - GPL2 - gpl-2.txt
  csv-driver-*.jar
 Concave Hull (Eric Grosso) - LGPL2.1, GPL2 - lgpl-2.1.txt, gpl-2.txt
  ConcaveHull*.{jar,txt}
 CTS extension (Michaël Michaud) - GPL2 - gpl-2.txt
  CTS-PlugIn-*.jar,
  lib/cts-*.jar - LGPL3 - lgpl-3.0.txt
  SLF4J (see above)
 DXF driver (Michaël Michaud) - GPL2 - gpl-2.txt
  driver-dxf-*.jar
 Five Color Map Coloring (Larry Reeder) - GPL3 - gpl-3.0.txt
  SLF4J (see above), JGraphT (see above)
 Graph Extension (Michaël Michaud) - GPL2 - gpl-2.txt
  graph-toolbox-*.jar, lib/jump-jgrapht-*.jar
  JGraphT (see above)
 Jump Chart (com.cadplan.jump) - GPL2 - gpl-2.txt
  JumpChart.jar
 JUMP DB Query Plugin (Larry Reeder) - GPL3 - gpl-3.0.txt
  jumpdbquery-*.jar, dbquery.properties
  jsyntaxpane-*.jar - Apache License Version 2.0 - apache_license-2.0.txt
  gt2-oracle-spatial-*.jar - LGPL2.1 - lgpl-2.1.txt
  mysql-connector-java-*.jar - GPL2 - gpl-2.txt
  sqlite-jdbc-*.jar - Apache License Version 2.0 - apache_license-2.0.txt
 Jump Fill Pattern (com.cadplan.jump) - GPL2 - gpl-2.txt
  JumpFillPattern.jar
 KML Driver (skyjump) - GPL2 - gpl-2.txt
  kml-driver-*.jar
 Matching (Michaël Michaud) - GPL2 - gpl-2.txt
   matching-*.jar
   utils-*.jar
 Measure Toolbox (Giuseppe Aruta) - GPL3 - gpl-3.0.txt
  MeasureToolbox_*.jar
 OpenKLEM (Geomatica) - GPL2 - gpl-2.0.txt
 topographic and hydrological analysis
  OpenKLEM-*.jar
  OpenKLEMOJ-*.jar
 OSM Driver (Stefan Steiniger) - GPL2 - gpl-2.txt
  oj_osm_reader_v*.jar
 Printer (com.cadplan.jump) - GPL2 - gpl-2.txt
  jumpPrinter.jar (needs Vertex Symbols extension)
  itext-*.jar - LGPL2.1 - lgpl-2.1.txt
 Pirol Csv Dataset 
  (de.fhOsnabrueck.jump.pirol.datasources.pirolCSV) - GPL2 - gpl-2.txt
  PirolCsv.jar, pbaseClasses.jar
 SetAttributes (Michaël Michaud) - GPL2 - gpl-2.txt
  SetAttributes-.jar
  jaxb/jaxb-*.jar
 Sextante Tools (es.unex.sextante.openjump.extensions) - GPL3 - gpl-3.0.txt
  sextante-binding-*.jar, sextante/*.*
 Sextante algorithms  (www.scolab.es) - GPL3 - gpl-3.0.txt
  sextante_new_algorithms.jar
 TableLayout-*.jar - Clearthought License - clearthought-2.0.txt
  xbean-*.jar - Apache License Version 2.0 - apache_license-2.0.txt
 Topology Extension (Michaël Michaud) - GPL2 - gpl-2.txt
  topology-*.jar
 Vertex Note (com.cadplan.jump) - GPL2 - gpl-2.txt
 Vertex Symbols (com.cadplan.jump) - GPL2 - gpl-2.txt
  VertexSymbols.jar
  itext-*.jar - LGPL2.1 - lgpl-2.1.txt
 View Manager Extension (Michaël Michaud) - GPL2 - gpl-2.txt
  ViewManager-*.jar
  jaxb/jaxb-*.jar
 WFS Plugin
  commons-httpclient-3.1.jar
  deegree2-core-2.6-pre2-20140511.220246-596.jar
  jaxen-1.1.1.jar
  vecmath-1.5.2.jar


2. Installation instructions
----------------------------
Try the shiny installers
 OpenJUMP-Installer-*.exe (for windows)
 OpenJUMP-Installer-*.jar (for linux & mac)
Or
 Extract the portable zip file distribution.
 OpenJUMP-Portable-*.zip


3. Running OpenJUMP
-------------------
To start OpenJUMP run the launcher for your platform from the 
<appfolder>/bin directory.
- On Windows, double-click on oj_windows.bat or OpenJUMP.exe
- On Linux/Unix, launch oj_linux.sh
- On MacOSX, launch oj_mac.command or OpenJUMP.app

Additionally, if you used the installer you should have
- On Windows, a start menu entry.
- On Linux, a link on the desktop.
- On MacOSX, a self contained app on the desktop. 
  Move it to 'Applications' folder if you like.

Further information can be found in the OJ wiki: http://ojwiki.soldin.de


Startup/Command line options
----------------------------
Several startup options are available, either for the Java Virtual Machine, 
or for the OpenJUMP application. To change them, edit the startup script 
accordingly. The scripts contain documentation comments, don't be afraid.

Useful Java VM options
-Xms defines the allocated memory for the virtual machine at startup.
  Example: -Xms256M will allocate 256M of memory for OpenJUMP
-Xmx defines the maximum allocated memory for the virtual machine.
  Example: -Xmx256M
-Dproperty=value set a jvm system property.

OpenJUMP command line syntax:

  oj_starter -option <argument> ... <[data|project]_file>...

OpenJUMP options:

  -default-plugins <file.xml>
    Specifies the configuration file of a standard set of functions realized
    as plugins. For example almost all functions of the "Tools" menu.
    This is configured as
      -default-plugins bin\default-plugins.xml

  -h, -help
    show the help information

  -i18n <locale>
    Overrides the operating systems default locale setting (language, 
    number format etc.) For example:
    - For starting OpenJUMP in French: use -i18n fr
    - languages available (09/2011): 
      cz (czech)
      de (german)
      en (english)
      es (spanish)
      fi (finnish)
      fr (french)
      hu (hungarian)
      it (italian)
      ja_JP (japanese)
      ml (malayalam)
      pt (portuguese)
      pt_BR (brazilian portuguese)
      ta_IN (indian tamil)
      te (telugu)
      zh_CN (chinese simplified)
      zh_HK (chinese Hong Kong)
    ATTENTION: If the specified language is not available then
               the language used is english (en).

  -plug-in-directory <path> 
    Sets the location of the plugin directory.
    Default: JUMP_HOME/lib/ext

  -project <path/project.jmp> 
    DEPRECATED: simply add the path as mentioned in the syntax above
    Open a project located on the file system at starting time

  -properties <file.xml>
    specifies the file where OpenJUMP persistent properties are stored.
    See Wiki article "How to use a plugin with a properties file in ECLIPSE".
    Default: JUMP_HOME\bin\workbench-properties.xml

  -state <some/folder>
    specifies the folder where OpenJUMP stores data between executions
    (workbench-state.xml).
    Default: JUMP_HOME or SETTINGS_HOME

  -v, -version
    show version information


4. Support
----------
for a general overview visit
  www.openjump.org - the OpenJUMP web site
  jump-pilot.sourceforge.net - alternative domain to the above
for support
  consult the OJ wiki
    http://ojwiki.soldin.de
  use mailing list or trackers
    http://ojwiki.soldin.de/index.php?title=OpenJUMP_Support

For commercial support, e.g. paid plugin development, contact the developer 
mailing list http://lists.sourceforge.net/lists/listinfo/jump-pilot-devel .


5. OpenJUMP history
-------------------
OpenJUMP is a "fork" of the JUMP "Java Unified Mapping Platform" software,
developed by Vividsolutions and released in 2003.
During 2004, some enthusiastic developers joined together to enhance further 
the features of JUMP. They launched an independent development branch called 
OpenJUMP. This name gives credit to the original JUMP development, and at the 
same time describes the objectives of this project to be fully open to anyone
wanting to contribute.
Since May 2005 a complete development source is available at:
www.sourceforge.net/projects/jump-pilot


6. Credits
----------
Many thanks to all the contributors of OpenJUMP for their time and efforts:

Original development team of JUMP was:
  at Vividsolutions (www.vividsolutions.com)
    Martin Davis
    Jon Aquino
    Alan Chang 
  at Refractions Research Inc (www.refractions.net) 
    David Blasby 
    Paul Ramsey 

OpenJUMP continuous or recent contributors (in alphabetical order):
  Edgar Soldin (edso, http://soldin.de)
  Giuseppe Aruta
  Jukka Rahkonen
  Michaël Michaud
  Nicolas Ribot
  Stefan Steiniger

Past contributors (in alphabetical order):
  Alberto de Luca (geomaticaeambiente.it)
  Andreas Schmitz (lat-lon.de)
  Axel Orth
  Basile Chandesris
  Bing Ran
  Eric Lemesre
  Erwan Bocher
  Ezequias Rodrigues da Rocha
  Fco Lavin
  Geoffrey G Roy
  Hisaji Ono
  Jaakko Ruutiainen
  Jan Ruzicka
  Joe Desbonet
  John Clark
  Jonathan Aquino
  Kevin Neufeld
  Landon Blake (Sunburned Surveyor)
  Larry Becker (ISA.com)
  Larry Reeder
  Martin Davis (refractions.net)
  Matthias Scholz
  Mohammed Rashad
  Ole Rahn
  Paolo Rizzi
  Paul Austin
  Pedro Doria Meunier
  Sascha Teichmann (intevation.de)
  Stephan Holl
  Steve Tanner
  Ugo Taddei
  Uwe Dallüge

Translation contributors are
  Chinese: Elton Chan
  Czech: Jan Ruzicka
  English: Landon Blake
  Finnish: Jukka Rahkonnen
  French: Basile Chandesris, Erwan Bocher, Steve Tanner, Michaël Michaud
  German: Florian Rengers, Stefan Steiniger, Edgar Soldin
  Hungarian: Zoltan Siki
  Italian: Giuseppe Aruta
  Japanese: Hisaji Ono
  Malayalam : Mohammed Rashad
  Portuguese (brazilian):
    Ezequias Rodrigues da Rocha, 
    Cristiano das Neves Almeida
  Spanish:
    Giuseppe Aruta, Steve Tanner, Fco Lavin, 
    Nacho Uve, Agustin Diez-Castillo
  Tamil: Vikram Santhanam
  Telugu: Ravi Vundavalli

Contributing projects and companies:
- Intevation GmbH
  Nightly Build process, collaborative PlugIn development (Print Layout PlugIn)
  contact: Jan Oliver Wagner/Stephan Holl
- Larry Becker and Robert Littlefield (SkyJUMP team)
  partly at Integrated Systems Analysts, Inc.
  for providing their Jump ISA tools code and numerous other improvements
- Lat/Lon GmbH (deeJUMP team)
  for providing some plugins and functionality (i.e. WFS and WMS Plugins)
  contact: Markus Müller/Andreas Schmitz
- Pirol Project from University of Applied Sciences Osnabrück
  for providing the attribute editor. Note that the project is finished now.
  (contact: Arnd Kielhorn)
- VividSolutions Inc. & Refractions Inc.
  for support and answering the never ending stream of questions, especially:
  Martin Davis (now at Refractions Inc.)
  David Zwiers

others:
- L. Paul Chew for providing the Delaunay triangulation algorithm to 
  create Voronoi diagrams

7. Latest changes
-----------------
Version 1.14.1 release rev.6147 (released on 04. March 2019)

  Improvements
  * Add an option to connect to a WMS with invalid certificate
  * speedup loading GeoPackage datasets w/ date/time columns utilizing 
    flex feature's lazy conversion
  * added format to FlexibleDateParser for dates containing 
    ISO 8601 time zone "-08; -0800; -08:00" eg. "2019/02/17 22:44:35.325+02"
  * Added calculus of number of classes on DEM statistics plugin

  Bug Fixes
  * Corrected typo in SpatialiteDSMetadata datasetInfoQuery string preventing 
    spatial tables to be listed
  * Corrected another bug preventing some Spatialite tables to be loaded.
  * bugfix "NPE with adding data into the second project with OJ"

  PLUS
  * PLUS upgrade KML extension
    - 0.2.5 (2019-02-17) also read <placemark> if no <Folder> exists
  * bugfix #485 "Cannot import kml", removed currently obsolete conversion to 
    FlexibleFeatureSchema
  * OpenKLEM Plugin: Corrected a list of bug and made some implementations:
    1) Reduced hydro table to 2500 cells for OpenKLEM output tab (speedup time)
    2) added monitoring to mostly all processes
    3) workaround to avoid 'gost' list of raster layers (partially solved)
    4) workaround to solve border false values on the borders for aspect and 
       slope rasters
    5) added file type to exported raster plugin


Version 1.14 release rev.6065 (released on 01. January 2019)

  Highlights
  * CORE source is now java 8
  * general java 11 runtime compatibility
  * improved raster profile tool.
    use as profile trace either a selected linestring or a drawn line
    calculate relative/absolute slope of the profile added
    calculate traveling time if profile is defined along route added
    export profile to DXF added
  * replaced SaveLegendPlugin with a new one, LegendPlugin, which allows
    to display a legend of symbols adopted into the view and to save it as 
    image file
  * replaced "Export style to SLD" with a new one, "Save Style" which allows
    to export either to SLD or to JUMP XLM layer style file
  * replaced "Import style from SLD" with "Load Style" plugin which allows 
    to load either SLD file or JUMP XML layer style file
  * speedup OJ startup by some seconds

  New Features
  * Added LegendPlugin to display a legend of symbols adopted into the view 
  * PLUS: Added OpenKLEM extension for topographic and Hydrological analysis
  * Reactivated Raster Legend plugin with option to save legend to image
  * Add two plugins :
    - GenerateUniqueRandomIdPlugIn (menu tools/edit attributes)
    - SelectAllOrderedFeaturesFromAttributeTablePlugIn (AttributeTable context menu)
  * Added histogram plugin for raster which calculates
    also statistic indices, relative and cumulative frequancies

  Improvements
  * Make IntersectPolygonLayersPlugIn faster
  * speedup some plugin's init by delaying gui and preventing double init
      org.openjump.core.ui.plugin.datastore.postgis.SaveToPostGISPlugIn
        took 0.43s now 0.01s
      org.openjump.core.ui.plugin.file.OpenFilePlugIn took 0.62s now 0.03s
      org.openjump.core.ui.plugin.file.OpenProjectPlugIn took 0.46s now 0.01s
      com.vividsolutions.jump.workbench.ui.zoom.InstallZoomBarPlugIn
        took 1.22s now 0.02s
    as a side note, testing shows that OJ startup gets faster with newer java
    versions _out of the box_, repeated tests on a 2 core laptop showed:
      jdk8 21.30s, jdk9 14,24s, jdk11 12,02s
  * Added SaveStylePlugin that allows to save OpenJUMP Style either to SLD file
    or to JUMP XML layer style file <filename.style.xml>. 
    Deactivated "Export style to SLD"
  * Added LoadStylePlugin that allows to load SLD file or JUMP XML layer
    style file as OpenJUMP style.
    Deactivated "Import style from SLD"
  * Added a standard icon from Kosmo Saig to RasterLegendPlugIn, 
    WMSLegendPlugIn and (Layer) LegendPlugIn 
  * Moved LegendPlugIn to Layer>Style and Layer tree>Style menu. 
    Deactivated SaveLegendPlugIn
  * New mechanism for SpatialDatabasesDSMetadata to get information about
    spatial tables: done in one query, to reduce the number of queries
    sent to the backend (took several minutes on big DB)
  * upgrade apache commons codec, compress, io, lang3 to latest
  * comment unused javax.xml.bind package call, added commented alternatives
  * upgrade xz to latest
  * Improve LayerView naming and saving, allow views based on view
  * WMSLegendPlugIn. Added capability to export WMS legend to PNG image file
  * DEMStatisticsPlugIn. Substitute output HTMLDoc with JTable.
    Allow selection of multiple layers on plugin dialog.
    Extend to multi band raster files
  * make target layer of EraseLayerAWithLayerBPlugIn updateable. Improve 18N.
  * make EraseLayerAWithLayerBPlugIn more robust

  Bug fixes
  * make sure workbench pane background is blue over on all implementations,
    before the bg color was determined by the UI used (blue on windows, 
    black or grey on osx and linux)
  * reenable macOS menu integration for java9+, added Preferences item
  * rework version detection for 'openjdk version "12-ea"' in start scripts
  * linux/mac starter remove javax.se.ee when run w/ java 11+
  * Fix LayerView to make it compatible with project persistence
  * Fix #478 : AdHoc SQL query can be interrupted properly
  * Fix FlexibleFeature#getAttributes() (was not implemented)
  * bugfix in SkeletonPlugIn (PLUS version, GraphToolBox)
  * bugfix in Dissolve2 : could not use geometry attribute as key
  * 3 bugfix in layerListener management (hopefully fix #419)
    - layerListener is removed when the concerned layer is removed
    - sridStyle set geometry srid only once, not once per Layer
    - layerListener added by AbstractPlugIn UndoableEdit is removed from
      LayerManager when the layer is removed
  * also remove layerListeners added with associated to AttributeTablePanel,
    AttributeTab, OneLayerAttributeTab, ViewAttributesFrame, InfoFrame,
    WorkbenchFrame, EnsureAllLayersHaveSrid when the Layer is removed
  * bugfix: handling date/time fields of JML datasets was still broken (empty
    strings was returned as String instead of null)
  * bugfix #475: log messages doubled in the console

  PLUS
  * Updated ColorChooser to version 1.3
  * Updated OpenKLEM to ver 20181222, correct some bugs
  * PLUS: add jaxb xml libs, which were removed from jdk9+, currently only
    needed by ViewManager extension, document extension and needed deps in
    readme.txt
  * Small fix in Topology extension (Adjust Polygon Boundaries)
  * Updated OpenKLEM to ver. 20181010:
    Moved Slope, Aspect, Hillshade plugin to Geomorphometry menu
  * Updated OpenKLEM to ver. 20181004:
    a) Homologated HillShade to ESRI standard
    b) Histogram. Correct bug.
  * Update GraphToolbox and MatchingPlugIn extensions
  * Raster profile plugin:
      Plugin recognizes Layer unit.
      Absolute or relative slope output
      Added capability to compute travel time on route depending to input flat,
      uphill and downhill speeds
      Simplified additional result output.
      Added capability to draw a slope profile
  * upgrade dxf-driver to 0.9.0 (could not export MultiPolygon)
  * small improvements in SkeletonPlugIn (PLUS version, GraphToolBox)
