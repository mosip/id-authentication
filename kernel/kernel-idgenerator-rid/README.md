## kernel-idgenerator-rid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-rid.md)

 [API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
 **Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)


```
#-----------------------------RID Properties---------------------------------------
# length of the rid
mosip.kernel.rid.length=29
# length of the center id
mosip.kernel.rid.centerid-length=5
#length of the machine id
mosip.kernel.rid.machineid-length=5
# length of the timestamp
mosip.kernel.rid.timestamp-length=14

```


 
**Database properties**
 
schema:reg

table:rid_seq
 
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

2.machine id of the device as string of size  metion in property.

For example: centerId="32345" and machineId="56789".

 
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
 




