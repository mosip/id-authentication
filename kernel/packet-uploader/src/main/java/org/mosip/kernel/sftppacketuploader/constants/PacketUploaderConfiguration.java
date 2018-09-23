package org.mosip.kernel.packetuploader.constants;

/**
 * Configuration for packet Uploader contains {@link #host} , {@link #port} ,
 * {@link #user} ,{@link #privateKeyFileName} , {@link #privateKeyPassphrase} ,
 * {@link #password} , {@link #sftpRemoteDirectory}
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploaderConfiguration {

	/**
	 * host name/IP of server
	 */
	private String host;
	/**
	 * port no of server
	 */
	private int port;
	/**
	 * username for authentication
	 */
	private String user;
	/**
	 * privateKey for authentication <i> Only open SSH format are Supported </i> <b>
	 * ppk not Supported</b>
	 */
	private String privateKeyFileName;
	/**
	 * pass phrase for private key, null if no encryption is there
	 */
	private String privateKeyPassphrase;
	/**
	 * password for authenticaation
	 */
	private String password;
	/**
	 * SFTP Remote directory <i> Directory should be present method will not creare
	 * directory on remote side</i>
	 */
	private String sftpRemoteDirectory;

	/**
	 * getter for {@link #host}
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * setter for {@link #host}
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * getter for {@link #port}
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * setter for {@link #port}
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * getter for {@link #user}
	 * 
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * setter for {@link #user}
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * getter for {@link #privateKeyFileName}
	 * 
	 * @return
	 */
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	/**
	 * setter for {@link #privateKeyFileName}
	 * 
	 * @param privateKeyFileName
	 */
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	/**
	 * getter for {@link #privateKeyPassphrase}
	 * 
	 * @return
	 */
	public String getPrivateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	/**
	 * setter for {@link #privateKeyPassphrase}
	 * 
	 * @param privateKeyPassphrase
	 */
	public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	/**
	 * getter for {@link #password}
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * setter for {@link #password}
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getter for {@link #sftpRemoteDirectory}
	 * 
	 * @return
	 */
	public String getSftpRemoteDirectory() {
		return sftpRemoteDirectory;
	}

	/**
	 * setter for {@link #sftpRemoteDirectory}
	 * 
	 * @param sftpRemoteDirectory
	 */
	public void setSftpRemoteDirectory(String sftpRemoteDirectory) {
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}

	/**
	 * 
	 */
	public PacketUploaderConfiguration() {
	}

	/**
	 * @param host
	 * @param port
	 * @param user
	 * @param privateKeyFileName
	 * @param privateKeyPassphrase
	 * @param sftpRemoteDirectory
	 */
	public PacketUploaderConfiguration(String host, int port, String user, String privateKeyFileName,
			String privateKeyPassphrase, String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.privateKeyFileName = privateKeyFileName;
		this.privateKeyPassphrase = privateKeyPassphrase;
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}

	/**
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param sftpRemoteDirectory
	 */
	public PacketUploaderConfiguration(String host, int port, String user, String password,
			String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}
}
