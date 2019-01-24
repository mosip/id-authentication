# kernel-idgenerator-machineid

1- [Background & Design](../../docs/design/kernel/kernel-idgenerator-machineid.md)

2- API Documentation

 ```
mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**
 
 mosip.kernel.machineid.length=4
 
 [application-dev.properties](../../config/application-dev.properties)


 **Database properties**
 
schema:master

table:mid_seq


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-machineid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowire interface MachineIdGenerator and call the method generateMachineId().

For example-

```
@Autowired
MachineIdGenerator <String> machineIdGenerator;

String machineId = machineIdGenerator.generateMachineId();

```
 

**Sample MID:**

GENERATED MID = 1000
 