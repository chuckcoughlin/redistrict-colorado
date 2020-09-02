#!/bin/bash
# Create the installation bundle. The ant build has already assembled
# all the files. Simply fix permissions, then tar and compress.
# We use 'tar' instead of 'zip' since it maintains permissions.
#
# Usage: $0 <release_dir> <bundle_name> 
#
DIR=$1
BUNDLE_NAME=$2

cd ${DIR}
chmod +x app/*.sh app/*.app app/*.bat
chmod +x app/PlanAnalyzer.app/Contents/MacOS/*

# Build the databasae so that users don't need command-line SQLite
mkdir app/db
DB=rc.db
DBDIR=${DIR}/app/db
SQL=${DIR}/app/sql
cd ${DBDIR}
sqlite3 $DB < ${SQL}/createTables.sql
sqlite3 $DB < ${SQL}/preferences.sql
sqlite3 $DB < ${SQL}/gates.sql
echo "${DB} creation compete."

cd ${DIR}
tar -czf ../${BUNDLE_NAME}.tgz app

echo "Install bundle complete."
