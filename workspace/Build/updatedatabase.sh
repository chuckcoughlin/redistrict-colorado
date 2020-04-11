
#	Update configuration tables in the redistricting database.
#	This does not create or drop tables. It only updates Preferences and GateProperties.
#	The current directory is the build project.
#
#!/bin/sh
#set -x
DB=rc.db
BUILD=`pwd`
export APP=${BUILD}/../../app
CONFIG=${BUILD}/../Configuration
DBDIR=${APP}/db
SQL=${CONFIG}/sql

mkdir -p ${DBDIR}
cd ${DBDIR}
sqlite3 $DB < ${SQL}/preferences.sql
sqlite3 $DB < ${SQL}/gates.sql
echo "${DB} update compete."