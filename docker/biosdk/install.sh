#!/bin/bash

#installs the Bio-SDK
set -e

echo "Installating Bio-SDK.."

echo "yum installs..."

yum install -y opencv openblas-serial lapack libgomp boost-system boost-filesystem boost-program-options libtiff libwebp jasper-libs ilmbase OpenEXR-libs libpng12

echo "local installs..."


mkdir -p /opt/Tech5
mkdir -p /faceSdk
mkdir -p /usr/share/face_sdk/

cp AuthSDK-Docker_27thFeb2020.tar /opt/Tech5
cp commons-codec-1.9.jar /opt/Tech5
cp kernel-cbeffutil-api-0.12.18.jar /opt/Tech5
cp kernel-core-0.12.18.jar /opt/Tech5
cp t5-finger-iris-mosip-auth-v1.2.jar /opt/Tech5
cp ABISTech5FaceSDKAdapter_v1.5.jar /opt/Tech5
cp face_sdk_1_5_mosip.tar /faceSdk
cp face_sdk.lic /usr/share/face_sdk/

cd /opt/Tech5/
tar xvf AuthSDK-Docker_27thFeb2020.tar
rm -f AuthSDK-Docker_27thFeb2020.tar

cd /faceSdk
tar -xvf face_sdk_1_5_mosip.tar
rm -f face_sdk_1_5_mosip.tar


yum -y localinstall tech5-face_sdk-cpu-1.5-0.x86_64.rpm tech5-face_sdk-cpu-devel-1.5-0.x86_64.rpm tech5-face_sdk-face_detector_100-1.5-0.noarch.rpm tech5-face_sdk-alignment_102-1.5-0.noarch.rpm tech5-face_sdk-builder_104-1.5-0.noarch.rpm


export LD_LIBRARY_PATH=$PATH:/opt/Tech5/nativeDocker

export loader_path_env=commons-codec-1.9.jar,t5-finger-iris-mosip-auth-v1.2.jar,ABISTech5FaceSDKAdapter_v1.5.jar,tech5-sdk-docker-v1.0.jar

echo "Installating Bio-SDK completed."
