## kernel-fsadapter-ceph

[Background & Design](../../docs/design/kernel/kernel-filesystemadapter.md)

[Refer - Steps-to-Install-and-configuration-CEPH](https://github.com/mosip/mosip/wiki/Getting-Started#64-steps-to-install-and-configuration-ceph)


**Maven Dependency**

```
<dependency>
	<groupId>io.mosip.kernel</groupId>
	<artifactId>kernel-fsadapter-ceph</artifactId>
	<version>${project.version}</version>
</dependency>
```

**Application Properties**

```
#---------------------FS Adapter- CEPH ----------------------------------------
mosip.kernel.fsadapter.ceph.access-key=P9OJLQYabndd8LH
mosip.kernel.fsadapter.ceph.secret-key=jAx8v9XeyftftihM2BvTiOMiC2M
mosip.kernel.fsadapter.ceph.endpoint=http://host-ip:port

```


**Exceptions to be handled while using this functionality:**

1. FSAdapterException


**Usage Sample**
  
Usage1: Store Packet
 
 ```
 
 @Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.storePacket("91001984930000120", FileUtils.openInputStream(new File("D:/hdfstest/testfolder/91001984930000120.zip")));

```

Usage2: Store Packet 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.storePacket("91001984930000120", new File("D:/hdfstest/testfolder/91001984930000120.zip"));

```

Usage3: Store File
 
 ```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.storeFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTH_THUMBS", FileUtils.openInputStream(new File("D:/hdfstest/testfolder/91001984930000120/Biometric/Applicant/BothThumbs.jpg")));

 ```

Usage4: Get Packet 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.getPacket("91001984930000120");

```

Usage5: Get File 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.getFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```

Usage6: Unpack Packet 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.unpackPacket("91001984930000120");

```

Usage7: Is Packet Present 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.isPacketPresent("91001984930000120");

```

Usage8: Check File Existence 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.checkFileExistence("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```

Usage9: Delete Packet 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.deletePacket("91001984930000120");

```

Usage10: Delete File 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.deleteFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS")

```

Usage11: Copy File 

```
@Autowired
private FileSystemAdapter cephAdapterImpl;

cephAdapterImpl.copyFile("91001984930000120", "BIOMETRIC/APPLICANT/BOTHTHUMBS", "202020202", "BIOMETRIC/APPLICANT/BOTHTHUMBS");

```
