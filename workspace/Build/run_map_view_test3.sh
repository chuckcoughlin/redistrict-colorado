#!/bin/bash
# Test Google Maps. Initialize the map with bounds.
#
# NOTE: To use ScenicView, start botth the tool and application outside
#        of Eclipse. Scripts are in ~/bin. 
export PATH=$PATH:/usr/local/bin
export APP=../../app
cd $APP

rm -rf dist
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home
mkdir -p logs
${JAVA_HOME}/bin/jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.MapViewTest3 --output dist
./dist/bin/java -Djdk.tls.client.protocols=TLSv1.2 -m rc.analyzer/redistrict.colorado.MapViewTest3 ""