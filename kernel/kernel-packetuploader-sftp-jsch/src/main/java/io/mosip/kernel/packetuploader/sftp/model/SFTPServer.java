package io.mosip.kernel.packetuploader.sftp.model;

/**
 * Configuration for packet Uploader contains {@link #host} , {@link #port} ,
 * {@link #user} ,{@link #privateKeyFileName} , {@link #privateKeyPassphrase} ,
 * {@link #password} , {@link #remoteDirectory}
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SFTPServer {

	/**
	 * Host name/IP of server
	 */
	private String host;
	/**
	 * Port no of server
	 */
	private int port;
	/**
	 * Username for authentication
	 */
	private String user;
	/**
	 * PrivateKey for authentication <i> Only open SSH format are Supported </i>
	 * <b> ppk not Supported</b>
	 */
	private String privateKeyFileName;
	/**
	 * Pass phrase for private key, null if no encryption is there
	 */
	private String privateKeyPassphrase;
	/**
	 * Password for authenticaation
	 */
	private String password;
	/**
	 * SFTP Remote directory <i> Directory should be present method will not
	 * creare directory on remote side</i>
	 */
	private String remoteDirectory;

	/**
	 * Getter for {@link #host}
	 * 
	 * @return {@link #host}
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Setter for {@link #host}
	 * 
	 * @param host
	 *            {@link #host}
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Getter for {@link #port}
	 * 
	 * @return {@link #port}
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Setter for {@link #port}
	 * 
	 * @param port
	 *            {@link #port}
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Getter for {@link #user}
	 * 
	 * @return {@link #user}
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Setter for {@link #user}
	 * 
	 * @param user
	 *            {@link #user}
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Getter for {@link #privateKeyFileName}
	 * 
	 * @return {@link #privateKeyFileName}
	 */
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	/**
	 * Setter for {@link #privateKeyFileName}
	 * 
	 * @param privateKeyFileName
	 *            {@link #privateKeyFileName}
	 */
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	/**
	 * Getter for {@link #privateKeyPassphrase}
	 * 
	 * @return {@link #privateKeyPassphrase}
	 */
	public String getPrivateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	/**
	 * Setter for {@link #privateKeyPassphrase}
	 * 
	 * @param privateKeyPassphrase
	 *            {@link #privateKeyPassphrase}
	 */
	public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	/**
	 * Getter for {@link #password}
	 * 
	 * @return {@link #password}
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Setter for {@link #password}
	 * 
	 * @param password
	 *            {@link #password}
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Getter for {@link #remoteDirectory}
	 * 
	 * @return {@link #remoteDirectory}
	 */
	public String getSftpRemoteDirectory() {
		return remoteDirectory;
	}

	/**
	 * Setter for {@link #remoteDirectory}
	 * 
	 * @param sftpRemoteDirectory
	 *            {@link #remoteDirectory}
	 */
	public void setSftpRemoteDirectory(String sftpRemoteDirectory) {
		this.remoteDirectory = sftpRemoteDirectory;
	}

	/**
	 * Constructor for this class
	 */
	public SFTPServer() {
	}

	/**
	 * Constructor for this class
	 * 
	 * @param host
	 *            {@link #host} host name or IP
	 * @param port
	 *            {@link #port} no
	 * @param user
	 *            {@link #user} username
	 * @param privateKeyFileName
	 *            {@link #privateKeyFileName} privateKey File Name <b>Should be
	 *            Open SSh Format</b>
	 * @param privateKeyPassphrase
	 *            {@link #privateKeyPassphrase} password for private key
	 * @param sftpRemoteDirectory
	 *            {@link #remoteDirectory} remote directory location
	 */
	public SFTPServer(String host, int port, String user,
			String privateKeyFileName, String privateKeyPassphrase,
			String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.privateKeyFileName = privateKeyFileName;
		this.privateKeyPassphrase = privateKeyPassphrase;
		this.remoteDirectory = sftpRemoteDirectory;
	}

	/**
	 * Constructor for this class
	 * 
	 * @param host
	 *            {@link #host} host name or IP
	 * @param port
	 *            {@link #port} no
	 * @param user
	 *            {@link #user} username
	 * @param password
	 *            {@link #password} password
	 * @param sftpRemoteDirectory
	 *            {@link #remoteDirectory} remote directory location
	 */
	public SFTPServer(String host, int port, String user,
			String password, String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.remoteDirectory = sftpRemoteDirectory;
	}
}
