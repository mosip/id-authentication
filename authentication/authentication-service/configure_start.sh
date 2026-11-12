#!/bin/bash

#installs the pre-requisites.
set -e

echo "Downloading pre-requisites install scripts"
wget --no-check-certificate --no-cache --no-cookies $artifactory_url_env/artifactory/libs-release-local/deployment/docker/id-authentication/configure_hsmclient.sh -O configure_hsmclient.sh

chmod +x configure_hsmclient.sh
./configure_hsmclient.sh

echo "Installating pre-requisites completed."

# The IAM adapter is not bundled in the jar; it is placed on the loader.path (PropertiesLauncher).
if [ -z "$iam_adapter_url_env" ]; then
  echo "iam_adapter_url_env is not set, cannot download the IAM adapter" >&2
  exit 1
fi
echo "Downloading IAM adapter..."
wget -q "${iam_adapter_url_env}" -O "${loader_path_env}/kernel-auth-adapter.jar"

# Exported so that the CMD shell (started via exec "$@") can expand $spring_args.
spring_args=""
if [ -n "$spring_config_name_env" ]; then
  spring_args="-Dspring.cloud.config.name=${spring_config_name_env}"
fi
export spring_args

exec "$@"
