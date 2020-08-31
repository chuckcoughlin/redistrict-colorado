#!/bin/bash
# Execute the DistrictPlanAnalyzer application. 
# Assume the installation bundle in ~/DistrictPlanAnalyzer
# 
export DIR=~/PlanAnalyzer/app
cd $DIR
cd ..
HOME=`pwd`
cd $DIR

mkdir -p logs
rm -rf dist

jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.PlanAnalyzer --output dist
./dist/bin/java -Djdk.tls.client.protocols=TLSv1.2 -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.PlanAnalyzer $HOME