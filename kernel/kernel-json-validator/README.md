## kernel-json-validator

 1- [Background & Design](../../design/kernel/kernel-datamapper.md)
 


 2- [API Documentation <TBA>](TBA)
 
 
 ```
 mvn javadoc:javadoc

 ```
 

 
 
**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-json-validator</artifactId>
			<version>${project.version}</version>
	</dependency>

```

The inputs which have to be provided are:

1. JSON to be validated as String.
2. Schema name against which JSON has to be validated.
   For example: if you need to validate json against any schema file named 'schema.json', provide 'schema.json' 

The Schema source is configurable.

The schema can be taken either from Config Server or from Local resource location.
1. To get the schema from local, set key 'property.source' in you property file as 'LOCAL'
2. To get the schema from spring cloud config server, set 'property.source' in your property file as 'CONFIG_SERVER'

**If you are taking schema file from config server, you have to set 'config.server.file.storage.uri' which will be ${spring.cloud.config.uri}/${spring.application.name}/${spring.profiles.active}/${spring.cloud.config.label}/**

The respose of the validation will be of type JsonValidatorResponseDto having a boolen 'valid' as true if JSON is valid and false if JSON is invalid, along with list of warnings as arrayList if any.

If there is any error which occurs while JSON validation, it will be thrown as Exception. 
You have to handle following custom exceptions in your code where you are using this functionality:

1. Config Server connection Exception.
2. File IO Exception
3. Http Request Exception
4. JSON IO Exception
5. JSON Schema IO Exception
6. JSON Validation Processing Exception
7. Null JSON Node Exception
8. Null JSON Schema Exception
9. Unidentified JSON Exception


** Usage: **

Example1:-

```
		@Autowired
		JsonValidator jsonValidatorImpl;
		
<TBA>

```
