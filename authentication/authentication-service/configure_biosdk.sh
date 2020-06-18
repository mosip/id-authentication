#!/bin/bash

#installs the pkcs11 libraries.
set -e

echo "Download the softhsm client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/biosdk/biosdk.zip
echo "Downloaded $artifactory_url_env"
unzip biosdk.zip
echo "Attempting to install softhsm client"
cd ./biosdk && /bin/bash ./install_biosdk.sh 
echo "Installation of softhsm client complete"

exec "$@"
