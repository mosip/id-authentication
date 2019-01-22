# kernel-idgenerator-tspid

1- [Background & Design]

2- API Documentation

 ```
mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**
 
 mosip.kernel.tspid.length=4
 
 [application-dev.properties](../../config/application-dev.properties)


 **Database properties**
 
schema:ida

table:tspid_seq


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-tspid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowire interface TspIdGenerator and call the method generateId().

For example-

```
@Autowired
TspIdGenerator <String> tspIdGenerator;

String tspId = tspIdGenerator.generateId();

```
 

**Sample TSPID:**

GENERATED TSPID = 1000
 