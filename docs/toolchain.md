# Toolchain

This document describes the prerequisites and credits the pre-cursors for installation and construction of the various tools in this repository.


***
## Table of Contents <a id="table-of-contents"></a>

  * [Software](#software)
    * [Third party jar files](#jarfiles)
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
* https://github.com/locationtech/jts/releases jts-core-16.1.jar VividSolutions JTS Topology Suite

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
### Shapefiles <a id="shapefiles"></a>
[toc](#table-of-contents)

The main code for analyzing ESRI shape files is derived from the Unified Mapping Platform, [OpenJump](https://sourceforge.net/projects/jump-pilot/files/latest/download). Code at version 1.14.1 was simplified and upgraded for Java 11.
Further details of the format were gleaned from `QGIS` C++ code downloadable [here](https://github.com/qgis/QGIS).
