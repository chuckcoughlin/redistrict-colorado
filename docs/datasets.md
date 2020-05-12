## Datasets
The list below describes some publicly-available data-sets that are compatible with the tools in this repository. Caution: The links may have changed since this writing.

* [American Fact Finder](https://factfinder.census.gov/faces/nav/jsf/pages/searchresults.xhtml?refresh=t) - Census Bureau search engine. The files below are results of custom searches.
 * co_counties.zip - Colorado county boundaries

* [Harvard dataverse](https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/NH5S2I)
 * co_2016.zip - Precinct-level presidential and senatorial election results for 2016. A typical feature field has a name like: *G16PREDCli*, which means:
```
    General election
    2016
    Presidential
    Democrat
    Clinton
```
* [TIGER Shapefiles](https://catalog.data.gov/dataset/tiger-line-shapefile-2012-2010-state-colorado-2010-census-voting-district-state-based-vtd)- These are the official US Census Bureau data files.
 * tl_2012_08_vtd10.zip - Voting district boundaries for the state of Colorado. Some of the features include:
 ```
    LSAD - Legal/Statistical Area Description
    FUNCSTAT - Functional status
    VTD - Voting district
 ```

* [Colorado Department of Local Affairs](https://demography.dola.colorado.gov/gis/gis-data/#census-and-acs) - 2014-2018 American Community Survey (demographic features). [See](file:///Users/chuckc/downloads/cntdem_acs_2014.htm) for a glossary of attribute names and their meanings.
  * ACS1418_bg.zip

* [autoredistrict](ftp://autoredistrict.org/pub/shapefiles_2010_vtd/Colorado/2010/2012/vtd/) - Results of the *autoredistrict* application run for the state of Colorado. Directories *2012* and *cd113* were zipped to create shapefiles.

Other resources:
* [Colorado Redistricting Data Used](https://www.colorado.gov/pacific/cga-redistrict/data-used) - This page documents the resources used in the state of Colorado for the 2010 redistricting effort.
 * HouseFinalPlan2010.zip - Colorado House district boundaries 2010.
 * SenateFinalPlan2010.zip - Colorado Senate district boundaries 2010.

 * [Moreno/South Map](https://redistricting.colorado.gov/proposed-congressional-maps) - Final congressional redistricting plan 2011.

* [DRA 2020](http://gardow.com/davebradlee/redistricting/default.html) - Dave's Redistricting, an interactive tool that lets you draw your own boundaries. Unfortunately the application does not export shapefiles.


* [The Atlas of Redistricting](https://projects.fivethirtyeight.com/redistricting-maps/) - the 538 organization's gerrymandering project has designed several redistricting options using 2010 census data. Their [datasets](https://github.com/fivethirtyeight/redistricting-atlas-data) provide good test cases for the current application.


* [Data.Census.gov](https://www.census.gov/data/academy/data-gems/2018/shapefiles.html) - This video shows how to download shapefiles from the US Census Bureau's American Factfinder. The data available include political boundaries and demographical information.

### GeoDatabase
The application does not handle ESRI GeoDatabase files directly. However, these files may be converted to shapefiles using the link below. The cost is <$10 per conversion. 
https://mygeodata.cloud/converter/gdb-to-shp
