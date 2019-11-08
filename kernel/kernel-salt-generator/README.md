## kernel-salt-generator

Salt Generator Job is a one-time job which is run to populate salts to be used to hash and encrypt data. This generic job takes below details as input and generates and populates salts in the given schema and table.

kernel-salt-generator requires data source details such as url, username, password, driverClassName to be provided as input in form of property key alias. The value of data source details should be provided in module specific properties as below.

```
<key-alias>.url=<url>
<key-alias>.username=<username>
<key-alias>.password=<password>
<key-alias>.driverClassName=<driverClassName>
```

kernel-salt-generator supports schema name and table name to be provided as input in form of direct value or key of the property which contains the value stored in config server.

** application.properties **

```
mosip.kernel.salt-generator.chunk-size=<chunkSize>
mosip.kernel.salt-generator.start-sequence=<startSeq>
mosip.kernel.salt-generator.end-sequence=<endSeq>
```

** module wise required properties **

```
mosip.kernel.salt-generator.db.key-alias=<property key alias providing the details for datasource such as url, username, password, driverClassName>
mosip.kernel.salt-generator.schemaName=<schemaName/property key containing the schemaName>
mosip.kernel.salt-generator.tableName=<tableName/property key containing the tableName>
```

[Background & Design](../../design/kernel/kernel-salt-generator.md)

Default Port and Context Path

```
server.port=8092
```

* If a module requires only one table to populate salt, then the above properties can be stored in config server and then execute the below command to run salt-generator.

**Java command:**

```
java -Dspring.cloud.config.uri=<url> -Dspring.cloud.config.label=<label> -Dspring.cloud.config.name=<name> -Dspring.profiles.active=<profile> -jar kernel-salt-generator.jar
```

**Build and Deployment commands:**

```
docker run -it -d -p 8092:8092 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} -e spring_config_name_env= {config_server_name} docker-registry.mosip.io:5000/kernel-salt-generator

```


* If a module requires multiple tables to be populated with salt in same DB, then the above properties can be stored in config server without the below properties and execute the command provided below to run salt generator.

** Properties not to be stored in config server **

```
mosip.kernel.salt-generator.schemaName=<schemaName/property key containing the schemaName>
mosip.kernel.salt-generator.tableName=<tableName/property key containing the tableName>
```

**Java command:**

```
java -Dspring.cloud.config.uri=<url> -Dspring.cloud.config.label=<label> -Dspring.cloud.config.name=<name> -Dspring.profiles.active=<profile> -Dmosip.kernel.salt-generator.schemaName=<schemaName/property key containing the schemaName> -Dmosip.kernel.salt-generator.tableName=<tableName/property key containing the tableName> -jar kernel-salt-generator.jar
```

**Build and Deployment commands:**

```
docker run -it -d -p 8092:8092 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} -e spring_config_name_env= {config_server_name} -e schema_name={schema} -e table_name={table} docker-registry.mosip.io:5000/kernel-salt-generator

```


* If a module requires multiple databases to be populated with salt, then the above properties can be stored in config server without the below properties and execute the command provided below to run salt generator.

** Properties not to be stored in config server **

```
mosip.kernel.salt-generator.db.key-alias=<property key alias providing the details for datasource such as url, username, password, driverClassName>
mosip.kernel.salt-generator.schemaName=<schemaName/property key containing the schemaName>
mosip.kernel.salt-generator.tableName=<tableName/property key containing the tableName>
```

**Java command:**

```
java -Dspring.cloud.config.uri=<url> -Dspring.cloud.config.label=<label> -Dspring.cloud.config.name=<name> -Dspring.profiles.active=<profile> -Dmosip.kernel.salt-generator.db.key-alias=<alias> -Dmosip.kernel.salt-generator.schemaName=<schemaName/property key containing the schemaName> -Dmosip.kernel.salt-generator.tableName=<tableName/property key containing the tableName> -jar kernel-salt-generator.jar
```

**Build and Deployment commands:**

```
docker run -it -d -p 8092:8092 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} -e spring_config_name_env= {config_server_name} -e db_alias={alias} -e schema_name={schema} -e table_name={table} docker-registry.mosip.io:5000/kernel-salt-generator

```