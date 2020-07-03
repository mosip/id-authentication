#!/bin/bash

#installs the pkcs11 libraries.
set -e

echo "Download the client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/hsm/client.zip
echo "Downloaded $artifactory_url_env"
unzip client.zip
echo "Attempting to install"
cd ./client && ./install.sh 
echo "Installation complete"

exec "$@"