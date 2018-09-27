package org.mosip.kernel.sftppacketuploader.constants;

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
	 * @return {@link #host}
	 */
	public String getHost() {
		return host;
	}

	/**
	 * setter for {@link #host}
	 * 
	 * @param host
	 *            {@link #host}
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * getter for {@link #port}
	 * 
	 * @return {@link #port}
	 */
	public int getPort() {
		return port;
	}

	/**
	 * setter for {@link #port}
	 * 
	 * @param port
	 *            {@link #port}
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * getter for {@link #user}
	 * 
	 * @return {@link #user}
	 */
	public String getUser() {
		return user;
	}

	/**
	 * setter for {@link #user}
	 * 
	 * @param user
	 *            {@link #user}
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * getter for {@link #privateKeyFileName}
	 * 
	 * @return {@link #privateKeyFileName}
	 */
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	/**
	 * setter for {@link #privateKeyFileName}
	 * 
	 * @param privateKeyFileName
	 *            {@link #privateKeyFileName}
	 */
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	/**
	 * getter for {@link #privateKeyPassphrase}
	 * 
	 * @return {@link #privateKeyPassphrase}
	 */
	public String getPrivateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	/**
	 * setter for {@link #privateKeyPassphrase}
	 * 
	 * @param privateKeyPassphrase
	 *            {@link #privateKeyPassphrase}
	 */
	public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	/**
	 * getter for {@link #password}
	 * 
	 * @return {@link #password}
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * setter for {@link #password}
	 * 
	 * @param password
	 *            {@link #password}
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getter for {@link #sftpRemoteDirectory}
	 * 
	 * @return {@link #sftpRemoteDirectory}
	 */
	public String getSftpRemoteDirectory() {
		return sftpRemoteDirectory;
	}

	/**
	 * setter for {@link #sftpRemoteDirectory}
	 * 
	 * @param sftpRemoteDirectory
	 *            {@link #sftpRemoteDirectory}
	 */
	public void setSftpRemoteDirectory(String sftpRemoteDirectory) {
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}

	/**
	 * Constructor for this class
	 */
	public PacketUploaderConfiguration() {
	}

	/**
	 * @param host
	 *            {@link #host} host name or IP
	 * @param port
	 *            {@link #port} no
	 * @param user
	 *            {@link #user} username
	 * @param privateKeyFileName
	 *            {@link #privateKeyFileName} privateKey File Name <b>Should be Open
	 *            SSh Format</b>
	 * @param privateKeyPassphrase
	 *            {@link #privateKeyPassphrase} password for private key
	 * @param sftpRemoteDirectory
	 *            {@link #sftpRemoteDirectory} remote directory location
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
	 *            {@link #host} host name or IP
	 * @param port
	 *            {@link #port} no
	 * @param user
	 *            {@link #user} username
	 * @param password
	 *            {@link #password} password
	 * @param sftpRemoteDirectory
	 *            {@link #sftpRemoteDirectory} remote directory location
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
