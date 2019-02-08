## kernel-config-server

[Background & Design]( https://github.com/mosip/mosip/wiki/MOSIP-Configuration-Server)

**Application Properties**

``` 
#Port where mosip spring cloud config server needs to run
server.port = 51000

#adding context path
server.servlet.path=/config-folder

#Git repository location where configuration files are stored
spring.cloud.config.server.git.uri=git@github.com:org/prj.git

#Path inside the GIT repo where config files are stored, in our case they are inside config directory
spring.cloud.config.server.git.search-paths=config

#Server would return a HTTP 404 status, if the application is not found.By default, this flag is set to true.
spring.cloud.config.server.accept-empty=false

#Spring Cloud Config Server makes a clone of the remote git repository and if somehow the local copy gets 
#dirty (e.g. folder content changes by OS process) so Spring Cloud Config Server cannot update the local copy
#from remote repository. For Force-pull in such case, we are setting the flag to true.
spring.cloud.config.server.git.force-pull=true

# Disabling health endpoints to improve performance of config server while in development, can be commented out in production.
spring.cloud.config.server.health.enabled=false

# Setting up refresh rate to 1 minute so that config server will check for updates in Git repo after every one minute,
#can be lowered down for production.
spring.cloud.config.server.git.refreshRate=60


# adding provision to clone on start of server instead of first request
spring.cloud.config.server.git.cloneOnStart=true

```

**Config hierarchy**

![Confif Properties](../../docs/design/kernel/_images/GlobalProperties_1.jpg)



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
spring.cloud.config.uri=http://confighost:51000
spring.cloud.config.label=master
spring.application.name=kernel-cname-service
spring.profiles.active=dev
management.endpoints.web.exposure.include=refresh
#management.security.enabled=false

#disabling health check so that client doesnt try to load properties from sprint config server every
# 5 minutes (should not be done in production)
health.config.enabled=false

```



