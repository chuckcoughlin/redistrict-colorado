#!/bin/bash
# This is the runner for executing from within Eclipse
#
# NOTE: To use ScenicView, start both the tool and application outside
#        of Eclipse. Scripts are in ~/bin. 
export PATH=$PATH:/usr/local/bin
export APP=../../app
cd $APP

rm -rf dist
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home
mkdir -p logs

${JAVA_HOME}/bin/jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.PlanAnalyzer --output dist
./dist/bin/java -Djdk.tls.client.protocols=TLSv1.2 -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.PlanAnalyzer ""