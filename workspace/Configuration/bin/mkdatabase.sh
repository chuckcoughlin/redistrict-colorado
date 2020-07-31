
#	Build a database for storage of redistriction map definitions.
#	We do not store the actual maps, only their definitions. We
#	still require the original data-files.	
#	The current directory is the build project.
#
#!/bin/sh
#set -x
DB=rc.db
CONFIG=`pwd`/..
export APP=${CONFIG}/../../app
DBDIR=${APP}/db
SQL=${CONFIG}/sql

mkdir -p ${DBDIR}
cd ${DBDIR}
cp $DB rc.db.bak
rm -f $DB
sqlite3 $DB < ${SQL}/createTables.sql
sqlite3 $DB < ${SQL}/preferences.sql
sqlite3 $DB < ${SQL}/gates.sql
echo "${DB} creation compete."