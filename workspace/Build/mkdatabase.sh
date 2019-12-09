
#	Build a database for atorage of redistriction map definitions.
#	We do not store the actual maps, only their definitions. We
#	still require the original data-files.	
#	The current directory is the build project.
#
#!/bin/sh
#set -x
DB=rc.db
BUILD=`pwd`
CONFIG=${BUILD}/../Configuration
CSV=${CONFIG}/csv
ETC=${CONFIG}/etc
SQL=${CONFIG}/sql

cd ${ETC}
rm -f $DB
sqlite3 $DB < ${SQL}/createTables.sql

# Change to CSV mode and load pose and action tables
cd ${CSV}
cat Pose.csv | tail -n+3|sed -e 's/	/,/g' >/tmp/pose
cat PoseMap.csv | tail -n+2|sed -e 's/	/,/g' >/tmp/posemap

cd ${ETC}
sqlite3 $DB << EOF
.mode csv
.import /tmp/pose Pose
.import /tmp/posemap PoseMap
EOF

cp ${DB} ${BERT_HOME}/etc/${DB}
echo "${DB} creation compete."