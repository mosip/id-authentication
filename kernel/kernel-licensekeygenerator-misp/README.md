## kernel-licensekeygenerator-misp

[Background & Design](../../docs/design/kernel/Kernel-licensekeygenerator-misp.md)

[API Documentation]

```
 mvn javadoc:javadoc

 ```
 
 **Properties to be added in Spring application environment using this component**
 
[application-dev.properties](../../config/application-dev.properties)
 
```
mosip.kernel.idgenerator.misp.license-key-length=8
```
 
 
**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-licensekeygenerator-misp</artifactId>
			<version>${project.version}</version>
	</dependency>

```
  
**Sample Usage**
  
```
	  Autowire the interface MISPLicenseGenerator
	  @Autowired
	  private MISPLicenseGenerator<String> mispLicenseGenerator;
```


```
	  Call generateLicense() from autowired mispLicenseGenerator instance to generate license key.     
	  String generatedLicense = mispLicenseGenerator.generateLicense());
```
	  
**Sample License**

```

 Generated License: u7y6thye
 
```   
   








