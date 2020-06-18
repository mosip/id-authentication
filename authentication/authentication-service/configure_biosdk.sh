#!/bin/bash

#installs the pkcs11 libraries.
set -e

echo "Download the softhsm client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xaa
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xab
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xac
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xad
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xae
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xaf
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xag
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xah
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xai
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xaj
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xak
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xal
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xam
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/xan

cat x* > ./biosdk-tech5.zip

echo "Downloaded $artifactory_url_env"
unzip biosdk-tech5.zip
echo "Attempting to install softhsm client"
cd ./biosdk-tech5 && /bin/bash ./install_biosdk.sh 
echo "Installation of softhsm client complete"

exec "$@"
