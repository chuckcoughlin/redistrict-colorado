## Redistrict Colorado
## Installation
This *DistrictPlanAnalyzer* repository contains an installation bundle with the latest pre-built version of the application. It is located [here](/release/DistrictPlanAnalyzer.zip).
This guide contains directions for system preparation, installation of the bundle and acquisition of the external datasets which details of the plans.

Note: In the following discussions we have assumed file and directory names to simplify the installation instructions. Modify the launch script if name or location changes are desired.

### Table of Contents <a id="table-of-contents"></a>

 * [Overview](#overview)
 * [System Preparation](#preparation)
 * [General Procedure](#general)
 * [Mac OSX](#osx)
 * [Linux](#linux)
 * [Windows](#windows)

### Overview <a id="overview"></a>
The installation bundle, `DistrictPlanAnalyzer.zip`, contains the following directory and file structure:

```
DistrictPlanAnalyzer
  analyzer_launcher.sh  - launch script
  db
    rc.db               - configuration database

```
The application consists of these three elements:           
  * Application - the application is a Java desktop application
   requiring Java 13 or higher. It is distributed as an executable
   *jar* file. It is launched by ..
  * Database - the application depends on a SQLite database to persist its state. SQLite is natively resident on most systems.
  * Shapefiles - the data files must be downloaded into the local file system. Locations of some publicly available data files are listed [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md). Shapefiles may be either the zipped configuration holding .shp, .dbf, and .shx files or a single DBase (.dbf) file.



### System Preparation <a id="preparation"></a>
The application requires Java 13 or newer. Java is downloadable from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html). Make sure to download the JRE (Java Runtime Environment) appropriate for your system.

 

  * Google Maps - In order to use the map overlay feature on the `District` and `Plan` panels, each user must have their own key to the Google Maps API. Directions for obtaining
  the free key may be obtained [here](https://developers.google.com/maps/documentation/javascript/tutorial#api_key). The key may be entered on the main menu pulldown under the Colorado flag. Once a new key has been entered the application must be restarted.
