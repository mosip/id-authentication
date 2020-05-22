#!/bin/bash

search_directory=$1
settings_file=$2
version=$3

echo "Attempting to publish all projects that matches version $settings_file with the settings as $version"
counter=0
find $search_directory/*/ -type f -name "pom.xml" | while read -r F
do
    xmllint xmllint --nowarning --xpath '/*[local-name()="project"]/*[local-name()="version"]' $F | grep $version
    if [ $? -eq 0 ] ; then
        mvn deploy -DskipTests -s $settings_file -f $F
    fi
done
