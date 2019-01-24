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
  
 
Usage Sample
 
 
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








