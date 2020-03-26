## Redistrict Colorado
This repository contains source-code for "RCAnalyzer", a tool that displays and analyzes voter maps. It is specifically designed to compare competing plans for redistricting Colorado after the 2020 census.

Many of the calculations are derived from the auto-redistrict project by Kevin Baas. GIS algorithms are
from an amalgam of OpenJUMP, GeoTools and OpenGIS open-source projects, greatly simplified to focus only on the file types necessary and modernized to run on current versions of Java and JavaFX graphics.

The application is a Java desktop application. Its function is to analyze district boundary "Shapefile" maps by combining them with population and voter affiliation statistics. The goal is to be able to compare competing map designs for fairness.

For further details see the [Toolchain](http://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/toolchain.md), [UserGuide](http://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/user-guides.md) and [Datasets](https://github.com/chuckcoughlin/redistrict-colorado/tree/master/docs/datasets.md) documents contained in this repository.
 Note that the actual datasets are not part of the application, but are publicly available and must be downloaded separately.
