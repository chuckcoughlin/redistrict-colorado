#!/bin/bash
# Run the map analyzer.
# Working directory is the project directory (Build).
# Point browser to view with Google Maps

export PATH=$PATH:/usr/local/bin
export BIN=../bin
export LIB=../lib
cd $BIN
echo "Executing the shapefile viewer."
mkdir -p logs
MP=${LIB}/rc-core.jar
MP=${MP}:${LIB}/rc-ui.jar
MP=${MP}:${LIB}/rc-analyzer.jar

echo $MP
java --module-path $MP -m redistrict-colorado/redistrict.colorado.MapAnalyzer ${LIB}
