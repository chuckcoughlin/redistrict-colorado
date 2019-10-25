# Toolchain

This document describes the prerequisites and credits the pre-cursors for installation and construction of the various tools in this repository.


***
## Table of Contents <a id="table-of-contents"></a>

  * [Software](#software)
    * [Shapefiles](#shapefiles)
  * [Validation](#validation)
    * [DRA2020](#DRA2020)


***
## Software <a id="software"/>
The development host is an iMac running OSX Mohave (10.14) and Java 10. It is downloadable from [here](https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase10-4425482.html). Make sure to download the JDK and install the “Development tools” into the default location (e.g. /usr/local/bin). Extend the system path to include this area.

The code repository resides on this machine.

### Shapefiles <a id="shapefiles"></a>
[toc](#table-of-contents)

The main code for analyzing ESRI shapefiles is derived from [OpenJump](https://sourceforge.net/projects/jump-pilot/files/latest/download).
Further details of the  format were gleaned from `QGIS` C++ code downloadable [here](https://github.com/qgis/QGIS).
