## PACKETUPLOADER-SFTP-JSCH module for kernel
This folder has PACKETUPLOADER-SFTP-JSCHE module which can be used to upload a packet using SFTP protocol.

### Api Documentation
[API Documentation <TBA>](TBA)


### The Flow to be followed is:
1. Create an object of SftpServer configurations.
2. Pass this object to create SFTP channel.
3. Use this channel to upload file.
4. Close the connection.


###### If there is any error which occurs while channel creation,upload and realease connection, it will be thrown as Exception. 

### Exceptions to be handled while using this functionality:
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

### Usage Sample
  Usage 1:
 
 ```
 SFTPServer configuration = new SFTPServer("http://104.211.212.28",22,username,password, remoteLocation);
 
@Autowired
	private PacketUploader<SFTPServer,SFTPChannel> packetUploader;
	
  SFTPChannel channel = packetUploader.createSFTPChannel(configuration);//create channel
  packetUploader.upload(channel, filePath));//upload file
  packetUploader.releaseConnection(channel);// release connection
 
 ```

 Usage 2:
 
 ```
SFTPServer configuration = new SFTPServer("http://104.211.212.28",22,username,privateKeyFileName, privateKeyPassphrase, remoteLocation);
 
@Autowired
	private PacketUploader<SFTPServer,SFTPChannel> packetUploader;
	
  SFTPChannel channel = packetUploader.createSFTPChannel(configuration);//create channel
  packetUploader.upload(channel, filePath));//upload file
  packetUploader.releaseConnection(channel);// release connection
 
 
 ```
