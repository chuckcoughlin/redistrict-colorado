# Toolchain

This document describes the construction and installation of the "RCAnalyzer" application. It defines prerequisites and credits its pre-cursors.


***
## Table of Contents <a id="table-of-contents"></a>

  * [Software](#software)
    * [Metrics](#metrics)
    * [GIS](#jump)
    * [Third party modules](#jarfiles)
    * [Shapefiles](#shapefiles)
    * [Development](#Development)
  * [Validation](#validation)
    * [DRA2020](#DRA2020)


***
## Software <a id="software"/>



  ### Metrics  <a id="metrics"></a>
  [toc](#table-of-contents)

Calculations used to compare redistricting plans are largely based on [auto-redistrict](http://autoredistrict.org/) by Kevin Baas.

  ### GIS  <a id="jump"></a>
  [toc](#table-of-contents)

Handling of GIS is based on several open source projects,
[OpenJUMP](http://www.openjump.org) and  [GeoTools](https://sourceforge.net/projects/geotools).
Code in both of these projects has been extensively modified and culled for simplicity.
Modifications include:
  * removal of features not required for redistricting
  * modularization for Java 13
  * use of JavaFX graphics classes

### Third-party Modules  <a id="jarfiles"></a>
[toc](#table-of-contents)

The *Archive* eclipse project is a collection of open-source library modules
used in the application. These have all been updated for compatibility with
Java 13.

* https://commons.apache.org/proper/commons-compress/ commons-compress-1.19.jar Apache commons compression handling. I modified code to remove dependencies on compression types that required external references. These included: 7Zip, Brotli, LZMA, XZ and ZStandard.
*  https://commons.apache.org/proper/commons-lang commons-lang3-3.9.jar Apache commons helper utilities
*  https://commons.apache.org/proper/commons-math commons-math3-3.6.jar Apache commons math utilities
* https://github.com/locationtech/jts/releases jts-core-16.1.jar VividSolutions JTS Topology Suite
* http://repo1.maven.org/maven2/com/fasterxml/jackson/core jackson-core-2.9.8.jar jackson-databind-2.9.8.jar java-annotations-2.9.8.jar JSON processing.
* https://bitbucket.org/xerial/sqlite-jdbc/downloads sqlite-jdbc-3.23.1.jar SQLite3.
* https://sourceforge.net/projects/geotools/files/GeoTools%2021%20Releases/21.2 GeoTools. 11000 source files, we use only a small fraction.
* http://www.openjump.org/jpp.html OpenJUMP 1.14. This GIS package is extremely comprehensive consisting of over 1600 source files. We have simplified it drastically, using perhaps 50.
* https://github.com/unitsofmeasurement/jsr-275 JSR-275 units of measure.
* https://sourceforge.net/projects/geographiclib/files/distrib/GeographicLib-1.50.zip/download GeographicLib, a Java library of routines for converting between geographic and geocentric coordinates.

*** Modularization ***<br/>
The open source jar files listed above have all been manually updated for Java13 module compatibility and then stored in our `git`  repository.

Thanks to [Michael Easter](https://github.com/codetojoy/easter_eggs_for_java_9/blob/master/egg_34_stack_overflow_47727869/run.sh) for tips on modularization.  For example, to modularize the JTS jar file:
Use the directory containing the un-modularized jar as the starting point. Follow the steps below and the  modularized result will be stored into ``lib``.
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

### Shapefiles <a id="shapefiles"></a>
[toc](#table-of-contents)

Code for analyzing ESRI shape files is derived from the Unified Mapping Platform, [OpenJump](https://sourceforge.net/projects/jump-pilot/files/latest/download). Code at version 1.14.1 was simplified and upgraded for Java 13.

## Development <a id="Development"/>
[toc](#table-of-contents)

The development host is an iMac running OSX Mohave (10.14) and Java 13 with the JavaFX 13 graphics classes. Java is downloadable from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html). Make sure to download the JDK and install the “Development tools” into the default location (e.g. /usr/local/bin). Extend the system path to include this area. JavaFX 13 is available from [here](https://openjfx.io/)

The build environment is *eclipse*. The 2019-09 version is available from [The Eclipse Foundation](https://www.eclipse.org/downloads/packages/). It requires a plugin update for Java 13, installable by drag and drop from [here](https://marketplace.eclipse.org/content/java-13-support-eclipse-2019-09-413).

JavaFX must be configured and available as a user library for both *eclipse* and the application. The steps to do so are explained [here](https://stackoverflow.com/questions/52144931/how-to-add-javafx-runtime-to-eclipse-in-java-11).
  * Download the JavaFX SDK and jmod files from [here](https://gluonhq.com/products/javafx)
  * Unzip the SDK file into Archive/lib and the modules into Archive/jmod.
  * From Eclipse Preferences/Java/Build Path/User Libraries create a new user library named __JavaFX13__. Add all jar files from the SDK download.
  * Configure projects to use this library.
  * Install the *eclipse* plugin `ShellWax`. This allows *bash* scripts to be run directly from *eclipse*.
***

## Validation <a id="validation"/>
[toc](#table-of-contents)

TBD
