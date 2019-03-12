## kernel-config-server

[Background & Design]( https://github.com/mosip/mosip/wiki/MOSIP-Configuration-Server )

Default Port and Context Path

```
server.port=51000
server.servlet.path=/config

```

**For Encryption Decryption of properties** <br/>
<br/>
Create keystore with following command: <br/>
`keytool -genkeypair -alias <your-alias> -keyalg RSA -keystore server.keystore -storepass <store-password> --dname "CN=<your-CN>,OU=<OU>,O=<O>,L=<L>,S=<S>,C=<C>"`

When you run the above command it will ask you for password for < your-alias > , choose your password or press enter for same password as < store-password >

The JKS keystore uses a proprietary format. It is recommended to migrate to PKCS12 which is an industry standard format, migrate it using following command:
`keytool -importkeystore -srckeystore server.keystore -destkeystore server.keystore -deststoretype pkcs12` <br/>
For more information look [here]( https://cloud.spring.io/spring-cloud-config/single/spring-cloud-config.html#_creating_a_key_store_for_testing )

**How To Run**
<br/>
To run the application: <br/>
Make sure you have configured ssh keys to connect to git, because it will take ssh keys from default location (${user.home}/.ssh) . 
Now run the jar using the following command: <br/>
<br/>
`java -jar -Dspring.cloud.config.server.git.uri=< git-repo-ssh-url > -Dspring.cloud.config.server.git.search-paths=< config-folder-location-in-git-repo > -Dencrypt.keyStore.location=file:///< file-location-of-keystore > -Dencrypt.keyStore.password=< keystore-passowrd > -Dencrypt.keyStore.alias=< keystore-alias > -Dencrypt.keyStore.secret=< keystore-secret > < jar-name >`
<br/>
<br/>
To run it inside Docker container provide the follwing run time arguments:
1. git_url_env 
The URL of your Git repo

2. git_config_folder_env
The folder inside your git repo which contains the configuration

3. encrypt_keyStore_location_env
The encrypt keystore location 

4. encrypt_keyStore_password_env
The encryption keystore password

5. encrypt_keyStore_alias_env
The encryption keystore alias

6. encrypt_keyStore_secret_env
The encryption keyStore secret

The final docker run command should look like:

`docker run --name=<name-the-container> -d -v <location-of-encrypt-keystore>/server.keystore:<mount-keystore-location-inside-container>/server.keystore:z -v /home/madmin/<location of folder containing git ssh keys>:<mount-ssh-location-inside-container>/.ssh:z -e git_url_env=<git_ssh_url_env> -e git_config_folder_env=<git_config_folder_env> -e encrypt_keyStore_location_env=file:///<mount-keystore-location-inside-container>/server.keystore -e encrypt_keyStore_password_env=<encrypt_keyStore_password_env> -e encrypt_keyStore_alias_env=<encrypt_keyStore_alias_env> -e encrypt_keyStore_secret_env=<encrypt_keyStore_secret_env> -p 51000:51000 <name-of-docker-image-you-built>`
<br/>
<br/>
**To Encrypt any property:** <br/>
Run the following command : <br/>
`curl http://<your-config-server-address>/<application-context-path-if-any>/encrypt -d <value-to-encrypt>`

And place the encrypted value in client application properties file with the format: <br/>
`password={cipher}<encrypted-value>`

**To Decrypt any property manually:** <br/>

`curl http://<your-config-server-address>/<application-context-path-if-any>/decrypt -d <encrypted-value-to-decrypt>`

**NOTE** There is no need to write decryption mechanism in client applications for encrypted values. They will be automatically decrypted by config server. 



**Application Properties**

``` 
#Port where mosip spring cloud config server needs to run
server.port = 51000

#adding context path
server.servlet.path=/config

# Uncomment spring.cloud.config.server.git.uri and spring.cloud.config.server.git.search-paths for # connecting to git Repo for configuration.
#################################################################
#Git repository location where configuration files are stored
#spring.cloud.config.server.git.uri=<your-git-repository-URL>

#Path inside the GIT repo where config files are stored, in our case they are inside config directory
#spring.cloud.config.server.git.search-paths=<folder-in-git-repository-containing-configuration>

# Uncomment spring.profiles.active and spring.cloud.config.server.native.search-locations for     # connecting to local file system for configuration.
#################################################################
# spring.profiles.active=native

# spring.cloud.config.server.native.search-locations=file:///<config-location-on-your-system>

#Server would return a HTTP 404 status, if the application is not found.By default, this flag is set to true.
spring.cloud.config.server.accept-empty=false

#Spring Cloud Config Server makes a clone of the remote git repository and if somehow the local copy gets 
#dirty (e.g. folder content changes by OS process) so Spring Cloud Config Server cannot update the local copy
#from remote repository. For Force-pull in such case, we are setting the flag to true.
spring.cloud.config.server.git.force-pull=true

# Disabling health endpoints to improve performance of config server while in development, can be commented out in production.
health.config.enabled=false

# Setting up refresh rate to 1 minute so that config server will check for updates in Git repo after every one minute,
#can be lowered down for production.
spring.cloud.config.server.git.refreshRate=60


# adding provision to clone on start of server instead of first request
spring.cloud.config.server.git.cloneOnStart=true

#For encryption of properties
###########################################
#pass at runtime
#encrypt.keyStore.location=file:///<your-encryption-keyStore-path>
#encrypt.keyStore.password=<your-encryption-keyStore-password>
#encrypt.keyStore.alias=<your-encryption-keyStore-alias>
#encrypt.keyStore.secret=<your-encryption-keyStore-secret>



```

**Config hierarchy**

![Config Properties](../../docs/design/kernel/_images/GlobalProperties_1.jpg)



**Maven dependency for Config client**

```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
			<version>${spring-cloud-config.version}</version>
		</dependency>

```


**Config client bootstrap.properties**

```
spring.cloud.config.uri=http://<config-host-url>:<config-port>
spring.cloud.config.label=<git-branch>
spring.application.name=<application-name>
spring.cloud.config.name=<property-file-to-pick-up-configuration-from>
spring.profiles.active=<active-profile>
management.endpoints.web.exposure.include=refresh
#management.security.enabled=false

#disabling health check so that client doesnt try to load properties from sprint config server every
# 5 minutes (should not be done in production)
spring.cloud.config.server.health.enabled=false

```
