## Redistrict Colorado
## Installation
This *PlanAnalyzer* repository contains an installation bundle with the latest pre-built version of the application.
This guide contains directions for system preparation, installation of the bundle and acquisition of the external datasets and application setup.

As appropriate, generic instructions are augmented with comments in the platform-specific sections.

Note: In the following discussions we have assumed file and directory names to simplify the installation instructions. Modify the launch script if name or location changes are desired.

### Table of Contents <a id="table-of-contents"></a>

 * [General Instructions](#instructions)
 * [Mac OSX](#osx)
 * [Linux](#linux)
 * [Windows](#windows)
 * [Updating](#updates)
 * [Appendix](##appendix)

### General Instructions <a id="instructions"></a>
The following steps apply to a clean initial installation. For application updates refer to the [Updates](#updates) section.

 1. Create a folder named `PlanAnalyzer` in your home directory. When complete this will contain all files needed to run the application.
 2. Navigate to the installation bundle located in the repository [here](/release/PlanAnalyzer.tgz), then click on the download link.
 3. The downloaded file is *PlanAnalyzer.tgz*. Unbundle it and move the resulting `app` folder into the `PlanAnalyzer` folder created earlier. Inside that `app` folder is a sub-folder named `db`. Move it up a level to be under `PlanAnalyzer`. You can now delete *PlanAnalyzer.tgz*.
 4. The application requires Java 13 or newer. Java is downloadable from [Oracle Java](https://www.oracle.com/java/technologies/javase-downloads.html). Make sure to download the version appropriate for your platform.
 5. Download any `shapefiles` that might be needed for the analysis. Store them in a new `data` subdirectory of `PlanAnalyzer`. At a minimum you will need a file that defines the district boundaries for any plan to be compared, a file that contains state-wide voter affiliations, and a file that contains population counts. See [Datasets](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md) for suggestions on where these files might be obtained.
 6. Start the application and configure an analysis. In general, the following elements must be defined:
     * Datasets: On the `Dataset` panel, create a new dataset for each `shapefile`. Use the edit button in the dataset row to display a panel that can be used for its definition. Once the file path to the external file is defined, a "Save" will cause it to be read. You can then see the attributes contained in the file. Be sure to define aliases for the attributes of interest. "Save" again.
     * Plans: On the `Plan` panel, create the plans to be compared. Use the edit button to display a configuration page that will let you associate each plan with a dataset. The dataset must be of type "boundary".
     * Setup: Also on the `Plan` panel, select the "Setup" button to configure parameters of the analysis. You must define datasets to be used for both demographic and affiliation comparisons. Additionally each of the metrics has weighting and scale factors. The "info" button in the upper right corner of the table provides some guidance as to reasonable scale factors. When complete "Save".
     * Properties: Once the setup is complete, return to each plan and select the "Properties". This will result in a computation of metrics for that plan aggregated to its district boundaries. This computation may take several minutes per plan, but does not have to be repeated unless the plan or comparison datasets change.
     * Results: Select the "Analyze" button to view the results.  

 Refer to the [User Guide](http://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/user-guide.md) for further details.
 7. In order to use map features on the `District` and `Plan` panels, the application must be configured with a Google Maps API key. Each installation must have their own key. Directions for obtaining
 the free key may be found [here](https://developers.google.com/maps/documentation/javascript/tutorial#api_key). With the application running, the key may be entered on the main menu pulldown under the Colorado flag. Once a key has been entered the first time, the application must be restarted for it to be recognized.

### Mac OSX <a id="Appendix"></a>
```
1) Your home directory can be located in the main menu of the Finder under Go->Home.
3) Double-clicking the .tgz file will uncompress and unpack it.
6) In the `app` folder, the file named PlanAnalyzer.app may be double-clicked to execute the application. This file may be moved to the desktop or Applications folder if desired.
```

### Linux <a id="linux"></a>
The following may be executed from a `bash` terminal session.
  ```
  1) mkdir ~/PlanAnalyzer
  2) tar -xzf PlanAnalyzer.tgz
  3) cd ~/PlanAnalyzer
     mv app/db ~/PlanAnalyzer
     mv app ~/PlanAnalyzer
  6) From the app directory move the script, run_analyzer.sh,
     to a directory in the execution PATH. Remove the file
     extension. Following this the application can be
     launched by:
          run_analyzer
  ```

### Windows <a id="windows"></a>
WARNING: Instructions for running on Windows are incomplete.
```
 1) On windows the home directory is in C:\Users, e.g.
    C:\Users\joe
 2) To uncompress and untar the bundle, you'll need
    a third-party tool like 7-zip from https://www.7-zip.org.
 6) Inside the `app` folder is a batch file named `run_analyzer.bat`.
    You may have to edit the file to correct the path to your Java
    installation. Create a shortcut to this file and place it on your
    desktop.
```
### Updating <a id="updates"></a>

1. Download the installation bundle located [here](/release/PlanAnalyzer.zip).
2. The bundle file is *PlanAnalyzer.tgz*. Uncompress and untar it and move the resulting `app` folder into your `PlanAnalyzer` directory, overwriting the existing `app` folder.
3. Restart the application.

### Appendix <a id="appendix"></a>
 A typical working installation consists of a  `PlanAnalyzer` folder in the users's home directory with contents as follows:

 ```
PlanAnalyzer
  app
    run_analyzer.sh  - launch script
    PlanAnalyzer.app - application runner for MacOSX
    lib              - third-party libraries
    mod              - JavaFX libraries
  db
     rc.db           - configuration database
  data               - contains zipped shapefiles
  logs               - log of the most recent session
 ```
 The main directory elements have the following functions:           
   * app - the application is a Java desktop application
    requiring Java 13 or higher. It is distributed as an executable
    *jar* file. It is launched by ..
   * db - the application depends on a SQLite database to persist its state. SQLite is natively resident on most systems.
   * data - the data files must be downloaded into the local file system. Locations of some publicly available data files are listed [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md). Shapefiles may be either the zipped configuration holding .shp, .dbf, and .shx files or a single DBase (.dbf) file.
