#!/bin/bash

#installs the pkcs11 libraries.
set -e

mkdir -p /biosdk
cd /biosdk

echo "Download the softhsm client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xaa
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xab
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xac
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xad
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xae
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xaf
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xag
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xah
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xai
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xaj
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xak
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xal
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xam
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/xan

cat x* > ./biosdk-tech5.zip

echo "Downloaded $artifactory_url_env"
unzip biosdk-tech5.zip
echo "Attempting to install softhsm client"
/bin/bash ./install_biosdk.sh 
echo "Installation of softhsm client complete"

cd /
rm -rf /biosdk

exec "$@"
