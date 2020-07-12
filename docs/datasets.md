## Datasets
The list below describes some publicly-available data-sets that are compatible with the tools in this repository. Caution: The links may have changed since this writing.

* [Harvard dataverse](https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/NH5S2I)
 * co_2016.zip - Precinct-level presidential and senatorial election results for 2016. A typical feature field has a name like: *G16PREDCli*, which means:
```
    General election
    2016
    Presidential
    Democrat
    Clinton
```
* [Colorado State Demography Office](https://demography.dola.colorado.gov/gis/gis-data/#census-and-acs) - 2014-2018 American Community Survey (demographic features). [See](file:///Users/chuckc/downloads/cntdem_acs_2014.htm) for a glossary of attribute names and their meanings.
  * ACS1418_bg.zip - demographic data by block group from the Census Bureau American Factfinder community survey.
  * ACS1418_counties.zip - county boundaries
  * Census Block Groups 2010.zip - demographic data by block group from the 2010 Census


* [Colorado Redistricting Data Used](https://www.colorado.gov/pacific/cga-redistrict/data-used) - This page documents the resources used in the state of Colorado for the 2010 redistricting effort.
   * HouseFinalPlan2010.zip - Colorado House district boundaries 2010.
   * SenateFinalPlan2010.zip - Colorado Senate district boundaries 2010.
   * [Moreno/South Map](https://redistricting.colorado.gov/proposed-congressional-maps) - Final congressional redistricting plan 2011.


* [autoredistrict](ftp://autoredistrict.org/pub/shapefiles_2010_vtd/Colorado/2010/2012/vtd/) - Results of the *autoredistrict* application run for the state of Colorado.
  * autoredistrict_chuck.dbf - fair plan as a result of my personal run of the *autoredistrict* application.


* [TIGER Shapefiles](https://www.census.gov/geographies/mapping-files/time-series/geo/tiger-data.html)- These are the official US Census Bureau data files.
    * ACS_2018_5YR_BG_08.gdb.zip - demographic data by block group from the Census Bureau American Factfinder community survey. Data are in ESRI GeoDatabase format and
    thus must be converted before use.


#### GeoDatabase
The application does not handle ESRI GeoDatabase files directly. However, these files may be converted to shapefiles using the link below. The cost is <$10 per conversion.
https://mygeodata.cloud/converter/gdb-to-shp
