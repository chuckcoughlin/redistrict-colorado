
#	Build a database for atorage of redistriction map definitions.
#	We do not store the actual maps, only their definitions. We
#	still require the original data-files.	
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
rm -f $DB
sqlite3 $DB < ${SQL}/createTables.sql
sqlite3 $DB < ${SQL}/preferences.sql
sqlite3 $DB < ${SQL}/gates.sql
echo "${DB} creation compete."