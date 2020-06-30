#!/bin/bash

#installs the pkcs11 libraries.
set -e

echo "Download the softhsm client from $artifactory_url_env"
wget $artifactory_url_env/artifactory/libs-release-local/hsm/client-centos.zip -O client.zip
echo "Downloaded $artifactory_url_env"
unzip client.zip
echo "Attempting to install softhsm client"
cd ./client && ./install.sh
echo "Installation of softhsm client complete"

