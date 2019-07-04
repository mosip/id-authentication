## kernel-fsadapter-hdfs

[Background & Design](../../docs/design/kernel/kernel-filesystemadapter.md)

[Refer - Steps-to-Install-and-configuration-HDFS](https://github.com/mosip/mosip/wiki/Steps-to-Install-and-configuration-HDFS)



[Api Documentation]


```
mvn javadoc:javadoc
```

**Application Properties**

```
# Name node url for HDFS
mosip.kernel.fsadapter.hdfs.name-node-url=hdfs://host-ip:port

# Username to access hdfs. Change this to application username (regprocessor,prereg or idrepo)
mosip.kernel.fsadapter.hdfs.user-name=mosipuser

# Enable if hadoop security authorization is 'true', dafault is false
mosip.kernel.fsadapter.hdfs.authentication-enabled=false 

# If HDFS is security is configured with Kerberos, Key Distribution Center domain
mosip.kernel.fsadapter.hdfs.kdc-domain=NODE-MASTER.EXAMPLE.COM

#keytab file path, must be set if authentication-enable is true
#read keytab file both classpath and physical path ,append appropriate prefix
#for classpath prefix classpath:mosip.keytab
#for physical path prefix file:/home/keys/mosip.keytab
mosip.kernel.fsadapter.hdfs.keytab-file=classpath:mosip.keytab

#mosip.kernel.fsadapter.hdfs.connect.timeout=6000
#mosip.kernel.fsadapter.hdfs.connect.max.retries.on.timeouts=10

# HDFS log level. Change this to debug to see hdfs logs
logging.level.org.apache.hadoop=warn
```

#### To check files in hdfs, follow these steps:

1. Login to hdfs name-node (ip-address) using default username & privatekey
2. Get KDC ticket with this command. Replace ${username} with application username (regprocessor,prereg or idrepo). When prompted for password, provide the configured password) 
```
> kinit ${username}
```
3. Use this command to check files in hdfs. Replace ${username} with application username.
```
> hdfs dfs -ls -R /user/${username}
```
4. Invalidate KDC ticket with this command.
```
> kdestory -A
```

- To use this api, add this to dependency list:

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
