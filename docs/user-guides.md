## Redistrict Colorado
## User Guide
*FairnessAnalyzer* is designed to answer the question "Which is fairer?" when dealing with competing redistricting plans. The user configures plans and controls their evaluation based on a collection of external datasets which contain
district geometries, voter
affiliation and demographic information. This guide walks through the use and installation of the application.  

 The application computes an array of metrics for the purpose of comparing two or more redistricting plans for fairness. The criteria used are largely based on the work of Kevin Baas and his [autoredistrict](http://autoredistrict.org/index.php) project.

 ### Table of Contents <a id="table-of-contents"></a>

 * [Overview](#overview)
 * [Plans](#plans)
    * [Calculations](#calculations)
      * [Setup](#setup)
      * [Metrics](#metrics)
 * [Datasets](#datasets)
    * [Attribute Aliases](#aliases)
    * [Sources](#sources)
 * [Districts](#districts)
 * [Installation](#installation)

### Overview <a id="overview"></a>

![SplitPane](/images/application_plans.png)
```                  Splash Screen     ```

The application's main interface is a split pane with
actions in the left side controlling what is displayed on the right. A *View* menu controls which of three contexts is displayed. The contexts are:
 * Plans - these are the redistricting plans. Each plan is based on a dataset containing voting district boundaries. The plan context includes a setup pane for configuration of the weightings, affiliation and demographic datasets necessary for plan evaluation. Most importantly, this context includes an analysis screen which shows the result of side-by-side comparisons.
 * Datasets - a dataset holds information necessary for the construction and evaluation of a plan. Datasets are not part of the application as distributed, but are loaded in from files downloaded independently. Datasets correspond to a "shapefile" and contain geographic and other information.
 * Districts - a voting district is one of the regions of a dataset. The purpose of the *district* screen is to view boundary details of a dataset.

![Menu](/images/view_menu.png)
```                  View Menu     ```

### Plans <a id="plans"></a>
The figure below shows the plan context after selection of a "Properties" button. The screen shows aggregated quantities by district
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

#### Calculations <a id="calculations"></a>
The sections below describe calculations made for each of the comparison metrics. These are largely described in the application's
comparison screen by selection of the appropriate blue "information" icon.

###### Setup <a id="setup"></a>
Before the comparison can be made, the criteria for making that comparison must be configured. The datasets used to evaluate demographics and affiliations are set on the `Setup` screen shown below.

![Setup](/images/metric_setup.png)
```                  Comparison Setup    ```

Additionally, parameters are defined for each of the separate metrics (with the exception of the `Composite`). This includes a weighting that defines the relative importance of the particular metric. A range is also set to define the best and worst possible numeric values for the metric. This allows the values to be reasonably scaled with respect to each other.

##### Metrics <a id="metrics"></a>
###### Compactness:
  To score compactness, we use the *Polsby-Popper Test*, essentially an *Isoperimetric Quotient* normalized to a circle.
 Specifically, this requires dividing the area of each district by the square of its perimeter and then dividing by 4𝛑, the isoperimetric quotient for a circle. This results in a value between 0.0 and 1.0. In order to obtain a district-wide score, we compute the harmonic mean of individual district scores. We want this value to be *maximized*.

###### Competitive Districts:
The number of "safe" or "non-competitive" districts
should be minimized.
In a non-competitive district, the minority party may not field
a serious candidate. This leaves voters with no real choices at election time. The score is simply the number of districts that are considered competitive.

On the setup page, the value "Competitiveness Threshold" specifies the
maximum differential between party affiliations for a district to be considered competitive. 15% is often taken to be a reasonable value .

###### County Crossings:
This metric tracks the number of countries
that are split across political boundaries. It is a crude measure of how well the plan preserves communities of interest. It is computed
by tallying the number of counties contained
or partially contained in a district and then
subtracting the total number of counties. We want this score to be minimized.
The same metric could be computed using municipal or other boundaries instead of counties if so desired. The limiting factor is the availability of appropriate shapefiles.


###### Partisan Asymmetry:
Partisan asymmetry is a measure of the extent to which boundaries favor one party or the other. The application provides 4 different algorithms for this metric.

`Mean-median`

`Partisan-bias`

`Declination`
The declination function is described [here](https://observablehq.com/@sahilchinoy/gerrymandering-the-declination-function). It is a measure of partisan symmetry that does not assume any particular seats-votes proportionality.
It is sensitive to either packing or cracking distortions.
When plotted it results in a geometric angle that can be easily visualized. In our usage a negative angle indicates an unfair Democratic advantage and a positive angle indicates a Republican advantage. An angle of more than 0.3 radians indicates probable manipulation.

  * Plot the Democratic district vote shares in increasing order
  * Plot point `R`, horizontally centered on the Republican districts, whose vertical position is the average Democratic vote share of all the Republican districts
  * Plot point `D`, which is the same as point `R` but for the Democratic districts
  * Plot point `M`, which is horizontally halfway at the transition between the Democratic and Republican districts, and vertically at 50% Democratic vote share
  * Draw line segments `RM` and `MD`, and compute the angle between them
  * Multiply the angle by a normalization factor of 2/π to get the declination. Its range is [-1,1].

`Efficiency Gap`
Efficiency gap is the sum of the differences between parties of "wasted" votes divided by the total number of projected votes. A "wasted" vote is any vote that does not help elect a candidate. This includes all the votes for the losing party and any votes over 50% for the winning party.

###### Population Balance:
The constitutionally-mandated purpose of redistricting is to balance the number of people within districts. A measure of population balance is simply the standard deviation
of the population of the districts. We normalize by the total population,
multiplied by 100 to give a result in percent. This value should be *minimized*.

Colorado sets the "unfair" limit of the difference between any district population and the mean at 5%. Some court cases have specified 1%.
A red X indicator is drawn on the bar if any individual district has a deviation greater that the specified "unfair" limit.

###### Proportionality:
Proportionality is the concept that the party mix of the elected officials
should match the mix of the population as a whole. This metric compares
the projected number of seats won by the dominant party versus the number of seats that were "deserved".  Any discrepancies greater than rounding error are flagged
by putting the  symbol of the benefiting party on the bar, meaning that the results are skewed
in favor of that party.

The numerical value of the metric is the number of seats in excess of the "deserved" number for the dominant party. Ideally this is less than 1.

###### Voting Power:
We define voting power as the ability to elect a candidate of one's choosing. Another way to state this is the ability to effect the outcome of one or more elections. For a single district, this can be summarized by taking the margin of victory (in votes) and dividing it by the total votes cast. To total this up by ethnicity, we take the sum of this over all elections weighted by the population percentage for all ethnicities. For example, for hispanics, we take the total number of votes in an election, multiply by the fraction of that district that is hispanic, and total that up over all districts. Then we do the same for margin of victory. Then we divide the margin of victory total by the votes cast total, and that gives us an estimate of the average voting power for that ethnicity. We want to minimize the variance between ethnicities, so we take the average of this over the entire population, and calculate the mean absolute deviation (M.A.D.) of the ethnicities from this. This gives us a summary of how uneven voting power is distributed among the ethnicities. We want this score to be minimized.

###### Composite:
The composite or overall metric is a compendium of all the other measures with
a weighting applied. The result is a number between 0 and 10. A score of 10 means that the plan is as fair as possible. Weighting and range limits
from unfair-to-fair are taken from the setup screen.

The overall metric calculation assigns a value
From 0-10 for each of the other metrics depending on how the score falls within the specified range. The composite result is simply the weighted average of the individual scores.  

### Datasets <a id="datasets"></a>

The following screen show the Dataset context in a state just after
pressing the "Edit" button for one of its entries.
![Datasets](/images/application_datasets.png)
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

##### Standard Attribute Aliases <a id="aliases"></a>
There is no standard, that I am aware of, for naming metadata within a shapefile. Consequently, in order to make use of the values in a common way, the user must assign standard aliases to feature attributes.

Within a shapefile, a feature corresponds to a geographical area and is represented by a polygon in latitude/longitude units. Features have an arbitrary set of attributes depending on the purpose of the file. There is no naming standard for these attributes. In order to correlate features from different layers we have adopted a set of standard names which are assigned by the user of
 *FairnessAnalyzer*. These are:
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

##### Sources <a id="sources"></a>
The location and contents of compatible data files
is described [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md).
Datasets
of interest must be downloaded and stored locally. The application re-reads the files each time it accesses them for the first time
in a new session.

### Districts <a id="districts"></a>
The *districts* section shows details of districts within any "boundary" dataset. The details are displayed simply by clicking on the district within the tree layout.

### Installation <a id="installation"></a>
The application consists of three components:
  * Application - the application is a Java desktop application
   requiring Java 13 or higher. It is distributed as an executable
   *jar* file.
  * Database - the application depends on a SQLite database to persist its state. SQLite is natively resident on most systems.
  * Shapefiles - the data files must be downloaded into the local file system. Locations of some publicly available data files are listed [here](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md). Shapefiles must be the zipped versions.
