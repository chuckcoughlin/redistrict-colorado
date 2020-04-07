#!/bin/bash
# Link JavaFX into a custom runtime, then execute.
# The custom JRE makes the application portable.
# (also I couldn't get this to work any other way)
# The current directory is Build
#
# NOTE: To use ScenicView, start botth the tool and application outside
#        of Eclipse. Scripts are in ~/bin. 
export PATH=$PATH:/usr/local/bin
export APP=../../app
cd $APP

rm -rf dist
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home
mkdir -p logs

${JAVA_HOME}/bin/jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.FairnessAnalyzer --output dist
./dist/bin/java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.FairnessAnalyzer ""