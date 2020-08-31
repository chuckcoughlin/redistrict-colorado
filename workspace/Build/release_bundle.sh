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
chmod +x app/*.sh
tar -czf ../${BUNDLE_NAME}.tgz app

echo "Install bundle complete."
