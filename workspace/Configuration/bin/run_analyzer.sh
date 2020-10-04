#!/bin/bash
# Execute the DistrictPlanAnalyzer application. 
# This should work for either Linux or MacOS
# 
export DIR=~/PlanAnalyzer/app
cd $DIR
cd ..
mkdir -p logs
HOME=`pwd`
cd $DIR
rm -rf dist

if [ -x /usr/libexec/java_home ]
then
   export BIN="`/usr/libexec/java_home`/bin/"
else
   export BIN=""
fi

${BIN}jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.PlanAnalyzer --output dist
./dist/bin/java -Djdk.tls.client.protocols=TLSv1.2 -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.PlanAnalyzer $HOME