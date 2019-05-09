## kernel-idobjectvalidator

[Background & Design](../../docs/design/kernel/kernel-idobjectvalidator.md)
 


[API Documentation]
 
 
 ```
 mvn javadoc:javadoc

 ```

**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idobjectvalidator</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Application Properties**

```
# Application name - the name appended at starting of file name to differentiate
# between different property files for different microservices
spring.application.name=kernel-idobjectvalidator
 
#Active Profile - will relate to development properties file in the server.
#If this property is absent then default profile will be activated which is
#the property file without any environment name at the end.
spring.profiles.active=dev


# defining current branch in which we are working as label
spring.cloud.config.label=master
 
# url where spring cloud config server is running 
spring.cloud.config.uri=http://confighost:50000

# rest api where the files will be stored in git, change it accordingly in case of change of storage location.
mosip.kernel.idobjectvalidator.file-storage-uri=${spring.cloud.config.uri}/${spring.application.name}/${spring.profiles.active}/${spring.cloud.config.label}/

# Plug in property source as either 'LOCAL' or 'CONFIG_SERVER' or 'APPLICATION_CONTEXT' through this key
mosip.kernel.idobjectvalidator.property-source=CONFIG_SERVER


mosip.kernel.idobjectvalidator.valid-json-file-name=mosip-sample-identity-json-data.json

mosip.kernel.idobjectvalidator.schema-file-name=mosip-identity-json-schema-int.json 

mosip.kernel.idobjectvalidator.null-schema-file-name=kernel-json-validator-null-schema-for-testing.json

mosip.kernel.idobjectvalidator.invalid-schema-file-name=kernel-json-validator-invalid-syntax-schema-for-testing.json


```



The inputs which have to be provided are:

1. IdObject to be validated as Object.
2. Schema name against which IdObject has to be validated.
   For example: if you need to validate IdObject against any schema file named 'schema.json', provide 'schema.json' 

The Schema source is configurable.

The schema can be taken either from Config Server or from Local resource location.
1. To get the schema from local, set key 'property.source' in you property file as 'LOCAL'
2. To get the schema from spring cloud config server, set 'property.source' in your property file as 'CONFIG_SERVER' or 'APPLICATION_CONTEXT'

**If you are taking schema file from config server, you have to set 'config.server.file.storage.uri' which will be ${spring.cloud.config.uri}/${spring.application.name}/${spring.profiles.active}/${spring.cloud.config.label}/**

The respose of the validation will be of type ValidationReport having a boolen 'valid' as true if IdObject is valid and false if IdObject is invalid, along with list of warnings as arrayList if any.

If there is any error which occurs while IdObject validation, it will be thrown as Exception. 
You have to handle following custom exceptions in your code where you are using this functionality:


1. File IO Exception
2. IdObject IO Exception
3. IdObject Schema IO Exception
4. IdObject Validation Processing Exception
5. Null JSON Node Exception
6. Null JSON Schema Exception



** Usage: **

Example1:-

```
		@Autowired
		@Qualifier("composite")
		IdObjectValidator idValidator;
		
 idValidator.validateIdObject(identityObject);    // true or false

```
