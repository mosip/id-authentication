package org.mosip.kernel.packetuploader.constants;

public class PacketUploaderConfiguration {

	private String host;
	private int port;
	private String user;
	private String privateKeyFileName;
	private String privateKeyPassphrase;
	private String password;
	private String sftpRemoteDirectory;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public String getPrivateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
		this.privateKeyPassphrase = privateKeyPassphrase;
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

	public PacketUploaderConfiguration() {
	}

	public PacketUploaderConfiguration(String host, int port, String user, String privateKeyFileName,
			String privateKeyPassphrase, String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.privateKeyFileName = privateKeyFileName;
		this.privateKeyPassphrase = privateKeyPassphrase;
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}

	public PacketUploaderConfiguration(String host, int port, String user, String password,
			String sftpRemoteDirectory) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.sftpRemoteDirectory = sftpRemoteDirectory;
	}
}
