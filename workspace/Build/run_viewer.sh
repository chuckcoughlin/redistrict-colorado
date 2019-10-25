#!/bin/bash
# Run the shape viewer.
# Working directory is the project directory (Build).
# Point browser to view with Google Maps

export PATH=$PATH:/usr/local/bin
export BIN=../bin
export LIB=../lib
cd $BIN
echo "Executing the shapefile viewer."
mkdir -p logs
MP=${LIB}/open-jump-core.jar
MP=${MP}:${LIB}/open-jump-ui.jar
MP=${MP}:${LIB}/shapefile-viewer.jar

echo $MP
java --module-path $MP -m redistrict-colorado/redistrict.colorado.ShapeViewer ${LIB}
