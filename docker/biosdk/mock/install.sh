#!/bin/bash

#installs the Bio-SDK
set -e

echo "Installating Mock Bio-SDK.."

export work_dir_env=/

cp mock-sdk-0.9-rc1.jar $work_dir_env


export loader_path_env=mock-sdk-0.9-rc1.jar


echo "Installating Mock Bio-SDK completed."
