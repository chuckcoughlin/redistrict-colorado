## Redistrict Colorado
## User Guide
*FairnessAnalyzer* is designed to answer the question "Which is better?" when dealing with competing redistricting plans. The user configures plans and controls their analysis based on a collection of datasets which contain
district geometries, voter
affiliation and demographic information. This guide walks through the use and installation of the application.  

 The application computes an array of metrics for the purpose of comparing two or more redistricting plans for fairness. The criteria used are largely based on the work of Kevin Baas and his [autoredistrict](http://autoredistrict.org/index.php) project.

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
  To score compactness, we use the *Polsby-Popper Test*, essentially an *Isoperimetric Quotient* normalized to a circle.
 Specifically, this requires dividing the area of each district by the square of its perimeter and then dividing by 4𝛑, the isoperimetric quotient for a circle. This results in a value between 0.0 and 1.0. In order to obtain a district-wide score, we compute the harmonic mean of individual district scores. We want this value to be *maximized*.

`Competitive Districts`
The number of "safe" or "non-competitive" districts
should be minimized.
In a non-competitive district, the minority party may not field
a serious candidate. This leaves voters with no real choices at election time. The score is simply the number of districts that are considered competitive.

On the setup page, the value "Competitiveness Threshold" specifies the
maximum party differential for a competitive district. This is nominally 15%.

`Population Balance`
The constitutionally-mandated purpose of redistricting is to balance the number of people within the districts. A measure of population balance is simply the standard deviation
of the population of the districts. We normalize by the total population,
multiplied by 100 to give a result in percent. This value should be *minimized*.

A red X indicator is drawn on the bar if any individual district has over a 1.0% deviation. Court cases have established 1% as
the maximum allowable difference between any district population and the mean.

`Proportionality`
Proportionality is the concept that the party mix of the elected officials
should match the mix of the population as a whole. This metric compares
the actual number of seats won by the dominant party versus the number of seats that were "deserved".  Any discrepancies greater than rounding error are flagged
with the  symbol of the benefiting party on the bar, meaning that the results are skewed
in favor of that party.



`Composite`

### Datasets

![Datasets](/images/application_datasets.png)

The following screen show the Dataset context in a state just after
pressing the "Edit" button for one of its entries.

```                  Dataset List     ```

On the right side, are components which allow us to change the properties of a dataset, its name, description, shapefile path and role. The role refers to the usage of the dataset in evaluation of a plan.
  * BOUNDARIES - defines the plan itself. It specifies
  the geometry of the district boundaries.
  * DEMOGRAPHICS - lists population counts both totals and by race
  within the overall plan boundaries. The boundaries of areas
  for which counts are tallied will almost certainly NOT coincide
  with the district boundaries.
  * AFFILIATION - as with the previous, datasets of this type are
  used to evaluate plans. Counts in this type of dataset reflect
  party affiliations.

In the figure above, the dataset had already been saved, at least once. Notice how the rows in its attribute list have been sorted
by rank. This is also the order in which columns are ordered in
the detail screen.

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
