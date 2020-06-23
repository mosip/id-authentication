#!/bin/bash

#installs the pkcs11 libraries.
set -e

mkdir -p /biosdk
cd /biosdk

echo "Download the biosdk from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/biosdk.zip
echo "Downloaded $artifactory_url_env"

unzip biosdk.zip
echo "Attempting to install biosdk"
/bin/bash ./install.sh 
echo "Installation of biosdk complete"

cd /
rm -rf /biosdk
