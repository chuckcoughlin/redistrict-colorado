## Redistrict Colorado
### User Guide
This guide walks through the installation and use of the *RCAnalyzer* application. *RCAnalyzer* computes metrics for redistricting plans based on a collection of "datasets" that contain
district geometries, voter
affiliation and demographic information based on publicly available shapefiles. The purpose of the application is to compare two or more redistricting plans for fairness. The criteria used are based on the work of Kevin Baas and his [autoredistrict](http://autoredistrict.org/index.php) project.

#### Overview

![SplitPane](/images/application_plans.png)

```                  Main Page     ```

The application's main interface is a split pane with
actions in the left side controlling what is displayed on the right. A *View* menu controls which of three contexts is displayed. The contexts are:
 * Plans - these are the redistricting plans. Each plan is based on a dataset containing boundaries. This context also includes a setup pane for configuration of the weightings, affiliation and demographic datasets that are necessary for plan evaluation.
 * Datasets - a dataset holds information necessary for the construction and evaluation of a plan. Datasets are not part of the application as distributed, but are loaded in from files downloaded independently. Datasets correspond to a "shapefile" and contain geographic and other information.
 * Districts - a district is one of the regions of a dataset. The purpose of the *district* screens is to view details of a dataset.

![Menu](/images/view_menu.png)

```                  View Menu     ```

#### Feature Attribute Aliases
Within a shapefile, a feature corresponds to a geographical area and is represented by a polygon in latitude/longitude units. Features have an arbitrary set of attributes depending on the purpose of the file. There is no naming standard for these attributes. In order to correlate features from different layers we have adopted a set of standard names which are assigned by the user of
 *RCAnalyzer*. These are:
 * ID - a unique identifier of the feature. This is the value that appears on the tree-view navigation panel in "District" scope.
 * BLACK - African-American population.
 * DEMOCRAT - votes cast for Democratic candidates.
 * GEOMETRY - defines the feature's geographic layout.
 * HISPANIC - Hispanic population.
 * POPULATION - total population of the area.
 * REPUBLICAN - votes cast for Republican candidates.
 * WHITE - Caucasian population count.
#### Data
The location and contents of compatible data files
is described [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md)
in a separate document.
Datasets
of interest should be downloaded and stored locally. The application re-reads the files each time it accesses them for the first time.
