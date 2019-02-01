## kernel-virusscanner-clamav

 
[Background & Design](../../docs/design/kernel/kernel-virusscanner.md)
 

[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
 
 **Maven Dependency**
 
 ```
 <dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-templatemanager-velocity</artifactId>
			<version>${project.version}</version>
 </dependency>
 ```
  
 **Application Properties**
 
 ```
mosip.kernel.virus-scanner.host=104.211.209.102
mosip.kernel.virus-scanner.port=3310
 ```
 
 
**Usage Sample**
 
 
 ```
	@Autowired
	VirusScanner<Boolean, String> virusScannerImpl;
	
	boolean isClean = false;

			try {
				isClean = virusScannerImpl.scanFile("filepath");
			} catch (VirusScanFailedException e) {
				....
			}
 
 ```








