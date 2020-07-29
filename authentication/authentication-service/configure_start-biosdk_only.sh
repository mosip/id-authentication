#!/bin/bash

#installs the pre-requisites.
set -e

echo "Downloading pre-requisites install scripts"
wget --no-check-certificate --no-cache --no-cookies $artifactory_url_env/artifactory/libs-release-local/deployment/docker/id-authentication/configure_biosdk.sh -O configure_biosdk.sh

echo "Installating pre-requisites.."
cd /
source configure_biosdk.sh

echo "Installating pre-requisites completed."

if [ ! -f "${work_dir_env}/${current_module_env}.jar" ]; then
    echo "Copying ${current_module_env}.jar to ${work_dir_env}"
    cp /${current_module_env}.jar "${work_dir_env}/"
fi


echo "Changing directory to ${work_dir_env}"
cd "${work_dir_env}"


exec "$@"
