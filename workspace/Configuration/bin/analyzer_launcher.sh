#!/bin/bash
# Execute the DistrictPlanAnalyzer application. 
# Assume the installation bundle in ~/FairnessAnalyzer
# 
export DIR=~/DistrictPlanAnalyzer
cd $DIR

mkdir -p logs

${JAVA_HOME}/bin/jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.DistrictPlanAnalyzer --output dist
./dist/bin/java -Djdk.tls.client.protocols=TLSv1.2 -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.DistrictPlanAnalyzer ""