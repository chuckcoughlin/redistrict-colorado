
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
CSV=${CONFIG}/csv
DBDIR=${CONFIG}/db
SQL=${CONFIG}/sql

mkdir -p ${DBDIR}
cd ${DBDIR}
rm -f $DB
sqlite3 $DB < ${SQL}/createTables.sql

# Change to CSV mode and load the attribute alias lookup table
cd ${CSV}
cat AttributeAlias.csv | tail -n+2|sed -e 's/	/,/g' >/tmp/attributealias

cd ${DBDIR}
sqlite3 $DB << EOF
.mode csv
.import /tmp/attributealias AttributeAlias
EOF
mkdir -p ${APP}/db
cp ${DB} ${APP}/db/${DB}
echo "${DB} creation compete."