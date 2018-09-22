package org.mosip.kernel.packetuploader.constants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploaderConfiguration {

	/**
	 * 
	 */
	private String host;
	/**
	 * 
	 */
	private int port;
	/**
	 * 
	 */
	private String user;
	/**
	 * 
	 */
	private String privateKeyFileName;
	/**
	 * 
	 */
	private String privateKeyPassphrase;
	/**
	 * 
	 */
	private String password;
	/**
	 * 
	 */
	private String sftpRemoteDirectory;

	/**
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return
	 */
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	/**
	 * @param privateKeyFileName
	 */
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	/**
	 * @return
	 */
	public String getPrivateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	/**
	 * @param privateKeyPassphrase
	 */
	public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return
	 */
	public String getSftpRemoteDirectory() {
		return sftpRemoteDirectory;
	}

	/**
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
