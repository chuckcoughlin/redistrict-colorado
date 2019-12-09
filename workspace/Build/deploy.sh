#!/bin/bash
# Working directory is the project directory (Build).
# Synchronize the distribution area with the current build products.
# The link_run_analyzer script has already created a stripped down JVM
# if the APP area.
export APP=../../app
mkdir -p ${APP}/bin
mkdir -p ${APP}/etc
mkdir -p ${APP}/sql
export PATH=$PATH:/usr/local/bin

cd ../Configuration
echo "Installing configuration files, database ..."


cd ../Build
echo "Synchronizing application jar files ..."

echo "Analyzer update is complete."
