package org.mosip.kernel.packetserver.configuration;

public class ServerConfiguration {

	private String host;
	private int port;
	private String username;
	private byte[] publicKey;
	private String hostKeyFileName;
	private String sftpRemoteDirectory;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public String getHostKeyFileName() {
		return hostKeyFileName;
	}

	public void setHostKeyFileName(String hostKeyFileName) {
		this.hostKeyFileName = hostKeyFileName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSftpRemoteDirectory() {
		return sftpRemoteDirectory;
	}

	public void setSftpRemoteDirectory(String sftpRemoteDirectory) {
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}

	public ServerConfiguration() {
	}

	public ServerConfiguration(String host, int port, String username, byte[] publicKey, String hostKeyFileName,
			String sftpRemoteDirectory, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.publicKey = publicKey;
		this.hostKeyFileName = hostKeyFileName;
		this.sftpRemoteDirectory = sftpRemoteDirectory;
		this.password = password;
	}
}
