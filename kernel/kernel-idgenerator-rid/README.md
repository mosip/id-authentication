## kernel-idgenerator-rid
This folder has RID generator module which can be used to generate RID as numeric string based on the centerId and DongleId provided.

 [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
 **Properties to be added in Spring application environment using this component**

[kernel-idgenerator-rid-dev.properties](../../config/kernel-idgenerator-rid-dev.properties)
 
 
 
 **Database properties**
 
schema:ids

table:rid
 
 
**Maven Dependency**

```
	<dependencies>
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-rid</artifactId>
			<version>${project.version}</version>
	</dependency>

```
   
  
**The inputs which have to be provided are:**

1.CenterId of the registration center as string of size  metion in property.

2.DongleId of the device as string of size  metion in property.

For example: centerId="32345" and dongleId="56789".

 
The response will be numeric string of desire size with centerId, dongleId, five digit sequence generated numbers and timestamp in format "yyyymmddhhmmss" of 14 digits.

**Usage Sample:**

Autowired interface RidGenerator and call the method generateId(centerId,machineId).

For example-

```
@Autowired
RidGenerator <String> ridGeneratorImpl;

String rid = ridGeneratorImpl.generateId("34532","67897");

```

**Sample RID:**

GENERATED RID = 34532678970000120181122173040
 




