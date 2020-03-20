## Redistrict Colorado
### User Guide
This guide walks through the installation and use of the *RCAnalyzer* application. *RCAnalyzer* computes metrics for a redistricting plan based on a collection of "datasets" that contain
district geometries, voter
affiliation and demographic information based on publicly available shapefiles.

#### Overview

![SplitPane](/images/application_plans.png)
```                  Main Page     ```

The application's main interface is a split pane with
actions in the left side controlling what is displayed on the right. A main menu controls which of the three scopes is displayed.
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
