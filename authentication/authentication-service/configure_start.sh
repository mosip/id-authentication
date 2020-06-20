#!/bin/bash

#installs the pre-requisites.
set -e

echo "Downloading pre-requisites install scripts"
wget --no-check-certificate --no-cache --no-cookies https://raw.githubusercontent.com/$git_user_env/$code_repo_env/$code_branch_env/authentication/$current_module_env/configure_biosdk.sh -O configure_biosdk.sh
wget --no-check-certificate --no-cache --no-cookies https://raw.githubusercontent.com/$git_user_env/$code_repo_env/$code_branch_env/authentication/$current_module_env/configure_softhsm.sh -O configure_softhsm.sh

echo "Installating pre-requisites.."
cd /
/bin/bash configure_biosdk.sh
cd /
/bin/bash configure_softhsm.sh

cd /

echo "Installating pre-requisites completed."


exec "$@"
