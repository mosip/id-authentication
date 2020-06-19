#!/bin/bash

#installs the pkcs11 libraries.
set -e

mkdir -p /biosdk
cd /biosdk

echo "Download the softhsm client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xaa
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xab
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xac
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xad
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xae
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xaf
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xag
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xah
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xai
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xaj
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xak
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xal
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xam
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/tech5/0.8/xan

cat x* > ./biosdk-tech5.zip

echo "Downloaded $artifactory_url_env"
unzip biosdk-tech5.zip
echo "Attempting to install softhsm client"
/bin/bash ./install_biosdk.sh 
echo "Installation of softhsm client complete"

cd /
rm -rf /biosdk
