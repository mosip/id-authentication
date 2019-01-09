## kernel-idgenerator-tsp

1- [Background & Design]

2- API Documentation

 ```
mvn javadoc:javadoc

 ```
 
  **Properties to be added in Spring application environment using this component**

[kernel-idgenerator-tsp-dev.properties](../../config/kernel-idgenerator-tsp-dev.properties)


 **Database properties**
 
schema:ids

table:tsp_id 


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-tspid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowired interface TspIdGenerator and call the method generateId().

For example-

```
@Autowired
TspIdGenerator <String> tspIdGenerator;

String tspId = tspIdGenerator.generateId();

```
 

**Sample TSPID:**

GENERATED TSPID = 1000
 