rem Execute the DistrictPlanAnalyzer application. 
rem Assume the installation bundle in ~/DistrictPlanAnalyzer
rem 
DIR=%CD%\PlanAnalyzer
cd ..


mkdir -p logs
rmdir \S \Q dist
JAVA_HOME=C:\Program Files\Java\JRE13\bin

%JAVA_HOME%\jlink --module-path lib:mod --add-modules rc.analyzer --launcher start=rc.analyzer/redistrict.colorado.PlanAnalyzer --output dist
dist\bin\java -Djdk.tls.client.protocols=TLSv1.2 -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -m rc.analyzer/redistrict.colorado.PlanAnalyzer %DIR%