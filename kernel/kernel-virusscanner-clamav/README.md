## kernel-virusscanner-clamav

 
 1- [Background & Design](../../design/kernel/kernel-virusscanner.md)
 

 2- [API Documentation <TBA>](TBA)
 
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
  
 
 3- Usage Sample
 
 
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








