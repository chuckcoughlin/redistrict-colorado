#!/bin/bash
# Link JavaFX into a custom runtime, then execute
# The custom JRE makes the application portable.
# (also I couldn't get this to work any other way)
export PATH=$PATH:/usr/local/bin
export APP=../../app
cd $APP
rm -rf dist
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home

${JAVA_HOME}/bin/jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.MapAnalyzer --output dist
./dist/bin/java -m rc.analyzer/redistrict.colorado.MapAnalyzer