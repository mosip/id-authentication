#!/bin/bash

# This scipt will run kernel-config-server.jar inside Docker container according to the run time arguments provided
# while giving Docker run command

# if all encryption arguments
if [ "x$encrypt_keyStore_location_env" != "x" ] && [ "x$encrypt_keyStore_password_env" != "x" ] && [ "x$encrypt_keyStore_alias_env" != "x" ] && [ "x$encrypt_keyStore_secret_env" != "x" ];
 then echo "Encryption has to be done";
   java -jar -Dspring.cloud.config.server.git.uri=${git_url_env} -Dspring.cloud.config.server.git.search-paths=${git_config_folder_env} -Dencrypt.keyStore.location=${encrypt_keyStore_location_env} -Dencrypt.keyStore.password=${encrypt_keyStore_password_env} -Dencrypt.keyStore.alias=${encrypt_keyStore_alias_env} -Dencrypt.keyStore.secret=${encrypt_keyStore_secret_env} kernel-config-server.jar;


# running without encryption
else 
 echo "Encryption configurations not Passed, Encryption is disabled"; 
 java -jar -Dspring.cloud.config.server.git.uri=${git_url_env} -Dspring.cloud.config.server.git.search-paths=${git_config_folder_env} kernel-config-server.jar; 
 
 fi
