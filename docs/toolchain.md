# Toolchain

This document describes the prerequisites and credits the pre-cursors for installation and construction of the various tools in this repository.


***
## Table of Contents <a id="table-of-contents"></a>

  * [Software](#software)
    * [Third party jar files](#jarfiles)
    * [Open Jump](#jump)
    * [Shapefiles](#shapefiles)
  * [Validation](#validation)
    * [DRA2020](#DRA2020)


***
## Software <a id="software"/>
The development host is an iMac running OSX Mohave (10.14) and Java 10. It is downloadable from [here](https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase10-4425482.html). Make sure to download the JDK and install the “Development tools” into the default location (e.g. /usr/local/bin). Extend the system path to include this area.

The code repository resides on this machine.
### Third-party Jar Files  <a id="jarfiles"></a>
[toc](#table-of-contents)
The *Archive* eclipse project is a collection of open-source library modules
used in the applications. These have all been updated for compatibility with
Java 11.

* https://commons.apache.org/proper/commons-compress/ commons-compress-1.19.jar Apache commons compression handling
*  https://commons.apache.org/proper/commons-lang commons-lang3-3.9.jar Apache commons helper utilities
* https://github.com/locationtech/jts/releases jts-core-16.1.jar VividSolutions JTS Topology Suite
* https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/ json-simple-1.1.1.jar JSON parser

*** Modularization ***<br/>
The open source jar files listed above have all been manually updated for Java11 module compatibility and then stored in our `git`  repository.

Thanks to [Michael Easter](https://github.com/codetojoy/easter_eggs_for_java_9/blob/master/egg_34_stack_overflow_47727869/run.sh) for the following example that shows how to modularize a JTS jar file.
The directory containing the un-modularized jar is the starting point. The  modularized result will be stored into ``lib``.
```
   jdeps --generate-module-info work jts-core-1.16.1.jar
   mkdir lib
   cp jts-core-1.16.1.jar lib
   rm -rf classes
   mkdir classes
   cd classes
   jar -xf ../jts-core-1.16.1.jar
   cd ../work/org.locationtech.jts
   javac -p org.locationtech.jts -d ../../classes module-info.java
   cd ../..
   jar -uf lib/jts-core-1.16.1.jar -C classes module-info.class
```
  ### OpenJUMP  <a id="jump"></a>
  [toc](#table-of-contents)

The core of the user interface is based on the open source project [OpenJUMP](https://live.osgeo.org/en/overview/openjump_overview.html). The application has
been modified for our purposes chiefly allowing it to build under current versions of Java. We have also added actions specifically for dealing with
redistricting metrics.

* Modifications:
  * Package structure - organized classes separating `core` from `ui`
  * Logging - use java.util/logging instead of log4j
  * XML - use java.xml instead of jdom
  * Serialization - use Jackson instead of custom java2xml.
* Features removed:
  * Database integration
  * `tiff`,
  * GML - Geography Markup Language
* New features:
  * Spreadsheet - provide output to Google sheets


### Shapefiles <a id="shapefiles"></a>
[toc](#table-of-contents)

The main code for analyzing ESRI shape files is derived from the Unified Mapping Platform, [OpenJump](https://sourceforge.net/projects/jump-pilot/files/latest/download). Code at version 1.14.1 was simplified and upgraded for Java 11.
Further details of the format were gleaned from `QGIS` C++ code downloadable [here](https://github.com/qgis/QGIS).
