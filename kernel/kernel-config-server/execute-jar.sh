#!/bin/bash

# This scipt will run kernel-config-server.jar inside Docker container according to the run time arguments provided
# while giving Docker run command

# if all encryption arguments and Spring scerity username and password is provided
if [ "x$encrypt_keyStore_location_env" != "x" ] && [ "x$encrypt_keyStore_password_env" != "x" ] && [ "x$encrypt_keyStore_alias_env" != "x" ] && [ "x$encrypt_keyStore_secret_env" != "x" ] && [ "x$spring_security_username_env" != "x" ]&& [ "x$spring_security_password_env" != "x" ];
 then echo "Encryption has to be done && spring security username password provided";
   java -jar -Dspring.cloud.config.server.git.uri=${git_url_env} -Dspring.cloud.config.server.git.search-paths=${git_config_folder_env} -Dencrypt.keyStore.location=${encrypt_keyStore_location_env} -Dencrypt.keyStore.password=${encrypt_keyStore_password_env} -Dencrypt.keyStore.alias=${encrypt_keyStore_alias_env} -Dencrypt.keyStore.secret=${encrypt_keyStore_secret_env} -Dspring.security.user.name=${spring_security_username_env} -Dspring.security.user.password=${spring_security_password_env} kernel-config-server.jar;

# if only encryption arguments are provided
elif [ "x$encrypt_keyStore_location_env" != "x" ] && [ "x$encrypt_keyStore_password_env" != "x" ] && [ "x$encrypt_keyStore_alias_env" != "x" ] && [ "x$encrypt_keyStore_secret_env" != "x" ]
then echo "Encryption ahs to be done. Starting with default spring security username and passsword";
     java -jar -Dspring.cloud.config.server.git.uri=${git_url_env} -Dspring.cloud.config.server.git.search-paths=${git_config_folder_env} -Dencrypt.keyStore.location=${encrypt_keyStore_location_env} -Dencrypt.keyStore.password=${encrypt_keyStore_password_env} -Dencrypt.keyStore.alias=${encrypt_keyStore_alias_env} -Dencrypt.keyStore.secret=${encrypt_keyStore_secret_env} kernel-config-server.jar;

# if only spring security username and password is provided
elif [ "x$spring_security_username_env" != "x" ]&& [ "x$spring_security_password_env" != "x" ]
then echo "Encryption is disabled, Starting with spring security username and password provided";
java -jar -Dspring.security.user.name=${spring_security_username_env}
  -Dspring.security.user.password=${spring_security_password_env} kernel-config-server.jar;

# running without encryption and spring security
else 
 echo "Encryption configurations not Passed, Encryption is disabled, and security username and password not provided,
 will start with default username and password"; 
 java -jar -Dspring.cloud.config.server.git.uri=${git_url_env} -Dspring.cloud.config.server.git.search-paths=${git_config_folder_env} kernel-config-server.jar; 
 
 fi
