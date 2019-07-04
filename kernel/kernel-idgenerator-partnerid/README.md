# kernel-idgenerator-partnerid

[Background & Design](../../docs/design/kernel/Kernel-idgenerator-partner.md)

API Documentation

 ```
mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**
 
 ```
 mosip.kernel.partnerid.length=4
 ```
 
 [application-dev.properties](../../config/application-dev.properties)


 **Database properties**
 
schema:master

table:tspid_seq


**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-partnerid</artifactId>
			<version>${project.version}</version>
	</dependency>

```


**Usage Sample:**

 Autowire interface PartnerIdGenerator and call the method generateId().

For example-

```
@Autowired
PartnerIdGenerator<String> service;;

String partnerId = service.generateId();

```
 

**Sample PartnerId:**

GENERATED PARTNERID = 1000
 