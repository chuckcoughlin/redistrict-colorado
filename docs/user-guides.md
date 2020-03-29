## Redistrict Colorado
## User Guide
This guide walks through the use and installation of the *RCAnalyzer* application. *RCAnalyzer* computes metrics for redistricting plans based on a collection of datasets which contain
district geometries, voter
affiliation and demographic information based on publicly available shapefiles. The purpose of the application is to compare two or more redistricting plans for fairness. The criteria used are largely based on the work of Kevin Baas and his [autoredistrict](http://autoredistrict.org/index.php) project.

### Overview

![SplitPane](/images/application_plans.png)

```                  Splash Screen     ```

The application's main interface is a split pane with
actions in the left side controlling what is displayed on the right. A *View* menu controls which of three contexts is displayed. The contexts are:
 * Plans - these are the redistricting plans. Each plan is based on a dataset containing boundaries. This context also includes a setup pane for configuration of the weightings, affiliation and demographic datasets that are necessary for plan evaluation.
 * Datasets - a dataset holds information necessary for the construction and evaluation of a plan. Datasets are not part of the application as distributed, but are loaded in from files downloaded independently. Datasets correspond to a "shapefile" and contain geographic and other information.
 * Districts - a district is one of the regions of a dataset. The purpose of the *district* screens is to view details of a dataset.

![Menu](/images/view_menu.png)

```                  View Menu     ```
### Plans
The figure below shows the application after selection of a "Properties" button. The screen shows aggregated quantities by district
based on the boundary, affiliation and demographics datasets selected for the analysis. When this page is first selected,
application computes the aggregated values. This is a compute-intensive
process and may take as long as a minute or two. Once the
calculations have been made, results are cached for speedy access.

![Plan Properties](/images/plan_properties.png)

```                  Plan Properties     ```

The next image shows the configuration screen for a plan.
It is accessed by choosing the edit button for that plan
from the plan list.
Besides the name and description, the user must identify
the dataset that is to be used to identify the district
boundaries for that plan. The color selection refers to the
bar color that will be used to identify this plan on the
plan comparison page.

![Plan Definition](/images/plan_definition.png)

```                  Plan Definition     ```

##### Calculations
The sections below describe calculations made for each of the comparison metrics. These are largely described in the application's
comparison screen by selection of the appropriate blue "information" icon.

`Compactness:`
  To measure compactness, we calculate the *Isoperimetric Quotient*
  of each district. This is obtained by dividing its area by the square of its perimeter. We then normalize by dividing by 0.07216878, the quotient of a hexagon, a theoretical optimum honeycomb shape. In order to obtain a grand total, we average the reciprocals of these for all districts and then take the reciprocal of that. This gives us a weighted average. We want this score to be maximized.

  The normalization step guarantees that the final score is in the range 0-1.

`Population Equality`

`Composite`

### Datasets
##### Feature Attribute Aliases
Within a shapefile, a feature corresponds to a geographical area and is represented by a polygon in latitude/longitude units. Features have an arbitrary set of attributes depending on the purpose of the file. There is no naming standard for these attributes. In order to correlate features from different layers we have adopted a set of standard names which are assigned by the user of
 *RCAnalyzer*. These are:
  * ID - a unique identifier of the feature. This is the value that appears on the tree-view navigation panel in "District" scope.
  * BLACK - African-American population.
  * DEMOCRAT - votes cast for Democratic candidates.
  * GEOMETRY - defines the feature's geographic layout.
  * HISPANIC - Hispanic population.
  * FEMALE - female population.
  * MALE - male population.
  * POPULATION - total population of the area.
  * REPUBLICAN - votes cast for Republican candidates.
  * WHITE - white population count.

 Note: Some of the demographic datasets do not include POPULATION as
 a feature attribute. For those, the application computes the total
 population by adding FEMALE and MALE counts.

##### Data
The location and contents of compatible data files
is described [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md).
Datasets
of interest must be downloaded and stored locally. The application re-reads the files each time it accesses them for the first time
in a new session.

### Districts

### Preparation and Installation
The installation of the application consists of three components:
  * Application - the application is a Java desktop application
   requiring Java 13 or higher. It is distributed as an executable
   *jar* file.
  * Database - the application depends on a SQLite database to persist its state. SQLite is natively resident on most systems.
  * Shapefiles - the data files must be downloaded into the local file system. Locations of some publicly available data files are listed [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md). Shapefiles must be the zipped versions.
