## kernel-fsadapter-hdfs

[Background & Design -TBA-] 

[Api Documentation]


```
mvn javadoc:javadoc
```

**Application Properties**

```
# Name node url for HDFS
mosip.kernel.fsadapter.hdfs.name-node-url=hdfs://104.211.240.243:51000

# Key Distribution Center domain for Kerberos
mosip.kernel.fsadapter.hdfs.kdc-domain=NODE-MASTER.SOUTHINDIA.CLOUDAPP.AZURE.COM

# Username to access hdfs. Change this to application username (regprocessor,prereg or idrepo)
mosip.kernel.fsadapter.hdfs.user-name=mosipuser

# Password for hdfs user
mosip.kernel.fsadapter.hdfs.user-pass=Mosip@dev123

# HDFS log level. Change this to debug to see hdfs logs
logging.level.org.apache.hadoop=warn

```


To use this api, add this to dependency list:

```
<dependency>
	<groupId>io.mosip.kernel</groupId>
	<artifactId>kernel-fsadapter-hdfs</artifactId>
	<version>${project.version}</version>
</dependency>
```


**Exceptions to be handled while using this functionality:**

1. FSAdapterException


**Usage Sample**
  
Usage1: Store Packet
 
 ```
 
 @Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.storePacket("91001984930000120", FileUtils.openInputStream(new File("D:/hdfstest/testfolder/91001984930000120.zip")));

```

Usage2: Store Packet 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.storePacket("91001984930000120", new File("D:/hdfstest/testfolder/91001984930000120.zip"));

```

Usage3: Store File
 
 ```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.storeFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTH_THUMBS", FileUtils.openInputStream(new File("D:/hdfstest/testfolder/91001984930000120/Biometric/Applicant/BothThumbs.jpg")));

 ```

Usage4: Get Packet 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.getPacket("91001984930000120");

```

Usage5: Get File 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.getFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```

Usage6: Unpack Packet 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.unpackPacket("91001984930000120");

```

Usage7: Is Packet Present 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.isPacketPresent("91001984930000120");

```

Usage8: Check File Existence 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.checkFileExistence("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```

Usage9: Delete Packet 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.deletePacket("91001984930000120");

```

Usage10: Delete File 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.deleteFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS")

```

Usage11: Copy File 

```
@Autowired
private FileSystemAdapter hdfsAdapterImpl;

hdfsAdapterImpl.copyFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS", "202020202", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```
