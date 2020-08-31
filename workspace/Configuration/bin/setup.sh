#!/bin/sh
# Create sub-directories within PlanAnalyzer 
# Then build a database for storage of redistriction map definitions.
# NOTE: We do not store the actual maps, only their definitions. We
#	still require the original data-files.	
#
#set -x
export DIR=~/PlanAnalyzer
cd $DIR

mkdir -p app db data logs
cd app

DB=rc.db
APP=`pwd`
DBDIR=${DIR}/db
SQL=${APP}/sql
cd ${DBDIR}
sqlite3 $DB < ${SQL}/createTables.sql
sqlite3 $DB < ${SQL}/preferences.sql
sqlite3 $DB < ${SQL}/gates.sql
echo "${DB} creation compete."