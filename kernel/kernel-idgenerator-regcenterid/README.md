# kernel-idgenerator-regcenterid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-regcenterid.md)

[API Documentation]

 ```
mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**
 ```
 mosip.kernel.registrationcenterid.length=4
 ```
 
 [application-dev.properties](../../config/application-dev.properties)


 **Database properties**
 
schema:master

table:rcid_seq


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-regcenterid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowire interface RegistrationCenterIdGenerator and call the method generateRegistrationCenterId().

For example-

```
@Autowired
RegistrationCenterIdGenerator <String> registrationCenterIdGenerator;

String regCenterId = registrationCenterIdGenerator.generateRegistrationCenterId();

```
 

**Sample RCID:**

GENERATED RCID = 1000
 