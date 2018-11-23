## kernel-packetuploader-sftp-jsch
[Background & Design](../../design/kernel/kernel-packetuploader.md)

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-packetuploader-sftp-jsch</artifactId>
		<version>${project.version}</</version>
	</dependency>
 ```

**If there is any error which occurs while channel creation,upload and release connection, it will be thrown as Exception.** 

**Exceptions to be handled while using this functionality:**
1.  ConnectionException
2.  SftpException
3.  NoSessionException
4.  IllegalConfigurationException
5.  IllegalIdentityException
6.  NullPathException
7.  EmptyPathException
8.  PacketSizeException
9.  NullConfigurationException
10. IllegalConfigurationException

**Usage Sample**



 *Usage 1:*
 
1. Create an object of SftpServer configurations.
2. Pass this object to create SFTP channel.
3. Use this channel to upload file.
4. Release the connection.
 
 ```
 @Autowired
	private PacketUploader<SFTPServer,SFTPChannel> packetUploader;
 
 SFTPServer configuration = new SFTPServer("http://104.211.212.28",22,username,password, remoteLocation);
 
  SFTPChannel channel = packetUploader.createSFTPChannel(configuration);//create channel
  packetUploader.upload(channel, filePath));//upload file
  packetUploader.releaseConnection(channel);// release connection
 
 ```

*Usage 2:*
 
 1. Create an object of SftpServer configurations.
 2. Pass this object to create SFTP channel.
 3. Use this channel to upload file.
 4. Release the connection.

 ```
  
@Autowired
	private PacketUploader<SFTPServer,SFTPChannel> packetUploader;

SFTPServer configuration = new SFTPServer("http://104.211.212.28",22,username,privateKeyFileName, privateKeyPassphrase, remoteLocation);

 SFTPChannel channel = packetUploader.createSFTPChannel(configuration);//create channel
  packetUploader.upload(channel, filePath));//upload file
  packetUploader.releaseConnection(channel);// release connection
 
 
 ```
