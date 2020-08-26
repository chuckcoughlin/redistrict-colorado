#!/bin/bash
# Create an installation bundle. It is simply a file to be unzipped
# in the user's home directory. It contains the jlinked-java code, an
# empty database and directory for storage of shapefiles.
BUILD=`pwd`
export INSTALL=${BUILD}/install
mkdir -p ${INSTALL}/db
mkdir -p ${INSTALL}/data

cd $INSTALL
echo "Synchronizing application jar files ..."


echo "Analyzer update is complete."
