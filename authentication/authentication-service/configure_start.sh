#!/bin/bash

#installs the pre-requisites.
set -e

echo "Downloading pre-requisites install scripts"
wget --no-check-certificate --no-cache --no-cookies $artifactory_url_env/artifactory/libs-release-local/deployment/docker/id-authentication/configure_biosdk.sh -O configure_biosdk.sh
wget --no-check-certificate --no-cache --no-cookies $artifactory_url_env/artifactory/libs-release-local/deployment/docker/id-authentication/configure_hsmclient.sh -O configure_hsmclient.sh

echo "Installating pre-requisites.."
source ./configure_biosdk.sh
cd ~
source ./configure_hsmclient.sh

echo "Installating pre-requisites completed."

exec "$@"
