# kernel-idgenerator-mispid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-mispid.md)

API Documentation

 ```
mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**
 
 ```
 mosip.kernel.mispid.length=3
 ```
 
 [application-dev.properties](../../config/application-dev.properties)


 **Database properties**
 
schema:master

table:tspid_seq


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-mispid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowire interface TspIdGenerator and call the method generateId().

For example-

```
@Autowired
TspIdGenerator <String> mispIdGenerator;

String mispId = mispIdGenerator.generateId();

```
 

**Sample MISPID:**

GENERATED MISPID = 100
 